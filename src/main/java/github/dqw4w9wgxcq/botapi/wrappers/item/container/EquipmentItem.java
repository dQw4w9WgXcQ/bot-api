package github.dqw4w9wgxcq.botapi.wrappers.item.container;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import org.jetbrains.annotations.NotNull;

public class EquipmentItem extends ContainerItem {
    public EquipmentItem(@NotNull Item delegate, int index) {
        super(delegate, index);
    }

    public EquipmentInventorySlot getSlot() {
        for (EquipmentInventorySlot slot : EquipmentInventorySlot.values()) {
            if (slot.getSlotIdx() == getIndex()) {
                return slot;
            }
        }

        throw new RuntimeException("no slot found for index " + getIndex());
    }
}
