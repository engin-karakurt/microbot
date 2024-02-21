package net.runelite.client.plugins.microbot.util.magic;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;
import org.apache.commons.lang3.NotImplementedException;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class Rs2Magic {
    public boolean canCast(MagicAction magicSpell) {
        return Microbot.getClient().getRealSkillLevel(Skill.MAGIC) >= magicSpell.getLevel();
    }

    public static void cast(MagicAction magicSpell) {
        MenuAction menuAction;
        if (magicSpell.getWidgetAction() == null) {
            if (magicSpell.getName().toLowerCase().contains("teleport") || magicSpell.getName().toLowerCase().contains("enchant")) {
                menuAction = MenuAction.CC_OP;
            } else {
                menuAction = MenuAction.WIDGET_TARGET;
            }
        } else {
            menuAction = magicSpell.getWidgetAction();
        }

        if (magicSpell.getWidgetId() == -1)
            throw new NotImplementedException("This spell has not been configured yet in the MagicAction.java class");

        Rs2Reflection.invokeMenu(-1, magicSpell.getWidgetId(), menuAction.getId(), 1, -1, "Cast", "<col=00ff00>" + magicSpell.getName() + "</col>", -1, -1);
    }

    public static void castOn(MagicAction magicSpell, Actor actor) {
        if (actor == null) return;
        if (!Rs2Camera.isTileOnScreen(actor.getLocalLocation())) {
            Rs2Camera.turnTo(actor.getLocalLocation());
            return;
        }
        cast(magicSpell);
        Point point = Perspective.localToCanvas(Microbot.getClient(), actor.getLocalLocation(), Microbot.getClient().getPlane());
        Microbot.getMouse().click(point);
    }

    public static void highAlch(String itemName, boolean exact) {
        Rs2Tab.switchToMagicTab();
        Widget item = Inventory.findItemInMemory(itemName, exact);
        Widget highAlch = Microbot.getClient().getWidget(MagicAction.HIGH_LEVEL_ALCHEMY.getWidgetId());
        alch(highAlch, item);
    }

    public static void highAlch(String itemName) {
        highAlch(itemName, false);
    }


    public static void highAlch(Widget item, boolean exact) {
        Rs2Tab.switchToMagicTab();
        Widget highAlch = Microbot.getClient().getWidget(MagicAction.HIGH_LEVEL_ALCHEMY.getWidgetId());
        alch(highAlch, item);
    }

    public static void highAlch(Widget widget) {
        highAlch(widget, false);
    }


    public static void highAlch() {
        Widget highAlch = Microbot.getClient().getWidget(MagicAction.HIGH_LEVEL_ALCHEMY.getWidgetId());
        alch(highAlch);
    }

    public static void lowAlch() {
        Widget lowAlch = Microbot.getClient().getWidget(MagicAction.LOW_LEVEL_ALCHEMY.getWidgetId());
        alch(lowAlch);
    }

    private static void alch(Widget alch, Widget item) {
        if (alch == null) return;
        Point point = new Point((int) alch.getBounds().getCenterX(), (int) alch.getBounds().getCenterY());
        sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Rs2Tab.getCurrentTab() == InterfaceTab.MAGIC), 5000);
        sleep(300, 600);
        Microbot.getMouse().click(point);
        sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Rs2Tab.getCurrentTab() == InterfaceTab.INVENTORY), 5000);
        sleep(300, 600);
        if (item == null) {
            Microbot.getMouse().click(point);
        } else {
            Inventory.useItemFast(item, "cast");
        }
    }

    private static void alch(Widget alch) {
        alch(alch, null);
    }

    public static void handleMenuSwapper(MenuEntry menuEntry) {
        if (widgetId == 0) return;
        menuEntry.setOption("Cast");
        menuEntry.setIdentifier(1);
        menuEntry.setParam0(-1);
        menuEntry.setTarget("<col=00ff00>" + Rs2Magic.widgetName + "</col>");
        menuEntry.setType(Rs2Magic.widgetAction);
        menuEntry.setParam1(Rs2Magic.widgetId);

    }
}