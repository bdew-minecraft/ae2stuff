package net.bdew.ae2stuff

import java.io.{File, FileWriter}

import net.bdew.lib.recipes.gencfg.{ConfigSection, GenericConfigLoader, GenericConfigParser}
import net.bdew.lib.recipes.{RecipeLoader, RecipeParser, RecipesHelper}

object Tuning extends ConfigSection

object TuningLoader {

  class Parser extends RecipeParser with GenericConfigParser

  val loader = new RecipeLoader with GenericConfigLoader {
    val cfgStore = Tuning
    override def newParser() = new Parser
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

