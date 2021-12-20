/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.compat

import buildcraft.api.tools.IToolWrench
import net.bdew.lib.Misc
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

object BCWrenchHandler extends WrenchHandler {
  override def canWrench(player: EntityPlayer, stack: ItemStack, x: Int, y: Int, z: Int): Boolean =
    Misc.asInstanceOpt(stack.getItem, classOf[IToolWrench]) exists (_.canWrench(player, x, y, z))

  override def doWrench(player: EntityPlayer, stack: ItemStack, x: Int, y: Int, z: Int): Unit =
    Misc.asInstanceOpt(stack.getItem, classOf[IToolWrench]) foreach (_.wrenchUsed(player, x, y, z))
}
