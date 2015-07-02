/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.encoder

import net.bdew.lib.gui.SlotClickable
import net.bdew.lib.items.ItemUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{IInventory, Slot}
import net.minecraft.item.ItemStack

class SlotFakeCrafting(inv: IInventory, slot: Int, x: Int, y: Int, onChanged: () => Unit) extends Slot(inv, slot, x, y) with SlotClickable {
  override def canTakeStack(p: EntityPlayer) = false
  override def onClick(button: Int, mods: Int, player: EntityPlayer) = {
    val newStack = if (player.inventory.getItemStack != null) {
      val stackCopy = player.inventory.getItemStack.copy()
      stackCopy.stackSize = 1
      stackCopy
    } else {
      null
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
  override def onClick(button: Int, mods: Int, player: EntityPlayer) = {
    val encoded = inv.getStackInSlot(slot)
    if (encoded != null) {
      if (mods == 0) {
        if (player.inventory.getItemStack == null) {
          player.inventory.setItemStack(encoded.copy())
          inv.decrStackSize(inv.slots.patterns, 1)
        }
      } else if (mods == 1) {
        if (ItemUtils.addStackToSlots(encoded.copy(), player.inventory, 0 until player.inventory.getSizeInventory, true) == null)
          inv.decrStackSize(inv.slots.patterns, 1)
      }
    }
    player.inventory.getItemStack
  }
}