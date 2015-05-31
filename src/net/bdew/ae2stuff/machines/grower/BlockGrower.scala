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
import net.bdew.lib.block.{HasTE, SimpleBlock}
import net.bdew.lib.tile.inventory.BreakableInventoryBlock
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import net.minecraft.world.World

object BlockGrower extends SimpleBlock("Grower", Material.iron) with HasTE[TileGrower] with BreakableInventoryBlock {
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
    iconOn = reg.registerIcon(modId + ":grower/main_on")
    iconOff = reg.registerIcon(modId + ":grower/main_off")
  }

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, xOffset: Float, yOffset: Float, zOffset: Float): Boolean = {
    if (!world.isRemote)
      player.openGui(AE2Stuff, MachineGrower.guiId, world, x, y, z)
    true
  }

  override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, player: EntityLivingBase, stack: ItemStack) {
    if (player.isInstanceOf[EntityPlayer])
      getTE(world, x, y, z).placingPlayer = player.asInstanceOf[EntityPlayer]
  }
}
