/*
 * Copyright (c) bdew, 2014 - 2017
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.grower

import net.bdew.ae2stuff.{AE2Stuff, AE2Textures}
import net.bdew.lib.Misc
import net.bdew.lib.gui._
import net.bdew.lib.gui.widgets.WidgetLabel

class GuiGrower(cont: ContainerGrower) extends BaseScreen(cont, if (cont.hasToolbox) 246 else 211, 166) {
  override val background = Texture(AE2Stuff.modId, "textures/gui/grower.png", rect)
  val upgradesRect = new Rect(179, 0, 32, 68)
  val toolBoxRect = new Rect(178, 86, 68, 68)
  override def initGui() {
    super.initGui()
    widgets.add(new WidgetLabel(BlockGrower.getLocalizedName, 8, 6, Color.darkGray))
    widgets.add(new WidgetLabel(Misc.toLocal("container.inventory"), 8, this.ySize - 96 + 3, Color.darkGray))
  }

  override protected def drawGuiContainerBackgroundLayer(f: Float, x: Int, y: Int): Unit = {
    super.drawGuiContainerBackgroundLayer(f, x, y)
    widgets.drawTexture(upgradesRect + rect.origin, AE2Textures.upgradesBackground3)
    if (cont.hasToolbox)
      widgets.drawTexture(toolBoxRect + rect.origin, AE2Textures.toolBoxBackground)
  }
}
