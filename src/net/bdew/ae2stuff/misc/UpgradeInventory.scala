/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
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
