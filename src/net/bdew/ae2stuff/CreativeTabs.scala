/*
 * Copyright (c) bdew, 2014 - 2017
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff

import net.bdew.ae2stuff.machines.encoder.BlockEncoder
import net.bdew.lib.CreativeTabContainer
import net.minecraft.item.ItemStack

object CreativeTabs extends CreativeTabContainer {
  val main = new Tab("bdew.ae2stuff", new ItemStack(BlockEncoder))
}
