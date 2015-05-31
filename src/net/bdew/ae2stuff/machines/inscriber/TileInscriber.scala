/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.inscriber

import appeng.api.AEApi
import appeng.api.config.Upgrades
import appeng.api.features.{IInscriberRecipe, InscriberProcessType}
import appeng.api.networking.GridNotification
import net.bdew.ae2stuff.grid.{GridTile, PoweredTile}
import net.bdew.ae2stuff.misc.UpgradeInventory
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.bdew.lib.data.{DataSlotFloat, DataSlotItemStack}
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.tile.inventory.{BreakableInventoryTile, PersistentInventoryTile, SidedInventory}
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class TileInscriber extends TileDataSlots with GridTile with SidedInventory with PersistentInventoryTile with BreakableInventoryTile with PoweredTile {
  override def getSizeInventory = 4
  override def getMachineRepresentation = new ItemStack(BlockInscriber)
  override def powerCapacity = MachineInscriber.powerCapacity

  object slots {
    val top = 0
    val middle = 1
    val bottom = 2
    val output = 3
  }

  val upgrades = new UpgradeInventory("upgrades", this, 5, Set(Upgrades.SPEED))
  val progress = DataSlotFloat("progress", this).setUpdate(UpdateKind.SAVE, UpdateKind.GUI)
  val output = DataSlotItemStack("output", this).setUpdate(UpdateKind.SAVE)

  def isWorking = output :!= null

  serverTick.listen(() => {
    if (isAwake) {
      if (!isWorking) {
        // No progress going - try starting
        findFinalRecipe foreach { recipe =>
          output := recipe.getOutput
          progress := 0
          decrStackSize(slots.middle, 1)
          if (recipe.getProcessType == InscriberProcessType.Press) {
            decrStackSize(slots.top, 1)
            decrStackSize(slots.bottom, 1)
          }
        }
        if (!isWorking) // Failed - go sleep
          sleep()
      }
      if (isWorking) {
        // Have something to do
        if (progress < 1) {
          // Not finished - try progressing
          val progressPerTick = (1F / MachineInscriber.cycleTicks) * (1 + upgrades.cards(Upgrades.SPEED))
          val powerNeeded = MachineInscriber.cyclePower * progressPerTick
          if (powerStored >= powerNeeded) {
            // Have enough power - consume it and add to progress
            progress += progressPerTick
            powerStored -= powerNeeded
          } else {
            // Not enough power - sleep
            sleep()
          }
        }
        if (progress >= 1) {
          // Finished - try to output
          val oStack = getStackInSlot(slots.output)
          if (oStack == null || (ItemUtils.isSameItem(oStack, output) && oStack.stackSize + output.stackSize <= oStack.getMaxStackSize)) {
            // Can output - finish process
            if (oStack == null) {
              setInventorySlotContents(slots.output, output)
            } else {
              oStack.stackSize += output.stackSize
              markDirty()
            }
            output := null
            progress := 0
          } else {
            // Can't output - switch to sleep mode
            sleep()
          }
        }
        requestPowerIfNeeded()
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

  import scala.collection.JavaConversions._

  def findFinalRecipe: Option[IInscriberRecipe] =
    AEApi.instance().registries().inscriber().getRecipes find isMatchingFullRecipe

  def isMatchingFullRecipe(rec: IInscriberRecipe) = getStackInSlot(slots.middle) != null &&
    ItemUtils.isSameItem(rec.getTopOptional.orNull(), getStackInSlot(slots.top)) &&
    ItemUtils.isSameItem(rec.getBottomOptional.orNull(), getStackInSlot(slots.bottom)) &&
    rec.getInputs.exists(rs => ItemUtils.isSameItem(rs, getStackInSlot(slots.middle)))

  def isMatchingPartialRecipe(rec: IInscriberRecipe, top: Option[ItemStack], middle: Option[ItemStack], bottom: Option[ItemStack]): Boolean = {
    if (top.isDefined) {
      if (!rec.getTopOptional.isPresent) return false
      if (!ItemUtils.isSameItem(rec.getTopOptional.get(), top.get)) return false
    }
    if (middle.isDefined) {
      if (!rec.getInputs.exists(rs => ItemUtils.isSameItem(rs, middle.get))) return false
    }
    if (bottom.isDefined) {
      if (!rec.getBottomOptional.isPresent) return false
      if (!ItemUtils.isSameItem(rec.getBottomOptional.get(), bottom.get)) return false
    }
    true
  }

  def isValidPartialRecipe(top: Option[ItemStack], middle: Option[ItemStack], bottom: Option[ItemStack]): Boolean = {
    AEApi.instance().registries().inscriber().getRecipes.exists(rec => isMatchingPartialRecipe(rec, top, middle, bottom))
  }

  override def isItemValidForSlot(slot: Int, stack: ItemStack) = slot match {
    case slots.top => isValidPartialRecipe(Some(stack), Option(inv(slots.middle)), Option(inv(slots.bottom)))
    case slots.middle => isValidPartialRecipe(Option(inv(slots.top)), Some(stack), Option(inv(slots.bottom)))
    case slots.bottom => isValidPartialRecipe(Option(inv(slots.top)), Option(inv(slots.middle)), Some(stack))
    case _ => false
  }

  override def canExtractItem(slot: Int, stack: ItemStack, side: Int) = slot == slots.output
  override def dropItems(): Unit = {
    super.dropItems()
    upgrades.dropInventory()
  }

  override def shouldRefresh(oldBlock: Block, newBlock: Block, oldMeta: Int, newMeta: Int, world: World, x: Int, y: Int, z: Int) = oldBlock != newBlock
  onWake.listen(() => worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 3))
  onSleep.listen(() => worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3))
}
