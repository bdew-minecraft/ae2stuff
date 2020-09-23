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

package net.bdew.ae2stuff.waila

import java.util

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor, IWailaDataProvider}
import net.bdew.ae2stuff.AE2Stuff
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World

class BaseDataProvider[T](cls: Class[T]) extends IWailaDataProvider {
  def getTailStrings(target: T, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = None
  def getHeadStrings(target: T, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = None
  def getBodyStrings(target: T, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = None
  def getNBTTag(player: EntityPlayerMP, te: T, tag: NBTTagCompound, world: World, pos: BlockPos): NBTTagCompound = tag

  final override def getNBTData(player: EntityPlayerMP, te: TileEntity, tag: NBTTagCompound, world: World, pos: BlockPos): NBTTagCompound =
    try {
      if (cls.isInstance(te))
        getNBTTag(player, te.asInstanceOf[T], tag, world, pos)
      else
        tag
    } catch {
      case e: Throwable =>
        AE2Stuff.logWarnException("Error in waila handler", e)
        tag
    }

  import scala.collection.JavaConversions._

  final override def getWailaTail(itemStack: ItemStack, tip: util.List[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler) = {
    try {
      if (cls.isInstance(accessor.getTileEntity))
        tip.addAll(getTailStrings(accessor.getTileEntity.asInstanceOf[T], itemStack, accessor, config))
      else if (cls.isInstance(accessor.getBlock))
        tip.addAll(getTailStrings(accessor.getBlock.asInstanceOf[T], itemStack, accessor, config))
    } catch {
      case e: Throwable =>
        AE2Stuff.logWarn("Error in waila handler: %s", e.toString)
        e.printStackTrace()
        tip.add("[%s%s%s]".format(TextFormatting.RED, e.toString, TextFormatting.RESET))
    }
    tip
  }

  final override def getWailaHead(itemStack: ItemStack, tip: util.List[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler) = {
    try {
      if (cls.isInstance(accessor.getTileEntity))
        tip.addAll(getHeadStrings(accessor.getTileEntity.asInstanceOf[T], itemStack, accessor, config))
      else if (cls.isInstance(accessor.getBlock))
        tip.addAll(getHeadStrings(accessor.getBlock.asInstanceOf[T], itemStack, accessor, config))
    } catch {
      case e: Throwable =>
        AE2Stuff.logWarn("Error in waila handler: %s", e.toString)
        e.printStackTrace()
        tip.add("[%s%s%s]".format(TextFormatting.RED, e.toString, TextFormatting.RESET))
    }
    tip
  }

  final override def getWailaBody(itemStack: ItemStack, tip: util.List[String], accessor: IWailaDataAccessor, config: IWailaConfigHandler) = {
    try {
      if (cls.isInstance(accessor.getTileEntity))
        tip.addAll(getBodyStrings(accessor.getTileEntity.asInstanceOf[T], itemStack, accessor, config))
      else if (cls.isInstance(accessor.getBlock))
        tip.addAll(getBodyStrings(accessor.getBlock.asInstanceOf[T], itemStack, accessor, config))
    } catch {
      case e: Throwable =>
        AE2Stuff.logWarn("Error in waila handler: %s", e.toString)
        e.printStackTrace()
        tip.add("[%s%s%s]".format(TextFormatting.RED, e.toString, TextFormatting.RESET))
    }
    tip
  }

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = ItemStack.EMPTY
}
