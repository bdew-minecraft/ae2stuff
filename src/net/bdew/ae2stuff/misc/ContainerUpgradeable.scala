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

import appeng.api.implementations.guiobjects.INetworkTool
import appeng.api.implementations.items.IUpgradeModule
import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.bdew.lib.items.ItemUtils
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{ClickType, IInventory, Slot}
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity

trait ContainerUpgradeable extends BaseContainer {
  var netToolSlot: Option[Int] = None
  var netToolObj: Option[INetworkTool] = None
  var upgradeInventory: IInventory = null

  def initUpgradeable(te: TileEntity, upgradeInventory: IInventory, player: EntityPlayer, baseX: Int, baseY: Int, astc: (Slot) => Unit): Unit = {
    this.upgradeInventory = upgradeInventory
    netToolSlot = UpgradeableHelper.findNetworktoolStack(player)
    netToolObj = netToolSlot map (x => UpgradeableHelper.getNetworkToolObj(player.inventory.getStackInSlot(x), te))

    netToolObj foreach { nt =>
      for (i <- 0 until 3; j <- 0 until 3) {
        astc(new SlotValidating(new InventoryHandlerAdapter(nt.getInventory), i + j * 3, baseX + i * 18, baseY + j * 18))
      }
    }
  }

  def hasToolbox = netToolObj.isDefined

  override def transferStackInSlot(player: EntityPlayer, slot: Int): ItemStack = {
    val stack = getSlot(slot).getStack
    if (netToolObj.contains(getSlot(slot).inventory)) {
      // Moving from the net tool - move into upgrades inv
      getSlot(slot).putStack(ItemUtils.addStackToSlots(stack, upgradeInventory, 0 until upgradeInventory.getSizeInventory, true))
    } else if (!stack.isEmpty && stack.getItem.isInstanceOf[IUpgradeModule] && stack.getItem.asInstanceOf[IUpgradeModule].getType(stack) != null) {
      // Is an upgrade
      if (getSlot(slot).inventory != upgradeInventory) {
        // If it's not in upgrade inventory try to move it there
        getSlot(slot).putStack(ItemUtils.addStackToSlots(stack, upgradeInventory, 0 until upgradeInventory.getSizeInventory, true))
      } else if (netToolObj.isDefined) {
        // Otherwise if we have a network tool, move it there
        getSlot(slot).putStack(ItemUtils.addStackToHandler(stack, netToolObj.get.getInventory))
      } else {
        // Otherwise let the default handler move it to player inventory
        super.transferStackInSlot(player, slot)
      }
    } else {
      // Otherwise let the default handler work
      super.transferStackInSlot(player, slot)
    }
    ItemStack.EMPTY
  }

  override def slotClick(slotNum: Int, button: Int, clickType: ClickType, player: EntityPlayer): ItemStack = {
    if (slotNum > 0 && slotNum < inventorySlots.size() && netToolSlot.isDefined) {
      val slot = getSlot(slotNum)
      if (slot.isHere(player.inventory, netToolSlot.get))
        return ItemStack.EMPTY
    }
    if (clickType == ClickType.SWAP && netToolSlot.contains(button))
      ItemStack.EMPTY
    else
      super.slotClick(slotNum, button, clickType, player)
  }
}
