package net.bdew.ae2stuff.nei

import codechicken.nei.api.{API, GuiInfo, IConfigureNEI}
import net.bdew.ae2stuff.machines.encoder.GuiEncoder

class NEI_AE2Stuff_Config extends IConfigureNEI {
  override def loadConfig() {
    GuiInfo.customSlotGuis.add(classOf[GuiEncoder])
    API.registerGuiOverlayHandler(classOf[GuiEncoder], EncoderOverlayHandler, "crafting")
  }

  override def getName = "AE2 Stuff"
  override def getVersion = "AE2STUFF_VER"
}
