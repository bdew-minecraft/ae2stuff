/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.encoder

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.ae2stuff.AE2Stuff
import net.bdew.lib.Misc
import net.bdew.lib.block.{HasTE, SimpleBlock}
import net.bdew.lib.rotate.{IconType, RotatableTileBlock}
import net.bdew.lib.tile.inventory.BreakableInventoryBlock
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.{ChatComponentTranslation, ChatStyle, EnumChatFormatting, IIcon}
import net.minecraft.world.World

object BlockEncoder extends SimpleBlock("Encoder", Material.iron) with HasTE[TileEncoder] with BreakableInventoryBlock with RotatableTileBlock {
  override val TEClass = classOf[TileEncoder]

  setHardness(1)

  var topIconOn: IIcon = null
  var topIconOff: IIcon = null

  override def getIcon(meta: Int, kind: IconType.Value): IIcon =
    if (kind == IconType.FRONT)
      if (meta == 1)
        topIconOn
      else
        topIconOff
    else
      blockIcon

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(reg: IIconRegister) {
    blockIcon = reg.registerIcon(Misc.iconName(modId, name, "side"))
    topIconOn = reg.registerIcon(Misc.iconName(modId, name, "top_on"))
    topIconOff = reg.registerIcon(Misc.iconName(modId, name, "top_off"))
  }

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, xOffset: Float, yOffset: Float, zOffset: Float): Boolean = {
    if (!world.isRemote) {
      if (getTE(world, x, y, z).getNode.isActive)
        player.openGui(AE2Stuff, MachineEncoder.guiId, world, x, y, z)
      else
        player.addChatMessage(
          new ChatComponentTranslation("ae2stuff.error.not_connected")
            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED))
        )
    }
    true
  }

  override def onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, player: EntityLivingBase, stack: ItemStack) {
    super.onBlockPlacedBy(world, x, y, z, player, stack)
    if (player.isInstanceOf[EntityPlayer])
      getTE(world, x, y, z).placingPlayer = player.asInstanceOf[EntityPlayer]
  }
}
