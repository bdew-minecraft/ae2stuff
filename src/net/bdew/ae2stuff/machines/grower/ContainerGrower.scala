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

package net.bdew.ae2stuff.machines.grower

import net.bdew.ae2stuff.misc.ContainerUpgradeable
import net.bdew.lib.data.base.ContainerDataSlots
import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.minecraft.entity.player.EntityPlayer

class ContainerGrower(te: TileGrower, player: EntityPlayer) extends BaseContainer(te) with ContainerDataSlots with ContainerUpgradeable {
  override lazy val dataSource = te

  for (y <- 0 until 3; x <- 0 until 9)
    this.addSlotToContainer(new SlotValidating(te, x + y * 9, 8 + x * 18, 17 + y * 18))

  for (i <- 0 until te.upgrades.getSizeInventory())
    addSlotToContainer(new SlotValidating(te.upgrades, i, 187, 8 + i * 18))

  bindPlayerInventory(player.inventory, 8, 84, 142)

  initUpgradeable(te, te.upgrades, player, 186, 94, addSlotToContainer)
}
