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

import net.bdew.ae2stuff.AE2Stuff
import net.bdew.lib.Event
import net.bdew.lib.tile.{TileExtended, TileTicking}

import scala.util.Random

trait SleepableTile extends TileExtended with TileTicking {
  private final val TRACE = false
  private var sleeping = false
  private val forceWakeupOn = Random.nextInt(100)

  serverTick.listen(() => {
    // This should prevent tiles from getting stuck sleeping forever
    if (getWorld.getTotalWorldTime % 100 == forceWakeupOn)
      wakeup()
  })

  val onSleep = Event()
  val onWake = Event()

  def isAwake = !sleeping

  def isSleeping = sleeping

  def sleep(): Unit = {
    if (TRACE && !sleeping) AE2Stuff.logInfo("SLEEP %s (%s)", getClass.getSimpleName, getPos)
    if (!sleeping) onSleep.trigger()
    sleeping = true
  }

  def wakeup(): Unit = {
    if (TRACE && sleeping) AE2Stuff.logInfo("WAKEUP %s (%s)", getClass.getSimpleName, getPos)
    if (sleeping) onWake.trigger()
    sleeping = false
  }
}

