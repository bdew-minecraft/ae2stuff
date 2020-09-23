/*
 * Copyright (c) bdew, 2014 - 2020
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
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

