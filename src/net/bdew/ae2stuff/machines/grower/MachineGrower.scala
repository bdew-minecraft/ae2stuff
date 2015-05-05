/*
 * Copyright (c) bdew, 2014
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.grower

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.{Machine, PoweredMachine}
import net.minecraft.entity.player.EntityPlayer

object MachineGrower extends Machine("Grower", BlockGrower) with GuiProvider with PoweredMachine {
  override def guiId = 2
  override type TEClass = TileGrower

  lazy val idlePowerDraw = tuning.getDouble("IdlePower")
  lazy val cyclePower = tuning.getDouble("CyclePower")
  lazy val cycleTicks = tuning.getInt("CycleTicks")
  lazy val powerCapacity = tuning.getDouble("PowerCapacity")

  @SideOnly(Side.CLIENT)
  override def getGui(te: TEClass, player: EntityPlayer) = new GuiGrower(new ContainerGrower(te, player))
  override def getContainer(te: TEClass, player: EntityPlayer) = new ContainerGrower(te, player)
}
