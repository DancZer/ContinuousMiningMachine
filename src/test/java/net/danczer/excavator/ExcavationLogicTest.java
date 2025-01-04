package net.danczer.excavator;

import net.danczer.excavator.wrapper.*;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExcavationLogicTest {

    private enum TestBlockType {
        Air, Rail, Sand, Dirt, Rock, Steel, Diamond, Obsidian, Bedrock, Lava, Fluid, Water
    }

    private final MinecartTestBlockLayer BlockLayerRock = new MinecartTestBlockLayer(new TestBlockType[]{
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
    });
    private final MinecartTestBlockLayer BlockLayerWithRail = new MinecartTestBlockLayer(new TestBlockType[]{
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rail, TestBlockType.Rail, TestBlockType.Rail, TestBlockType.Rail, TestBlockType.Rail,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
    });
    private final MinecartTestBlockLayer BlockLayerWithSingleRail = new MinecartTestBlockLayer(new TestBlockType[]{
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rail, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
    });

    private final MinecartTestBlockLayer BlockLayerAir = new MinecartTestBlockLayer(new TestBlockType[]{
            TestBlockType.Air, TestBlockType.Air, TestBlockType.Air, TestBlockType.Air, TestBlockType.Air,
            TestBlockType.Air, TestBlockType.Air, TestBlockType.Air, TestBlockType.Air, TestBlockType.Air,
            TestBlockType.Air, TestBlockType.Air, TestBlockType.Air, TestBlockType.Air, TestBlockType.Air,
            TestBlockType.Air, TestBlockType.Air, TestBlockType.Air, TestBlockType.Air, TestBlockType.Air,
            TestBlockType.Air, TestBlockType.Air, TestBlockType.Air, TestBlockType.Air, TestBlockType.Air,
    });


    private final MinecartTestBlockLayer BlockLayerMinedTunnel = new MinecartTestBlockLayer(new TestBlockType[]{
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Air, TestBlockType.Air, TestBlockType.Air, TestBlockType.Air, TestBlockType.Air,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
            TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock, TestBlockType.Rock,
    });

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
    DancZerBlock railDefaultBlock;
    @Mock
    DancZerBlockState railDefaultBlockState;


    @Mock
    DancZerBlock torchDefaultBlock;
    @Mock
    DancZerBlockState torchDefaultBlockState;

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

    Set<BlockPos> removedBlocks = new HashSet<>();

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
        when(itemStackAny.getItem()).thenReturn(new DancZerItem() {
            @Override
            public FabricItem getFabricItem() {
                return null;
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
    void shouldRollWhenNothingToMine() {
        setupMinecartForRolling();

        MinecartTestBlockArea area = new MinecartTestBlockArea(
                BlockLayerRock,
                BlockLayerWithRail,
                BlockLayerAir,
                BlockLayerAir,
                BlockLayerAir);

        area.initializeMock(world, removedBlocks);

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.Rolling);
    }

    @ParameterizedTest
    @MethodSource("hazardFixtureFoundNear")
    void shouldStopWhenHazardFoundNear(TestBlockType blockType, int x, int y, int z, ExcavationLogic.MiningStatus expectedStatus) {
        setupMinecartForRolling();

        MinecartTestBlockArea area = new MinecartTestBlockArea(
                BlockLayerRock,
                BlockLayerWithRail,
                BlockLayerAir,
                BlockLayerAir,
                BlockLayerAir);

        area.getLayer(y).setBlockType(blockType, x, z);

        area.initializeMock(world, removedBlocks);

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(expectedStatus);
    }

    @ParameterizedTest
    @MethodSource("hazardFixtureFoundFar")
    void shouldStopWhenHazardFoundFar(TestBlockType blockType, int x, int y, int z, ExcavationLogic.MiningStatus expectedStatus) {
        setupMinecartForRolling();

        MinecartTestBlockArea area = new MinecartTestBlockArea(
                BlockLayerRock,
                BlockLayerWithSingleRail,
                BlockLayerAir,
                BlockLayerAir,
                BlockLayerAir);

        area.getLayer(y).setBlockType(blockType, x, z);

        area.initializeMock(world, removedBlocks);

        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(expectedStatus);
    }


    /**             -2-1 0 1 2
     *  3 XX XX      X X X   X
     *  2 X   X      X X X   X
     *  1 X   X      X X X   X
     *  0 X   X      X X X   X
     * -1 X   X      X X X X X
     */
    static List<Arguments> hazardFixtureFoundNear() {
        List<Arguments> fixture = new ArrayList<>();

        final int x = 1;
        final int maxY = 3;

        fixture.add(Arguments.of(TestBlockType.Fluid, x, -1, 0, ExcavationLogic.MiningStatus.HazardUnknownFluid));
        fixture.add(Arguments.of(TestBlockType.Lava, x, -1, 0, ExcavationLogic.MiningStatus.HazardLava));
        fixture.add(Arguments.of(TestBlockType.Water, x, -1, 0, ExcavationLogic.MiningStatus.HazardWater));

        for (int y = 0; y <= maxY-1; y++) {
            for (int z = -1; z <= 1; z++) {
                fixture.add(Arguments.of(TestBlockType.Fluid, x, y, z, ExcavationLogic.MiningStatus.HazardUnknownFluid));
                fixture.add(Arguments.of(TestBlockType.Lava, x, y, z, ExcavationLogic.MiningStatus.HazardLava));
                fixture.add(Arguments.of(TestBlockType.Water, x, y, z, ExcavationLogic.MiningStatus.HazardWater));
            }
        }

        fixture.add(Arguments.of(TestBlockType.Fluid, x, maxY, 0, ExcavationLogic.MiningStatus.HazardUnknownFluid));
        fixture.add(Arguments.of(TestBlockType.Lava, x, maxY, 0, ExcavationLogic.MiningStatus.HazardLava));
        fixture.add(Arguments.of(TestBlockType.Water, x, maxY, 0, ExcavationLogic.MiningStatus.HazardWater));

        return fixture;
    }

    /**             -2-1 0 1 2
     *  3 XXXXX      X X X X X
     *  2 XXXXX      X X X X
     *  1 XXXXX      X X X X
     *  0 XXXXX      X X X X
     * -1 XXXXX      X X X X X
     */
    static List<Arguments> hazardFixtureFoundFar() {
        List<Arguments> fixture = new ArrayList<>();

        final int x = 2;
        final int z = 0;

        for (int y = 0; y <= 2; y++) {
                fixture.add(Arguments.of(TestBlockType.Fluid, x, y, z, ExcavationLogic.MiningStatus.HazardUnknownFluid));
                fixture.add(Arguments.of(TestBlockType.Lava, x, y, z, ExcavationLogic.MiningStatus.HazardLava));
                fixture.add(Arguments.of(TestBlockType.Water, x, y, z, ExcavationLogic.MiningStatus.HazardWater));
        }

        return fixture;
    }

    @Test
    void mineInFrontAllThreeBlocks() {
        setupMinecartForRolling();

        MinecartTestBlockArea area = new MinecartTestBlockArea(
                BlockLayerRock,
                BlockLayerWithRail.setBlockType(TestBlockType.Steel, 1, 0),
                BlockLayerAir.setBlockType(TestBlockType.Diamond, 1, 0),
                BlockLayerAir.setBlockType(TestBlockType.Sand, 1, 0),
                BlockLayerAir.setBlockType(TestBlockType.Dirt, 1, 0));

        area.initializeMock(world, removedBlocks);

        when(shovelMiningToolItem0.getMiningSpeedMultiplier(any(DancZerItemStack.class), any(DancZerBlockState.class))).thenReturn(1.5f);
        when(shovelMiningToolItem1.getMiningSpeedMultiplier(any(DancZerItemStack.class), any(DancZerBlockState.class))).thenReturn(3.0f);
        when(pickAxeMiningToolItem0.getMiningSpeedMultiplier(any(DancZerItemStack.class), any(DancZerBlockState.class))).thenReturn(1.5f);
        when(pickAxeMiningToolItem1.getMiningSpeedMultiplier(any(DancZerItemStack.class), any(DancZerBlockState.class))).thenReturn(3.0f);

        when(pickAxeMiningToolItem1.isSuitableFor(any(DancZerBlockState.class))).thenReturn(true);

        final int maxIteration = 1000;

        List<BlockPos> miningPosList = new ArrayList<>();
        int i = 0;

        do {
            if (i > 0) {
                assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.Mining);

                if (miningPosList.isEmpty() || !miningPosList.get(miningPosList.size() - 1).equals(logic.getMiningPos())) {
                    miningPosList.add(logic.getMiningPos());
                }
            }
            i++;

            logic.tick();
        } while (i < maxIteration && logic.getMiningStatus() == ExcavationLogic.MiningStatus.Mining);

        for (BlockPos miningPos : miningPosList) {
            verify(world, times(1)).setBlockBreakingInfo(anyInt(), eq(miningPos), eq(-1));
            verify(world, times(6)).setBlockBreakingInfo(anyInt(), eq(miningPos), intThat(argument -> argument % 2 == 0));
            verify(world, times(1)).setBlockBreakingInfo(anyInt(), eq(miningPos), eq(10));
            verify(world).breakBlock(eq(miningPos), anyBoolean());
        }

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.Rolling);

        //clean inventory to simulate the missing items
        setupInventory(new DancZerItemStack[0]);

        logic.tick();
        assertThat(logic.getMiningPos()).isNull();
        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.MissingToolchain);
    }

    @Test
    void notOnRail() {
        setupMinecartForRolling();

        MinecartTestBlockArea area = new MinecartTestBlockArea(
                BlockLayerRock,
                BlockLayerRock,
                BlockLayerMinedTunnel,
                BlockLayerMinedTunnel,
                BlockLayerMinedTunnel);

        area.initializeMock(world, removedBlocks);

        logic.tick();
        assertThat(logic.getMiningPos()).isNull();
        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.Rolling);
    }


    @Test
    void onRailAndStopped() {
        setupMinecartForRolling();

        when(entity.getVelocity()).thenReturn(new Vec3d(0, 0, 0));

        MinecartTestBlockArea area = new MinecartTestBlockArea(
                BlockLayerRock,
                BlockLayerWithRail,
                BlockLayerMinedTunnel,
                BlockLayerMinedTunnel,
                BlockLayerMinedTunnel);

        area.initializeMock(world, removedBlocks);

        logic.tick();
        assertThat(logic.getMiningPos()).isNull();
        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.Rolling);
    }


    private void setupMinecartForRolling() {

        when(shovelMiningToolItem0.getMiningSpeedMultiplier(any(DancZerItemStack.class), any(DancZerBlockState.class))).thenReturn(1.5f);
        when(shovelMiningToolItem1.getMiningSpeedMultiplier(any(DancZerItemStack.class), any(DancZerBlockState.class))).thenReturn(3.0f);
        when(pickAxeMiningToolItem0.getMiningSpeedMultiplier(any(DancZerItemStack.class), any(DancZerBlockState.class))).thenReturn(1.5f);
        when(pickAxeMiningToolItem1.getMiningSpeedMultiplier(any(DancZerItemStack.class), any(DancZerBlockState.class))).thenReturn(3.0f);

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

        setupInventory(new DancZerItemStack[]{
                itemStackMiningToolItemShovel0,
                itemStackMiningToolItemPickAxe0,
                itemStackRailBlockItem0,
                itemStackTorchBlockItem0,
                itemStackStoneBlockItem,
                itemStackEmpty,
        });

        when(entity.getBlockPos()).thenReturn(new BlockPos(0, 0, 0));
        when(entity.getVelocity()).thenReturn(new Vec3d(1, 0, 0));

        doAnswer((Answer<String>) invocation -> {
            System.out.println("Block breaking info: " + invocation.getArgument(1) + ", " + invocation.getArgument(2));
            return "Hello";
        }).when(world).setBlockBreakingInfo(anyInt(), any(BlockPos.class), anyInt());

        doAnswer((Answer<String>) invocation -> {
            System.out.println("Block removed at: " + invocation.getArgument(0));
            removedBlocks.add(invocation.getArgument(0));
            return "Hello";
        }).when(world).breakBlock(any(BlockPos.class), anyBoolean());

        when(railBlockItem0.getBlock()).thenReturn(railDefaultBlock);
        when(railDefaultBlock.getDefaultState()).thenReturn(railDefaultBlockState);

        when(torchBlockItem0.getBlock()).thenReturn(torchDefaultBlock);
        when(torchDefaultBlock.getDefaultState()).thenReturn(torchDefaultBlockState);
    }

    private static class MinecartTestBlockLayer {
        final int blockCountX = 5;
        final int blockCountY = 5;
        final int blockCount = blockCountX * blockCountY;

        TestBlockType[] blockTypes;
        DancZerBlockState[] blockStates;

        public MinecartTestBlockLayer(TestBlockType[] blockTypes) {
            assertThat(blockTypes.length).isSameAs(blockCount);
            this.blockTypes = blockTypes;
            blockStates = new DancZerBlockState[blockTypes.length];
        }

        public MinecartTestBlockLayer setBlockType(TestBlockType blockType, int x, int z) {
            blockTypes[getIdx(x, z)] = blockType;
            return this;
        }

        private int getIdx(int x, int z) {
            return blockCountX * (z + 2) + x + 2;
        }

        public void initializeMock(int y, DancZerWorld world, Set<BlockPos> removedBlocks) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    int idx = getIdx(x, z);
                    var blockPos = new BlockPos(x, y, z);
                    blockStates[idx] = createBlockState(blockPos, blockTypes[idx], world, removedBlocks);
                }
            }
        }

        private DancZerBlockState createBlockState(BlockPos blockPos, TestBlockType blockType, DancZerWorld world, Set<BlockPos> removedBlocks) {
            var blockState = mock(DancZerBlockState.class);

            when(blockState.isBlockHarvested(eq(world), eq(blockPos))).then(invocation -> blockType == TestBlockType.Air || blockType == TestBlockType.Rail || removedBlocks.contains(invocation.getArgument(1)));
            when(blockState.isFluid()).thenReturn(blockType == TestBlockType.Water || blockType == TestBlockType.Lava || blockType == TestBlockType.Fluid);
            when(blockState.isLava()).thenReturn(blockType == TestBlockType.Lava);
            when(blockState.isWater()).thenReturn(blockType == TestBlockType.Water);

            switch (blockType) {
                case Air -> {
                }
                case Rail -> {
                    when(blockState.isRailTrack()).thenReturn(true);
                }
                case Sand -> {
                    when(blockState.getHardness(eq(world), eq(blockPos))).thenReturn(0.1f);
                }
                case Dirt -> {
                    when(blockState.getHardness(eq(world), eq(blockPos))).thenReturn(0.5f);
                }
                case Rock -> {
                    when(blockState.getHardness(eq(world), eq(blockPos))).thenReturn(1f);
                }
                case Steel -> {
                    when(blockState.getHardness(eq(world), eq(blockPos))).thenReturn(2f);
                }
                case Diamond -> {
                    when(blockState.getHardness(eq(world), eq(blockPos))).thenReturn(10f);
                }
                case Obsidian -> {
                    when(blockState.getHardness(eq(world), eq(blockPos))).thenReturn(50f);
                }
                case Bedrock -> {
                    when(blockState.getHardness(eq(world), eq(blockPos))).thenReturn(100f);
                }

            }
            when(world.getBlockState(eq(blockPos))).thenReturn(blockState);

            return blockState;
        }
    }

    private static class MinecartTestBlockArea {
        final int bottomIdx = -1;
        final int topIdx = 3;
        MinecartTestBlockLayer[] array;

        public MinecartTestBlockArea(
                MinecartTestBlockLayer belowBlocks,
                MinecartTestBlockLayer levelBlocks,
                MinecartTestBlockLayer aboveBlocks,
                MinecartTestBlockLayer aboveBlocks2,
                MinecartTestBlockLayer aboveBlocks3) {
            array = new MinecartTestBlockLayer[]{belowBlocks,
                    levelBlocks,
                    aboveBlocks,
                    aboveBlocks2,
                    aboveBlocks3};
        }

        public MinecartTestBlockLayer getLayer(int y) {
            assertThat(y).isBetween(bottomIdx, topIdx);

            return array[y + 1];
        }

        public void initializeMock(DancZerWorld world, Set<BlockPos> removedBlocks) {
            for (int i = -1; i <= 3; i++) {
                getLayer(i).initializeMock(i, world, removedBlocks);
            }
        }
    }
}