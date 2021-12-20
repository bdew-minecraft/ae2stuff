/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.wireless

import java.util

import appeng.api.AEApi
import appeng.api.networking.{GridFlags, IGridConnection}
import net.bdew.ae2stuff.AE2Stuff
import net.bdew.ae2stuff.grid.{GridTile, VariableIdlePower}
import net.bdew.lib.block.BlockRef
import net.bdew.lib.data.base.{TileDataSlots, UpdateKind}
import net.bdew.lib.multiblock.data.DataSlotPos
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class TileWireless extends TileDataSlots with GridTile with VariableIdlePower {
  val cfg = MachineWireless

  val link = DataSlotPos("link", this).setUpdate(UpdateKind.SAVE, UpdateKind.WORLD)

  var connection: IGridConnection = null

  lazy val myPos = BlockRef.fromTile(this)

  def isLinked = link.isDefined
  def getLink = link flatMap (_.getTile[TileWireless](worldObj))

  override def getFlags = util.EnumSet.of(GridFlags.DENSE_CAPACITY)

  serverTick.listen(() => {
    if (connection == null && link.isDefined) {
      try {
        setupConnection()
      } catch {
        case t: Throwable =>
          AE2Stuff.logWarnException("Failed setting up wireless link %s <-> %s: %s", t, myPos, link.get, t.getMessage)
          doUnlink()
      }
    }
  })

  def doLink(other: TileWireless): Boolean = {
    if (other.link.isEmpty) {
      other.link.set(myPos)
      link.set(other.myPos)
      setupConnection()
    } else false
  }

  def doUnlink(): Unit = {
    breakConnection()
    getLink foreach { that =>
      this.link := None
      that.link := None
    }
  }

  def setupConnection(): Boolean = {
    getLink foreach { that =>
      connection = AEApi.instance().createGridConnection(this.getNode, that.getNode)
      that.connection = connection
      val dx = this.xCoord - that.xCoord
      val dy = this.yCoord - that.yCoord
      val dz = this.zCoord - that.zCoord
      //val power = cfg.powerBase + cfg.powerDistanceMultiplier * (dx * dx + dy * dy + dz * dz)
      val dist = math.sqrt(dx * dx + dy * dy + dz * dz)
      val power = cfg.powerBase + cfg.powerDistanceMultiplier * dist * math.log(dist * dist + 3)
      this.setIdlePowerUse(power)
      that.setIdlePowerUse(power)
      worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, 1, 3)
      worldObj.setBlockMetadataWithNotify(that.xCoord, that.yCoord, that.zCoord, 1, 3)
      return true
    }
    false
  }

  def breakConnection(): Unit = {
    if (connection != null)
      connection.destroy()
    connection = null
    setIdlePowerUse(0D)
    getLink foreach { other =>
      other.connection = null
      other.setIdlePowerUse(0D)
      worldObj.setBlockMetadataWithNotify(other.xCoord, other.yCoord, other.zCoord, 0, 3)
    }
    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3)
  }

  override def getMachineRepresentation: ItemStack = new ItemStack(BlockWireless)

  override def shouldRefresh(oldBlock: Block, newBlock: Block, oldMeta: Int, newMeta: Int, world: World, x: Int, y: Int, z: Int): Boolean =
    newBlock != BlockWireless
}
