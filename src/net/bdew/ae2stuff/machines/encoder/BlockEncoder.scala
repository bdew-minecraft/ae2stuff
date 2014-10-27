package net.bdew.ae2stuff.machines.encoder

import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.bdew.ae2stuff.AE2Stuff
import net.bdew.lib.block.{HasTE, SimpleBlock}
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{ChatComponentTranslation, ChatStyle, EnumChatFormatting, IIcon}
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

object BlockEncoder extends SimpleBlock("Encoder", Material.iron) with HasTE[TileEncoder] {
  override val TEClass = classOf[TileEncoder]

  var topIcon: IIcon = null

  override def getIcon(side: Int, meta: Int) =
    if (side == ForgeDirection.UP.ordinal())
      topIcon
    else
      blockIcon

  @SideOnly(Side.CLIENT)
  override def registerBlockIcons(reg: IIconRegister) {
    blockIcon = reg.registerIcon(modId + ":encoder/side")
    topIcon = reg.registerIcon(modId + ":encoder/top")
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
}
