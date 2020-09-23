/*
 * Copyright (c) bdew, 2014 - 2020
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
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