/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.compat

import net.bdew.lib.Misc
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

trait WrenchHandler {
  def canWrench(player: EntityPlayer, stack: ItemStack, x: Int, y: Int, z: Int): Boolean
  def doWrench(player: EntityPlayer, stack: ItemStack, x: Int, y: Int, z: Int): Unit
}

object WrenchRegistry {
  var registry = List.empty[WrenchHandler]

  def init(): Unit = {
    registry :+= AEWrenchHandler
    if (Misc.haveModVersion("BuildCraftAPI|tools"))
      registry :+= BCWrenchHandler
  }

  def findWrench(player: EntityPlayer, stack: ItemStack, x: Int, y: Int, z: Int) =
    registry.find(_.canWrench(player, stack, x, y, z))
}
