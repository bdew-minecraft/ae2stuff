/*
 * Copyright (c) bdew, 2014
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.grid

import java.util

import appeng.api.AEApi
import appeng.api.networking._
import appeng.api.util.{AECableType, AEColor, DimensionalCoord}
import net.bdew.lib.tile.TileExtended
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.ForgeDirection

trait GridTile extends TileExtended with IGridHost with IGridBlock {
  var node: IGridNode = null
  var initialized = false

  serverTick.listen(() => {
    if (!initialized)
      getNode.updateState()
    initialized = true
  })

  persistSave.listen((tag) => {
    if (node != null)
      node.saveToNBT("ae_node", tag)
  })

  persistLoad.listen((tag) => {
    if (node != null)
      node.destroy()
    node = AEApi.instance().createGridNode(this)
    node.loadFromNBT("ae_node", tag)
    initialized = false
  })

  override def invalidate() {
    if (node != null) {
      node.destroy()
      node = null
      initialized = false
    }
    super.invalidate()
  }

  def getNode = {
    if (getWorldObj == null || getWorldObj.isRemote) null
    else {
      if (node == null)
        node = AEApi.instance().createGridNode(this)
      node
    }
  }

  // IGridHost

  override def getGridNode(p1: ForgeDirection) = getNode
  override def getCableConnectionType(p1: ForgeDirection) = AECableType.COVERED
  override def securityBreak() = getWorldObj.setBlockToAir(xCoord, yCoord, zCoord)

  // IGridBlock
  override def getIdlePowerUsage = 0
  override def getFlags = util.EnumSet.noneOf(classOf[GridFlags])
  override def getGridColor = AEColor.Transparent
  override def getConnectableSides = util.EnumSet.allOf(classOf[ForgeDirection])
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
