package github.dqw4w9wgxcq.botapi.wrappers.widget;

import github.dqw4w9wgxcq.botapi.commons.CommonsKt;
import github.dqw4w9wgxcq.botapi.commons.LogKt;
import github.dqw4w9wgxcq.botapi.commons.NotFoundException;
import github.dqw4w9wgxcq.botapi.interact.Interact;
import github.dqw4w9wgxcq.botapi.wrappers.Identifiable;
import github.dqw4w9wgxcq.botapi.wrappers.Interactable;
import github.dqw4w9wgxcq.botapi.wrappers.RlWrapper;
import kotlin.jvm.functions.Function1;
import lombok.experimental.Delegate;
import net.runelite.api.widgets.WidgetInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Widget extends RlWrapper<net.runelite.api.widgets.Widget> implements net.runelite.api.widgets.Widget, Interactable, Identifiable {
    public static @Nullable Widget @NotNull [] wrap(net.runelite.api.widgets.Widget[] rls) {
        if (rls == null) {
            return new Widget[0];
        }
        Widget[] out = new Widget[rls.length];
        for (int i = 0; i < rls.length; i++) {
            Widget wrapped = wrap(rls[i]);
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
            LogKt.debug(() -> "bounds null at " + parseId(rl.getId()));
            return null;
        }

        if (bounds.getX() == -1) {
            LogKt.debug(() -> "bounds x -1 at " + parseId(rl.getId()));
            return null;
        }

        if (bounds.getY() == -1) {
            LogKt.debug(() -> "bounds y -1 at " + parseId(rl.getId()));
            return null;
        }

        return new Widget(rl);
    }

    private static @NotNull String parseId(int id) {
        return WidgetInfo.TO_GROUP(id) + "," + WidgetInfo.TO_CHILD(id);
    }

    private Widget(@NotNull net.runelite.api.widgets.Widget rl) {
        super(rl);
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
        return super.getRl();
    }

    @Override
    public @NotNull Rectangle getBounds() {
        Rectangle out = getRl().getBounds();
        if (out == null || out.x == -1 || out.y == -1) {
            throw new RuntimeException("bounds should have been filtered by this point:" + out);
        }
        return out;
    }

    @Override
    public @Nullable Object[] getOnOpListener() {
        return CommonsKt.onGameThread(() -> getRl().getOnOpListener());
    }

    @Nullable
    public Widget getParentOrNull() {
        net.runelite.api.widgets.Widget parentNullable = CommonsKt.onGameThread(() -> getRl().getParent());
        if (parentNullable == null) {
            return null;
        }
        return new Widget(parentNullable);
    }

    @Override
    @NotNull
    public Widget getParent() {
        Widget parentNullable = getParentOrNull();
        if (parentNullable == null) {
            throw new NotFoundException("parent widget null");
        }

        return parentNullable;
    }

    @Override
    public int getParentId() {
        return CommonsKt.onGameThread(getRl()::getParentId);
    }

    @Nullable
    public Widget getChildOrNull(int index) {
        return wrap(getRl().getChild(index));
    }

    @Override
    @NotNull
    public Widget getChild(int index) {
        Widget childOrNull = getChildOrNull(index);
        if (childOrNull == null) {
            throw new NotFoundException("child widget null index " + index);
        }
        return childOrNull;
    }

    @Override
    public @Nullable Widget @NotNull [] getChildren() {
        return Widget.wrap(getRl().getChildren());
    }

    public @NotNull List<@NotNull Widget> getChildrenList() {
        ArrayList<Widget> out = new ArrayList<>();
        for (Widget child : getChildren()) {
            if (child != null) {
                out.add(child);
            }
        }
        return out;
    }

    @Override
    public boolean isHidden() {
        return CommonsKt.onGameThread(getRl()::isHidden);
    }

    @Override
    public String toString() {
        return "Widget(" + getParentId() + "" + getId() + ")";
    }

    //Unit return causes nosuchmethoderror
    @NotNull
    @Override
    public Object interact(@NotNull Function1<? super String, Boolean> actionMatches) {
        Interact.INSTANCE.withWidget(this, actionMatches);
        return new Object();
    }
}
