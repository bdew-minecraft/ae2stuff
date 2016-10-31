/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.wireless

import net.bdew.ae2stuff.misc.{BlockActiveTexture, BlockWrenchable, MachineMaterial}
import net.bdew.lib.block.{BaseBlock, HasTE}
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.World

object BlockWireless extends BaseBlock("Wireless", MachineMaterial) with HasTE[TileWireless] with BlockWrenchable with BlockActiveTexture {
  override val TEClass = classOf[TileWireless]

  setHardness(1)

  override def onBlockActivatedReal(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, heldItem: ItemStack, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = false

  override def breakBlock(world: World, pos: BlockPos, state: IBlockState): Unit = {
    getTE(world, pos).doUnlink()
    super.breakBlock(world, pos, state)
  }

  override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack): Unit = {
    super.onBlockPlacedBy(world, pos, state, placer, stack)
    if (placer.isInstanceOf[EntityPlayer])
      getTE(world, pos).placingPlayer = placer.asInstanceOf[EntityPlayer]
  }
}
