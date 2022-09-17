package github.dqw4w9wgxcq.botapi.wrappers.entity.actor;

import github.dqw4w9wgxcq.botapi.Client;
import github.dqw4w9wgxcq.botapi.wrappers.Locatable;
import lombok.experimental.Delegate;
import net.runelite.api.NPCComposition;
import net.runelite.api.Renderable;
import net.runelite.api.coords.WorldPoint;
import org.jetbrains.annotations.NotNull;

public final class NPC extends Actor<net.runelite.api.NPC> implements net.runelite.api.NPC, NPCComposition {
    @Delegate(types = {NPCComposition.class})
    private final NPCComposition compositionDelegate;

    public NPC(@NotNull net.runelite.api.NPC rl) {
        super(rl);
        compositionDelegate = Client.INSTANCE.getNpcDefinition(rl.getId());
    }

    private interface Excludes {
        net.runelite.api.Actor getInteracting();
    }

    @Delegate(types = {net.runelite.api.NPC.class, Renderable.class}, excludes = {Excludes.class, NPCComposition.class, Locatable.class})
    private @NotNull net.runelite.api.NPC getDelegate() {
        return rl;
    }

    @Override
    public @NotNull WorldPoint getWorldLocation() {
        return rl.getWorldLocation();
    }
}
