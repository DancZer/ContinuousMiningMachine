package net.danczer.excavator;

import net.danczer.excavator.wrapper.*;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcavationLogicTest {

    @Mock
    DancZerItemStack itemStackEmpty;
    @Mock
    DancZerItemStack itemStackAny;
    @Mock
    DancZerItemStack itemStackStoneBlockItem;
    @Mock
    DancZerItemStack itemStackTorchBlockItem0;
    @Mock
    DancZerItemStack itemStackTorchBlockItem1;
    @Mock
    DancZerItemStack itemStackRailBlockItem0;
    @Mock
    DancZerItemStack itemStackRailBlockItem1;
    @Mock
    DancZerItemStack itemStackMiningToolItemAxe;
    @Mock
    DancZerItemStack itemStackMiningToolItemShovel0;
    @Mock
    DancZerItemStack itemStackMiningToolItemShovel1;
    @Mock
    DancZerItemStack itemStackMiningToolItemPickAxe0;
    @Mock
    DancZerItemStack itemStackMiningToolItemPickAxe1;

    @Mock
    DancZerBlockItem stoneBlockItem;
    @Mock
    DancZerBlockItem torchBlockItem0;
    @Mock
    DancZerBlockItem torchBlockItem1;
    @Mock
    DancZerBlockItem railBlockItem0;
    @Mock
    DancZerBlockItem railBlockItem1;
    @Mock
    DancZerMiningToolItem shovelMiningToolItem0;
    @Mock
    DancZerMiningToolItem shovelMiningToolItem1;
    @Mock
    DancZerMiningToolItem pickAxeMiningToolItem0;
    @Mock
    DancZerMiningToolItem pickAxeMiningToolItem1;
    @Mock
    DancZerMiningToolItem axeMiningToolItem;

    @Mock
    DancZerEntity entity;
    @Mock
    DancZerInventory inventory;
    @Mock
    DancZerWorld world;
    @Mock
    ExcavatorConfig config;

    @InjectMocks
    ExcavationLogic logic;

    private void setupInventory(DancZerItemStack[] inventoryItems) {
        when(inventory.size()).thenReturn(inventoryItems.length);
        for (int i = 0; i < inventoryItems.length; i++) {
            when(inventory.getStack(i)).thenReturn(inventoryItems[i]);
        }
    }

    @Test
    void emptyInventory() {
        when(itemStackEmpty.isEmpty()).thenReturn(true);

        setupInventory(new DancZerItemStack[]{
                itemStackEmpty
        });

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.MissingToolchain);
    }

    @Test
    void inventoryWithAny() {
        when(itemStackAny.isEmpty()).thenReturn(false);
        when(itemStackAny.getItem()).thenReturn(new FabricItem() {
            @Override
            public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
                return false;
            }
        });

        setupInventory(new DancZerItemStack[]{
                itemStackAny
        });

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.MissingToolchain);
    }

    @Test
    void inventoryWithStone() {
        when(itemStackStoneBlockItem.isEmpty()).thenReturn(false);
        when(itemStackStoneBlockItem.getItem()).thenReturn(stoneBlockItem);

        setupInventory(new DancZerItemStack[]{
                itemStackStoneBlockItem
        });

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.MissingToolchain);
    }

    @Test
    void inventoryWithWrongTool() {
        when(config.getShovelItems()).thenReturn(List.of(shovelMiningToolItem0, shovelMiningToolItem1));
        when(config.getPickAxeItems()).thenReturn(List.of(pickAxeMiningToolItem0, pickAxeMiningToolItem1));

        when(itemStackMiningToolItemAxe.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemAxe.getItem()).thenReturn(axeMiningToolItem);

        setupInventory(new DancZerItemStack[]{
                itemStackMiningToolItemAxe
        });

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.MissingToolchain);
    }

    @Test
    void inventoryWithToolCount1() {
        when(config.getShovelItems()).thenReturn(List.of(shovelMiningToolItem0, shovelMiningToolItem1));

        when(itemStackMiningToolItemShovel0.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemShovel0.getItem()).thenReturn(shovelMiningToolItem0);

        setupInventory(new DancZerItemStack[]{
                itemStackMiningToolItemShovel0
        });

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.MissingToolchain);
    }

    @Test
    void inventoryWithToolCount2() {
        when(config.getShovelItems()).thenReturn(List.of(shovelMiningToolItem0, shovelMiningToolItem1));
        when(config.getPickAxeItems()).thenReturn(List.of(pickAxeMiningToolItem0, pickAxeMiningToolItem1));

        when(itemStackMiningToolItemShovel0.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemShovel0.getItem()).thenReturn(shovelMiningToolItem0);

        when(itemStackMiningToolItemPickAxe0.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemPickAxe0.getItem()).thenReturn(pickAxeMiningToolItem0);

        setupInventory(new DancZerItemStack[]{
                itemStackMiningToolItemShovel0,
                itemStackMiningToolItemPickAxe0
        });

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.MissingToolchain);
    }

    @Test
    void inventoryWithRequiredTools() {
        when(config.getShovelItems()).thenReturn(List.of(shovelMiningToolItem0, shovelMiningToolItem1));
        when(config.getPickAxeItems()).thenReturn(List.of(pickAxeMiningToolItem0, pickAxeMiningToolItem1));
        when(config.getRailItems()).thenReturn(List.of(railBlockItem0));

        when(itemStackMiningToolItemShovel0.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemShovel0.getItem()).thenReturn(shovelMiningToolItem0);

        when(itemStackMiningToolItemPickAxe0.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemPickAxe0.getItem()).thenReturn(pickAxeMiningToolItem0);

        when(itemStackRailBlockItem0.isEmpty()).thenReturn(false);
        when(itemStackRailBlockItem0.getItem()).thenReturn(railBlockItem0);

        setupInventory(new DancZerItemStack[]{
                itemStackMiningToolItemShovel0,
                itemStackMiningToolItemPickAxe0,
                itemStackRailBlockItem0
        });

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.InventoryIsFull);
    }


    @Test
    void inventoryIsFull() {
        when(itemStackMiningToolItemShovel0.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemShovel0.getItem()).thenReturn(shovelMiningToolItem0);

        when(itemStackMiningToolItemPickAxe0.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemPickAxe0.getItem()).thenReturn(pickAxeMiningToolItem0);

        when(itemStackRailBlockItem0.isEmpty()).thenReturn(false);
        when(itemStackRailBlockItem0.getItem()).thenReturn(railBlockItem0);

        when(itemStackTorchBlockItem0.isEmpty()).thenReturn(false);
        when(itemStackTorchBlockItem0.getItem()).thenReturn(torchBlockItem0);

        when(itemStackStoneBlockItem.isEmpty()).thenReturn(false);
        when(itemStackStoneBlockItem.getItem()).thenReturn(stoneBlockItem);

        setupInventory(new DancZerItemStack[]{
                itemStackStoneBlockItem,
                itemStackStoneBlockItem,
                itemStackMiningToolItemShovel0,
                itemStackMiningToolItemPickAxe0,
                itemStackRailBlockItem0,
                itemStackTorchBlockItem0,
                itemStackStoneBlockItem,
                itemStackMiningToolItemShovel0,
                itemStackMiningToolItemPickAxe0,
                itemStackRailBlockItem0,
                itemStackTorchBlockItem0,
                itemStackStoneBlockItem,
        });

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.MissingToolchain);
    }

    @Test
    void prioritySelection() {
        when(config.getShovelItems()).thenReturn(List.of(shovelMiningToolItem0, shovelMiningToolItem1));
        when(config.getPickAxeItems()).thenReturn(List.of(pickAxeMiningToolItem0, pickAxeMiningToolItem1));
        when(config.getRailItems()).thenReturn(List.of(railBlockItem0, railBlockItem1));
        when(config.getTorchItems()).thenReturn(List.of(torchBlockItem0, torchBlockItem1));

        when(itemStackMiningToolItemShovel0.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemShovel0.getItem()).thenReturn(shovelMiningToolItem0);

        when(itemStackMiningToolItemShovel1.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemShovel1.getItem()).thenReturn(shovelMiningToolItem1);


        when(itemStackMiningToolItemPickAxe0.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemPickAxe0.getItem()).thenReturn(pickAxeMiningToolItem0);

        when(itemStackMiningToolItemPickAxe1.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemPickAxe1.getItem()).thenReturn(pickAxeMiningToolItem1);


        when(itemStackRailBlockItem0.isEmpty()).thenReturn(false);
        when(itemStackRailBlockItem0.getItem()).thenReturn(railBlockItem0);

        when(itemStackRailBlockItem1.isEmpty()).thenReturn(false);
        when(itemStackRailBlockItem1.getItem()).thenReturn(railBlockItem1);


        when(itemStackTorchBlockItem0.isEmpty()).thenReturn(false);
        when(itemStackTorchBlockItem0.getItem()).thenReturn(torchBlockItem0);

        when(itemStackTorchBlockItem1.isEmpty()).thenReturn(false);
        when(itemStackTorchBlockItem1.getItem()).thenReturn(torchBlockItem1);

        setupInventory(new DancZerItemStack[]{
                itemStackMiningToolItemShovel1,
                itemStackMiningToolItemPickAxe1,
                itemStackRailBlockItem1,
                itemStackTorchBlockItem1,
                itemStackMiningToolItemShovel0,
                itemStackMiningToolItemPickAxe0,
                itemStackRailBlockItem0,
                itemStackTorchBlockItem0,
                itemStackMiningToolItemShovel1,
                itemStackMiningToolItemPickAxe1,
                itemStackRailBlockItem1,
                itemStackTorchBlockItem1
        });

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.InventoryIsFull);
    }

    @Test
    void rollingMinecart() {
        when(config.getShovelItems()).thenReturn(List.of(shovelMiningToolItem0, shovelMiningToolItem1));
        when(config.getPickAxeItems()).thenReturn(List.of(pickAxeMiningToolItem0, pickAxeMiningToolItem1));
        when(config.getRailItems()).thenReturn(List.of(railBlockItem0));
        when(config.getTorchItems()).thenReturn(List.of(torchBlockItem0));

        when(itemStackEmpty.isEmpty()).thenReturn(true);

        when(itemStackMiningToolItemShovel0.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemShovel0.getItem()).thenReturn(shovelMiningToolItem0);

        when(itemStackMiningToolItemPickAxe0.isEmpty()).thenReturn(false);
        when(itemStackMiningToolItemPickAxe0.getItem()).thenReturn(pickAxeMiningToolItem0);

        when(itemStackRailBlockItem0.isEmpty()).thenReturn(false);
        when(itemStackRailBlockItem0.getItem()).thenReturn(railBlockItem0);

        when(itemStackTorchBlockItem0.isEmpty()).thenReturn(false);
        when(itemStackTorchBlockItem0.getItem()).thenReturn(torchBlockItem0);

        when(itemStackStoneBlockItem.isEmpty()).thenReturn(false);
        when(itemStackStoneBlockItem.getItem()).thenReturn(stoneBlockItem);

        when(itemStackEmpty.isEmpty()).thenReturn(true);

        var entityPos = new BlockPos(0,0,0);
        var entityPosBlockState = mock(DancZerBlockState.class);

        when(entityPosBlockState.isRailTrack()).thenReturn(false);

        when(entity.getBlockPos()).thenReturn(entityPos);
        when(world.getBlockState(entityPos)).thenReturn(entityPosBlockState);

        setupInventory(new DancZerItemStack[]{
                itemStackMiningToolItemShovel0,
                itemStackMiningToolItemPickAxe0,
                itemStackRailBlockItem0,
                itemStackTorchBlockItem0,
                itemStackStoneBlockItem,
                itemStackEmpty,
        });

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.Rolling);
    }
}