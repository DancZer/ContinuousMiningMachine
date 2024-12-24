package net.danczer.excavator.wrapper;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.item.BlockItem;

public class DancZerBlockItem implements DancZerItem {
    private final BlockItem blockItem;

    public DancZerBlockItem(BlockItem blockItem) {
        this.blockItem = blockItem;
    }

    public DancZerBlock getBlock(){
        return new DancZerBlock(blockItem.getBlock());
    }

    @Override
    public FabricItem getFabricItem() {
        return blockItem;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof DancZerItem) && ((DancZerItem) obj).getFabricItem().equals(this.getFabricItem());
    }
}
