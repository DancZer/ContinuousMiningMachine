package net.danczer.excavator.wrapper;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;

public class DancZerMiningToolItem implements DancZerItem {

    private final MiningToolItem miningToolItem;

    public DancZerMiningToolItem(MiningToolItem miningToolItem) {
        this.miningToolItem = miningToolItem;
    }

    public boolean invokeIsSuitableFor(DancZerBlockState state) {
        return miningToolItem.isSuitableFor(state.getState());
    }

    public float invokeGetMiningSpeedMultiplier(ItemStack stack, DancZerBlockState state) {
        return miningToolItem.getMiningSpeedMultiplier(stack, state.getState());
    }

    public boolean isSuitableFor(DancZerBlockState blockState) {
        return miningToolItem.isSuitableFor(blockState.getState());
    }

    public float getMiningSpeedMultiplier(DancZerItemStack itemStack, DancZerBlockState blockState) {
        return miningToolItem.getMiningSpeedMultiplier(itemStack.getItemStack(), blockState.getState());
    }

    @Override
    public FabricItem getFabricItem() {
        return miningToolItem;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof DancZerItem) && ((DancZerItem) obj).getFabricItem().equals(this.getFabricItem());
    }
}
