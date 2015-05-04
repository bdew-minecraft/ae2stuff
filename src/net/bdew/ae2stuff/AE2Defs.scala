package net.bdew.ae2stuff

import appeng.api.AEApi

object AE2Defs {
  lazy val definitions = AEApi.instance().definitions()
  lazy val materials = definitions.materials()
  lazy val blocks = definitions.blocks()
  lazy val items = definitions.items()
  lazy val parts = definitions.parts()

  private def call(o: Object, m: String) = o.getClass.getMethod(m).invoke(o)

  def part(name: String) = call(parts, name)

  def material(name: String) = call(materials, name)
}
