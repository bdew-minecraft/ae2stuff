/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.grid

import net.bdew.ae2stuff.AE2Stuff
import net.bdew.lib.Event
import net.bdew.lib.tile.TileExtended

import scala.util.Random

trait SleepableTile extends TileExtended {
  private final val TRACE = false
  private var sleeping = false
  private val forceWakeupOn = Random.nextInt(100)

  serverTick.listen(() => {
    // This should prevent tiles from getting stuck sleeping forever
    if (getWorldObj.getTotalWorldTime % 100 == forceWakeupOn)
      wakeup()
  })

  val onSleep = Event()
  val onWake = Event()

  def isAwake = !sleeping

  def isSleeping = sleeping

  def sleep(): Unit = {
    if (TRACE && !sleeping) AE2Stuff.logInfo("SLEEP %s (%d,%d,%d)", getClass.getSimpleName, xCoord, yCoord, zCoord)
    if (!sleeping) onSleep.trigger()
    sleeping = true
  }

  def wakeup(): Unit = {
    if (TRACE && sleeping) AE2Stuff.logInfo("WAKEUP %s (%d,%d,%d)", getClass.getSimpleName, xCoord, yCoord, zCoord)
    if (sleeping) onWake.trigger()
    sleeping = false
  }
}

