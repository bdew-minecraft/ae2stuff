/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.items.visualiser

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.PlayerEvent.{PlayerChangedDimensionEvent, PlayerLoggedOutEvent, PlayerRespawnEvent}
import net.bdew.lib.block.BlockRef
import net.minecraft.entity.player.EntityPlayer

import scala.collection.mutable

object VisualiserPlayerTracker {

  case class Entry(pos: BlockRef, dim: Int, last: Long)

  var map = mutable.Map.empty[EntityPlayer, Entry]

  def init() {
    FMLCommonHandler.instance().bus().register(this)
  }

  def clear() = map.clear()

  def needToUpdate(player: EntityPlayer, pos: BlockRef, dim: Int): Boolean = {
    if (map.isDefinedAt(player)) {
      val last = map(player)
      val now = player.worldObj.getTotalWorldTime
      if (last.pos != pos || last.dim != dim || last.last < now - 100) {
        map += player -> Entry(pos, dim, player.worldObj.getTotalWorldTime)
        true
      } else false
    } else {
      map += player -> Entry(pos, dim, player.worldObj.getTotalWorldTime)
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
