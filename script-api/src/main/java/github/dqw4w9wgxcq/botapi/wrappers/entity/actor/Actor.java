package github.dqw4w9wgxcq.botapi.wrappers.entity.actor;

import github.dqw4w9wgxcq.botapi.Reflect;
import github.dqw4w9wgxcq.botapi.wrappers.Locatable;
import github.dqw4w9wgxcq.botapi.wrappers.entity.Entity;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Actor<RL extends net.runelite.api.Actor> implements Entity, net.runelite.api.Actor {
    private interface Excludes {
        net.runelite.api.Actor getInteracting();
    }

    @Delegate(types = net.runelite.api.Actor.class, excludes = {Excludes.class, Locatable.class})
    public final @NotNull RL rl;

    public Actor(@NotNull RL rl) {
        this.rl = rl;
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
            throw new IllegalStateException("weird interacting type: " + interacting.getClass().getName(), null);
        }
    }

    public boolean isAnimating() {
        return getAnimation() != -1;
    }

    public int getPathLength() {
        return Reflect.INSTANCE.getInt2(Reflect.INSTANCE.getPathLength(), rl, Reflect.INSTANCE.getPathLengthDecoder());
    }

    public boolean isMoving() {
        return getPathLength() != 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Actor)) {
            return false;
        }

        return rl == ((Actor<?>) obj).rl;
    }
}
