/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff

import net.bdew.lib.gui.Texture
import net.minecraft.util.ResourceLocation

object AE2Textures {
  val inscriberTexture = new ResourceLocation("appliedenergistics2", "textures/guis/inscriber.png")
  val inscriberBackground = Texture(inscriberTexture, 0, 0, 176, 176)
  val inscriberProgress = Texture(inscriberTexture, 135, 177, 6, 18)
  val upgradesBackground3 = Texture(inscriberTexture, 179, 0, 32, 68)
  val toolBoxBackground = Texture(inscriberTexture, 178, 86, 68, 68)

  val upgradesBackground5 = Texture("appliedenergistics2", "textures/guis/mac.png", 179, 0, 32, 104)
}
