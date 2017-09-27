/*
 * Copyright (c) bdew, 2014 - 2017
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.jei

import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import mezz.jei.api.{IModPlugin, IModRegistry, JEIPlugin}

@JEIPlugin
class AE2StuffJeiPlugin extends IModPlugin {
  override def register(registry: IModRegistry): Unit = {
    registry.getRecipeTransferRegistry.addRecipeTransferHandler(EncoderTransferHandler, VanillaRecipeCategoryUid.CRAFTING)
  }
}