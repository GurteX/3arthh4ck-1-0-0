package me.earth.earthhack.impl.modules.other.autokit;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.util.math.StopWatch;

public class AutoKit extends Module {
    public AutoKit() {
        super("AutoKit", Category.Other);
        this.listeners.add(new ListenerScreens(this));
        this.listeners.add(new ListenerMove(this));
        this.listeners.add(new ListenerPosition(this));
        this.setData(new AutoKitData(this));
    }

    protected final StopWatch timer = new StopWatch();
    protected boolean shouldKit;

    protected final Setting<Mode> mode =
            register(new EnumSetting<>("Mode", Mode.AUTO));
    protected final StringSetting kit =
            register(new StringSetting("Kit", "1"));
    protected final Setting<Boolean> height =
            register(new BooleanSetting("OnDrop", false));

    protected enum Mode {
        AUTO,
        INSTANT
    }

    @Override
    protected void onEnable() {
        if (mode.getValue() == Mode.AUTO) {
            timer.reset();
            shouldKit = false;
        } else {
            mc.player.sendChatMessage("/kit " + kit.getValue());
            disable();
        }
    }

    @Override
    protected void onDisable() {
        if (mode.getValue() == Mode.AUTO) {
            timer.reset();
            shouldKit = false;
        } else {
            super.disable();
        }
    }
}