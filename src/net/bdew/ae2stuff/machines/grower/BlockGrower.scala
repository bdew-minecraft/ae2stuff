package net.bdew.ae2stuff.machines.grower

import net.bdew.ae2stuff.AE2Stuff
import net.bdew.lib.block.{HasTE, SimpleBlock}
import net.bdew.lib.tile.inventory.BreakableInventoryBlock
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

object BlockGrower extends SimpleBlock("Grower", Material.iron) with HasTE[TileGrower] with BreakableInventoryBlock {
  override val TEClass = classOf[TileGrower]

  setHardness(1)

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, xOffset: Float, yOffset: Float, zOffset: Float): Boolean = {
    if (!world.isRemote)
      player.openGui(AE2Stuff, MachineGrower.guiId, world, x, y, z)
    true
  }
}
