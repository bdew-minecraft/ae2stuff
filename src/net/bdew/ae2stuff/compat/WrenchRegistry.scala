/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.compat

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos

trait WrenchHandler {
  def canWrench(player: EntityPlayer, stack: ItemStack, pos: BlockPos): Boolean
  def doWrench(player: EntityPlayer, stack: ItemStack, pos: BlockPos): Unit
}

object WrenchRegistry {
  var registry = List.empty[WrenchHandler]

  def init(): Unit = {
    registry :+= AEWrenchHandler
  }

  def findWrench(player: EntityPlayer, stack: ItemStack, pos: BlockPos) =
    registry.find(_.canWrench(player, stack, pos))
}
