package net.danczer.excavator.wrapper;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DancZerItemStack {
    private final ItemStack itemStack;

    public DancZerItemStack(FabricItem item){
        this.itemStack = new ItemStack((Item)item);
    }

    public DancZerItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public FabricItem getItem() {
        return itemStack.getItem();
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
