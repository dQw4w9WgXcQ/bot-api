package github.dqw4w9wgxcq.botapi.wrappers;

import github.dqw4w9wgxcq.botapi.Client;
import github.dqw4w9wgxcq.botapi.commons.CommonsKt;
import github.dqw4w9wgxcq.botapi.entities.Players;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import org.jetbrains.annotations.NotNull;

public interface Locatable {
    @NotNull WorldPoint getWorldLocation();

    default @NotNull Point getSceneLocation() {
        return CommonsKt.toScene(getWorldLocation(), Client.INSTANCE.getBaseX(), Client.INSTANCE.getBaseY());
    }

    default int getPlane() {
        return getWorldLocation().getPlane();
    }

    default int getX() {
        return getWorldLocation().getX();
    }

    default int getY() {
        return getWorldLocation().getY();
    }

    default int getRegion() {
        return getWorldLocation().getRegionID();
    }

    default int distance(Point to) {
        return CommonsKt.distance(getSceneLocation(), to);
    }

    default int distance(WorldPoint to) {
        return getWorldLocation().distanceTo(to);
    }

    default int distance(Locatable to) {
        return distance(to.getWorldLocation());
    }

    default int distance() {
        return distance(Players.INSTANCE.local());
    }
}
