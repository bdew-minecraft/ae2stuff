package net.bdew.ae2stuff.jei

import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import mezz.jei.api.recipe.transfer.{IRecipeTransferError, IRecipeTransferHandler}
import net.bdew.ae2stuff.machines.encoder.ContainerEncoder
import net.bdew.ae2stuff.network.{MsgSetRecipe, NetHandler}
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.nbt.NBT
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound

import scala.collection.JavaConversions._

object EncoderTransferHandler extends IRecipeTransferHandler[ContainerEncoder] {
  override def getContainerClass = classOf[ContainerEncoder]
  override def getRecipeCategoryUid = VanillaRecipeCategoryUid.CRAFTING

  override def transferRecipe(container: ContainerEncoder, recipeLayout: IRecipeLayout, player: EntityPlayer, maxTransfer: Boolean, doTransfer: Boolean): IRecipeTransferError = {
    if (!doTransfer) return null

    val data = new NBTTagCompound

    for ((ingredients, slot) <- recipeLayout.getItemStacks.getGuiIngredients.values().filter(_.isInput).zipWithIndex) {
      val items = for (stack <- ingredients.getAllIngredients) yield {
        val copy = stack.copy()
        copy.stackSize = 1
        NBT.from(copy.writeToNBT)
      }
      data.setList(slot.toString, items)
    }

    NetHandler.sendToServer(MsgSetRecipe(data))

    return null
  }
}
