package net.bdew.ae2stuff.grid

import net.bdew.ae2stuff.AE2Stuff
import net.minecraft.tileentity.TileEntity

trait SleepableTile extends TileEntity {
  private final val TRACE = false
  private var sleeping = false

  def isAwake = !sleeping

  def isSleeping = sleeping

  def sleep(): Unit = {
    if (TRACE && !sleeping) AE2Stuff.logInfo("SLEEP %s (%d,%d,%d)", getClass.getSimpleName, xCoord, yCoord, zCoord)
    sleeping = true
  }

  def wakeup(): Unit = {
    if (TRACE && sleeping) AE2Stuff.logInfo("WAKEUP %s (%d,%d,%d)", getClass.getSimpleName, xCoord, yCoord, zCoord)
    sleeping = false
  }
}
