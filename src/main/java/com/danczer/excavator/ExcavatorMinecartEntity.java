package com.danczer.excavator;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.ContainerMinecartEntity;
import net.minecraft.entity.item.minecart.FurnaceMinecartEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ExcavatorMinecartEntity extends ContainerMinecartEntity implements IHopper {

    private static final DataParameter<Integer> MINING_STATUS = EntityDataManager.createKey(FurnaceMinecartEntity.class, DataSerializers.VARINT);

    private static final Logger LOGGER = LogManager.getLogger();

    private static final double PushForce = 0.02;
    private static final double CollectBlockWithHardness = 3f;

    private final ExcavatorMinecartLogic logic = new ExcavatorMinecartLogic(this);

    private int prevPushMinedBlockCount;
    private int prevPlacedTrackCount;
    private int prevPlacedTorchCount;

    private boolean isBlocked = true;
    private int transferTicker = -1;
    private final BlockPos lastPosition = BlockPos.ZERO;

    public ExcavatorMinecartEntity(FMLPlayMessages.SpawnEntity packet, World worldIn) {
        super(ExcavatorMod.EXCAVATOR_ENTITY, worldIn);
    }

    public ExcavatorMinecartEntity(EntityType<? extends ExcavatorMinecartEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public ExcavatorMinecartEntity(World worldIn, double x, double y, double z) {
        super(ExcavatorMod.EXCAVATOR_ENTITY, x, y, z, worldIn);
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(MINING_STATUS, ExcavatorMinecartLogic.MiningStatus.Rolling.ordinal());
    }

    public AbstractMinecartEntity.Type getMinecartType() {
        return null;
    }

    public BlockState getDefaultDisplayTile() {
        return Blocks.OBSERVER.getDefaultState();
    }

    public int getDefaultDisplayTileOffset() {
        return 1;
    }

    public int getSizeInventory() {
        return ExcavatorContainer.InventorySize;
    }

    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
        boolean flag = !receivingPower;
        if (flag != this.getBlocked()) {
            this.setBlocked(flag);
        }
    }

    public boolean getBlocked() {
        return this.isBlocked;
    }

    /**
     * Set whether this hopper minecart is being blocked by an activator rail.
     */
    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
    }

    /**
     * Returns the worldObj for this tileEntity.
     */
    public World getWorld() {
        return this.world;
    }

    public double getXPos() {
        return this.getPosX();
    }

    /**
     * Gets the world Y position for this hopper entity.
     */
    public double getYPos() {
        return this.getPosY() + 0.5D;
    }

    /**
     * Gets the world Z position for this hopper entity.
     */
    public double getZPos() {
        return this.getPosZ();
    }

    public boolean isInventoryFull() {
        if (isEmpty()) {
            return false;
        }

        for (int i = 0; i < ExcavatorContainer.InventorySize; i++) {
            ItemStack itemStack = getStackInSlot(i);

            if (itemStack.getItem() == logic.getRailTypeItem()) continue;
            if (itemStack.getItem() == logic.getTorchTypeItem()) continue;

            if (itemStack.isEmpty() || itemStack.getCount() < itemStack.getMaxStackSize()) return false;
        }

        return true;
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        logic.writeAdditional(compound);

        compound.putInt("TransferCooldown", this.transferTicker);
        compound.putBoolean("Enabled", this.isBlocked);
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        logic.readAdditional(compound);

        this.transferTicker = compound.getInt("TransferCooldown");
        this.isBlocked = compound.contains("Enabled") ? compound.getBoolean("Enabled") : true;
    }

    public void tick() {
        logic.setRailTypeItem((BlockItem)Blocks.RAIL.asItem());
        logic.setTorchTypeItem((BlockItem)Blocks.WALL_TORCH.asItem());

        excavatorTick();

        super.tick();

        hopperTick();
    }

    private boolean isCreativeMode() {
        return world.getServer() != null && world.getServer().getGameType() == GameType.CREATIVE;
    }

    private void excavatorTick() {
        if (!this.world.isRemote && this.isAlive() && this.getBlocked()) {
            boolean isFull = isInventoryFull();

            boolean hasRails = true;
            boolean hasTorch = true;

            if (!isCreativeMode()) {
                hasRails = hasInventoryItem(logic.getRailTypeItem());
                hasTorch = hasInventoryItem(logic.getTorchTypeItem());
            }

            LOGGER.debug("isFull: " + isFull);
            LOGGER.debug("hasRails: " + hasRails);
            LOGGER.debug("hasTorch: " + hasTorch);

            if (!isFull && hasRails && hasTorch) {
                logic.tick();

                setMiningStatus(logic.pathMiningStatus);

                LOGGER.debug("Logic Hazard: " + logic.pathMiningStatus);

                if (logic.pathMiningStatus == ExcavatorMinecartLogic.MiningStatus.MiningInProgress) {
                    if (prevPushMinedBlockCount != logic.getMinedBlockCount()) {
                        prevPushMinedBlockCount = logic.getMinedBlockCount();
                        //push it a bit to the direction
                        setMotion(logic.getDirectoryVector().scale(PushForce));
                        LOGGER.debug("Logic setMotion: Push");
                    } else {
                        LOGGER.debug("Logic setMotion: leave");
                    }
                } else if (logic.pathMiningStatus != ExcavatorMinecartLogic.MiningStatus.Rolling){
                    LOGGER.debug("Logic setMotion: ZERO");
                    setMotion(Vector3d.ZERO);
                }
            } else {
                if (!hasRails || !hasTorch) {
                    setMiningStatus(ExcavatorMinecartLogic.MiningStatus.DepletedConsumable);
                } else {
                    setMiningStatus(ExcavatorMinecartLogic.MiningStatus.Rolling);
                }
            }

            if (!isCreativeMode()) {
                if (prevPlacedTorchCount != logic.getPlacedTorchCount()) {
                    prevPlacedTorchCount = logic.getPlacedTorchCount();

                    reduceInventoryItem(logic.getTorchTypeItem());
                }

                if (prevPlacedTrackCount != logic.getPlacedTrackCount()) {
                    prevPlacedTrackCount = logic.getPlacedTrackCount();

                    reduceInventoryItem(logic.getRailTypeItem());
                }
            }
        }

        if (rand.nextInt(4) == 0) {
            ShowMiningStatus();
        }
    }

    private void reduceInventoryItem(Item item) {
        for (int i = 0; i < ExcavatorContainer.InventorySize; i++) {
            ItemStack itemStack = getStackInSlot(i);

            if (!itemStack.isEmpty() && itemStack.getItem() == item) {
                itemStack.shrink(1);
                return;
            }
        }
    }

    private boolean hasInventoryItem(Item item) {
        boolean found = false;
        for (int i = 0; i < ExcavatorContainer.InventorySize; i++) {
            ItemStack itemStack = getStackInSlot(i);

            if (!itemStack.isEmpty() && itemStack.getItem() == item && !found) {
                found = true;
            }
        }

        return found;
    }

    private void ShowMiningStatus() {
        ExcavatorMinecartLogic.MiningStatus miningStatus = getMiningStatus();
        LOGGER.debug("Hazard: " + miningStatus);

        IParticleData particleType = null;

        switch (miningStatus) {
            case MiningInProgress:
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
            case DepletedConsumable:
                particleType = ParticleTypes.WITCH;
                break;
            case Rolling:
            default:
                break;
        }

        if (particleType != null) {
            this.world.addParticle(particleType, this.getPosX(), this.getPosY() + 0.8D, this.getPosZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    private ExcavatorMinecartLogic.MiningStatus getMiningStatus() {
        return ExcavatorMinecartLogic.MiningStatus.Find(this.dataManager.get(MINING_STATUS));
    }

    private void setMiningStatus(ExcavatorMinecartLogic.MiningStatus miningStatus) {
        this.dataManager.set(MINING_STATUS, miningStatus.Value);
    }

    private void hopperTick() {
        if (!this.world.isRemote && this.isAlive() && this.getBlocked()) {
            BlockPos blockpos = this.getPosition();
            if (blockpos.equals(this.lastPosition)) {
                --this.transferTicker;
            } else {
                this.setTransferTicker(0);
            }

            if (!this.canTransfer()) {
                this.setTransferTicker(0);
                if (this.captureDroppedItems()) {
                    this.setTransferTicker(4);
                    this.markDirty();
                }
            }
        }
    }

    public void setTransferTicker(int transferTickerIn) {
        this.transferTicker = transferTickerIn;
    }

    /**
     * Returns whether the hopper cart can currently transfer an item.
     */
    public boolean canTransfer() {
        return this.transferTicker > 0;
    }

    public boolean captureDroppedItems() {
        if (HopperTileEntity.pullItems(this)) {
            return true;
        } else {
            List<ItemEntity> list = this.world.getEntitiesWithinAABB(ItemEntity.class, this.getBoundingBox().grow(0.25D, 0.0D, 0.25D), EntityPredicates.IS_ALIVE);

            for (ItemEntity itemEntity : list) {
                Item item = itemEntity.getItem().getItem();

                //collect only usefull blocks
                if (item instanceof BlockItem) {
                    BlockItem blockItem = (BlockItem) item;
                    BlockState blockState = blockItem.getBlock().getDefaultState();

                    if (blockState.getRequiresTool() && blockState.getBlockHardness(world, getPosition()) >= CollectBlockWithHardness) {
                        HopperTileEntity.captureItem(this, itemEntity);
                    }
                } else {
                    HopperTileEntity.captureItem(this, itemEntity);
                }
            }

            return false;
        }
    }

    public void killMinecart(DamageSource source) {
        super.killMinecart(source);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.entityDropItem(Blocks.OBSERVER);
            this.entityDropItem(Blocks.REDSTONE_BLOCK);
            this.entityDropItem(Blocks.HOPPER);
        }
    }

    public Container createContainer(int id, PlayerInventory playerInventoryIn) {
        return new ExcavatorContainer(id, playerInventoryIn, this);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
