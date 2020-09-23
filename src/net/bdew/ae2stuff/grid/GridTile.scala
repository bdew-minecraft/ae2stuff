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

import java.util

import appeng.api.AEApi
import appeng.api.networking._
import appeng.api.networking.events.MENetworkEvent
import appeng.api.util.{AECableType, AEColor, AEPartLocation, DimensionalCoord}
import appeng.me.GridAccessException
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.tile.inventory.BreakableInventoryTile
import net.bdew.lib.tile.{TileExtended, TileTicking}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.common.FMLCommonHandler

trait GridTile extends TileExtended with TileTicking with IGridHost with IGridBlock {
  var node: IGridNode = null
  var initialized = false

  var placingPlayer: EntityPlayer = null

  serverTick.listen(() => {
    if (!initialized) {
      if (placingPlayer != null)
        getNode.setPlayerID(Security.getPlayerId(placingPlayer))
      getNode.updateState()
      initialized = true
    }
  })

  persistSave.listen((tag) => {
    if (node != null)
      node.saveToNBT("ae_node", tag)
  })

  persistLoad.listen((tag) => {
    if (FMLCommonHandler.instance().getEffectiveSide.isServer) {
      unRegisterNode()
      node = AEApi.instance().grid().createGridNode(this)
      if (tag.hasKey("ae_node"))
        node.loadFromNBT("ae_node", tag)
    }
    initialized = false
  })

  def unRegisterNode(): Unit = {
    if (node != null) {
      node.destroy()
      node = null
      initialized = false
    }
  }

  override def invalidate(): Unit = {
    unRegisterNode()
    super.invalidate()
  }

  override def onChunkUnload(): Unit = {
    unRegisterNode()
    super.onChunkUnload()
  }

  def getNode = {
    if (getWorld == null || getWorld.isRemote) null
    else {
      if (node == null)
        node = AEApi.instance().grid().createGridNode(this)
      node
    }
  }

  def safePostEvent(ev: MENetworkEvent) = {
    try {
      getNode.getGrid.postEvent(ev)
      true
    } catch {
      case _: GridAccessException => false
    }
  }

  // IGridHost
  override def getGridNode(aePartLocation: AEPartLocation): IGridNode = getNode
  override def getCableConnectionType(aePartLocation: AEPartLocation): AECableType = AECableType.COVERED
  override def securityBreak() = {
    ItemUtils.throwItemAt(getWorld, getPos, new ItemStack(getBlockType))
    if (this.isInstanceOf[BreakableInventoryTile])
      this.asInstanceOf[BreakableInventoryTile].dropItems()
    getWorld.setBlockToAir(getPos)
  }

  // IGridBlock
  override def getIdlePowerUsage = 0
  override def getFlags = util.EnumSet.noneOf(classOf[GridFlags])
  override def getGridColor = AEColor.TRANSPARENT
  override def getConnectableSides = util.EnumSet.allOf(classOf[EnumFacing])
  override def getMachine = this
  override def isWorldAccessible = true
  override def getLocation = new DimensionalCoord(this)

  // Needs to be implemented by subclass
  override def getMachineRepresentation: ItemStack

  // Default notifications do nothing
  override def onGridNotification(p1: GridNotification) {}
  override def setNetworkStatus(p1: IGrid, p2: Int) {}
  override def gridChanged() {}
}
