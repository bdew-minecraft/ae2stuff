package net.bdew.ae2stuff.misc

import java.util

import cofh.api.block.IDismantleable
import cpw.mods.fml.common.Optional
import net.bdew.ae2stuff.compat.WrenchRegistry
import net.bdew.lib.block.BlockKeepData
import net.bdew.lib.items.ItemUtils
import net.bdew.lib.tile.inventory.BreakableInventoryTile
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World

@Optional.Interface(modid = "CoFHAPI|block", iface = "cofh.api.block.IDismantleable")
trait BlockWrenchable extends Block with IDismantleable {
  override def dismantleBlock(player: EntityPlayer, world: World, x: Int, y: Int, z: Int, returnDrops: Boolean): util.ArrayList[ItemStack] = {
    val item =
      if (this.isInstanceOf[BlockKeepData]) {
        this.asInstanceOf[BlockKeepData].getSavedBlock(world, x, y, z, world.getBlockMetadata(x, y, z))
      } else {
        val te = world.getTileEntity(x, y, z)

        if (te != null && te.isInstanceOf[BreakableInventoryTile])
          te.asInstanceOf[BreakableInventoryTile].dropItems()

        new ItemStack(this)
      }

    world.setBlockToAir(x, y, z)

    val ret = new util.ArrayList[ItemStack]()

    if (returnDrops)
      ret.add(item)
    else
      ItemUtils.throwItemAt(world, x, y, z, item)

    ret
  }

  override def canDismantle(player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Boolean = true

  def onBlockActivatedReal(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, xOffs: Float, yOffs: Float, zOffs: Float): Boolean

  final override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, side: Int, xOffs: Float, yOffs: Float, zOffs: Float): Boolean = {
    if (super.onBlockActivated(world, x, y, z, player, side, xOffs, yOffs, zOffs)) return true
    if (player.isSneaking) {
      for {
        stack <- Option(player.inventory.getCurrentItem)
        wrench <- WrenchRegistry.findWrench(player, stack, x, y, z)
      } {
        wrench.doWrench(player, stack, x, y, z)
        if (!world.isRemote) world.func_147480_a(x, y, z, true) //destroyBlock
        return true
      }
      false
    } else {
      if (!world.isRemote)
        onBlockActivatedReal(world, x, y, z, player, side, xOffs, yOffs, zOffs)
      else
        true
    }
  }
}