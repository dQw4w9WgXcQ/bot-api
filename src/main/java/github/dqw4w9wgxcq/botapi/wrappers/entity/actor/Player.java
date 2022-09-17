package github.dqw4w9wgxcq.botapi.wrappers.entity.actor;

import github.dqw4w9wgxcq.botapi.Client;
import github.dqw4w9wgxcq.botapi.commons.NotFoundException;
import github.dqw4w9wgxcq.botapi.commons.RetryableBotException;
import kotlin.jvm.functions.Function1;
import lombok.experimental.Delegate;
import net.runelite.api.PlayerComposition;
import net.runelite.api.Renderable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Player extends Actor<net.runelite.api.Player> implements net.runelite.api.Player, PlayerComposition {
    @Delegate(types = PlayerComposition.class)
    private final PlayerComposition compositionDelegate;

    public Player(@NotNull net.runelite.api.Player rl) {
        super(rl);
        compositionDelegate = getPlayerComposition();
    }

    @NotNull
    @Override
    public List<String> getFilteredActions() {
        return super.getFilteredActions();
    }

    @Override
    public boolean hasAction(@NotNull Function1<? super String, Boolean> actionMatches) {
        return super.hasAction(actionMatches);
    }

    @Override
    public boolean hasAction(@NotNull String actionIgnoreCase) {
        return super.hasAction(actionIgnoreCase);
    }

    private interface Excludes {
        net.runelite.api.Actor getInteracting();

        String getName();
    }

    @Delegate(types = {net.runelite.api.Player.class, Renderable.class}, excludes = {PlayerComposition.class, Excludes.class})
    private @NotNull net.runelite.api.Player getDelegate() {
        return super.rl;
    }

    @NotNull
    @Override
    public String getName() {
        String name = super.rl.getName();
        if (name == null) {
            throw new NotFoundException("player name null");
        }
        return name;
    }

    @Override
    public @Nullable String @Nullable [] getActions() {
        return Client.INSTANCE.getPlayerOptions();
    }

    public int getIndex() {
        net.runelite.api.Player[] players = Client.INSTANCE.getCachedPlayers();
        for (int i = 0; i < players.length; i++) {
            if (super.rl == players[i]) {
                return i;
            }
        }

        throw new RetryableBotException("player wasn't in the players array");
    }
}
