/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.misc

import net.bdew.lib.block.BlockRef
import net.bdew.lib.nbt.NBT
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound

trait ItemLocationStore extends Item {
  def hasLocation(stack: ItemStack) =
    stack.getItem == this && stack.hasTagCompound && stack.getTagCompound.hasKey("loc")

  def getLocation(stack: ItemStack) =
    BlockRef.fromNBT(stack.getTagCompound.getCompoundTag("loc"))

  def getDimension(stack: ItemStack) =
    stack.getTagCompound.getInteger("dim")

  def setLocation(stack: ItemStack, loc: BlockRef, dimension: Int) = {
    if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound)
    val tag = stack.getTagCompound
    tag.setTag("loc", NBT.from(loc.writeToNBT _))
    tag.setInteger("dim", dimension)
  }

  def clearLocation(stack: ItemStack) = {
    if (stack.hasTagCompound) {
      stack.getTagCompound.removeTag("loc")
      stack.getTagCompound.removeTag("dim")
    }
  }
}
