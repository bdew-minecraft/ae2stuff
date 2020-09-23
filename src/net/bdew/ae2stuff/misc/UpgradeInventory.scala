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

import appeng.api.config.Upgrades
import appeng.api.implementations.items.IUpgradeModule
import net.bdew.lib.data.DataSlotInventory
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.bdew.lib.items.ItemUtils
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class UpgradeInventory(name: String, parent: TileDataSlots, size: Int, kinds: Set[Upgrades]) extends DataSlotInventory(name, parent, size) {
  override def getInventoryStackLimit() = 1
  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean =
    if (!stack.isEmpty && stack.getItem.isInstanceOf[IUpgradeModule])
      kinds.contains(stack.getItem.asInstanceOf[IUpgradeModule].getType(stack))
    else false

  var cards = Map.empty[Upgrades, Int].withDefaultValue(0)

  def updateUpgradeCounts() =
    cards = inv filter { x =>
      !x.isEmpty && x.getItem.isInstanceOf[IUpgradeModule]
    } map { x =>
      x.getItem.asInstanceOf[IUpgradeModule].getType(x)
    } groupBy identity mapValues (_.length) withDefaultValue 0

  override def load(t: NBTTagCompound, kind: UpdateKind.Value): Unit = {
    super.load(t, kind)
    updateUpgradeCounts()
  }

  override def markDirty(): Unit = {
    updateUpgradeCounts()
    super.markDirty()
  }

  def dropInventory(): Unit = {
    if (parent.getWorldObject != null && !parent.getWorld.isRemote) {
      for (stack <- inv if !stack.isEmpty) {
        ItemUtils.throwItemAt(parent.getWorld, parent.getPos, stack)
      }
      clear()
    }
  }
}
