package net.bdew.ae2stuff.machines.grower

import net.bdew.ae2stuff.AE2Stuff
import net.bdew.lib.Misc
import net.bdew.lib.gui.widgets.WidgetLabel
import net.bdew.lib.gui.{BaseScreen, Color, Texture}

class GuiGrower(cont: ContainerGrower) extends BaseScreen(cont, 176, 166) {
  override val background = Texture(AE2Stuff.modId, "textures/gui/grower.png", rect)
  override def initGui() {
    super.initGui()
    widgets.add(new WidgetLabel(BlockGrower.getLocalizedName, 8, 6, Color.darkgray))
    widgets.add(new WidgetLabel(Misc.toLocal("container.inventory"), 8, this.ySize - 96 + 3, Color.darkgray))
  }
}
