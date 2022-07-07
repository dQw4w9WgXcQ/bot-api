package github.dqw4w9wgxcq.botapi.wrappers.item;

import github.dqw4w9wgxcq.botapi.game.Client;
import github.dqw4w9wgxcq.botapi.wrappers.Identifiable;
import github.dqw4w9wgxcq.botapi.wrappers.Nameable;
import github.dqw4w9wgxcq.botapi.wrappers.RlWrapper;
import lombok.experimental.Delegate;
import net.runelite.api.ItemComposition;
import org.jetbrains.annotations.NotNull;

public class Item extends RlWrapper<net.runelite.api.Item> implements Identifiable, Nameable, ItemComposition {
    @Delegate(types = {ItemComposition.class}, excludes = {Identifiable.class})
    private final ItemComposition definitionDelegate;

    public Item(@NotNull net.runelite.api.Item rl) {
        super(rl);
        definitionDelegate = Client.INSTANCE.getItemDefinition(rl.getId());
    }

    public int getQuantity() {
        return getRl().getQuantity();
    }

    public int getId() {
        return getRl().getId();
    }

    @Override
    public String toString() {
        return getName() + " " + getId();
    }
}
