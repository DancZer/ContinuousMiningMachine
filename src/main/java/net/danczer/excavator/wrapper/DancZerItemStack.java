package net.danczer.excavator.wrapper;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.item.*;

public class DancZerItemStack {
    private final ItemStack itemStack;

    public DancZerItemStack(DancZerItem item) {
        this.itemStack = new ItemStack((Item)item.getFabricItem());
    }

    public DancZerItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public DancZerItem getItem() {
        FabricItem item = itemStack.getItem();

        if (item instanceof MiningToolItem) {
            return new DancZerMiningToolItem((MiningToolItem) item);
        } else if (item instanceof BlockItem) {
            return new DancZerBlockItem((BlockItem) item);
        } else {
            return null;
        }
    }

    public boolean isEmpty() {
        return itemStack.isEmpty();
    }

    public int getCount() {
        return itemStack.getCount();
    }

    public int getMaxCount() {
        return itemStack.getMaxCount();
    }

    public void split(int amount) {
        itemStack.split(amount);
    }
}
