/*
 * Copyright (c) bdew, 2014 - 2017
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.grid

import appeng.api.AEApi
import appeng.api.config.SecurityPermissions
import appeng.api.networking.security.ISecurityGrid
import appeng.api.networking.{IGrid, IGridNode}
import com.mojang.authlib.GameProfile
import net.minecraft.entity.player.EntityPlayer

object Security {
  def getPlayerId(p: GameProfile): Int = AEApi.instance().registries().players().getID(p)
  def getPlayerId(e: EntityPlayer): Int = AEApi.instance().registries().players().getID(e)
  def getPlayerFromId(id: Int): Option[EntityPlayer] = Option(AEApi.instance().registries().players().findPlayer(id))

  def playerHasPermission(grid: IGrid, playerID: Int, permission: SecurityPermissions): Boolean = {
    if (grid == null) return true
    val gs = grid.getCache[ISecurityGrid](classOf[ISecurityGrid])
    if (gs == null || !gs.isAvailable) return true
    gs.hasPermission(playerID, permission)
  }

  def isNodeOnSecureNetwork(n: IGridNode): Boolean = {
    if (n.getGrid == null) return false
    val gs = n.getGrid.getCache[ISecurityGrid](classOf[ISecurityGrid])
    if (gs == null) return false
    gs.isAvailable
  }
}
