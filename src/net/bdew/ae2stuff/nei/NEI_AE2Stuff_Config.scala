/*
 * Copyright (c) bdew, 2014
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

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
