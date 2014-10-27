package net.bdew.ae2stuff.network

case class MsgSetRecipe(nbt: SerializableNBT) extends NetHandler.Message
