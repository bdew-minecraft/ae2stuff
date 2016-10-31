/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.encoder

import net.bdew.ae2stuff.AE2Stuff
import net.bdew.ae2stuff.misc.{BlockActiveTexture, BlockWrenchable, MachineMaterial}
import net.bdew.lib.block.{BaseBlock, BlockKeepData, HasTE}
import net.bdew.lib.rotate.{BlockFacingMeta, Properties}
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util._
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object BlockEncoder extends BaseBlock("Encoder", MachineMaterial) with HasTE[TileEncoder] with BlockWrenchable with BlockFacingMeta with BlockKeepData with BlockActiveTexture {
  override val TEClass = classOf[TileEncoder]

  setHardness(1)

  override def getStateFromMeta(meta: Int) =
    getDefaultState
      .withProperty(Properties.FACING, EnumFacing.getFront(meta & 7))
      .withProperty(BlockActiveTexture.Active, Boolean.box((meta & 8) > 0))

  override def getMetaFromState(state: IBlockState) = {
    state.getValue(Properties.FACING).ordinal() | (if (state.getValue(BlockActiveTexture.Active)) 8 else 0)
  }

  override def onBlockActivatedReal(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, heldItem: ItemStack, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (getTE(world, pos).getNode.isActive) {
      player.openGui(AE2Stuff, MachineEncoder.guiId, world, pos.getX, pos.getY, pos.getZ)
    } else {
      import net.bdew.lib.helpers.ChatHelper._
      player.addChatMessage(L("ae2stuff.error.not_connected").setColor(Color.RED))
    }
    true
  }

  override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack): Unit = {
    super.onBlockPlacedBy(world, pos, state, placer, stack)
    if (placer.isInstanceOf[EntityPlayer])
      getTE(world, pos).placingPlayer = placer.asInstanceOf[EntityPlayer]
  }
}
