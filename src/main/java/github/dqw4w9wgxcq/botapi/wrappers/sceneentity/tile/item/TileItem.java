package github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.item;

import github.dqw4w9wgxcq.botapi.commons.CommonsKt;
import github.dqw4w9wgxcq.botapi.itemcontainer.inventory.Inventory;
import github.dqw4w9wgxcq.botapi.wrappers.item.Item;
import github.dqw4w9wgxcq.botapi.wrappers.sceneentity.tile.TileEntity;
import kotlin.NotImplementedError;
import lombok.experimental.Delegate;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TileItem extends Item implements TileEntity, net.runelite.api.TileItem {
    @Delegate(types = {net.runelite.api.TileItem.class}, excludes = {LocatableExcludes.class, net.runelite.api.Item.class})
    private final net.runelite.api.TileItem rlTileItem;
    private final Tile tile;

    public TileItem(@NotNull net.runelite.api.TileItem rl, Tile tile) {
        super(new net.runelite.api.Item(rl.getId(), rl.getQuantity()));
        rlTileItem = rl;
        this.tile = tile;
    }

    @Override
    public @Nullable String @Nullable [] getActions() {
        throw new NotImplementedError();//todo
    }

    public void take() {
        int count = Inventory.INSTANCE.count(getId());
        interact("Take");
        CommonsKt.waitUntil(3000, 50, () -> Inventory.INSTANCE.count(getId()) != count);
    }

    @Override
    public @NotNull WorldPoint getWorldLocation() {
        return tile.getWorldLocation();
    }

    @Override
    public @NotNull Point getSceneLocation() {
        return tile.getSceneLocation();
    }

    @Override
    public int getPlane() {
        return getWorldLocation().getPlane();
    }
}
