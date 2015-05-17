/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.grid

import appeng.api.config.{AccessRestriction, Actionable, PowerMultiplier}
import appeng.api.networking.energy.{IAEPowerStorage, IEnergyGrid}
import appeng.api.networking.events.MENetworkPowerStorage
import net.bdew.lib.Misc
import net.bdew.lib.data.DataSlotDouble
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}

trait PoweredTile extends TileDataSlots with GridTile with SleepableTile with IAEPowerStorage {
  def powerCapacity: Double

  val powerStored = new DataSlotDouble("power", this).setUpdate(UpdateKind.SAVE)

  private var postedReq = false

  def requestPowerIfNeeded(): Unit = {
    if (node != null && node.isActive) {
      if (!postedReq && powerStored / powerCapacity < 0.9D) {
        postedReq = true
        safePostEvent(new MENetworkPowerStorage(this, MENetworkPowerStorage.PowerEventType.REQUEST_POWER))
      } else if (powerStored / powerCapacity < 0.5D) {
        val net = node.getGrid.getCache[IEnergyGrid](classOf[IEnergyGrid])
        if (net.getStoredPower / net.getMaxStoredPower > 0.2) {
          val drawn = net.extractAEPower(Misc.min(powerCapacity - powerStored, net.getMaxStoredPower * 0.1), Actionable.MODULATE, PowerMultiplier.CONFIG)
          if (drawn > 0) {
            powerStored += drawn
            wakeup()
          }
        }
      }
    }
  }

  override def getAECurrentPower: Double = powerStored
  override def getAEMaxPower: Double = powerCapacity

  override def injectAEPower(v: Double, actionable: Actionable): Double = {
    val canStore = Misc.clamp(v, 0D, powerCapacity - powerStored)
    if (actionable == Actionable.MODULATE && canStore > 0) {
      powerStored += canStore
      postedReq = false
      wakeup()
    }
    v - canStore
  }

  override def extractAEPower(v: Double, actionable: Actionable, powerMultiplier: PowerMultiplier): Double = 0

  override def getPowerFlow = AccessRestriction.WRITE
  override def isAEPublicPowerStorage = true
}
