package net.danczer.excavator;

import net.minecraft.inventory.Inventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ExcavationLogicTest {

    @Mock
    Inventory inventory;

    @InjectMocks
    ExcavationLogic logic;

    @Test
    void inventoryFull() {
        logic.tick();

        assertThat(logic.getMiningStatus()).isSameAs(ExcavationLogic.MiningStatus.InventoryIsFull);
    }
}