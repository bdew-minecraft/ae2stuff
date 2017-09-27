/*
 * Copyright (c) bdew, 2014 - 2017
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.compat

import appeng.api.implementations.items.IAEWrench
import net.bdew.lib.Misc
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos

object AEWrenchHandler extends WrenchHandler {
  override def canWrench(player: EntityPlayer, stack: ItemStack, pos: BlockPos): Boolean =
    Misc.asInstanceOpt(stack.getItem, classOf[IAEWrench]) exists (_.canWrench(stack, player, pos))

  override def doWrench(player: EntityPlayer, stack: ItemStack, pos: BlockPos): Unit = {}
}
