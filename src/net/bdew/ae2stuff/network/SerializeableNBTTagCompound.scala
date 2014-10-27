package net.bdew.ae2stuff.network

import java.io.{ObjectInputStream, ObjectOutputStream}

import net.minecraft.nbt.{CompressedStreamTools, NBTSizeTracker, NBTTagCompound}

class SerializableNBT(var tag: NBTTagCompound = new NBTTagCompound) extends Serializable {

  private def writeObject(out: ObjectOutputStream) {
    out.writeObject(CompressedStreamTools.compress(tag))
  }

  private def readObject(in: ObjectInputStream) {
    val obj = in.readObject()
    tag = CompressedStreamTools.func_152457_a(obj.asInstanceOf[Array[Byte]], new NBTSizeTracker(1024 * 1024))
  }
}

object SerializableNBT {
  implicit def ser2content(v: SerializableNBT) = v.tag
  implicit def content2ser(v: NBTTagCompound) = new SerializableNBT(v)
}

