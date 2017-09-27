/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.encoder

import net.bdew.ae2stuff.network.{MsgSetRecipe, NetHandler}
import net.bdew.lib.Misc
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.Machine
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object MachineEncoder extends Machine("Encoder", BlockEncoder) with GuiProvider {
  override def guiId = 1
  override type TEClass = TileEncoder

  lazy val idlePowerDraw = tuning.getDouble("IdlePower")

  @SideOnly(Side.CLIENT)
  override def getGui(te: TEClass, player: EntityPlayer) = new GuiEncoder(new ContainerEncoder(te, player))
  override def getContainer(te: TEClass, player: EntityPlayer) = new ContainerEncoder(te, player)

  NetHandler.regServerHandler {
    case (MsgSetRecipe(data), player) =>
      Misc.asInstanceOpt(player.openContainer, classOf[ContainerEncoder]).foreach { cont =>
        for ((slotNum, recIdx) <- cont.te.slots.recipe.zipWithIndex) {
          val items = data.tag.getList[ItemStack](slotNum.toString)
          if (items.nonEmpty)
            cont.te.setInventorySlotContents(recIdx, cont.te.findMatchingRecipeStack(items))
          else
            cont.te.setInventorySlotContents(recIdx, ItemStack.EMPTY)
        }
        cont.updateRecipe()
      }
  }
}
