package net.bdew.ae2stuff

import net.bdew.ae2stuff.machines.encoder.MachineEncoder
import net.bdew.lib.config.MachineManager

object Machines extends MachineManager(Tuning.getSection("Machines"), AE2Stuff.guiHandler, CreativeTabs.main) {
  registerMachine(MachineEncoder)
}
