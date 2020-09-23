/*
 * Copyright (c) bdew, 2014 - 2020
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.bdew.ae2stuff.jei

import mezz.jei.api.gui.IRecipeLayout
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

  override def transferRecipe(container: ContainerEncoder, recipeLayout: IRecipeLayout, player: EntityPlayer, maxTransfer: Boolean, doTransfer: Boolean): IRecipeTransferError = {
    if (!doTransfer) return null

    val data = new NBTTagCompound

    for ((ingredients, slot) <- recipeLayout.getItemStacks.getGuiIngredients.values().filter(_.isInput).zipWithIndex) {
      val items = for (stack <- ingredients.getAllIngredients) yield {
        val copy = stack.copy()
        copy.setCount(1)
        NBT.from(copy.writeToNBT)
      }
      data.setList(slot.toString, items)
    }

    NetHandler.sendToServer(MsgSetRecipe(data))

    return null
  }
}
