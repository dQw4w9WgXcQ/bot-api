package github.dqw4w9wgxcq.botapi.wrappers.entity.tile.object;

import github.dqw4w9wgxcq.botapi.Client;
import github.dqw4w9wgxcq.botapi.wrappers.entity.tile.TileEntity;
import lombok.experimental.Delegate;
import net.runelite.api.ObjectComposition;
import org.jetbrains.annotations.NotNull;

//getId returns the compostion id.  getRl().getId() represents the id of the entity.
public abstract class TileObject<RL extends net.runelite.api.TileObject> implements TileEntity, net.runelite.api.TileObject, ObjectComposition {
    @Delegate(types = net.runelite.api.TileObject.class, excludes = {ObjectComposition.class})
    public final @NotNull RL rl;

    @Delegate(types = {ObjectComposition.class})
    private ObjectComposition compositionDelegate;

    public TileObject(@NotNull RL rl) {
        this.rl = rl;

        compositionDelegate = Client.INSTANCE.getObjectDefinition(rl.getId());
        if (compositionDelegate.getName().equals("null") && compositionDelegate.getImpostorIds() != null) {
            ObjectComposition impostorComposition = compositionDelegate.getImpostor();
            if (impostorComposition != null) {
                String name = impostorComposition.getName();
                if (name != null && !name.equals("null")) {
                    compositionDelegate = impostorComposition;
                }
            }
        }
    }
}
