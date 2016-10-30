/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.encoder

import net.bdew.ae2stuff.AE2Defs
import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{ClickType, InventoryCrafting}
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager

class ContainerEncoder(val te: TileEncoder, player: EntityPlayer) extends BaseContainer(te) {

  for (x <- 0 until 3; y <- 0 until 3) {
    addSlotToContainer(new SlotFakeCrafting(te, te.slots.recipe(x + y * 3), 10 + x * 18, 17 + y * 18, updateRecipe))
  }

  addSlotToContainer(new SlotFakeCraftingResult(te, te.slots.result, 104, 35))
  val patternSlot = addSlotToContainer(new SlotValidating(te, te.slots.patterns, 143, 17))
  addSlotToContainer(new SlotFakeEncodedPattern(te, te.slots.encoded, 143, 53))

  bindPlayerInventory(player.inventory, 8, 84, 142)

  def updateRecipe() {
    val c = new InventoryCrafting(this, 3, 3)
    for (i <- 0 until 9)
      c.setInventorySlotContents(i, te.getStackInSlot(te.slots.recipe(i)))
    val r = CraftingManager.getInstance.findMatchingRecipe(c, te.getWorld)
    te.setInventorySlotContents(te.slots.result, r)
  }

  override def slotClick(slotNum: Int, button: Int, clickType: ClickType, player: EntityPlayer): ItemStack = {
    import scala.collection.JavaConversions._
    if (inventorySlots.isDefinedAt(slotNum)) {
      val slot = getSlot(slotNum)
      if (slot == patternSlot && button == 0 && clickType == ClickType.PICKUP) {
        val playerStack = player.inventory.getItemStack
        val slotStack = slot.getStack
        if (AE2Defs.items.encodedPattern().isSameAs(playerStack)) {
          if (slotStack == null) {
            slot.putStack(AE2Defs.materials.blankPattern().maybeStack(playerStack.stackSize).get())
            player.inventory.setItemStack(null)
            detectAndSendChanges()
            return null
          } else if (slotStack.stackSize + playerStack.stackSize <= slotStack.getMaxStackSize) {
            slotStack.stackSize += playerStack.stackSize
            slot.onSlotChanged()
            player.inventory.setItemStack(null)
            detectAndSendChanges()
            return null
          }
        }
      }
    }

    // This is a hacky workaround!
    // When a player changes the contents of a slot, isChangingQuantityOnly is set to true,
    // preventing updates to OTHER slots from being detected and sent back
    // Here i ensure changes are sent back before returning so NetHandlerPlayServer.processClickWindow doesn't
    // get the opportunity to mess things up
    val r = super.slotClick(slotNum, button, clickType, player)
    detectAndSendChanges()
    r
  }

  override def transferStackInSlot(player: EntityPlayer, slot: Int): ItemStack = {
    val fromSlot = getSlot(slot)
    val clickedStack = fromSlot.getStack
    if (fromSlot.inventory == player.inventory && AE2Defs.items.encodedPattern().isSameAs(clickedStack)) {
      val patternStack = patternSlot.getStack
      if (patternStack == null) {
        patternSlot.putStack(AE2Defs.materials.blankPattern().maybeStack(clickedStack.stackSize).get())
        fromSlot.putStack(null)
        return null
      } else if (patternStack.stackSize + clickedStack.stackSize <= patternStack.getMaxStackSize) {
        patternStack.stackSize += clickedStack.stackSize
        patternSlot.onSlotChanged()
        fromSlot.putStack(null)
        return null
      }
    }
    super.transferStackInSlot(player, slot)
  }

  override def detectAndSendChanges() {
    super.detectAndSendChanges()
    if (!te.getWorld.isRemote) {
      if (!te.getNode.isActive) {
        player.closeScreen()
      }
    }
  }

  override def canInteractWith(player: EntityPlayer) = te.isUseableByPlayer(player)
}
