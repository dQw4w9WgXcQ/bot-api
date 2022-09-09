package github.dqw4w9wgxcq.botapi.wrappers.sceneentity.actor;

import github.dqw4w9wgxcq.botapi.Client;
import lombok.experimental.Delegate;
import net.runelite.api.NPCComposition;
import net.runelite.api.Renderable;
import org.jetbrains.annotations.NotNull;

public final class NPC extends Actor<net.runelite.api.NPC> implements net.runelite.api.NPC, NPCComposition {
    private interface CompositionExcludes {
        int getId();
    }

    @Delegate(types = {NPCComposition.class}, excludes = {CompositionExcludes.class})
    private final NPCComposition compositionDelegate;

    public NPC(@NotNull net.runelite.api.NPC rl) {
        super(rl);
        compositionDelegate = Client.INSTANCE.getNpcDefinition(rl.getId());
    }

    private interface Excludes {
        net.runelite.api.Actor getInteracting();

        String getName();

        int getCombatLevel();
    }

    @Delegate(types = {net.runelite.api.NPC.class, Renderable.class}, excludes = {Excludes.class})
    @NotNull
    private net.runelite.api.NPC delegate() {
        return getRl();
    }
}
