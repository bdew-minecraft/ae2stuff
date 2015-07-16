/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.nei

import codechicken.nei.api.IOverlayHandler
import codechicken.nei.recipe.IRecipeHandler
import net.bdew.ae2stuff.network.{MsgSetRecipe3, NetHandler}
import net.bdew.lib.nbt._
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.nbt.NBTTagCompound

import scala.collection.JavaConversions._

object EncoderOverlayHandler extends IOverlayHandler {
  override def overlayRecipe(firstGui: GuiContainer, recipe: IRecipeHandler, recipeIndex: Int, shift: Boolean) {
    val items = recipe.getIngredientStacks(recipeIndex)
    val stacks = (for (pStack <- items if pStack != null) yield {
      val x = (pStack.relx - 25) / 18
      val y = (pStack.rely - 6) / 18
      val stacks = pStack.items.toList.filter(x => x != null && x.getItem != null) map { stack =>
        val copy = stack.copy()
        copy.stackSize = 1
        NBT.from(copy.writeToNBT)
      }
      (y * 3 + x) -> stacks
    }).toMap
    val data = new NBTTagCompound
    for (i <- 0 until 9)
      data.setList(i.toString, stacks.getOrElse(i, List.empty))
    NetHandler.sendToServer(new MsgSetRecipe3(data))
  }
}
