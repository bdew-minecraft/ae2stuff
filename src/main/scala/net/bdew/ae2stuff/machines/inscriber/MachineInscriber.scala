/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.inscriber

import appeng.api.config.Upgrades
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.ae2stuff.AE2Stuff
import net.bdew.ae2stuff.network.{MsgSetLock, NetHandler}
import net.bdew.lib.Misc
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.Machine
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

object MachineInscriber extends Machine("Inscriber", BlockInscriber) with GuiProvider {
  override def guiId = 3
  override type TEClass = TileInscriber

  lazy val idlePowerDraw = tuning.getDouble("IdlePower")
  lazy val cyclePower = tuning.getDouble("CyclePower")
  lazy val powerCapacity = tuning.getDouble("PowerCapacity")
  lazy val cycleTicks = tuning.getInt("CycleTicks")

  AE2Stuff.onPostInit.listen { ev =>
    // Can't do this too early, causes error
    Upgrades.SPEED.registerItem(new ItemStack(BlockInscriber), 5)
  }

  NetHandler.regServerHandler {
    case (MsgSetLock(slot, lock), player) =>
      Misc.asInstanceOpt(player.openContainer, classOf[ContainerInscriber]).foreach { cont =>
        slot match {
          case "top" => cont.te.topLocked := lock
          case "bottom" => cont.te.bottomLocked := lock
          case _ => sys.error("Invalid slot")
        }
      }
  }

  @SideOnly(Side.CLIENT)
  override def getGui(te: TEClass, player: EntityPlayer) = new GuiInscriber(new ContainerInscriber(te, player))
  override def getContainer(te: TEClass, player: EntityPlayer) = new ContainerInscriber(te, player)
}
