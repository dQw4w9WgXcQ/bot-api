package github.dqw4w9wgxcq.botapi.wrappers.item;

import github.dqw4w9wgxcq.botapi.Client;
import github.dqw4w9wgxcq.botapi.wrappers.Identifiable;
import github.dqw4w9wgxcq.botapi.wrappers.Nameable;
import lombok.experimental.Delegate;
import net.runelite.api.ItemComposition;
import org.jetbrains.annotations.NotNull;

public class Item implements Nameable, ItemComposition {
    public final net.runelite.api.Item rl;
    @Delegate(types = {ItemComposition.class}, excludes = {Identifiable.class})
    private final ItemComposition definitionDelegate;

    public Item(@NotNull net.runelite.api.Item rl) {
        this.rl = rl;
        definitionDelegate = Client.INSTANCE.getItemDefinition(rl.getId());
    }

    public int getQuantity() {
        return rl.getQuantity();
    }

    public int getId() {
        return rl.getId();
    }

    @Override
    public String toString() {
        return getName() + " " + getId();
    }
}
