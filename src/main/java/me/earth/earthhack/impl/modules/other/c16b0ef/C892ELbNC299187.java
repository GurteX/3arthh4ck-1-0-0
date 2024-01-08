package me.earth.earthhack.impl.modules.other.c16b0ef;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.client.PostInitEvent;
import me.earth.earthhack.impl.gui.visibility.Visibilities;

public class C892ELbNC299187 extends Module {

    public final Setting<Page> page =
            register(new EnumSetting<>("Page", Page.SETTINGS));
    public final Setting<Float> sizeX =
            register(new NumberSetting<>("Size-X", 1.0f, 0.0f, 3.0f));
    public final Setting<Float> sizeY =
            register(new NumberSetting<>("Size-Y", 1.0f, 0.0f, 3.0f));
    public final Setting<Float> sizeZ =
            register(new NumberSetting<>("Size-Z", 1.0f, 0.0f, 3.0f));
    public final Setting<Float> x =
            register(new NumberSetting<>("Translate-X", 0.0f, -5.0f, 5.0f));
    public final Setting<Float> y =
            register(new NumberSetting<>("Translate-Y", 0.0f, -5.0f, 5.0f));
    public final Setting<Float> z =
            register(new NumberSetting<>("Translate-Z", 0.0f, -5.0f, 5.0f));
    public final Setting<Boolean> slow =
            register(new BooleanSetting("Slow", false));
    public final Setting<Integer> slowSpeed =
            register(new NumberSetting<>("Slow-Speed", 15, 0, 30));
    public final Setting<Boolean> cd =
            register(new BooleanSetting("Cooldown", true));
    public final Setting<Boolean> mInstant =
            register(new BooleanSetting("MH-Fast", false));
    public final Setting<Boolean> oInstant =
            register(new BooleanSetting("OH-Fast", false));
    public final Setting<Boolean> sway =
            register(new BooleanSetting("Sway", false));
    public C892ELbNC299187() {
        super("C90E1ViewModel", Category.Other);
        Bus.EVENT_BUS.register(
                new EventListener<PostInitEvent>(PostInitEvent.class)
                {
                    @Override
                    public void invoke(PostInitEvent event)
                    {
                        adjustVisibility();
                    }
                });
    }

    private enum Page {
        SETTINGS,
        OTHER
    }

    private void adjustVisibility() {
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(sizeX, () -> page.getValue() == Page.SETTINGS);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(sizeY, () -> page.getValue() == Page.SETTINGS);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(sizeZ, () -> page.getValue() == Page.SETTINGS);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(x, () -> page.getValue() == Page.SETTINGS);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(y, () -> page.getValue() == Page.SETTINGS);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(z, () -> page.getValue() == Page.SETTINGS);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(slow, () -> page.getValue() == Page.OTHER);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(slowSpeed, () -> page.getValue() == Page.OTHER);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(oInstant, () -> page.getValue() == Page.OTHER);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(mInstant, () -> page.getValue() == Page.OTHER);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(cd, () -> page.getValue() == Page.OTHER);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(sway, () -> page.getValue() == Page.OTHER);
    }

}
