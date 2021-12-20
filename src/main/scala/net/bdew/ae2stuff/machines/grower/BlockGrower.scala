/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.grower

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.ae2stuff.AE2Stuff
import net.bdew.ae2stuff.misc.{BlockWrenchable, MachineMaterial}
import net.bdew.lib.Misc
import net.bdew.lib.block.{BlockKeepData, HasTE, SimpleBlock}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.World

object BlockGrower extends SimpleBlock("Grower", MachineMaterial) with HasTE[TileGrower] with BlockWrenchable with BlockKeepData {
  override val TEClass = classOf[TileGrower]

  setHardness(1)

  var iconOn: IIcon = null
  var iconOff: IIcon = null

  override def getIcon(side: Int, meta: Int) =
    if (meta == 1)
      iconOn
    else
      iconOff

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(reg: IIconRegister) {
    iconOn = reg.registerIcon(Misc.iconName(modId, name, "main_on"))
    iconOff = reg.registerIcon(Misc.iconName(modId, name, "main_off"))
  }

  override def onBlockActivatedReal(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, xOffs: Float, yOffs: Float, zOffs: Float): Boolean = {
    player.openGui(AE2Stuff, MachineGrower.guiId, world, x, y, z)
    true
  }

  override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, player: EntityLivingBase, stack: ItemStack) {
    if (player.isInstanceOf[EntityPlayer])
      getTE(world, x, y, z).placingPlayer = player.asInstanceOf[EntityPlayer]
  }
}
