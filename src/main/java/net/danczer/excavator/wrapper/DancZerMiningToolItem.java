package net.danczer.excavator.wrapper;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;

public class DancZerMiningToolItem implements FabricItem {

    private final MiningToolItem miningToolItem;

    public DancZerMiningToolItem(MiningToolItem miningToolItem) {
        this.miningToolItem = miningToolItem;
    }

    public MiningToolItem getMiningToolItem() {
        return miningToolItem;
    }

    public boolean invokeIsSuitableFor(DancZerBlockState state)
    {
        return miningToolItem.isSuitableFor(state.getState());
    }

    public float invokeGetMiningSpeedMultiplier(ItemStack stack, DancZerBlockState state){
        return miningToolItem.getMiningSpeedMultiplier(stack, state.getState());
    }

    public boolean isSuitableFor(DancZerBlockState blockState) {
        return miningToolItem.isSuitableFor(blockState.getState());
    }

    public float getMiningSpeedMultiplier(DancZerItemStack itemStack, DancZerBlockState blockState) {
        return miningToolItem.getMiningSpeedMultiplier(itemStack.getItemStack(), blockState.getState());
    }
}
