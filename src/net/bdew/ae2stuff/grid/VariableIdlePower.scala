/*
 * Copyright (c) bdew, 2014
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.grid

import appeng.api.networking.events.MENetworkPowerIdleChange

trait VariableIdlePower extends GridTile {
  private var currentPowerUsage = 0D

  override def getIdlePowerUsage = currentPowerUsage

  def setIdlePowerUse(v: Double) {
    if (v != currentPowerUsage) {
      currentPowerUsage = v
      if (node != null && node.getGrid != null)
        node.getGrid.postEvent(new MENetworkPowerIdleChange(node))
    }
  }
}
