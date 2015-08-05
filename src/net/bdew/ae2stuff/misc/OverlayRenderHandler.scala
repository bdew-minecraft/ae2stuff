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
}
