/*
 * Copyright (c) bdew, 2014 - 2015
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff.machines.wireless

import net.bdew.lib.machine.Machine

object MachineWireless extends Machine("Wireless", BlockWireless) {
  lazy val powerBase = tuning.getDouble("PowerBase")
  lazy val powerDistanceMultiplier = tuning.getDouble("PowerDistanceMultiplier")
}
