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
import net.bdew.ae2stuff.items.visualiser.{ItemVisualiser, VisualisationModes, VisualiserOverlayRender}
import net.bdew.ae2stuff.network.{MsgVisualisationMode, NetHandler}
import net.bdew.lib.{Client, Misc}
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.common.MinecraftForge

object MouseEventHandler {
  def init() {
    MinecraftForge.EVENT_BUS.register(this)
  }

  @SubscribeEvent
  def handleMouseEvent(ev: MouseEvent) {
    if (ev.dwheel == 0) return
    if (Client.minecraft.currentScreen != null) return
    val player = Client.player
    if (player == null || !player.isSneaking) return
    val stack = player.inventory.getCurrentItem
    if (stack == null || stack.getItem == null) return
    if (stack.getItem == ItemVisualiser) {
      val newMode =
        if (ev.dwheel.signum > 0)
          Misc.nextInSeq(VisualisationModes.modes, ItemVisualiser.getMode(stack))
        else
          Misc.prevInSeq(VisualisationModes.modes, ItemVisualiser.getMode(stack))
      ItemVisualiser.setMode(stack, newMode)
      VisualiserOverlayRender.needListRefresh = true
      NetHandler.sendToServer(MsgVisualisationMode(newMode))
      ev.setCanceled(true)
    }
  }
}
