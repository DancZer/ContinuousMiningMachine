package net.danczer.excavator.wrapper;


import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;

public class DancZerBlock {

    private final Block block;

    public DancZerBlock(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    public DancZerBlockState getDefaultState() {
        return new DancZerBlockState(block.getDefaultState());
    }

    public boolean isRail(){
        return block instanceof AbstractRailBlock;
    }
}
