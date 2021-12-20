/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.items.visualiser

import java.util
import java.util.Locale

import appeng.api.networking.{GridFlags, IGridConnection, IGridHost}
import net.bdew.ae2stuff.misc.ItemLocationStore
import net.bdew.ae2stuff.network.{MsgVisualisationData, MsgVisualisationMode, NetHandler}
import net.bdew.lib.Misc
import net.bdew.lib.block.BlockRef
import net.bdew.lib.helpers.ChatHelper._
import net.bdew.lib.items.SimpleItem
import net.minecraft.entity.Entity
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumChatFormatting
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

object ItemVisualiser extends SimpleItem("Visualiser") with ItemLocationStore {
  setMaxStackSize(1)

  override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, xOff: Float, yOff: Float, zOff: Float): Boolean = {
    if (!world.isRemote) {
      val pos = BlockRef(x, y, z)
      pos.getTile[IGridHost](world) foreach { tile =>
        setLocation(stack, pos, world.provider.dimensionId)
        player.addChatMessage(L("ae2stuff.visualiser.bound", pos.x.toString, pos.y.toString, pos.z.toString).setColor(Color.GREEN))
      }
    }
    true
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
        player.addChatMessage(L("ae2stuff.visualiser.set", L("ae2stuff.visualiser.mode." + mode.toString.toLowerCase(Locale.US)).setColor(Color.YELLOW)))
      }
  }

  override def onUpdate(stack: ItemStack, world: World, entity: Entity, slot: Int, active: Boolean): Unit = {
    if (!active || world.isRemote || !entity.isInstanceOf[EntityPlayerMP]) return

    val player = entity.asInstanceOf[EntityPlayerMP]
    val stack = player.inventory.getCurrentItem

    if (!hasLocation(stack)) return

    val dim = getDimension(stack)
    val loc = getLocation(stack)

    if ((dim == world.provider.dimensionId) && VisualiserPlayerTracker.needToUpdate(player, loc, dim)) {
      for {
        host <- loc.getTile[IGridHost](world)
        node <- Option(host.getGridNode(ForgeDirection.UNKNOWN))
        grid <- Option(node.getGrid)
      } {
        import scala.collection.JavaConversions._
        var seen = Set.empty[IGridConnection]
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
  }

  override def addInformation(stack: ItemStack, player: EntityPlayer, list: util.List[_], extended: Boolean): Unit = {
    val strings = list.asInstanceOf[util.List[String]]
    strings.add("%s %s%s".format(
      Misc.toLocal("ae2stuff.visualiser.mode"),
      EnumChatFormatting.YELLOW,
      Misc.toLocal("ae2stuff.visualiser.mode." + getMode(stack).toString.toLowerCase(Locale.US))
    ))
  }

}
