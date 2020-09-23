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

package net.bdew.ae2stuff.items.visualiser

import java.io._

import net.bdew.ae2stuff.AE2Stuff

object VNodeFlags extends Enumeration {
  val DENSE, MISSING = Value
}

object VLinkFlags extends Enumeration {
  val DENSE, COMPRESSED = Value
}

case class VNode(x: Int, y: Int, z: Int, flags: VNodeFlags.ValueSet)

case class VLink(node1: VNode, node2: VNode, channels: Byte, flags: VLinkFlags.ValueSet)

class VisualisationData(var nodes: Seq[VNode], var links: Seq[VLink]) extends Externalizable {
  def this() = this(Seq.empty, Seq.empty)

  override def readExternal(in: ObjectInput): Unit = {
    val ver = in.readInt()
    if (ver != VisualisationData.VERSION) {
      AE2Stuff.logWarn("Visualisation data version mismatch, expected %d, got %d - make sure client/server versions are not mismatched")
    } else {
      val nodeCount = in.readInt()
      val linkCount = in.readInt()
      nodes = Vector.empty ++ (for (i <- 0 until nodeCount) yield {
        val x = in.readInt()
        val y = in.readInt()
        val z = in.readInt()
        val f = in.readByte()
        VNode(x, y, z, VNodeFlags.ValueSet.fromBitMask(Array(f.toLong)))
      })

      links = for (i <- 0 until linkCount) yield {
        val n1 = in.readInt()
        val n2 = in.readInt()
        val c = in.readByte()
        val f = in.readByte()
        VLink(nodes(n1), nodes(n2), c, VLinkFlags.ValueSet.fromBitMask(Array(f.toLong)))
      }
    }
  }

  override def writeExternal(out: ObjectOutput): Unit = {
    out.writeInt(VisualisationData.VERSION)
    out.writeInt(nodes.size)
    out.writeInt(links.size)
    for (n <- nodes) {
      out.writeInt(n.x)
      out.writeInt(n.y)
      out.writeInt(n.z)
      out.writeByte(n.flags.toBitMask(0).toByte)
    }

    val nodeMap = nodes.zipWithIndex.toMap

    for (l <- links) {
      out.writeInt(nodeMap(l.node1))
      out.writeInt(nodeMap(l.node2))
      out.writeByte(l.channels)
      out.writeByte(l.flags.toBitMask(0).toByte)
    }
  }
}

object VisualisationData {
  final val VERSION = 1
}

object VisualisationModes extends Enumeration {
  val FULL, NODES, CHANNELS, NONUM, P2P = Value
  val modes = List(FULL, NODES, CHANNELS, NONUM, P2P)
}