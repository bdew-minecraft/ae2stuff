/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
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
      val now = player.worldObj.getTotalWorldTime
      if (last.loc != loc || last.last < now - 100) {
        map += player -> Entry(loc, player.worldObj.getTotalWorldTime)
        true
      } else false
    } else {
      map += player -> Entry(loc, player.worldObj.getTotalWorldTime)
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
