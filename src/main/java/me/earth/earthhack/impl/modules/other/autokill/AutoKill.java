package me.earth.earthhack.impl.modules.other.autokill;

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
import me.earth.earthhack.impl.modules.combat.autotrap.AutoTrap;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.text.TextColor;

public class AutoKill extends Module {

    protected final Setting<Boolean> onSpawn =
            register(new BooleanSetting("OnlyOnSpawn", true));
    public AutoKill() {
        super("AutoKill", Category.Other);
        this.listeners.add(new ListenerLoop(this));
        this.setData(new AutoKillData(this));
    }

    protected boolean willDo;
    protected final StopWatch enabledTimer = new StopWatch();
    protected final StopWatch timer = new StopWatch();

    @Override
    public String getDisplayInfo() {
        return willDo ? TextColor.RED + enabledTimer.getTime() : TextColor.GREEN + enabledTimer.getTime();
    }

    @Override
    public void onEnable() {
        enabledTimer.reset();
        timer.reset();
    }

    @Override
    public void onDisable() {
        enabledTimer.reset();
        timer.reset();
    }
}
