/*
 * Copyright (c) bdew, 2014
 * https://github.com/bdew/ae2stuff
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.ae2stuff

import java.io.{File, FileWriter}

import appeng.api.AEApi
import appeng.api.util.{AEColor, AEColoredItemDefinition, AEItemDefinition}
import net.bdew.lib.recipes.gencfg.{ConfigSection, GenericConfigLoader, GenericConfigParser}
import net.bdew.lib.recipes.{RecipeLoader, RecipeParser, RecipesHelper, StackRef}

object Tuning extends ConfigSection

object TuningLoader {

  case class StackMaterial(name: String) extends StackRef

  case class StackPart(name: String) extends StackRef

  case class StackPartColored(name: String, color: String) extends StackRef

  class Parser extends RecipeParser with GenericConfigParser {
    def specMaterial = "M" ~> ":" ~> ident ^^ StackMaterial
    def specPart = "P" ~> ":" ~> ident ^^ StackPart
    def specPartColored = "C" ~> ":" ~> ident ~ "/" ~ ident ^^ { case name ~ sl ~ color => StackPartColored(name, color)}
    override def spec = specMaterial | specPart | specPartColored | super.spec
  }

  val loader = new RecipeLoader with GenericConfigLoader {
    val cfgStore = Tuning
    override def newParser() = new Parser

    val materials = AEApi.instance().materials()
    val parts = AEApi.instance().parts()

    override def getConcreteStack(s: StackRef, cnt: Int) = s match {
      case StackMaterial(name) =>
        materials.getClass.getField("material" + name).get(materials).asInstanceOf[AEItemDefinition].stack(cnt)
      case StackPart(name) =>
        parts.getClass.getField("part" + name).get(parts).asInstanceOf[AEItemDefinition].stack(cnt)
      case StackPartColored(name, colorName) =>
        val color = AEColor.valueOf(colorName)
        parts.getClass.getField("part" + name).get(parts).asInstanceOf[AEColoredItemDefinition].stack(color, cnt)
      case _ => super.getConcreteStack(s, cnt)
    }
  }

  def loadDelayed() = loader.processRecipeStatements()

  def loadConfigFiles() {
    if (!AE2Stuff.configDir.exists()) {
      AE2Stuff.configDir.mkdir()
      val nl = System.getProperty("line.separator")
      val f = new FileWriter(new File(AE2Stuff.configDir, "readme.txt"))
      f.write("Any .cfg files in this directory will be loaded after the internal configuration, in alphabetic order" + nl)
      f.write("Files in 'overrides' directory with matching names cab be used to override internal configuration" + nl)
      f.close()
    }

    RecipesHelper.loadConfigs(
      modName = "AE2 Stuff",
      listResource = "/assets/ae2stuff/config/files.lst",
      configDir = AE2Stuff.configDir,
      resBaseName = "/assets/ae2stuff/config/",
      loader = loader)
  }
}

