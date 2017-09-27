/*
 * Copyright (c) bdew, 2014 - 2017
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.misc

import net.bdew.lib.nbt.Type.TInt
import net.bdew.lib.nbt.{ConvertedType, NBT}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos

case class PosAndDimension(pos: BlockPos, dim: Int)

object PosAndDimension {

  implicit object TPosAndDimension extends ConvertedType[PosAndDimension, NBTTagCompound] {
    override def encode(v: PosAndDimension) =
      NBT(
        "x" -> v.pos.getX,
        "y" -> v.pos.getY,
        "z" -> v.pos.getZ,
        "dim" -> v.dim
      )

    override def decode(t: NBTTagCompound) =
      if (t.hasKey("x", TInt.id) && t.hasKey("y", TInt.id) && t.hasKey("z", TInt.id) && t.hasKey("dim", TInt.id))
        Some(PosAndDimension(new BlockPos(t.getInteger("x"), t.getInteger("y"), t.getInteger("z")), t.getInteger("dim")))
      else
        None
  }

}