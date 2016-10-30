/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.encoder

import java.util

import appeng.api.networking.events.{MENetworkEventSubscribe, MENetworkPowerStatusChange}
import appeng.api.networking.storage.IStorageGrid
import appeng.util.item.AEItemStack
import net.bdew.ae2stuff.AE2Defs
import net.bdew.ae2stuff.grid.GridTile
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.block.TileKeepData
import net.bdew.lib.nbt.NBT
import net.bdew.lib.rotate.RotatableTile
import net.bdew.lib.tile.TileExtended
import net.bdew.lib.tile.inventory.{PersistentInventoryTile, SidedInventory}
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.oredict.OreDictionary

class TileEncoder extends TileExtended with GridTile with PersistentInventoryTile with SidedInventory with RotatableTile with TileKeepData {
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

    newStack.setTagCompound(
      NBT(
        "in" -> getRecipe.map(x =>
          if (x == null)
            new NBTTagCompound
          else
            NBT.from(x.writeToNBT _)
        ).toList,
        "out" -> List(getResult),
        "crafting" -> true
      )
    )

    newStack
  }

  def findMatchingRecipeStack(stacks: Iterable[ItemStack]): ItemStack = {
    import scala.collection.JavaConversions._

    // This is a hack to fix various borked NEI handlers, e.g. IC2
    var allStacks = stacks
    for (x <- stacks if x.getItemDamage == OreDictionary.WILDCARD_VALUE && x.getMaxDamage < x.getItemDamage) {
      val toAdd = new util.ArrayList[ItemStack]()
      x.getItem.getSubItems(x.getItem, null, toAdd)
      allStacks = toAdd.toList ++ allStacks
    }

    val storage = node.getGrid.getCache[IStorageGrid](classOf[IStorageGrid]).getItemInventory.getStorageList

    for {
      stack <- allStacks
      found <- Option(storage.findPrecise(AEItemStack.create(stack)))
    } {
      val copy = found.getItemStack.copy()
      copy.stackSize = 1
      return copy
    }

    // Get the virst variant if we can't find any matches
    allStacks.head
  }

  override def afterTileBreakSave(t: NBTTagCompound): NBTTagCompound = {
    t.removeTag("ae_node")
    t
  }

  // Inventory stuff

  allowSided = true
  override def canExtractItem(slot: Int, stack: ItemStack, side: EnumFacing) = false
  override def getSlotsForFace(side: EnumFacing): Array[Int] = Array(slots.patterns)
  override def isItemValidForSlot(slot: Int, stack: ItemStack) =
    slot == slots.patterns && blankPattern.isSameAs(stack)

  override def getMachineRepresentation = new ItemStack(BlockEncoder)
  override def getIdlePowerUsage = MachineEncoder.idlePowerDraw

  @MENetworkEventSubscribe
  def networkPowerStatusChange(ev: MENetworkPowerStatusChange): Unit = {
    val currentState = worldObj.getBlockState(pos)
    if (currentState.getValue(BlockEncoder.ACTIVE) != node.isActive)
      worldObj.setBlockState(pos, currentState.withProperty(BlockEncoder.ACTIVE, Boolean.box(node.isActive)), 3)
  }

  override def shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean = newSate.getBlock != BlockEncoder
}
