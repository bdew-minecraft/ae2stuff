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

package net.bdew.ae2stuff.machines.encoder

import net.bdew.ae2stuff.network.{MsgSetRecipe, NetHandler}
import net.bdew.lib.Misc
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.Machine
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object MachineEncoder extends Machine("Encoder", BlockEncoder) with GuiProvider {
  override def guiId = 1
  override type TEClass = TileEncoder

  lazy val idlePowerDraw = tuning.getDouble("IdlePower")

  @SideOnly(Side.CLIENT)
  override def getGui(te: TEClass, player: EntityPlayer) = new GuiEncoder(new ContainerEncoder(te, player))
  override def getContainer(te: TEClass, player: EntityPlayer) = new ContainerEncoder(te, player)

  NetHandler.regServerHandler {
    case (MsgSetRecipe(data), player) =>
      Misc.asInstanceOpt(player.openContainer, classOf[ContainerEncoder]).foreach { cont =>
        for ((slotNum, recIdx) <- cont.te.slots.recipe.zipWithIndex) {
          val items = data.tag.getList[ItemStack](slotNum.toString)
          if (items.nonEmpty)
            cont.te.setInventorySlotContents(recIdx, cont.te.findMatchingRecipeStack(items))
          else
            cont.te.setInventorySlotContents(recIdx, ItemStack.EMPTY)
        }
        cont.updateRecipe()
      }
  }
}
