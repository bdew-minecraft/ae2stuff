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

package net.bdew.ae2stuff.items.visualiser

import net.bdew.ae2stuff.misc.PosAndDimension
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent.{PlayerChangedDimensionEvent, PlayerLoggedOutEvent, PlayerRespawnEvent}

import scala.collection.mutable

object VisualiserPlayerTracker {

  case class Entry(loc: PosAndDimension, last: Long)

  var map = mutable.Map.empty[EntityPlayer, Entry]

  def init() {
    MinecraftForge.EVENT_BUS.register(this)
  }

  def clear() = map.clear()

  def needToUpdate(player: EntityPlayer, loc: PosAndDimension): Boolean = {
    if (map.isDefinedAt(player)) {
      val last = map(player)
      val now = player.world.getTotalWorldTime
      if (last.loc != loc || last.last < now - 100) {
        map += player -> Entry(loc, player.world.getTotalWorldTime)
        true
      } else false
    } else {
      map += player -> Entry(loc, player.world.getTotalWorldTime)
      true
    }
  }

  def reset(player: EntityPlayer) = map -= player

  @SubscribeEvent
  def handlePlayerLogout(ev: PlayerLoggedOutEvent) = reset(ev.player)

  @SubscribeEvent
  def handlePlayerChangedDimension(ev: PlayerChangedDimensionEvent) = reset(ev.player)

  @SubscribeEvent
  def handlePlayerRespawn(ev: PlayerRespawnEvent) = reset(ev.player)
}
