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

import appeng.api.config.Upgrades
import net.bdew.ae2stuff.AE2Stuff
import net.bdew.lib.gui.GuiProvider
import net.bdew.lib.machine.{Machine, PoweredMachine}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object MachineGrower extends Machine("Grower", BlockGrower) with GuiProvider with PoweredMachine {
  override def guiId = 2
  override type TEClass = TileGrower

  lazy val idlePowerDraw = tuning.getDouble("IdlePower")
  lazy val cyclePower = tuning.getDouble("CyclePower")
  lazy val cycleTicks = tuning.getInt("CycleTicks")
  lazy val powerCapacity = tuning.getDouble("PowerCapacity")

  AE2Stuff.onPostInit.listen { ev =>
    // Can't do this too early, causes error
    Upgrades.SPEED.registerItem(new ItemStack(BlockGrower), 3)
  }

  @SideOnly(Side.CLIENT)
  override def getGui(te: TEClass, player: EntityPlayer) = new GuiGrower(new ContainerGrower(te, player))
  override def getContainer(te: TEClass, player: EntityPlayer) = new ContainerGrower(te, player)
}
