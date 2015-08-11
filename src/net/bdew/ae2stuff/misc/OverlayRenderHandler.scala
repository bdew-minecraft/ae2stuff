/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.misc

import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.bdew.ae2stuff.AE2Stuff
import net.bdew.lib.Client
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
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
    val dx = p.lastTickPosX + (p.posX - p.lastTickPosX) * ev.partialTicks
    val dy = p.lastTickPosY + (p.posY - p.lastTickPosY) * ev.partialTicks
    val dz = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * ev.partialTicks

    GL11.glPushMatrix()
    GL11.glTranslated(-dx, -dy, -dz)

    for (renderer <- renderers) {
      try {
        renderer.doRender(ev.partialTicks)
      } catch {
        case t: Throwable =>
          AE2Stuff.logErrorException("Error in overlay renderer %s", t, renderer)
      }
    }

    GL11.glPopMatrix()
  }

  def renderFloatingText(text: String, x: Double, y: Double, z: Double, color: Int): Unit = {
    val renderManager = RenderManager.instance
    val fontRenderer = Client.fontRenderer
    val tessellator = Tessellator.instance

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

    val yOffset = -4

    GL11.glDisable(GL11.GL_TEXTURE_2D)
    tessellator.startDrawingQuads()
    val stringMiddle = fontRenderer.getStringWidth(text) / 2
    tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.5F)
    tessellator.addVertex(-stringMiddle - 1, -1 + yOffset, 0.0D)
    tessellator.addVertex(-stringMiddle - 1, 8 + yOffset, 0.0D)
    tessellator.addVertex(stringMiddle + 1, 8 + yOffset, 0.0D)
    tessellator.addVertex(stringMiddle + 1, -1 + yOffset, 0.0D)
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
