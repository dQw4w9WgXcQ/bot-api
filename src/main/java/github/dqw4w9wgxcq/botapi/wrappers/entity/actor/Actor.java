package github.dqw4w9wgxcq.botapi.wrappers.entity.actor;

import github.dqw4w9wgxcq.botapi.Client;
import github.dqw4w9wgxcq.botapi.Refl;
import github.dqw4w9wgxcq.botapi.commons.CommonsKt;
import github.dqw4w9wgxcq.botapi.commons.FatalException;
import github.dqw4w9wgxcq.botapi.wrappers.entity.Entity;
import lombok.experimental.Delegate;
import net.runelite.api.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Actor<RL extends net.runelite.api.Actor> implements Entity, net.runelite.api.Actor {
    private interface Excludes {
        net.runelite.api.Actor getInteracting();
    }

    @Delegate(types = net.runelite.api.Actor.class, excludes = Excludes.class)
    public final @NotNull RL rl;

    public Actor(@NotNull RL rl) {
        this.rl = rl;
    }

    @Override
    public @NotNull Point getSceneLocation() {
        return CommonsKt.toScene(rl.getWorldLocation(), Client.INSTANCE.getBaseX(), Client.INSTANCE.getBaseY());
    }

    @Override
    public int getPlane() {
        return getWorldLocation().getPlane();
    }

    @Override
    public @Nullable Actor<? extends net.runelite.api.Actor> getInteracting() {
        net.runelite.api.Actor interacting = rl.getInteracting();
        if (interacting == null) {
            return null;
        } else if (interacting instanceof net.runelite.api.Player) {
            return new Player((net.runelite.api.Player) interacting);
        } else if (interacting instanceof net.runelite.api.NPC) {
            return new NPC((net.runelite.api.NPC) interacting);
        } else {
            throw new FatalException("weird interacting type: " + interacting.getClass().getName(), null);
        }
    }

    public boolean isAnimating() {
        return getAnimation() != -1;
    }

    public int getPathLength() {
        return Refl.INSTANCE.getInt2(Refl.INSTANCE.getPathLength(), rl, Refl.INSTANCE.getPathLengthDecodingMult());
    }

    public boolean isMoving() {
        return getPathLength() != 0;
    }
}
