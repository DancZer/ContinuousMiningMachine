package net.danczer.excavator.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DancZerEntity {
    private final Entity entity;

    public DancZerEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public BlockPos getBlockPos() {
        return entity.getBlockPos();
    }

    public Vec3d getVelocity() {
        return entity.getVelocity();
    }
}
