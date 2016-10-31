/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.misc

import appeng.api.implementations.guiobjects.{IGuiItem, INetworkTool}
import net.bdew.ae2stuff.AE2Defs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity

object UpgradeableHelper {
  lazy val networkTool = AE2Defs.items.networkTool()

  def isNetworkTool(is: ItemStack) = networkTool.isSameAs(is)

  def getNetworkToolObj(is: ItemStack, te: TileEntity) =
    networkTool.maybeItem().get().asInstanceOf[IGuiItem].getGuiObject(is, te.getWorld, te.getPos).asInstanceOf[INetworkTool]

  def findNetworktoolStack(player: EntityPlayer) = (for {
    i <- 0 until player.inventory.getSizeInventory
    stack <- Option(player.inventory.getStackInSlot(i))
    if UpgradeableHelper.networkTool.isSameAs(stack)
  } yield i).headOption
}
