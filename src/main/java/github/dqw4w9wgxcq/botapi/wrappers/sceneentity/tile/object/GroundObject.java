package github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.object;

import lombok.experimental.Delegate;
import net.runelite.api.ObjectComposition;
import org.jetbrains.annotations.NotNull;

public final class GroundObject extends TileObject<net.runelite.api.GroundObject> implements net.runelite.api.GroundObject {
    public GroundObject(@NotNull net.runelite.api.GroundObject delegate) {
        super(delegate);
    }

    @NotNull
    @Delegate(types = {net.runelite.api.GroundObject.class}, excludes = {LocatableExcludes.class, ObjectComposition.class})
    public net.runelite.api.GroundObject getDelegate() {
        return super.getRl();
    }
}
