package net.bdew.ae2stuff.nei

import codechicken.nei.api.IOverlayHandler
import codechicken.nei.recipe.IRecipeHandler
import net.bdew.ae2stuff.network.{MsgSetRecipe, NetHandler}
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}

object EncoderOverlayHandler extends IOverlayHandler {
  override def overlayRecipe(firstGui: GuiContainer, recipe: IRecipeHandler, recipeIndex: Int, shift: Boolean) {
    val items = recipe.getIngredientStacks(recipeIndex)
    val stacksList = new NBTTagList
    import scala.collection.JavaConversions._
    for (pStack <- items) {
      if (pStack != null) {
        val x = (pStack.relx - 25) / 18
        val y = (pStack.rely - 6) / 18
        val stackTag = pStack.items(0).writeToNBT(new NBTTagCompound)
        stackTag.setInteger("slot", y * 3 + x)
        stacksList.appendTag(stackTag)
      }
    }
    val tag = new NBTTagCompound
    tag.setTag("recipe", stacksList)
    NetHandler.sendToServer(new MsgSetRecipe(tag))
  }
}
