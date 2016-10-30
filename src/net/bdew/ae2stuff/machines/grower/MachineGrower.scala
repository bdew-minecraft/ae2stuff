/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.grower

import appeng.api.config.Upgrades
import net.bdew.ae2stuff.AE2Stuff
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.{Machine, PoweredMachine}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object MachineGrower extends Machine("Grower", BlockGrower) with GuiProvider with PoweredMachine {
  override def guiId = 2
  override type TEClass = TileGrower

  lazy val idlePowerDraw = tuning.getDouble("IdlePower")
  lazy val cyclePower = tuning.getDouble("CyclePower")
  lazy val cycleTicks = tuning.getInt("CycleTicks")
  lazy val powerCapacity = tuning.getDouble("PowerCapacity")

  AE2Stuff.onPostInit.listen { ev =>
    // Can't do this too early, causes error
    Upgrades.SPEED.registerItem(new ItemStack(BlockGrower), 3)
  }

  @SideOnly(Side.CLIENT)
  override def getGui(te: TEClass, player: EntityPlayer) = new GuiGrower(new ContainerGrower(te, player))
  override def getContainer(te: TEClass, player: EntityPlayer) = new ContainerGrower(te, player)
}
