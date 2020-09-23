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

import net.bdew.ae2stuff.misc.{OverlayRenderHandler, WorldOverlayRenderer}
import net.bdew.ae2stuff.network.{MsgVisualisationData, NetHandler}
import net.bdew.lib.Client
import net.minecraft.inventory.EntityEquipmentSlot
import org.lwjgl.opengl.GL11

object VisualiserOverlayRender extends WorldOverlayRenderer {
  var currentLinks = new VisualisationData()
  var dense, normal = Seq.empty[VLink]

  val staticList = GL11.glGenLists(1)
  var needListRefresh = true

  final val SIZE = 0.2D

  NetHandler.regClientHandler {
    case MsgVisualisationData(data) =>
      currentLinks = data
      val (dense1, normal1) = currentLinks.links.partition(_.flags.contains(VLinkFlags.DENSE))
      dense = dense1
      normal = normal1
      needListRefresh = true
  }

  def setColor(rgb: (Double, Double, Double), mul: Double): Unit = {
    GL11.glColor3d(rgb._1 * mul, rgb._2 * mul, rgb._3 * mul)
  }

  def renderNodes(): Unit = {
    GL11.glBegin(GL11.GL_QUADS)

    for (node <- currentLinks.nodes) {
      val color =
        if (node.flags.contains(VNodeFlags.MISSING))
          (1D, 0D, 0D)
        else if (node.flags.contains(VNodeFlags.DENSE))
          (1D, 1D, 0D)
        else
          (0D, 0D, 1D)

      setColor(color, 1D) // +Y
      GL11.glVertex3d(node.x + 0.5D - SIZE, node.y + 0.5D + SIZE, node.z + 0.5D + SIZE)
      GL11.glVertex3d(node.x + 0.5D + SIZE, node.y + 0.5D + SIZE, node.z + 0.5D + SIZE)
      GL11.glVertex3d(node.x + 0.5D + SIZE, node.y + 0.5D + SIZE, node.z + 0.5D - SIZE)
      GL11.glVertex3d(node.x + 0.5D - SIZE, node.y + 0.5D + SIZE, node.z + 0.5D - SIZE)

      setColor(color, 0.5D) // -Y
      GL11.glVertex3d(node.x + 0.5D + SIZE, node.y + 0.5D - SIZE, node.z + 0.5D - SIZE)
      GL11.glVertex3d(node.x + 0.5D + SIZE, node.y + 0.5D - SIZE, node.z + 0.5D + SIZE)
      GL11.glVertex3d(node.x + 0.5D - SIZE, node.y + 0.5D - SIZE, node.z + 0.5D + SIZE)
      GL11.glVertex3d(node.x + 0.5D - SIZE, node.y + 0.5D - SIZE, node.z + 0.5D - SIZE)

      setColor(color, 0.8D) // +/- Z
      GL11.glVertex3d(node.x + 0.5D + SIZE, node.y + 0.5D - SIZE, node.z + 0.5D + SIZE)
      GL11.glVertex3d(node.x + 0.5D + SIZE, node.y + 0.5D + SIZE, node.z + 0.5D + SIZE)
      GL11.glVertex3d(node.x + 0.5D - SIZE, node.y + 0.5D + SIZE, node.z + 0.5D + SIZE)
      GL11.glVertex3d(node.x + 0.5D - SIZE, node.y + 0.5D - SIZE, node.z + 0.5D + SIZE)
      GL11.glVertex3d(node.x + 0.5D - SIZE, node.y + 0.5D + SIZE, node.z + 0.5D - SIZE)
      GL11.glVertex3d(node.x + 0.5D + SIZE, node.y + 0.5D + SIZE, node.z + 0.5D - SIZE)
      GL11.glVertex3d(node.x + 0.5D + SIZE, node.y + 0.5D - SIZE, node.z + 0.5D - SIZE)
      GL11.glVertex3d(node.x + 0.5D - SIZE, node.y + 0.5D - SIZE, node.z + 0.5D - SIZE)

      setColor(color, 0.6D) // +/- X
      GL11.glVertex3d(node.x + 0.5D + SIZE, node.y + 0.5D + SIZE, node.z + 0.5D - SIZE)
      GL11.glVertex3d(node.x + 0.5D + SIZE, node.y + 0.5D + SIZE, node.z + 0.5D + SIZE)
      GL11.glVertex3d(node.x + 0.5D + SIZE, node.y + 0.5D - SIZE, node.z + 0.5D + SIZE)
      GL11.glVertex3d(node.x + 0.5D + SIZE, node.y + 0.5D - SIZE, node.z + 0.5D - SIZE)
      GL11.glVertex3d(node.x + 0.5D - SIZE, node.y + 0.5D - SIZE, node.z + 0.5D + SIZE)
      GL11.glVertex3d(node.x + 0.5D - SIZE, node.y + 0.5D + SIZE, node.z + 0.5D + SIZE)
      GL11.glVertex3d(node.x + 0.5D - SIZE, node.y + 0.5D + SIZE, node.z + 0.5D - SIZE)
      GL11.glVertex3d(node.x + 0.5D - SIZE, node.y + 0.5D - SIZE, node.z + 0.5D - SIZE)
    }

    GL11.glEnd()
  }

  def renderLinks(links: Seq[VLink], width: Float, onlyP2P: Boolean): Unit = {
    GL11.glLineWidth(width)
    GL11.glBegin(GL11.GL_LINES)

    for (link <- links if (!onlyP2P) || link.flags.contains(VLinkFlags.COMPRESSED)) {
      if (link.flags.contains(VLinkFlags.COMPRESSED)) {
        GL11.glColor3f(1, 0, 1)
      } else if (link.flags.contains(VLinkFlags.DENSE)) {
        GL11.glColor3f(1, 1, 0)
      } else {
        GL11.glColor3f(0, 0, 1)
      }

      GL11.glVertex3d(link.node1.x + 0.5D, link.node1.y + 0.5D, link.node1.z + 0.5D)
      GL11.glVertex3d(link.node2.x + 0.5D, link.node2.y + 0.5D, link.node2.z + 0.5D)
    }

    GL11.glEnd()
  }

  val renderNodesModes = Set(VisualisationModes.NODES, VisualisationModes.FULL, VisualisationModes.NONUM)
  val renderLinksModes = Set(VisualisationModes.CHANNELS, VisualisationModes.FULL, VisualisationModes.NONUM, VisualisationModes.P2P)

  override def doRender(partialTicks: Float): Unit = {
    if (Client.player != null) {

      val main = Client.player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND)
      val off = Client.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND)

      val stack =
        if (main != null && main.getItem == ItemVisualiser)
          main
        else if (off != null && off.getItem == ItemVisualiser)
          off
        else return

      val mode = ItemVisualiser.getMode(stack)

      GL11.glPushAttrib(GL11.GL_ENABLE_BIT)

      GL11.glDisable(GL11.GL_LIGHTING)
      GL11.glDisable(GL11.GL_TEXTURE_2D)
      GL11.glDisable(GL11.GL_DEPTH_TEST)

      if (needListRefresh) {
        needListRefresh = false
        GL11.glNewList(staticList, GL11.GL_COMPILE)

        if (renderNodesModes.contains(mode))
          renderNodes()

        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)

        if (renderLinksModes.contains(mode)) {
          renderLinks(dense, 16F, mode == VisualisationModes.P2P)
          renderLinks(normal, 4F, mode == VisualisationModes.P2P)
        }

        GL11.glEndList()
      }

      GL11.glCallList(staticList)

      // Labels are rendered every frame because they need to face the camera

      if (mode == VisualisationModes.FULL) {
        for (link <- currentLinks.links if link.channels > 0) {
          OverlayRenderHandler.renderFloatingText(link.channels.toString,
            (link.node1.x + link.node2.x) / 2D + 0.5D, (link.node1.y + link.node2.y) / 2D + 0.5D, (link.node1.z + link.node2.z) / 2D + 0.5D, 0xFFFFFF)
        }
      }

      GL11.glPopAttrib()
    }
  }
}
