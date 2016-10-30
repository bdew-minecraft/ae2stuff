/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.grower

import appeng.api.config.Upgrades
import appeng.api.implementations.items.IGrowableCrystal
import appeng.api.networking.GridNotification
import net.bdew.ae2stuff.AE2Defs
import net.bdew.ae2stuff.grid.{GridTile, PoweredTile}
import net.bdew.ae2stuff.misc.UpgradeInventory
import net.bdew.lib.block.TileKeepData
import net.bdew.lib.data.base.TileDataSlots
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.tile.inventory.{PersistentInventoryTile, SidedInventory}
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry

class TileGrower extends TileDataSlots with GridTile with SidedInventory with PersistentInventoryTile with PoweredTile with TileKeepData {
  override def getSizeInventory = 3 * 9
  override def getMachineRepresentation = new ItemStack(BlockGrower)
  override def powerCapacity = MachineGrower.powerCapacity

  val upgrades = new UpgradeInventory("upgrades", this, 3, Set(Upgrades.SPEED))

  val redstoneDust = GameRegistry.findItem("minecraft", "redstone")
  val netherQuartz = GameRegistry.findItem("minecraft", "quartz")
  val crystal = AE2Defs.items.crystalSeed.maybeItem().get().asInstanceOf[IGrowableCrystal]
  val chargedCertusQuartz = AE2Defs.materials.certusQuartzCrystalCharged()
  val fluixCrystal = AE2Defs.materials.fluixCrystal

  serverTick.listen(() => {
    if (worldObj.getTotalWorldTime % MachineGrower.cycleTicks == 0 && isAwake) {
      var hadWork = false
      val needPower = MachineGrower.cyclePower * (1 + upgrades.cards(Upgrades.SPEED))
      if (powerStored >= needPower) {
        val invZipped = inv.zipWithIndex.filter(_._1 != null)
        for ((stack, slot) <- invZipped if stack.getItem.isInstanceOf[IGrowableCrystal]) {
          var ns = stack
          for (i <- 0 to upgrades.cards(Upgrades.SPEED) if stack.getItem.isInstanceOf[IGrowableCrystal])
            ns = stack.getItem.asInstanceOf[IGrowableCrystal].triggerGrowth(stack)
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
        powerStored -= needPower
      } else {
        sleep()
      }
      requestPowerIfNeeded()
    }
  })

  override def afterTileBreakSave(t: NBTTagCompound): NBTTagCompound = {
    t.removeTag("ae_node")
    t
  }

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

  override def shouldRefresh(oldBlock: Block, newBlock: Block, oldMeta: Int, newMeta: Int, world: World, pos: BlockPos) = oldBlock != newBlock
  onWake.listen(() => worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 3))
  onSleep.listen(() => worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3))
}
