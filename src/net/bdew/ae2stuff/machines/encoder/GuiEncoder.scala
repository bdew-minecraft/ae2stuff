/*
 * Copyright (c) bdew, 2014 - 2017
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.encoder

import net.bdew.ae2stuff.AE2Stuff
import net.bdew.lib.Misc
import net.bdew.lib.gui.widgets.WidgetLabel
import net.bdew.lib.gui.{BaseScreen, Color, Texture}

class GuiEncoder(cont: ContainerEncoder) extends BaseScreen(cont, 176, 166) {
  override val background = Texture(AE2Stuff.modId, "textures/gui/encoder.png", rect)
  override def initGui() {
    super.initGui()
    widgets.add(new WidgetLabel(BlockEncoder.getLocalizedName, 8, 6, Color.darkGray))
    widgets.add(new WidgetLabel(Misc.toLocal("container.inventory"), 8, this.ySize - 96 + 3, Color.darkGray))
  }
}
