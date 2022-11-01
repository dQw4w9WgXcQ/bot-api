package github.dqw4w9wgxcq.botapi.wrappers.item.container;

import github.dqw4w9wgxcq.botapi.commons.CommonsKt;
import github.dqw4w9wgxcq.botapi.commons.Wait;
import github.dqw4w9wgxcq.botapi.interact.Interact;
import github.dqw4w9wgxcq.botapi.itemcontainer.Inventory;
import github.dqw4w9wgxcq.botapi.wrappers.Interactable;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryItem extends ContainerItem implements Interactable {
    public InventoryItem(@NotNull net.runelite.api.Item delegate, int index) {
        super(delegate, index);
    }

    public boolean isNoted() {
        return getNote() == 799;
    }

    @Override
    public @Nullable String @Nullable [] getActions() {
        String[] inventoryActions = getInventoryActions().clone();

        if (inventoryActions[1] == null) {
            inventoryActions[1] = "Use";
        } else if (inventoryActions[0] == null) {
            inventoryActions[0] = "Use";
        }

        return inventoryActions;
    }

    public void drop() {
        int startCount = Inventory.INSTANCE.count(getId());
        interact("drop");
        CommonsKt.waitUntil(Wait.defaultTimeout, Wait.defaultPollRate, () -> Inventory.INSTANCE.count(getId()) != startCount);
    }

    //Unit cause nosuchmethoderror
    @Override
    public @NotNull Object interact(@NotNull Function1<? super String, Boolean> actionMatches) {
        Interact.INSTANCE.withInventory(this, actionMatches);
        return new Object();
    }
}
