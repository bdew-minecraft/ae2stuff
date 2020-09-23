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

import net.bdew.ae2stuff.AE2Stuff
import net.bdew.lib.Client
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11

trait WorldOverlayRenderer {
  def doRender(partialTicks: Float): Unit
}

object OverlayRenderHandler {
  var renderers = List.empty[WorldOverlayRenderer]
  MinecraftForge.EVENT_BUS.register(this)

  def register(r: WorldOverlayRenderer) = renderers +:= r

  @SubscribeEvent
  def onRenderWorldLastEvent(ev: RenderWorldLastEvent): Unit = {
    val p = Client.player
    val dx = p.lastTickPosX + (p.posX - p.lastTickPosX) * ev.getPartialTicks
    val dy = p.lastTickPosY + (p.posY - p.lastTickPosY) * ev.getPartialTicks
    val dz = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * ev.getPartialTicks

    GL11.glPushMatrix()
    GL11.glTranslated(-dx, -dy, -dz)

    for (renderer <- renderers) {
      try {
        renderer.doRender(ev.getPartialTicks)
      } catch {
        case t: Throwable =>
          AE2Stuff.logErrorException("Error in overlay renderer %s", t, renderer)
      }
    }

    GL11.glPopMatrix()
  }

  def renderFloatingText(text: String, x: Double, y: Double, z: Double, color: Int): Unit = {
    val renderManager = Client.minecraft.getRenderManager
    val fontRenderer = Client.fontRenderer
    val tessellator = Tessellator.getInstance()

    val scale = 0.027F
    GL11.glColor4f(1f, 1f, 1f, 0.5f)
    GL11.glPushMatrix()
    GL11.glTranslated(x, y, z)
    GL11.glNormal3f(0.0F, 1.0F, 0.0F)
    GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F)
    GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F)
    GL11.glScalef(-scale, -scale, scale)
    GL11.glDisable(GL11.GL_LIGHTING)
    GL11.glDepthMask(false)
    GL11.glDisable(GL11.GL_DEPTH_TEST)
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    GL11.glDisable(GL11.GL_TEXTURE_2D)

    val yOffset = -4
    val stringMiddle = fontRenderer.getStringWidth(text) / 2

    val buffer = tessellator.getBuffer
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

    buffer.pos(-stringMiddle - 1, -1 + yOffset, 0.0D).color(0, 0, 0, 0.5f).endVertex()
    buffer.pos(-stringMiddle - 1, 8 + yOffset, 0.0D).color(0, 0, 0, 0.5f).endVertex()
    buffer.pos(stringMiddle + 1, 8 + yOffset, 0.0D).color(0, 0, 0, 0.5f).endVertex()
    buffer.pos(stringMiddle + 1, -1 + yOffset, 0.0D).color(0, 0, 0, 0.5f).endVertex()

    tessellator.draw()

    GL11.glEnable(GL11.GL_TEXTURE_2D)
    GL11.glColor4f(1f, 1f, 1f, 0.5f)
    fontRenderer.drawString(text, -fontRenderer.getStringWidth(text) / 2, yOffset, color)
    GL11.glEnable(GL11.GL_DEPTH_TEST)
    GL11.glDepthMask(true)
    fontRenderer.drawString(text, -fontRenderer.getStringWidth(text) / 2, yOffset, color)
    GL11.glEnable(GL11.GL_LIGHTING)
    GL11.glDisable(GL11.GL_BLEND)
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
    GL11.glPopMatrix()
  }
}
