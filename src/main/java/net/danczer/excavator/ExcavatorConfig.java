package net.danczer.excavator;

import net.danczer.excavator.wrapper.DancZerBlock;
import net.danczer.excavator.wrapper.DancZerBlockItem;
import net.danczer.excavator.wrapper.DancZerMiningToolItem;

import java.util.List;

public interface ExcavatorConfig {
    List<DancZerBlockItem> getTorchItems();
    List<DancZerBlock> getWallTorchBlocks();
    List<DancZerBlockItem> getRailItems();
    List<DancZerMiningToolItem> getPickAxeItems();
    List<DancZerMiningToolItem> getShovelItems();

    void validate();
}
