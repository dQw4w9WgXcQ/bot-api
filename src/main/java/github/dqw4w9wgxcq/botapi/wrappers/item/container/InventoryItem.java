package github.dqw4w9wgxcq.botapi.wrappers.item.container;

import github.dqw4w9wgxcq.botapi.interact.Interact;
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
        interact("drop");
    }

    //Unit cause nosuchmethoderror
    @NotNull
    @Override
    public Object interact(@NotNull Function1<? super String, Boolean> actionMatches) {
        Interact.INSTANCE.withInventory(this, actionMatches);
        return new Object();
    }
}
