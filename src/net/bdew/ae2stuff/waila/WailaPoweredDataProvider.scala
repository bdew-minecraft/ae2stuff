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

package net.bdew.ae2stuff.waila

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.bdew.ae2stuff.grid.PoweredTile
import net.bdew.lib.{DecFormat, Misc}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World

object WailaPoweredDataProvider extends BaseDataProvider(classOf[PoweredTile]) {
  override def getNBTTag(player: EntityPlayerMP, te: PoweredTile, tag: NBTTagCompound, world: World, pos: BlockPos): NBTTagCompound = {
    tag.setDouble("waila_power_stored", te.powerStored)
    tag.setDouble("waila_power_capacity", te.powerCapacity)
    tag.setBoolean("waila_power_sleep", te.isSleeping)
    tag
  }

  override def getBodyStrings(target: PoweredTile, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = {
    val nbt = acc.getNBTData
    if (nbt.hasKey("waila_power_stored")) {
      List(
        Misc.toLocalF("ae2stuff.waila.power", DecFormat.short(nbt.getDouble("waila_power_stored")), DecFormat.short(nbt.getDouble("waila_power_capacity"))),
        if (nbt.getBoolean("waila_power_sleep"))
          TextFormatting.RED + Misc.toLocal("ae2stuff.waila.sleep.true") + TextFormatting.RESET
        else
          TextFormatting.GREEN + Misc.toLocal("ae2stuff.waila.sleep.false") + TextFormatting.RESET
      )
    } else List.empty
  }
}
