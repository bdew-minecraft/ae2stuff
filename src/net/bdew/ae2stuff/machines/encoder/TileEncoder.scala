/*
 * Copyright (c) bdew, 2014
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.encoder

import net.bdew.ae2stuff.AE2Defs
import net.bdew.ae2stuff.grid.GridTile
import net.bdew.lib.Misc
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.tile.TileExtended
import net.bdew.lib.tile.inventory.{BreakableInventoryTile, PersistentInventoryTile, SidedInventory}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}

class TileEncoder extends TileExtended with GridTile with PersistentInventoryTile with BreakableInventoryTile with SidedInventory {
  override def getSizeInventory = 12

  object slots {
    val recipe = (0 until 9).toArray
    val result = 9
    val patterns = 10
    val encoded = 11
  }

  lazy val blankPattern = AE2Defs.materials.blankPattern()
  lazy val encodedPattern = AE2Defs.items.encodedPattern().maybeItem().get()

  def getRecipe = slots.recipe map getStackInSlot

  def getResult = getStackInSlot(slots.result)

  override def markDirty() {
    if (!worldObj.isRemote)
      inv(slots.encoded) = encodePattern() // direct to prevent an infinite recursion
    super.markDirty()
  }

  def encodePattern(): ItemStack = {
    if (getResult == null || !getRecipe.exists(_ != null) || getStackInSlot(slots.patterns) == null)
      return null

    val newStack = new ItemStack(encodedPattern)

    val tag = new NBTTagCompound
    val inList = new NBTTagList
    val outList = new NBTTagList

    for (s <- getRecipe)
      if (s != null)
        inList.appendTag(Misc.applyMutator(s.writeToNBT, new NBTTagCompound))
      else
        inList.appendTag(new NBTTagCompound)

    outList.appendTag(Misc.applyMutator(getResult.writeToNBT, new NBTTagCompound))

    tag.setTag("in", inList)
    tag.setTag("out", outList)
    tag.setBoolean("crafting", true)

    newStack.setTagCompound(tag)
    newStack
  }

  override def dropItems() {
    if (getWorldObj != null && !getWorldObj.isRemote && getStackInSlot(slots.patterns) != null) {
      ItemUtils.throwItemAt(getWorldObj, xCoord, yCoord, zCoord, getStackInSlot(slots.patterns))
    }
    inv = new Array[ItemStack](inv.length)
  }

  // Inventory stuff

  allowSided = true
  override def canExtractItem(slot: Int, stack: ItemStack, side: Int) = false
  override def getAccessibleSlotsFromSide(side: Int) = Array(slots.patterns)
  override def isItemValidForSlot(slot: Int, stack: ItemStack) =
    slot == slots.patterns && blankPattern.isSameAs(stack)

  override def getMachineRepresentation = new ItemStack(BlockEncoder)
  override def getIdlePowerUsage = MachineEncoder.idlePowerDraw
}
