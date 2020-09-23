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

package net.bdew.ae2stuff.machines.wireless

import net.bdew.ae2stuff.misc.WorldOverlayRenderer
import net.bdew.lib.Client
import net.bdew.lib.PimpVanilla._
import net.minecraft.util.math.RayTraceResult
import org.lwjgl.opengl.GL11

object WirelessOverlayRender extends WorldOverlayRenderer {
  override def doRender(partialTicks: Float): Unit = {
    val mop = Client.minecraft.objectMouseOver
    if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
      val pos = mop.getBlockPos
      for {
        tile <- Client.world.getTileSafe[TileWireless](pos)
        other <- tile.link.value
      } {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT)

        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GL11.glLineWidth(4.0F)

        GL11.glBegin(GL11.GL_LINES)
        GL11.glColor3f(0, 0, 1)
        GL11.glVertex3d(pos.getX + 0.5D, pos.getY + 0.5D, pos.getZ + 0.5D)
        GL11.glVertex3d(other.getX + 0.5D, other.getY + 0.5D, other.getZ + 0.5D)
        GL11.glEnd()

        GL11.glPopAttrib()
      }
    }
  }
}
