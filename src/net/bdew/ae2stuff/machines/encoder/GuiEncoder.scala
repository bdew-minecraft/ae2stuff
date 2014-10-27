package net.bdew.ae2stuff.machines.encoder

import net.bdew.ae2stuff.AE2Stuff
import net.bdew.lib.gui.{BaseScreen, Texture}

class GuiEncoder(cont: ContainerEncoder) extends BaseScreen(cont, 176, 166) {
  override val background = Texture(AE2Stuff.modId, "textures/gui/encoder.png", rect)
}
