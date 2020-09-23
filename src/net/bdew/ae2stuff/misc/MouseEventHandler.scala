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

import net.bdew.ae2stuff.items.visualiser.{ItemVisualiser, VisualisationModes, VisualiserOverlayRender}
import net.bdew.ae2stuff.network.{MsgVisualisationMode, NetHandler}
import net.bdew.lib.{Client, Misc}
import net.minecraftforge.client.event.MouseEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object MouseEventHandler {
  def init() {
    MinecraftForge.EVENT_BUS.register(this)
  }

  @SubscribeEvent
  def handleMouseEvent(ev: MouseEvent) {
    if (ev.getDwheel == 0) return
    if (Client.minecraft.currentScreen != null) return
    val player = Client.player
    if (player == null || !player.isSneaking) return
    val stack = player.inventory.getCurrentItem
    if (stack.isEmpty) return
    if (stack.getItem == ItemVisualiser) {
      val newMode =
        if (ev.getDwheel.signum > 0)
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
