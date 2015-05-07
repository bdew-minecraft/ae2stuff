/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.inscriber

import net.bdew.ae2stuff.misc.ContainerUpgradeable
import net.bdew.lib.data.base.ContainerDataSlots
import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.minecraft.entity.player.EntityPlayer

class ContainerInscriber(val te: TileInscriber, player: EntityPlayer) extends BaseContainer(te) with ContainerDataSlots with ContainerUpgradeable {
  override lazy val dataSource = te

  addSlotToContainer(new SlotValidating(te, te.slots.top, 45, 16))
  addSlotToContainer(new SlotValidating(te, te.slots.middle, 63, 39))
  addSlotToContainer(new SlotValidating(te, te.slots.bottom, 45, 62))
  addSlotToContainer(new SlotValidating(te, te.slots.output, 113, 40))

  for (i <- 0 until te.upgrades.getSizeInventory())
    addSlotToContainer(new SlotValidating(te.upgrades, i, 187, 8 + i * 18))

  bindPlayerInventory(player.inventory, 8, 94, 152)

  initUpgradeable(te, te.upgrades, player, 186, 113, addSlotToContainer(_))

  override def canInteractWith(p: EntityPlayer) = te.isUseableByPlayer(p)
}
