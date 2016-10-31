/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.waila

import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.bdew.ae2stuff.grid.PoweredTile
import net.bdew.lib.{DecFormat, Misc}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World

object WailaPoweredDataProvider extends BaseDataProvider(classOf[PoweredTile]) {
  override def getNBTTag(player: EntityPlayerMP, te: PoweredTile, tag: NBTTagCompound, world: World, pos: BlockPos): NBTTagCompound = {
    tag.setDouble("waila_power_stored", te.powerStored)
    tag.setDouble("waila_power_capacity", te.powerCapacity)
    tag.setBoolean("waila_power_sleep", te.isSleeping)
    tag
  }

  override def getBodyStrings(target: PoweredTile, stack: ItemStack, acc: IWailaDataAccessor, cfg: IWailaConfigHandler): Iterable[String] = {
    val nbt = acc.getNBTData
    if (nbt.hasKey("waila_power_stored")) {
      List(
        Misc.toLocalF("ae2stuff.waila.power", DecFormat.short(nbt.getDouble("waila_power_stored")), DecFormat.short(nbt.getDouble("waila_power_capacity"))),
        if (nbt.getBoolean("waila_power_sleep"))
          TextFormatting.RED + Misc.toLocal("ae2stuff.waila.sleep.true") + TextFormatting.RESET
        else
          TextFormatting.GREEN + Misc.toLocal("ae2stuff.waila.sleep.false") + TextFormatting.RESET
      )
    } else List.empty
  }
}
