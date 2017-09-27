/*
 * Copyright (c) bdew, 2014 - 2017
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.misc

import net.bdew.ae2stuff.compat.WrenchRegistry
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.World

trait BlockWrenchable extends Block {
  def onBlockActivatedReal(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, heldItem: ItemStack, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean

  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ)) return true
    if (player.isSneaking) {
      for {
        stack <- Option(player.inventory.getCurrentItem)
        wrench <- WrenchRegistry.findWrench(player, stack, pos)
      } {
        wrench.doWrench(player, stack, pos)
        if (!world.isRemote) world.destroyBlock(pos, true)
        return true
      }
      false
    } else {
      if (!world.isRemote)
        onBlockActivatedReal(world, pos, state, player, hand, player.getHeldItem(hand), side, hitX, hitY, hitZ)
      else
        true
    }
  }
}