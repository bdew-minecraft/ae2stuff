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

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.items.{IItemHandler, IItemHandlerModifiable}

class InventoryHandlerAdapter(handler: IItemHandler) extends IInventory {
  private def allSlots = 0 until handler.getSlots
  private def allStacks = allSlots map handler.getStackInSlot

  override def getSizeInventory: Int = handler.getSlots

  override def isEmpty: Boolean = allStacks.forall(_.isEmpty)
  override def clear(): Unit = allSlots.foreach(removeStackFromSlot)

  override def getInventoryStackLimit: Int = allSlots.map(handler.getSlotLimit).max

  override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean =
    handler.insertItem(index, stack, true).getCount < stack.getCount

  override def getStackInSlot(index: Int): ItemStack = handler.getStackInSlot(index)
  override def decrStackSize(index: Int, count: Int): ItemStack = handler.extractItem(index, count, false)
  override def removeStackFromSlot(index: Int): ItemStack = handler.extractItem(index, handler.getStackInSlot(index).getCount, false)

  override def setInventorySlotContents(index: Int, stack: ItemStack): Unit = {
    handler match {
      case x: IItemHandlerModifiable => x.setStackInSlot(index, stack)
      case _ =>
        removeStackFromSlot(index)
        handler.insertItem(index, stack, false)
    }
  }

  override val getName = ""
  override val hasCustomName = false
  override val getDisplayName = new TextComponentString("")

  override def getFieldCount = 0
  override def getField(id: Int) = throw new UnsupportedOperationException
  override def setField(id: Int, value: Int) = throw new UnsupportedOperationException

  override def markDirty() = {}
  override def isUsableByPlayer(player: EntityPlayer) = true
  override def openInventory(player: EntityPlayer) = {}
  override def closeInventory(player: EntityPlayer) = {}
}
