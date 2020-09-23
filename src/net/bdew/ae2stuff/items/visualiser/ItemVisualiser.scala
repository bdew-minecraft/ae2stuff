/*
 * Copyright (c) bdew, 2014 - 2020
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.bdew.ae2stuff.items.visualiser

import java.util
import java.util.Locale

import appeng.api.networking.{GridFlags, IGridConnection, IGridHost}
import appeng.api.util.AEPartLocation
import com.mojang.realmsclient.gui.ChatFormatting
import net.bdew.ae2stuff.misc.ItemLocationStore
import net.bdew.ae2stuff.network.{MsgVisualisationData, MsgVisualisationMode, NetHandler}
import net.bdew.lib.Misc
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.helpers.ChatHelper._
import net.bdew.lib.items.BaseItem
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumFacing, EnumHand}
import net.minecraft.world.World

object ItemVisualiser extends BaseItem("visualiser") with ItemLocationStore {
  setMaxStackSize(1)

  override def onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) = {
    val stack = player.getHeldItem(hand)
    if (!world.isRemote) {
      world.getTileSafe[IGridHost](pos) foreach { tile =>
        setLocation(stack, pos, world.provider.getDimension)
        player.sendStatusMessage(L("ae2stuff.visualiser.bound", pos.getX.toString, pos.getY.toString, pos.getZ.toString).setColor(Color.GREEN), true)
      }
    }
    EnumActionResult.SUCCESS
  }

  def getMode(stack: ItemStack) = {
    if (stack.hasTagCompound)
      VisualisationModes(stack.getTagCompound.getByte("mode"))
    else
      VisualisationModes.FULL
  }

  def setMode(stack: ItemStack, mode: VisualisationModes.Value) = {
    if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound)
    stack.getTagCompound.setByte("mode", mode.id.toByte)
  }

  NetHandler.regServerHandler {
    case (MsgVisualisationMode(mode), player) =>
      if (player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem == this) {
        setMode(player.inventory.getCurrentItem, mode)

        import net.bdew.lib.helpers.ChatHelper._
        player.sendStatusMessage(L("ae2stuff.visualiser.set", L("ae2stuff.visualiser.mode." + mode.toString.toLowerCase(Locale.US)).setColor(Color.YELLOW)), true)
      }
  }

  override def onUpdate(stack: ItemStack, world: World, entity: Entity, slot: Int, active: Boolean): Unit = {
    if (world.isRemote || !entity.isInstanceOf[EntityPlayerMP]) return

    val player = entity.asInstanceOf[EntityPlayerMP]

    val main = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND)
    val off = player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND)

    val stack =
      if (main != null && main.getItem == this)
        main
      else if (off != null && off.getItem == this)
        off
      else return

    for {
      boundLoc <- getLocation(stack) if (boundLoc.dim == world.provider.getDimension) && VisualiserPlayerTracker.needToUpdate(player, boundLoc)
      host <- world.getTileSafe[IGridHost](boundLoc.pos)
      node <- Option(host.getGridNode(AEPartLocation.INTERNAL))
      grid <- Option(node.getGrid)
    } {
      import scala.collection.JavaConversions._
      var connections = Set.empty[IGridConnection]
      val nodes = (for (node <- grid.getNodes) yield {
        val block = node.getGridBlock
        if (block.isWorldAccessible && block.getLocation.isInWorld(world)) {
          val loc = block.getLocation
          connections ++= node.getConnections
          var flags = VNodeFlags.ValueSet.empty
          if (!node.meetsChannelRequirements()) flags += VNodeFlags.MISSING
          if (node.hasFlag(GridFlags.DENSE_CAPACITY)) flags += VNodeFlags.DENSE
          Some(node -> VNode(loc.x, loc.y, loc.z, flags))
        } else None
      }).flatten.toMap

      val connList = for {
        c <- connections
        n1 <- nodes.get(c.a())
        n2 <- nodes.get(c.b()) if n1 != n2
      } yield {
        var flags = VLinkFlags.ValueSet.empty
        if (c.a().hasFlag(GridFlags.DENSE_CAPACITY) && c.b().hasFlag(GridFlags.DENSE_CAPACITY)) flags += VLinkFlags.DENSE
        if (c.a().hasFlag(GridFlags.CANNOT_CARRY_COMPRESSED) && c.b().hasFlag(GridFlags.CANNOT_CARRY_COMPRESSED)) flags += VLinkFlags.COMPRESSED
        VLink(n1, n2, c.getUsedChannels.toByte, flags)
      }

      NetHandler.sendTo(MsgVisualisationData(new VisualisationData(nodes.values.toList, connList.toList)), player)
    }
  }

  override def addInformation(stack: ItemStack, worldIn: World, tooltip: util.List[String], flagIn: ITooltipFlag) = {
    super.addInformation(stack, worldIn, tooltip, flagIn)
    tooltip.add("%s %s%s".format(
      Misc.toLocal("ae2stuff.visualiser.mode"),
      ChatFormatting.YELLOW,
      Misc.toLocal("ae2stuff.visualiser.mode." + getMode(stack).toString.toLowerCase(Locale.US))
    ))
  }
}
