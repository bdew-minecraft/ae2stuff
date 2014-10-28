package net.bdew.ae2stuff.nei

import codechicken.nei.api.IOverlayHandler
import codechicken.nei.recipe.IRecipeHandler
import net.bdew.ae2stuff.network.{MsgSetRecipe, NetHandler}
import net.bdew.lib.network.ItemStackSerialize
import net.minecraft.client.gui.inventory.GuiContainer

object EncoderOverlayHandler extends IOverlayHandler {
  override def overlayRecipe(firstGui: GuiContainer, recipe: IRecipeHandler, recipeIndex: Int, shift: Boolean) {
    val items = recipe.getIngredientStacks(recipeIndex)
    import scala.collection.JavaConversions._
    val stacks = for {
      pStack <- items
      if pStack != null && pStack.items.nonEmpty && pStack.items(0) != null && pStack.items(0).getItem != null
    } yield {
      val x = (pStack.relx - 25) / 18
      val y = (pStack.rely - 6) / 18
      (y * 3 + x) -> new ItemStackSerialize(pStack.items(0))
    }
    NetHandler.sendToServer(new MsgSetRecipe(stacks.toMap))
  }
}
