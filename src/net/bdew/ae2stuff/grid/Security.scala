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
