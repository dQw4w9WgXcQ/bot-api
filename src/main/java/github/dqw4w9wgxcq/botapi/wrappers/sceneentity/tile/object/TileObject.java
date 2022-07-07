package github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.object;

import github.dqw4w9wgxcq.botapi.commons.CommonsKt;
import github.dqw4w9wgxcq.botapi.game.Client;
import github.dqw4w9wgxcq.botapi.wrappers.RlWrapper;
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.TileEntity;
import lombok.experimental.Delegate;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import org.jetbrains.annotations.NotNull;

//getId returns the compostion id.  getRl().getId() represents the id of the entity.
public abstract class TileObject<RL extends net.runelite.api.TileObject> extends RlWrapper<RL> implements TileEntity, net.runelite.api.TileObject, ObjectComposition {
    interface CompositionExcludes {
        int getId();
    }

    @Delegate(types = {ObjectComposition.class}, excludes = {CompositionExcludes.class})
    private ObjectComposition compositionDelegate;

    public TileObject(@NotNull RL rl) {
        super(rl);
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

    @Override
    public int getId() {
        return getRl().getId();
    }

    @Override
    public int getX() {
        return TileEntity.super.getX();
    }

    @Override
    public int getY() {
        return TileEntity.super.getY();
    }

    @Override
    public @NotNull WorldPoint getWorldLocation() {
        return getRl().getWorldLocation();
    }

    @Override
    public @NotNull Point getSceneLocation() {
        return CommonsKt.toScene(getWorldLocation(), Client.INSTANCE.getBaseX(), Client.INSTANCE.getBaseY());
    }
}
