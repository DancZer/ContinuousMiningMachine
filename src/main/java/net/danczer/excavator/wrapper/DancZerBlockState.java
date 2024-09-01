package net.danczer.excavator.wrapper;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


public class DancZerBlockState {

    private final BlockState state;

    public DancZerBlockState(BlockState state) {
        this.state = state;
    }

    public BlockState getState() {
        return state;
    }

    public DancZerBlockState rotate(BlockRotation railRotation) {
        return new DancZerBlockState(state.rotate(railRotation));
    }

    public DancZerBlock getBlock() {
        return new DancZerBlock(state.getBlock());
    }

    public RailShape getRailShape() {
        Block block = state.getBlock();

        if (block instanceof AbstractRailBlock railBlock) {
            return state.get(railBlock.getShapeProperty());
        } else {
            return RailShape.NORTH_SOUTH;
        }
    }

    public boolean isRailTrack() {
        return state.isIn(BlockTags.RAILS);
    }

    public boolean isBlockHarvested(DancZerWorld world, BlockPos pos) {
        return state.getCollisionShape(world.getWorld(), pos).isEmpty() || isRailTrack();
    }

    public boolean isSign() {
        return state.isIn(BlockTags.SIGNS) || state.isIn(BlockTags.WALL_SIGNS) || state.isIn(BlockTags.STANDING_SIGNS);
    }

    public boolean contains(DirectionProperty horizontalFacing) {
        return state.contains(horizontalFacing);
    }

    public DancZerBlockState with(DirectionProperty property, Direction value) {
        return new DancZerBlockState(state.with(property, value));
    }

    public boolean isAir() {
        return state.isAir();
    }

    private FluidState getFluidState() {
        return state.getFluidState();
    }

    public boolean isFluid(){
        return !getFluidState().isEmpty();
    }

    public boolean isLava(){
        return getFluidState().isIn(FluidTags.LAVA);
    }
    public boolean isWater(){
        return getFluidState().isIn(FluidTags.WATER);
    }

    public float getHardness(DancZerWorld world, BlockPos miningPos) {
        return state.getHardness(world.getWorld(), miningPos);
    }

    public boolean isToolRequired() {
        return state.isToolRequired();
    }

    public boolean isOf(DancZerBlock block) {
        return state.isOf(block.getBlock());
    }
}
