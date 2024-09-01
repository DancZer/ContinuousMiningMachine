package net.danczer.excavator.wrapper;

import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
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

    public boolean setBlockState(BlockPos pos, DancZerBlockState state, int flags) {
        return world.setBlockState(pos, state.getState(), flags);
    }

    public DancZerBlockState getBlockState(BlockPos pos) {
        return new DancZerBlockState(world.getBlockState(pos));
    }

    public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
        world.setBlockBreakingInfo(entityId, pos, progress);
    }

    public void playSound(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
        world.playSound(x, y, z, sound, category, volume, pitch, useDistance);
    }

    public void breakBlock(BlockPos pos, boolean drop) {
        world.breakBlock(pos, drop);
    }
}
