/*
 * Copyright (c) bdew, 2014 - 2017
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.misc

import net.bdew.lib.block.BaseBlock
import net.minecraft.block.properties.{IProperty, PropertyBool}
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{IBlockAccess, World}

object BlockActiveTexture {
  val Active = PropertyBool.create("active")
}

trait BlockActiveTexture extends BaseBlock {
  override def getProperties: List[IProperty[_]] = super.getProperties :+ BlockActiveTexture.Active

  def isActive(world: IBlockAccess, pos: BlockPos) = world.getBlockState(pos).getValue(BlockActiveTexture.Active)

  def setActive(world: World, pos: BlockPos, v: Boolean) = {
    val state = world.getBlockState(pos)
    if (state.getBlock == this && state.getValue(BlockActiveTexture.Active) != v)
      world.setBlockState(pos, state.withProperty(BlockActiveTexture.Active, Boolean.box(v)), 3)
  }

  override def getMetaFromState(state: IBlockState): Int =
    if (state.getValue(BlockActiveTexture.Active)) 1 else 0

  //noinspection ScalaDeprecation
  override def getStateFromMeta(meta: Int): IBlockState =
    if ((meta & 1) == 1)
      super.getStateFromMeta(meta).withProperty(BlockActiveTexture.Active, Boolean.box(true))
    else
      super.getStateFromMeta(meta).withProperty(BlockActiveTexture.Active, Boolean.box(false))
}

