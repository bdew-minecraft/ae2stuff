/*
 * Copyright (c) bdew, 2014 - 2017
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.misc

import net.bdew.lib.PimpVanilla._
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos

trait ItemLocationStore extends Item {
  def getLocation(stack: ItemStack) =
    if (stack.hasTagCompound)
      stack.getTagCompound.get[PosAndDimension]("loc")
    else
      None

  def setLocation(stack: ItemStack, loc: BlockPos, dimension: Int) = {
    if (!stack.hasTagCompound) stack.setTagCompound(new NBTTagCompound)
    stack.getTagCompound.set("loc", PosAndDimension(loc, dimension))
  }

  def clearLocation(stack: ItemStack) = {
    if (stack.hasTagCompound) {
      stack.getTagCompound.removeTag("loc")
    }
  }
}
