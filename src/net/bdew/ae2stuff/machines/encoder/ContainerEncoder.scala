package net.bdew.ae2stuff.machines.encoder

import net.bdew.lib.gui.{BaseContainer, SlotValidating}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager

class ContainerEncoder(val te: TileEncoder, player: EntityPlayer) extends BaseContainer(te) {

  for (x <- 0 until 3; y <- 0 until 3) {
    addSlotToContainer(new SlotFakeCrafting(te, te.slots.recipe(x + y * 3), 10 + x * 18, 17 + y * 18, updateRecipe))
  }

  addSlotToContainer(new SlotFakeCraftingResult(te, te.slots.result, 104, 35))
  addSlotToContainer(new SlotValidating(te, te.slots.patterns, 143, 17))
  addSlotToContainer(new SlotFakeEncodedPattern(te, te.slots.encoded, 143, 53))

  bindPlayerInventory(player.inventory, 8, 84, 142)

  def updateRecipe() {
    val c = new InventoryCrafting(this, 3, 3)
    for (i <- 0 until 9)
      c.setInventorySlotContents(i, te.getStackInSlot(te.slots.recipe(i)))
    val r = CraftingManager.getInstance.findMatchingRecipe(c, te.getWorldObj)
    te.setInventorySlotContents(te.slots.result, r)
  }

  override def slotClick(slotNum: Int, button: Int, modifiers: Int, player: EntityPlayer): ItemStack = {
    // This is a hacky workaround!
    // When a player changes the contents of a slot, isChangingQuantityOnly is set to true,
    // preventing updates to OTHER slots from being detected and sent back
    // Here i ensure changes are sent back before returning so NetHandlerPlayServer.processClickWindow doesn't
    // get the opportunity to mess things up
    val r = super.slotClick(slotNum, button, modifiers, player)
    detectAndSendChanges()
    r
  }

  override def detectAndSendChanges() {
    super.detectAndSendChanges()
    if (!te.getWorldObj.isRemote) {
      if (!te.getNode.isActive) {
        player.closeScreen()
      }
    }
  }

  override def canInteractWith(player: EntityPlayer) = te.isUseableByPlayer(player)
}
