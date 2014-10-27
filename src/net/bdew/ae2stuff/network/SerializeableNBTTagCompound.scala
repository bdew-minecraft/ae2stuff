package net.bdew.ae2stuff.network

import java.io.{ObjectInputStream, ObjectOutputStream}

import net.minecraft.nbt.{CompressedStreamTools, NBTSizeTracker, NBTTagCompound}

class SerializableNBT extends Serializable {
  var tag = new NBTTagCompound

  private def writeObject(out: ObjectOutputStream) {
    out.write(CompressedStreamTools.compress(tag))
    CompressedStreamTools.write(tag, out)
  }

  private def readObject(in: ObjectInputStream) {
    tag = CompressedStreamTools.func_152457_a(in.readObject().asInstanceOf[Array[Byte]], new NBTSizeTracker(1024 * 1024))
  }
}

object SerializableNBT {
  implicit def ser2content(v: SerializableNBT) = v.tag
}

