package net.bdew.ae2stuff.network

import net.bdew.lib.network.ItemStackSerialize

case class MsgSetRecipe(recipe: Map[Int, ItemStackSerialize]) extends NetHandler.Message