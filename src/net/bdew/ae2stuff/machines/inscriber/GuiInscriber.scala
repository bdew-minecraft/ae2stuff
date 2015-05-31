/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.inscriber

import net.bdew.ae2stuff.AE2Textures
import net.bdew.ae2stuff.misc.WidgetSlotLock
import net.bdew.lib.Misc
import net.bdew.lib.gui._
import net.bdew.lib.gui.widgets.{WidgetFillDataSlot, WidgetLabel}

import scala.collection.mutable

class GuiInscriber(cont: ContainerInscriber) extends BaseScreen(cont, if (cont.hasToolbox) 246 else 211, 176) {
  override def rect = new Rect(guiLeft, guiTop, 176, 176)
  override val background = AE2Textures.inscriberBackground

  val upgradesRect = new Rect(179, 0, 32, 104)
  val toolBoxRect = new Rect(178, 105, 68, 68)

  override def initGui() {
    super.initGui()
    widgets.add(new WidgetLabel(Misc.toLocal("tile.ae2stuff.Inscriber.name"), 8, 6, Color.darkGray))
    widgets.add(new WidgetLabel(Misc.toLocal("container.inventory"), 8, this.ySize - 96 + 3, Color.darkGray))
    widgets.add(new WidgetFillDataSlot(new Rect(135, 39, 6, 18), AE2Textures.inscriberProgress, Direction.UP, cont.te.progress, 1F) {
      override def handleTooltip(p: Point, tip: mutable.MutableList[String]) = tip += "%.0f".format(cont.te.progress * 100) + "%"
    })
    widgets.add(WidgetSlotLock(Rect(45 - 11, 16 + 4, 8, 8), cont.te.topLocked, "top"))
    widgets.add(WidgetSlotLock(Rect(45 - 11, 62 + 4, 8, 8), cont.te.bottomLocked, "bottom"))
  }

  override protected def drawGuiContainerBackgroundLayer(f: Float, x: Int, y: Int): Unit = {
    super.drawGuiContainerBackgroundLayer(f, x, y)
    widgets.drawTexture(upgradesRect + rect.origin, AE2Textures.upgradesBackground5)
    if (cont.hasToolbox)
      widgets.drawTexture(toolBoxRect + rect.origin, AE2Textures.toolBoxBackground)
  }
}
