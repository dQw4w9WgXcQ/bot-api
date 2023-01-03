package github.dqw4w9wgxcq.botapi.wrappers.item.container;

import github.dqw4w9wgxcq.botapi.wrappers.item.Item;
import org.jetbrains.annotations.NotNull;

public abstract class ContainerItem extends Item {
    private final int index;

    public ContainerItem(@NotNull net.runelite.api.Item delegate, int index) {
        super(delegate);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
