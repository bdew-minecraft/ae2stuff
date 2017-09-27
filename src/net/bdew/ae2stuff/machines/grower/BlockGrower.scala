/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.grower

import net.bdew.ae2stuff.AE2Stuff
import net.bdew.ae2stuff.misc.{BlockActiveTexture, BlockWrenchable, MachineMaterial}
import net.bdew.lib.block.{BaseBlock, BlockKeepData, HasTE}
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.World

object BlockGrower extends BaseBlock("grower", MachineMaterial) with HasTE[TileGrower] with BlockKeepData with BlockWrenchable with BlockActiveTexture {
  override val TEClass = classOf[TileGrower]

  setHardness(1)

  override def onBlockActivatedReal(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, heldItem: ItemStack, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    player.openGui(AE2Stuff, MachineGrower.guiId, world, pos.getX, pos.getY, pos.getZ)
    true
  }

  override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack): Unit = {
    super.onBlockPlacedBy(world, pos, state, placer, stack)
    if (placer.isInstanceOf[EntityPlayer])
      getTE(world, pos).placingPlayer = placer.asInstanceOf[EntityPlayer]
  }
}
