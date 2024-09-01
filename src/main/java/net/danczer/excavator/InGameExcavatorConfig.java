package net.danczer.excavator;

import net.danczer.excavator.wrapper.DancZerBlock;
import net.danczer.excavator.wrapper.DancZerBlockItem;
import net.danczer.excavator.wrapper.DancZerMiningToolItem;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;

import java.util.ArrayList;
import java.util.List;

public class InGameExcavatorConfig implements ExcavatorConfig{
    private static final List<DancZerBlockItem> USABLE_RAIL_ITEMS = new ArrayList<>();
    private static final List<DancZerBlockItem> USABLE_TORCH_ITEMS = new ArrayList<>();
    private static final List<DancZerBlock> TORCH_WALL_BLOCKS = new ArrayList<>();
    private static final List<DancZerMiningToolItem> USABLE_PICKAXE_ITEMS = new ArrayList<>();
    private static final List<DancZerMiningToolItem> USABLE_SHOVEL_ITEMS = new ArrayList<>();

    static {
        USABLE_TORCH_ITEMS.add(new DancZerBlockItem((BlockItem) Items.TORCH));
        USABLE_TORCH_ITEMS.add(new DancZerBlockItem((BlockItem)Items.REDSTONE_TORCH));
        USABLE_TORCH_ITEMS.add(new DancZerBlockItem((BlockItem)Items.SOUL_TORCH));

        TORCH_WALL_BLOCKS.add(new DancZerBlock(Blocks.WALL_TORCH));
        TORCH_WALL_BLOCKS.add(new DancZerBlock(Blocks.REDSTONE_WALL_TORCH));
        TORCH_WALL_BLOCKS.add(new DancZerBlock(Blocks.SOUL_WALL_TORCH));

        USABLE_RAIL_ITEMS.add(new DancZerBlockItem((BlockItem)Items.RAIL));
        USABLE_RAIL_ITEMS.add(new DancZerBlockItem((BlockItem)Items.POWERED_RAIL));

        USABLE_PICKAXE_ITEMS.add(new DancZerMiningToolItem((MiningToolItem)Items.NETHERITE_PICKAXE));
        USABLE_PICKAXE_ITEMS.add(new DancZerMiningToolItem((MiningToolItem)Items.DIAMOND_PICKAXE));
        USABLE_PICKAXE_ITEMS.add(new DancZerMiningToolItem((MiningToolItem)Items.GOLDEN_PICKAXE));
        USABLE_PICKAXE_ITEMS.add(new DancZerMiningToolItem((MiningToolItem)Items.IRON_PICKAXE));
        USABLE_PICKAXE_ITEMS.add(new DancZerMiningToolItem((MiningToolItem)Items.STONE_PICKAXE));
        USABLE_PICKAXE_ITEMS.add(new DancZerMiningToolItem((MiningToolItem)Items.WOODEN_PICKAXE));

        USABLE_SHOVEL_ITEMS.add(new DancZerMiningToolItem((MiningToolItem)Items.NETHERITE_SHOVEL));
        USABLE_SHOVEL_ITEMS.add(new DancZerMiningToolItem((MiningToolItem)Items.DIAMOND_SHOVEL));
        USABLE_SHOVEL_ITEMS.add(new DancZerMiningToolItem((MiningToolItem)Items.IRON_SHOVEL));
        USABLE_SHOVEL_ITEMS.add(new DancZerMiningToolItem((MiningToolItem)Items.GOLDEN_SHOVEL));
        USABLE_SHOVEL_ITEMS.add(new DancZerMiningToolItem((MiningToolItem)Items.STONE_SHOVEL));
        USABLE_SHOVEL_ITEMS.add(new DancZerMiningToolItem((MiningToolItem)Items.WOODEN_SHOVEL));
    }

    @Override
    public List<DancZerBlockItem> getTorchItems() {
        return USABLE_TORCH_ITEMS;
    }

    @Override
    public List<DancZerBlock> getWallTorchBlocks() {
        return TORCH_WALL_BLOCKS;
    }

    @Override
    public List<DancZerBlockItem> getRailItems() {
        return USABLE_RAIL_ITEMS;
    }

    @Override
    public List<DancZerMiningToolItem> getPickAxeItems() {
        return USABLE_PICKAXE_ITEMS;
    }

    @Override
    public List<DancZerMiningToolItem> getShovelItems() {
        return USABLE_SHOVEL_ITEMS;
    }

    @Override
    public void validate() {
        if(assertListContainsNotNull(USABLE_TORCH_ITEMS)){
            throw new NullPointerException("Invalid Torch in the usable list for excavator!");
        }

        if(assertListContainsNotNull(USABLE_RAIL_ITEMS)){
            throw new NullPointerException("Invalid Rail in the usable list for excavator!");
        }

        if(assertListContainsNotNull(USABLE_PICKAXE_ITEMS)){
            throw new NullPointerException("Invalid Pickaxe in the usable list for excavator!");
        }

        if(assertListContainsNotNull(USABLE_SHOVEL_ITEMS)){
            throw new NullPointerException("Invalid Shovel in the usable list for excavator!");
        }
    }

    private <T> boolean assertListContainsNotNull(List<T> list) {
        for (Object obj: list) {
            if(obj == null) return true;
        }

        return false;
    }
}
