/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.encoder

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.ae2stuff.network.{MsgSetRecipe, MsgSetRecipe2, MsgSetRecipe3, NetHandler}
import net.bdew.lib.Misc
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.Machine
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{ChatComponentText, ChatStyle, EnumChatFormatting}

object MachineEncoder extends Machine("Encoder", BlockEncoder) with GuiProvider {
  override def guiId = 1
  override type TEClass = TileEncoder

  lazy val idlePowerDraw = tuning.getDouble("IdlePower")

  @SideOnly(Side.CLIENT)
  override def getGui(te: TEClass, player: EntityPlayer) = new GuiEncoder(new ContainerEncoder(te, player))
  override def getContainer(te: TEClass, player: EntityPlayer) = new ContainerEncoder(te, player)

  import net.bdew.lib.nbt._

  NetHandler.regServerHandler {
    case (MsgSetRecipe(recipe), player) =>
      player.addChatMessage(new ChatComponentText("Your client version of AE2 Stuff, please update")
        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)))

    case (MsgSetRecipe2(recipe), player) =>
      Misc.asInstanceOpt(player.openContainer, classOf[ContainerEncoder]).foreach { cont =>
        for ((slotNum, recIdx) <- cont.te.slots.recipe.zipWithIndex) {
          if (recipe.isDefinedAt(recIdx))
            cont.te.setInventorySlotContents(recIdx, cont.te.findMatchingRecipeStack(recipe(recIdx) map (_.stack)))
          else
            cont.te.setInventorySlotContents(recIdx, null)
        }
        cont.updateRecipe()
      }

    case (MsgSetRecipe3(data), player) =>
      Misc.asInstanceOpt(player.openContainer, classOf[ContainerEncoder]).foreach { cont =>
        for ((slotNum, recIdx) <- cont.te.slots.recipe.zipWithIndex) {
          val items = data.tag.getList[NBTTagCompound](slotNum.toString) map ItemStack.loadItemStackFromNBT
          if (items.nonEmpty)
            cont.te.setInventorySlotContents(recIdx, cont.te.findMatchingRecipeStack(items))
          else
            cont.te.setInventorySlotContents(recIdx, null)
        }
        cont.updateRecipe()
      }
  }
}
