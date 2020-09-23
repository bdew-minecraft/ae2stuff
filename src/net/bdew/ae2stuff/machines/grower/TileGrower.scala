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
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TileGrower extends TileDataSlots with GridTile with SidedInventory with PersistentInventoryTile with PoweredTile with TileKeepData {
  override def getSizeInventory = 3 * 9
  override def getMachineRepresentation = new ItemStack(BlockGrower)
  override def powerCapacity = MachineGrower.powerCapacity

  val upgrades = new UpgradeInventory("upgrades", this, 3, Set(Upgrades.SPEED))

  val redstoneDust = Items.REDSTONE
  val netherQuartz = Items.QUARTZ
  val crystal = AE2Defs.items.crystalSeed.maybeItem().get().asInstanceOf[IGrowableCrystal]
  val chargedCertusQuartz = AE2Defs.materials.certusQuartzCrystalCharged()
  val fluixCrystal = AE2Defs.materials.fluixCrystal

  serverTick.listen(() => {
    if (world.getTotalWorldTime % MachineGrower.cycleTicks == 0 && isAwake) {
      var hadWork = false
      val needPower = MachineGrower.cyclePower * (1 + upgrades.cards(Upgrades.SPEED))
      if (powerStored >= needPower) {
        val invZipped = inv.zipWithIndex.filterNot(_._1.isEmpty)
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
          (_, empty) <- inv.zipWithIndex.find(x => x._1.isEmpty || (fluixCrystal.isSameAs(x._1) && x._1.getCount <= x._1.getMaxStackSize - 2))
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
    !stack.isEmpty && (
      stack.getItem.isInstanceOf[IGrowableCrystal]
        || stack.getItem == netherQuartz
        || stack.getItem == redstoneDust
        || chargedCertusQuartz.isSameAs(stack)
      )

  override def canExtractItem(slot: Int, stack: ItemStack, side: EnumFacing) = !isItemValidForSlot(slot, stack)

  override def shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean = newSate.getBlock != BlockGrower

  onWake.listen(() => BlockGrower.setActive(world, pos, true))
  onSleep.listen(() => BlockGrower.setActive(world, pos, false))
}
