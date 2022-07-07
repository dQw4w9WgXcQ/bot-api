package github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.object;

import lombok.experimental.Delegate;
import net.runelite.api.ObjectComposition;
import org.jetbrains.annotations.NotNull;

public final class WallObject extends TileObject<net.runelite.api.WallObject> implements net.runelite.api.WallObject {
    public WallObject(@NotNull net.runelite.api.WallObject delegate) {
        super(delegate);
    }

    @NotNull
    @Delegate(types = {net.runelite.api.WallObject.class}, excludes = {LocatableExcludes.class, ObjectComposition.class})
    public net.runelite.api.WallObject delegate() {
        return getRl();
    }
}
