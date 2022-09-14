package github.dqw4w9wgxcq.botapi.wrappers.entity.tile.object;

import lombok.experimental.Delegate;
import net.runelite.api.ObjectComposition;
import org.jetbrains.annotations.NotNull;

public final class DecorativeObject extends TileObject<net.runelite.api.DecorativeObject> implements net.runelite.api.DecorativeObject {
    public DecorativeObject(@NotNull net.runelite.api.DecorativeObject rl) {
        super(rl);
    }

    @NotNull
    @Delegate(types = {net.runelite.api.DecorativeObject.class}, excludes = {ObjectComposition.class})
    private net.runelite.api.DecorativeObject getDelegate() {
        return rl;
    }
}
