/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.nei

import java.util

import codechicken.nei.guihook.{IContainerInputHandler, IContainerTooltipHandler}
import codechicken.nei.recipe.GuiCraftingRecipe
import net.bdew.ae2stuff.machines.inscriber.GuiInscriber
import net.bdew.lib.gui.{Point, Rect}
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.item.ItemStack

object InscriberGuiHandler extends IContainerInputHandler with IContainerTooltipHandler {
  override def keyTyped(guiContainer: GuiContainer, c: Char, i: Int) = false
  override def lastKeyTyped(guiContainer: GuiContainer, c: Char, i: Int) = false
  override def onMouseDragged(guiContainer: GuiContainer, i: Int, i1: Int, i2: Int, l: Long) = Unit
  override def onMouseClicked(guiContainer: GuiContainer, i: Int, i1: Int, i2: Int) = Unit
  override def onMouseScrolled(guiContainer: GuiContainer, i: Int, i1: Int, i2: Int) = Unit
  override def mouseScrolled(guiContainer: GuiContainer, i: Int, i1: Int, i2: Int): Boolean = false
  override def onKeyTyped(guiContainer: GuiContainer, c: Char, i: Int) = Unit
  override def onMouseUp(guiContainer: GuiContainer, i: Int, i1: Int, i2: Int) = Unit
  override def handleItemDisplayName(guiContainer: GuiContainer, itemStack: ItemStack, list: util.List[String]) = list
  override def handleItemTooltip(guiContainer: GuiContainer, itemStack: ItemStack, i: Int, i1: Int, list: util.List[String]) = list

  val recipesRect = Rect(82, 39, 26, 16)

  override def handleTooltip(guiContainer: GuiContainer, x: Int, y: Int, list: util.List[String]): util.List[String] = {
    if (guiContainer.isInstanceOf[GuiInscriber] && recipesRect.contains(Point(x - guiContainer.guiLeft, y - guiContainer.guiTop)))
      list.add("Recipes")
    list
  }

  override def mouseClicked(guiContainer: GuiContainer, x: Int, y: Int, button: Int): Boolean = {
    if (guiContainer.isInstanceOf[GuiInscriber] && recipesRect.contains(Point(x - guiContainer.guiLeft, y - guiContainer.guiTop))) {
      GuiCraftingRecipe.openRecipeGui("inscriber")
      true
    }
    else false
  }

}
