/*
 * Copyright (c) bdew, 2014 - 2017
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
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
