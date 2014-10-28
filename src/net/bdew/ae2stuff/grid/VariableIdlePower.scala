package net.bdew.ae2stuff.grid

import appeng.api.networking.events.MENetworkPowerIdleChange

trait VariableIdlePower extends GridTile {
  private var currentPowerUsage = 0D

  override def getIdlePowerUsage = currentPowerUsage

  def setIdlePowerUse(v: Double) {
    currentPowerUsage = v
    if (node != null && node.getGrid != null)
      node.getGrid.postEvent(new MENetworkPowerIdleChange(node))
  }
}
