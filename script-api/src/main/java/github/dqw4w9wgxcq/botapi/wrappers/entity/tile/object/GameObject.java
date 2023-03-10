package github.dqw4w9wgxcq.botapi.wrappers.entity.tile.object;

import github.dqw4w9wgxcq.botapi.Client;
import github.dqw4w9wgxcq.botapi.commons.CommonsKt;
import github.dqw4w9wgxcq.botapi.commons.RetryException;
import github.dqw4w9wgxcq.botapi.entities.Players;
import github.dqw4w9wgxcq.botapi.movement.Movement;
import github.dqw4w9wgxcq.botapi.movement.pathfinding.local.LocalPathfinding;
import github.dqw4w9wgxcq.botapi.wrappers.Locatable;
import kotlin.jvm.functions.Function1;
import lombok.experimental.Delegate;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import org.jetbrains.annotations.NotNull;

public final class GameObject extends TileObject<net.runelite.api.GameObject> implements net.runelite.api.GameObject {
    public GameObject(@NotNull net.runelite.api.GameObject rl) {
        super(rl);
    }

    @NotNull
    @Delegate(types = {net.runelite.api.GameObject.class}, excludes = {ObjectComposition.class, Locatable.class})
    public net.runelite.api.GameObject getDelegate() {
        return super.rl;
    }

    @Override
    public @NotNull Boolean interact(@NotNull Function1<? super String, Boolean> actionMatches) {
        Point min = getSceneMinLocation();
        Point myTile = Players.INSTANCE.local().getSceneLocation();
        double lowestDist = Double.MAX_VALUE;
        Point best = null;
        for (int x = 0; x < sizeX(); x++) {
            for (int y = 0; y < sizeY(); y++) {
                Point offset = new Point(min.getX() + x, min.getY() + y);
                double dist = CommonsKt.distance(myTile, offset);
                if (dist < lowestDist && LocalPathfinding.INSTANCE.canReach(offset, myTile, true)) {
                    lowestDist = dist;
                    best = offset;
                }
            }
        }

        if (best == null) {
            throw new RetryException("game object not reachable " + getName() + " " + getId() + " " + getSceneMinLocation() + " " + sizeX() + " " + sizeY());
        }

        if (!Movement.INSTANCE.checkDoor(best, true)) {
            return false;
        }

        interactUnchecked(actionMatches);
        return true;
    }

    @Override
    public @NotNull Point getSceneLocation() {
        return getSceneMinLocation();
    }

    @Override
    public @NotNull WorldPoint getWorldLocation() {
        Point sceneLocation = getSceneLocation();
        return WorldPoint.fromScene(Client.INSTANCE, sceneLocation.getX(), sceneLocation.getY(), rl.getPlane());
    }
}