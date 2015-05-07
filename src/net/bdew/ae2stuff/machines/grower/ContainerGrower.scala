/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.grower

import net.bdew.ae2stuff.misc.ContainerUpgradeable
import net.bdew.lib.data.base.ContainerDataSlots
import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.minecraft.entity.player.EntityPlayer

class ContainerGrower(te: TileGrower, player: EntityPlayer) extends BaseContainer(te) with ContainerDataSlots with ContainerUpgradeable {
  override lazy val dataSource = te

  for (y <- 0 until 3; x <- 0 until 9)
    this.addSlotToContainer(new SlotValidating(te, x + y * 9, 8 + x * 18, 17 + y * 18))

  for (i <- 0 until te.upgrades.getSizeInventory())
    addSlotToContainer(new SlotValidating(te.upgrades, i, 187, 8 + i * 18))

  bindPlayerInventory(player.inventory, 8, 84, 142)

  initUpgradeable(te, te.upgrades, player, 186, 94, addSlotToContainer(_))

  override def canInteractWith(p: EntityPlayer) = te.isUseableByPlayer(p)
}
