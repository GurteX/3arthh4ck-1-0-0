package me.earth.earthhack.impl.modules.other.autokill;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.api.module.data.DefaultData;

final class AutoKillData extends DefaultData<AutoKill> {
    public AutoKillData(AutoKill module) {
        super(module);
        register(module.onSpawn, "Only allow /kill on spawn");
    }
}
