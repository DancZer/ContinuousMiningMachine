package net.danczer.excavator.wrapper;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class DancZerInventory {
    private final Inventory inventory;

    public DancZerInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int size() {
        return inventory.size();
    }

    public DancZerItemStack getStack(int slot) {
        return new DancZerItemStack(inventory.getStack(slot));
    }
}
