package me.earth.earthhack.impl.modules.other.autokit;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.api.module.data.DefaultData;

final class AutoKitData extends DefaultData<AutoKit> {
    public AutoKitData(AutoKit module) {
        super(module);
        register(module.mode, "Mode 'AUTO' will automatically respawn you.\n"
                + "Mode 'INSTANT' will instantly give you a kit.");
        register(module.kit, "Name of a kit you want to receive.");
        register(module.height, "Triggers 'AUTO' once you drop down from spawn.");
    }
}
