package me.earth.earthhack.impl.modules.movement.accel;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.api.module.data.DefaultData;;

public class AccelerationData extends DefaultData<Acceleration> {
    public AccelerationData(Acceleration module) {
        super(module);
        register(module.accountSlow, "To check for slowness effect (Recommended)");
        register(module.fastStop, "If you want to stop after you stop moving (Instant)");
        register(module.inAir, "If you want it to work in Air");
        register(module.strict, "Strict mode, for strict servers");
        register(module.superStrict, "Very Strict, recommended for GrimAC");
        register(module.info, "Information in the brackets");
    }

    @Override
    public String getDescription() {
        return "Makes you move faster";
    }
}
