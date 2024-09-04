package net.danczer.excavator.wrapper;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DancZerWorld {

    public World getWorld() {
        return world;
    }

    private final World world;

    public DancZerWorld(World world) {
        this.world = world;
    }

    public boolean setBlockState(BlockPos pos, DancZerBlockState state) {
        return world.setBlockState(pos, state.getState());
    }

    public boolean setBlockStateWithDir(BlockPos pos, DancZerBlockState state, Direction direction) {
        if(state.contains(Properties.HORIZONTAL_FACING)){
            return setBlockState(pos, state.with(Properties.HORIZONTAL_FACING, direction));
        }else{
            return setBlockState(pos, state);
        }
    }

    public DancZerBlockState getBlockState(BlockPos pos) {
        return new DancZerBlockState(world.getBlockState(pos));
    }

    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
        world.setBlockBreakingInfo(entityId, pos, progress);
    }

    public void playAxeStripSound(double x, double y, double z){
        world.playSound(0.0, 0.0, 0.0, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
    }

    public void playShovelFlattenSound(double x, double y, double z){
        world.playSound(0.0, 0.0, 0.0, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F, true);
    }

    public void playSound(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
        world.playSound(x, y, z, sound, category, volume, pitch, useDistance);
    }

    public void breakBlock(BlockPos pos, boolean drop) {
        world.breakBlock(pos, drop);
    }
}
