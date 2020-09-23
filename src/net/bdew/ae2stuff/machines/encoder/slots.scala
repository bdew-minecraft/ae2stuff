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

package net.bdew.ae2stuff.machines.encoder

import net.bdew.lib.gui.SlotClickable
import net.bdew.lib.items.ItemUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{ClickType, IInventory, Slot}
import net.minecraft.item.ItemStack

class SlotFakeCrafting(inv: IInventory, slot: Int, x: Int, y: Int, onChanged: () => Unit) extends Slot(inv, slot, x, y) with SlotClickable {
  override def canTakeStack(p: EntityPlayer) = false

  override def onClick(clickType: ClickType, button: Int, player: EntityPlayer): ItemStack = {
    val newStack = if (!player.inventory.getItemStack.isEmpty) {
      val stackCopy = player.inventory.getItemStack.copy()
      stackCopy.setCount(1)
      stackCopy
    } else {
      ItemStack.EMPTY
    }

    if (!ItemStack.areItemStacksEqual(newStack, inventory.getStackInSlot(slot))) {
      inventory.setInventorySlotContents(slot, newStack)
      onChanged()
    }

    player.inventory.getItemStack
  }
}

class SlotFakeCraftingResult(inv: IInventory, slot: Int, x: Int, y: Int) extends Slot(inv, slot, x, y) {
  override def canTakeStack(p: EntityPlayer) = false
  override def isItemValid(s: ItemStack) = false
}

class SlotFakeEncodedPattern(inv: TileEncoder, slot: Int, x: Int, y: Int) extends Slot(inv, slot, x, y) with SlotClickable {

  override def onClick(clickType: ClickType, button: Int, player: EntityPlayer): ItemStack = {
    val encoded = inv.getStackInSlot(slot)
    if (!encoded.isEmpty) {
      if (clickType == ClickType.PICKUP) {
        if (player.inventory.getItemStack.isEmpty) {
          player.inventory.setItemStack(encoded.copy())
          inv.decrStackSize(inv.slots.patterns, 1)
        }
      } else if (clickType == ClickType.QUICK_MOVE) {
        if (ItemUtils.addStackToSlots(encoded.copy(), player.inventory, 0 until player.inventory.getSizeInventory, true).isEmpty)
          inv.decrStackSize(inv.slots.patterns, 1)
      }
    }
    player.inventory.getItemStack
  }
}