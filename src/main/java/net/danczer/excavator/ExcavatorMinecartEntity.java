package net.danczer.excavator;

import com.mojang.logging.LogUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;

public class ExcavatorMinecartEntity extends StorageMinecartEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final TrackedData<Integer> MINING_STATUS;

    static {
        MINING_STATUS = DataTracker.registerData(ExcavatorMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }
    private static final float MinecartPushForce = 0.005f;

    private final ExcavationLogic excavationLogic = new ExcavationLogic(this, this);
    private boolean enabled = true;
    private int transferTicker = -1;

    public ExcavatorMinecartEntity(EntityType<? extends StorageMinecartEntity> entityType, World world) {
        super(entityType, world);
    }

    private ExcavatorMinecartEntity(World world, double x, double y, double z) {
        super(ExcavatorMod.EXCAVATOR_ENTITY, x, y, z, world);
    }

    public static ExcavatorMinecartEntity create(World world, double x, double y, double z){
        return new ExcavatorMinecartEntity(world, x, y, z);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(MINING_STATUS, 0);
    }

    public AbstractMinecartEntity.Type getMinecartType() {
        return null;
    }

    protected Item getItem() {
        return ExcavatorMod.EXCAVATOR_MINECART_ITEM;
    }

    public ItemStack getPickBlockStack()
    {
        return new ItemStack(getItem());
    }

    public BlockState getDefaultContainedBlock() {
        return Blocks.REDSTONE_BLOCK.getDefaultState();
    }

    public int getDefaultBlockOffset() {
        return 1;
    }

    public int size() {
        return ExcavatorScreenHandler.InventorySize;
    }

    public ExcavatorScreenHandler getScreenHandler(int id, PlayerInventory playerInventoryIn) {
        return new ExcavatorScreenHandler(id, playerInventoryIn, this);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public World getWorld() {
        return this.world;
    }

    public void onActivatorRail(int x, int y, int z, boolean powered) {
        boolean bl = !powered;
        if (bl != this.isEnabled()) {
            this.setEnabled(bl);
        }
    }

    public void tick() {
        excavatorTick();
    }

    private void excavatorTick() {
        ExcavationLogic.MiningStatus prevStatus = excavationLogic.miningStatus;

        if (!this.world.isClient && this.isAlive() && this.isEnabled()) {
            excavationLogic.updateExcavatorToolchain();

            excavationLogic.tick();

            setMiningStatus(excavationLogic.miningStatus);
        }

        if(excavationLogic.miningStatus == ExcavationLogic.MiningStatus.Rolling){
            if(prevStatus == ExcavationLogic.MiningStatus.Mining){
                LOGGER.debug("Minecart Pushed");
                setVelocity(excavationLogic.getDirectoryVector().multiply(MinecartPushForce));
            }
            super.tick();
        }else{
            setVelocity(Vec3d.ZERO);
        }

        if (this.random.nextInt(4) == 0) {
            showMiningStatus();
        }
    }

    private void showMiningStatus() {
        ExcavationLogic.MiningStatus miningStatus = getMiningStatus();
        LOGGER.debug("Hazard: " + miningStatus);

        ParticleEffect particleType = null;

        switch (miningStatus) {
            case Mining:
                particleType = ParticleTypes.LARGE_SMOKE;
                break;
            case HazardCliff:
                particleType = ParticleTypes.ENTITY_EFFECT;
                break;
            case HazardLava:
                particleType = ParticleTypes.FALLING_LAVA;
                break;
            case HazardWater:
                particleType = ParticleTypes.FALLING_WATER;
                break;
            case HazardUnknownFluid:
                particleType = ParticleTypes.BUBBLE;
                break;
            case MissingToolchain:
                particleType = ParticleTypes.WITCH;
                break;
            case InventoryIsFull:
                particleType = ParticleTypes.FALLING_HONEY;
                break;
            case EmergencyStop:
                particleType = ParticleTypes.COMPOSTER;
                break;
            case Rolling:
            default:
                break;
        }

        if (particleType != null) {
            this.world.addParticle(particleType, this.getX(), this.getY() + 0.8D, this.getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    private ExcavationLogic.MiningStatus getMiningStatus() {
        return ExcavationLogic.MiningStatus.Find(this.getDataTracker().get(MINING_STATUS));
    }

    private void setMiningStatus(ExcavationLogic.MiningStatus miningStatus) {
        this.dataTracker.set(MINING_STATUS, miningStatus.Value);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound compound) {
        excavationLogic.writeNbt(compound);

        compound.putInt("TransferCooldown", this.transferTicker);
        compound.putBoolean("Enabled", this.enabled);

        return super.writeNbt(compound);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);

        excavationLogic.readNbt(compound);

        this.transferTicker = compound.getInt("TransferCooldown");
        this.enabled = !compound.contains("Enabled") || compound.getBoolean("Enabled");
    }
}
