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

  def getNode = {
    if (node == null)
      node = AEApi.instance().createGridNode(this)
    node
  }

  // IGridHost

  override def getGridNode(p1: ForgeDirection) = getNode
  override def getCableConnectionType(p1: ForgeDirection) = AECableType.COVERED
  override def securityBreak() = getWorldObj.setBlockToAir(xCoord, yCoord, zCoord)

  // IGridBlock
  override def getIdlePowerUsage = 1
  override def getFlags = util.EnumSet.noneOf(classOf[GridFlags])
  override def getGridColor = AEColor.Transparent
  override def getConnectableSides = util.EnumSet.allOf(classOf[ForgeDirection])
  override def getMachine = this
  override def isWorldAccessable = true
  override def getLocation = new DimensionalCoord(this)

  override def onGridNotification(p1: GridNotification)
  override def getMachineRepresentation: ItemStack
  override def setNetworkStatus(p1: IGrid, p2: Int)
  override def gridChanged()
}
