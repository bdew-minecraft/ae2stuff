package net.bdew.ae2stuff.jei

import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import mezz.jei.api.{BlankModPlugin, IModRegistry, JEIPlugin}

@JEIPlugin
class AE2StuffJeiPlugin extends BlankModPlugin {
  override def register(registry: IModRegistry): Unit = {
    registry.getRecipeTransferRegistry.addRecipeTransferHandler(EncoderTransferHandler, VanillaRecipeCategoryUid.CRAFTING)
  }
}