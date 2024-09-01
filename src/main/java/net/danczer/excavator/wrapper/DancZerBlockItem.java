package net.danczer.excavator.wrapper;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.item.BlockItem;

public class DancZerBlockItem implements FabricItem {
    private final BlockItem blockItem;

    public DancZerBlockItem(BlockItem blockItem) {
        this.blockItem = blockItem;
    }

    public BlockItem getBlockItem() {
        return blockItem;
    }

    public DancZerBlock getBlock(){
        return new DancZerBlock(blockItem.getBlock());
    }
}
