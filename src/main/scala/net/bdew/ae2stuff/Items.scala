/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff

import net.bdew.ae2stuff.items.ItemWirelessKit
import net.bdew.ae2stuff.items.visualiser.ItemVisualiser
import net.bdew.ae2stuff.machines.wireless.MachineWireless
import net.bdew.lib.config.ItemManager

object Items extends ItemManager(CreativeTabs.main) {
  if (MachineWireless.enabled)
    regItem(ItemWirelessKit)

  regItem(ItemVisualiser)
}
