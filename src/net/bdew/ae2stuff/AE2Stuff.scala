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

package net.bdew.ae2stuff

import java.io.File

import net.bdew.ae2stuff.compat.WrenchRegistry
import net.bdew.ae2stuff.items.visualiser.{VisualiserOverlayRender, VisualiserPlayerTracker}
import net.bdew.ae2stuff.machines.wireless.WirelessOverlayRender
import net.bdew.ae2stuff.misc.{Icons, MouseEventHandler, OverlayRenderHandler}
import net.bdew.ae2stuff.network.NetHandler
import net.bdew.lib.Event
import net.bdew.lib.gui.GuiHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event._
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import org.apache.logging.log4j.Logger

@Mod(modid = AE2Stuff.modId, version = "AE2STUFF_VER", name = "AE2 Stuff", dependencies = "required-after:appliedenergistics2;required-after:bdlib@[BDLIB_VER,)", acceptedMinecraftVersions = "[1.12,1.12.2]", modLanguage = "scala")
object AE2Stuff {
  var log: Logger = null
  var instance = this

  final val modId = "ae2stuff"
  final val channel = "bdew.ae2stuff"

  var configDir: File = null

  val guiHandler = new GuiHandler

  def logDebug(msg: String, args: Any*) = log.debug(msg.format(args: _*))
  def logInfo(msg: String, args: Any*) = log.info(msg.format(args: _*))
  def logWarn(msg: String, args: Any*) = log.warn(msg.format(args: _*))
  def logError(msg: String, args: Any*) = log.error(msg.format(args: _*))
  def logWarnException(msg: String, t: Throwable, args: Any*) = log.warn(msg.format(args: _*), t)
  def logErrorException(msg: String, t: Throwable, args: Any*) = log.error(msg.format(args: _*), t)

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) {
    log = event.getModLog
    configDir = new File(event.getModConfigurationDirectory, "AE2Stuff")
    TuningLoader.loadConfigFiles()
    Machines.load()
    Items.load()
    if (event.getSide == Side.CLIENT) {
      Icons.init()
    }
  }

  @EventHandler
  def init(event: FMLInitializationEvent) {
    NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler)
    NetHandler.init()
    if (event.getSide == Side.CLIENT) {
      OverlayRenderHandler.register(WirelessOverlayRender)
      OverlayRenderHandler.register(VisualiserOverlayRender)
      MouseEventHandler.init()
    }
    VisualiserPlayerTracker.init()
    WrenchRegistry.init()
    FMLInterModComms.sendMessage("waila", "register", "net.bdew.ae2stuff.waila.WailaHandler.loadCallback")
    TuningLoader.loadDelayed()
  }

  val onPostInit = Event[FMLPostInitializationEvent]

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) {
    onPostInit.trigger(event)
  }

  @EventHandler
  def onServerStarting(event: FMLServerStartingEvent): Unit = {
    VisualiserPlayerTracker.clear()
  }
}
