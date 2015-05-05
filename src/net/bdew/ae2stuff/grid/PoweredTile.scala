/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.grid

import appeng.api.config.{Actionable, PowerMultiplier}
import appeng.api.networking.energy.IEnergyGrid
import net.bdew.lib.Misc
import net.bdew.lib.data.DataSlotDouble
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}

trait PoweredTile extends TileDataSlots with GridTile with SleepableTile {
  def powerCapacity: Double
  val powerStored = new DataSlotDouble("power", this).setUpdate(UpdateKind.SAVE)

  serverTick.listen(() => {
    if (node != null) {
      val net = node.getGrid.getCache[IEnergyGrid](classOf[IEnergyGrid])
      if ((powerStored / powerCapacity < 0.9) && (net.getStoredPower / net.getMaxStoredPower > 0.2)) {
        val drawn = net.extractAEPower(Misc.min(powerCapacity - powerStored, net.getMaxStoredPower * 0.1), Actionable.MODULATE, PowerMultiplier.CONFIG)
        if (drawn > 0) {
          powerStored += drawn
          wakeup()
        }
      }
    }
  })
}
