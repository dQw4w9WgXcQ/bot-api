package github.dqw4w9wgxcq.botapi.wrappers;

import github.dqw4w9wgxcq.botapi.commons.CommonsKt;
import github.dqw4w9wgxcq.botapi.commons.LogKt;
import github.dqw4w9wgxcq.botapi.commons.NotFoundException;
import github.dqw4w9wgxcq.botapi.interact.Interact;
import kotlin.jvm.functions.Function1;
import lombok.experimental.Delegate;
import net.runelite.api.widgets.WidgetInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Widget implements net.runelite.api.widgets.Widget, Interactable, Identifiable {
    public final @NotNull net.runelite.api.widgets.Widget rl;

    private Widget(@NotNull net.runelite.api.widgets.Widget rl) {
        this.rl = rl;
    }

    private interface Excludes {
        net.runelite.api.widgets.Widget getChild(int index);

        net.runelite.api.widgets.Widget[] getChildren();

        net.runelite.api.widgets.Widget getParent();

        int getParentId();

        Object[] getOnOpListener();

        boolean isHidden();

        Rectangle getBounds();
    }

    @Delegate(excludes = Excludes.class)
    public @NotNull net.runelite.api.widgets.Widget widgetDelegate() {
        return rl;
    }

    @Override
    public @NotNull Rectangle getBounds() {
        Rectangle bounds = rl.getBounds();
        if (bounds == null || bounds.x == -1 || bounds.y == -1) {
            throw new IllegalStateException("bounds:" + bounds + " of widget:" + this + " should have been filtered by this point ");
        }

        return bounds;
    }

    @Override
    public @Nullable Object[] getOnOpListener() {
        return CommonsKt.onGameThread(rl::getOnOpListener);
    }

    public @Nullable Widget getParentOrNull() {
        net.runelite.api.widgets.Widget parent = CommonsKt.onGameThread(rl::getParent);
        if (parent == null) {
            return null;
        }

        return new Widget(parent);
    }

    @Override
    public @NotNull Widget getParent() {
        Widget parentNullable = getParentOrNull();
        if (parentNullable == null) {
            throw new NotFoundException("parent widget null");
        }

        return parentNullable;
    }

    @Override
    public int getParentId() {
        return CommonsKt.onGameThread(rl::getParentId);
    }

    public @Nullable Widget getChildOrNull(int index) {
        return wrap(rl.getChild(index));
    }

    @Override
    public @NotNull Widget getChild(int index) {
        Widget childOrNull = getChildOrNull(index);
        if (childOrNull == null) {
            throw new NotFoundException("child widget null index " + index);
        }
        return childOrNull;
    }

    @Override
    public @Nullable Widget @Nullable [] getChildren() {
        return Widget.wrap(rl.getChildren());
    }

    public @NotNull List<@NotNull Widget> getChildrenList() {
        Widget[] children = getChildren();
        if (children == null) {
            return Collections.emptyList();
        }

        ArrayList<Widget> out = new ArrayList<>();
        for (Widget child : children) {
            if (child != null) {
                out.add(child);
            }
        }

        return out;
    }

    @Override
    public boolean isHidden() {
        return CommonsKt.onGameThread(rl::isHidden);
    }

    @Override
    public String toString() {
        return "Widget(" + getParentId() + "" + getId() + ")";
    }

    @Override
    //kotlin.Unit causes nosuchmethoderror
    public @NotNull Object interact(@NotNull Function1<? super String, Boolean> actionMatches) {
        Interact.INSTANCE.withWidget(this, actionMatches);
        return new Object();
    }

    public static @Nullable Widget @Nullable [] wrap(@Nullable net.runelite.api.widgets.Widget @Nullable [] rl) {
        if (rl == null) {
            return null;
        }

        Widget[] out = new Widget[rl.length];
        for (int i = 0; i < rl.length; i++) {
            Widget wrapped = wrap(rl[i]);
            if (wrapped == null) {
                continue;
            }
            out[i] = wrapped;
        }

        return out;
    }

    public static @Nullable Widget wrap(@Nullable net.runelite.api.widgets.Widget rl) {
        if (rl == null) {
            return null;
        }

        Rectangle bounds = rl.getBounds();
        if (bounds == null) {
            LogKt.debug(() -> "bounds null at " + idToString(rl.getId()));
            return null;
        }

        if (bounds.getX() == -1) {
            LogKt.debug(() -> "bounds x -1 at " + idToString(rl.getId()));
            return null;
        }

        if (bounds.getY() == -1) {
            LogKt.debug(() -> "bounds y -1 at " + idToString(rl.getId()));
            return null;
        }

        return new Widget(rl);
    }

    private static @NotNull String idToString(int id) {
        return WidgetInfo.TO_GROUP(id) + "," + WidgetInfo.TO_CHILD(id);
    }
}
