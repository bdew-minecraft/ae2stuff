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
import net.bdew.ae2stuff.network.{MsgSetRecipe2, NetHandler}
import net.bdew.lib.network.ItemStackSerialize
import net.minecraft.client.gui.inventory.GuiContainer

object EncoderOverlayHandler extends IOverlayHandler {
  override def overlayRecipe(firstGui: GuiContainer, recipe: IRecipeHandler, recipeIndex: Int, shift: Boolean) {
    val items = recipe.getIngredientStacks(recipeIndex)
    import scala.collection.JavaConversions._
    val stacks = for (pStack <- items if pStack != null) yield {
      val x = (pStack.relx - 25) / 18
      val y = (pStack.rely - 6) / 18
      val stacks = pStack.items.toList.filter(x => x != null && x.getItem != null).map(new ItemStackSerialize(_))
      if (stacks.nonEmpty)
        Some((y * 3 + x) -> stacks)
      else
        None
    }
    NetHandler.sendToServer(new MsgSetRecipe2(stacks.flatten.toMap))
  }
}
