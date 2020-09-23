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
    val r = CraftingManager.findMatchingResult(c, te.getWorld)
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
          if (slotStack.isEmpty) {
            slot.putStack(AE2Defs.materials.blankPattern().maybeStack(playerStack.getCount).get())
            player.inventory.setItemStack(ItemStack.EMPTY)
            detectAndSendChanges()
            return ItemStack.EMPTY
          } else if (slotStack.getCount + playerStack.getCount <= slotStack.getMaxStackSize) {
            slotStack.grow(playerStack.getCount)
            slot.onSlotChanged()
            player.inventory.setItemStack(ItemStack.EMPTY)
            detectAndSendChanges()
            return ItemStack.EMPTY
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
      if (patternStack.isEmpty) {
        patternSlot.putStack(AE2Defs.materials.blankPattern().maybeStack(clickedStack.getCount).get())
        fromSlot.putStack(ItemStack.EMPTY)
        return ItemStack.EMPTY
      } else if (patternStack.getCount + clickedStack.getCount <= patternStack.getMaxStackSize) {
        patternStack.grow(clickedStack.getCount)
        patternSlot.onSlotChanged()
        fromSlot.putStack(ItemStack.EMPTY)
        return ItemStack.EMPTY
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
}
