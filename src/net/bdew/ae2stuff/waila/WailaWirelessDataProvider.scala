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
import net.bdew.ae2stuff.machines.wireless.TileWireless
import net.bdew.lib.PimpVanilla._
import net.bdew.lib.nbt.NBT
import net.bdew.lib.{DecFormat, Misc}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object WailaWirelessDataProvider extends BaseDataProvider(classOf[TileWireless]) {
  override def getNBTTag(player: EntityPlayerMP, te: TileWireless, tag: NBTTagCompound, world: World, pos: BlockPos): NBTTagCompound = {
    tag.setTag("wireless_waila",
      te.link map (link => NBT(
        "connected" -> true,
        "target" -> link,
        "channels" -> (if (te.connection != null) te.connection.getUsedChannels else 0),
        "power" -> te.getIdlePowerUsage
      )) getOrElse NBT("connected" -> false)
    )
    tag
  }

  override def getBodyStrings(target: TileWireless, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = {
    if (acc.getNBTData.hasKey("wireless_waila")) {
      val data = acc.getNBTData.getCompoundTag("wireless_waila")
      if (data.getBoolean("connected")) {
        data.get[BlockPos]("target") map { pos =>
          List(
            Misc.toLocalF("ae2stuff.waila.wireless.connected", pos.getX, pos.getY, pos.getZ),
            Misc.toLocalF("ae2stuff.waila.wireless.channels", data.getInteger("channels")),
            Misc.toLocalF("ae2stuff.waila.wireless.power", DecFormat.short(data.getDouble("power")))
          )
        } getOrElse List(Misc.toLocal("ae2stuff.waila.wireless.notconnected"))
      } else {
        List(Misc.toLocal("ae2stuff.waila.wireless.notconnected"))
      }
    } else List.empty
  }
}
