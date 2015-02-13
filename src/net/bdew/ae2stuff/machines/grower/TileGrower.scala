/*
 * Copyright (c) bdew, 2014
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.grower

import appeng.api.AEApi
import appeng.api.implementations.items.IGrowableCrystal
import cpw.mods.fml.common.registry.GameRegistry
import net.bdew.ae2stuff.grid.{GridTile, VariableIdlePower}
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.tile.TileExtended
import net.bdew.lib.tile.inventory.{BreakableInventoryTile, PersistentInventoryTile, SidedInventory}
import net.minecraft.item.ItemStack

class TileGrower extends TileExtended with GridTile with SidedInventory with PersistentInventoryTile with BreakableInventoryTile with VariableIdlePower {
  override def getSizeInventory = 3 * 9
  override def getMachineRepresentation = new ItemStack(BlockGrower)

  var sleepMode = false

  val redstoneDust = GameRegistry.findItem("minecraft", "redstone")
  val netherQuartz = GameRegistry.findItem("minecraft", "quartz")
  val crystal = AEApi.instance().items().itemCrystalSeed.item().asInstanceOf[IGrowableCrystal]
  val chargedCertusQuartz = AEApi.instance().materials().materialCertusQuartzCrystalCharged
  val fluixCrystal = AEApi.instance().materials().materialFluixCrystal

  setIdlePowerUse(MachineGrower.idlePowerDraw)

  serverTick.listen(() => {
    if (!sleepMode && getNode.isActive && (getWorldObj.getTotalWorldTime % MachineGrower.cycleTicks == 0)) {
      var hadWork = false
      val invZipped = inv.zipWithIndex.filter(_._1 != null)
      for ((stack, slot) <- invZipped if stack.getItem.isInstanceOf[IGrowableCrystal]) {
        val ns = stack.getItem.asInstanceOf[IGrowableCrystal].triggerGrowth(stack)
        setInventorySlotContents(slot, ns)
        hadWork = true
      }
      for {
        (cert, certPos) <- invZipped.find(x => chargedCertusQuartz.sameAsStack(x._1))
        redstonePos <- ItemUtils.findItemInInventory(this, redstoneDust)
        netherPos <- ItemUtils.findItemInInventory(this, netherQuartz)
        (_, empty) <- inv.zipWithIndex.find(x => x._1 == null || (fluixCrystal.sameAsStack(x._1) && x._1.stackSize <= x._1.getMaxStackSize - 2))
      } {
        decrStackSize(certPos, 1)
        decrStackSize(netherPos, 1)
        decrStackSize(redstonePos, 1)
        ItemUtils.addStackToSlots(fluixCrystal.stack(2), this, 0 until getSizeInventory, false)
        hadWork = true
      }
      if (hadWork) {
        setIdlePowerUse(MachineGrower.activePowerDraw)
      } else {
        setIdlePowerUse(MachineGrower.idlePowerDraw)
        sleepMode = true
      }
    }
  })

  override def markDirty(): Unit = {
    sleepMode = false
    super.markDirty()
  }

  allowSided = true
  override def isItemValidForSlot(slot: Int, stack: ItemStack) =
    stack != null && (
      stack.getItem.isInstanceOf[IGrowableCrystal]
        || stack.getItem == netherQuartz
        || stack.getItem == redstoneDust
        || chargedCertusQuartz.sameAsStack(stack)
      )
  override def canExtractItem(slot: Int, stack: ItemStack, side: Int) = !isItemValidForSlot(slot, stack)
}
