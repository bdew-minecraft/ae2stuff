/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.misc

import net.bdew.ae2stuff.network.{MsgSetLock, NetHandler}
import net.bdew.lib.Misc
import net.bdew.lib.data.DataSlotBoolean
import net.bdew.lib.gui.widgets.Widget
import net.bdew.lib.gui.{Point, Rect}

import scala.collection.mutable

case class WidgetSlotLock(rect: Rect, state: DataSlotBoolean, slot: String) extends Widget {
  override def draw(mouse: Point): Unit = {
    if (state)
      parent.drawTexture(rect, Icons.lockOn)
    else
      parent.drawTexture(rect, Icons.lockOff)
  }

  override def handleTooltip(p: Point, tip: mutable.MutableList[String]): Unit = {
    if (state) {
      tip += Misc.toLocal("ae2stuff.gui.lock.on")
    } else {
      tip += Misc.toLocal("ae2stuff.gui.lock.off")
      tip += Misc.toLocal("ae2stuff.gui.lock.note")
    }

  }

  override def mouseClicked(p: Point, button: Int): Unit = {
    NetHandler.sendToServer(MsgSetLock(slot, !state))
  }
}
