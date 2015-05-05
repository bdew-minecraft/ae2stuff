/*
 * Copyright (c) bdew, 2014
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.grower

import appeng.api.implementations.items.IGrowableCrystal
import appeng.api.networking.GridNotification
import cpw.mods.fml.common.registry.GameRegistry
import net.bdew.ae2stuff.AE2Defs
import net.bdew.ae2stuff.grid.{GridTile, PoweredTile}
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.tile.TileExtended
import net.bdew.lib.tile.inventory.{BreakableInventoryTile, PersistentInventoryTile, SidedInventory}
import net.minecraft.item.ItemStack

class TileGrower extends TileExtended with GridTile with SidedInventory with PersistentInventoryTile with BreakableInventoryTile with PoweredTile {
  override def getSizeInventory = 3 * 9
  override def getMachineRepresentation = new ItemStack(BlockGrower)
  override def powerCapacity = MachineGrower.powerCapacity

  val redstoneDust = GameRegistry.findItem("minecraft", "redstone")
  val netherQuartz = GameRegistry.findItem("minecraft", "quartz")
  val crystal = AE2Defs.items.crystalSeed.maybeItem().get().asInstanceOf[IGrowableCrystal]
  val chargedCertusQuartz = AE2Defs.materials.certusQuartzCrystalCharged()
  val fluixCrystal = AE2Defs.materials.fluixCrystal

  serverTick.listen(() => {
    if (getWorldObj.getTotalWorldTime % MachineGrower.cycleTicks == 0 && isAwake) {
      var hadWork = false
      if (getNode.isActive && powerStored >= MachineGrower.cyclePower) {
        val invZipped = inv.zipWithIndex.filter(_._1 != null)
        for ((stack, slot) <- invZipped if stack.getItem.isInstanceOf[IGrowableCrystal]) {
          val ns = stack.getItem.asInstanceOf[IGrowableCrystal].triggerGrowth(stack)
          setInventorySlotContents(slot, ns)
          hadWork = true
        }
        for {
          (cert, certPos) <- invZipped.find(x => chargedCertusQuartz.isSameAs(x._1))
          redstonePos <- ItemUtils.findItemInInventory(this, redstoneDust)
          netherPos <- ItemUtils.findItemInInventory(this, netherQuartz)
          (_, empty) <- inv.zipWithIndex.find(x => x._1 == null || (fluixCrystal.isSameAs(x._1) && x._1.stackSize <= x._1.getMaxStackSize - 2))
        } {
          decrStackSize(certPos, 1)
          decrStackSize(netherPos, 1)
          decrStackSize(redstonePos, 1)
          ItemUtils.addStackToSlots(fluixCrystal.maybeStack(2).get(), this, 0 until getSizeInventory, false)
          hadWork = true
        }
      }
      if (hadWork) {
        powerStored -= MachineGrower.cyclePower
      } else {
        sleep()
      }
    }
  })

  override def onGridNotification(p1: GridNotification): Unit = {
    wakeup()
  }

  override def markDirty(): Unit = {
    wakeup()
    super.markDirty()
  }

  allowSided = true

  override def getIdlePowerUsage = MachineGrower.idlePowerDraw

  override def isItemValidForSlot(slot: Int, stack: ItemStack) =
    stack != null && (
      stack.getItem.isInstanceOf[IGrowableCrystal]
        || stack.getItem == netherQuartz
        || stack.getItem == redstoneDust
        || chargedCertusQuartz.isSameAs(stack)
      )

  override def canExtractItem(slot: Int, stack: ItemStack, side: Int) = !isItemValidForSlot(slot, stack)
}
