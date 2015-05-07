/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.waila

import mcp.mobius.waila.api.IWailaRegistrar
import net.bdew.ae2stuff.AE2Stuff
import net.bdew.ae2stuff.grid.PoweredTile

object WailaHandler {
  def loadCallback(reg: IWailaRegistrar) {
    AE2Stuff.logDebug("WAILA callback received, loading...")
    reg.registerBodyProvider(WailaPoweredDataProvider, classOf[PoweredTile])
    reg.registerNBTProvider(WailaPoweredDataProvider, classOf[PoweredTile])
  }
}
