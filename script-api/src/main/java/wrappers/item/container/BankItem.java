package github.dqw4w9wgxcq.botapi.wrappers.item.container;

import net.runelite.api.Item;
import org.jetbrains.annotations.NotNull;

public class BankItem extends ContainerItem {
    public BankItem(@NotNull Item delegate, int index) {
        super(delegate, index);
    }

    public boolean isPlaceholder() {
        return getPlaceholderTemplateId() == 14401;
    }
}
