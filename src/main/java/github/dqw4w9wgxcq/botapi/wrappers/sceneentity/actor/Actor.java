package github.dqw4w9wgxcq.botapi.wrappers.sceneentity.actor;

import github.dqw4w9wgxcq.botapi.Refl;
import github.dqw4w9wgxcq.botapi.commons.CommonsKt;
import github.dqw4w9wgxcq.botapi.game.Client;
import github.dqw4w9wgxcq.botapi.wrappers.RlWrapper;
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.SceneEntity;
import net.runelite.api.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Actor<RL extends net.runelite.api.Actor> extends RlWrapper<RL> implements SceneEntity, net.runelite.api.Actor {
    public Actor(@NotNull RL rl) {
        super(rl);
    }

    @Override
    public int getPlane() {
        return getWorldLocation().getPlane();
    }

    @Override
    public @Nullable Actor<?> getInteracting() {
        net.runelite.api.Actor interacting = getRl().getInteracting();
        if (interacting == null) {
            return null;
        } else if (interacting instanceof net.runelite.api.Player) {
            return new Player((net.runelite.api.Player) interacting);
        } else if (interacting instanceof net.runelite.api.NPC) {
            return new NPC((net.runelite.api.NPC) interacting);
        } else {
            throw new RuntimeException("weird interacting type: " + interacting.getClass().getName());
        }
    }

    @Override
    public @NotNull Point getSceneLocation() {
        return CommonsKt.toScene(getWorldLocation(), Client.INSTANCE.getBaseX(), Client.INSTANCE.getBaseY());
    }

    public boolean isAnimating() {
        return getAnimation() != -1;
    }

    public int getPathLength() {
        int out = Refl.INSTANCE.getInt2(Refl.INSTANCE.getPathLength(), getRl(), Refl.INSTANCE.getPathLengthDecoder());
        //System.out.println("path length: " + out);
        return out;
    }

    public boolean isMoving() {
        return getPathLength() != 0;
    }
}
