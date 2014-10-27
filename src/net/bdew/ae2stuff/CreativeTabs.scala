package net.bdew.ae2stuff

import net.bdew.ae2stuff.machines.encoder.BlockEncoder
import net.bdew.lib.CreativeTabContainer
import net.minecraft.item.Item

object CreativeTabs extends CreativeTabContainer {
  val main = new Tab("bdew.ae2stuff", Item.getItemFromBlock(BlockEncoder))
}
