package me.earth.earthhack.impl.modules.other.fasthole;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.api.module.data.DefaultData;

final class FastHoleData extends DefaultData<FastHole> {
    public FastHoleData(FastHole module) {
        super(module);
        register(module.activeKey, "A key you want to use");
        register(module.fadeTime, "Time for render to fade out");
        register(module.render, "Renders your past positions");
        register(module.useTimer, "If you want to use timer or not");
    }

    @Override
    public String getDescription()
    {
        return "Blinks from your hole somewhere else";
    }
}
