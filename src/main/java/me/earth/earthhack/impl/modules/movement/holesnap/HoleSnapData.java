package me.earth.earthhack.impl.modules.movement.holesnap;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.api.module.data.DefaultData;

final class HoleSnapData extends DefaultData<HoleSnap>
{
    public HoleSnapData(HoleSnap module)
    {
        super(module);
        register(module.mode, "-Motion: Will use timer to get into hole.\n-" +
                "Instant: Instant (Recommended with low range) .");
        register(module.wait, "Waits for a hole instead of disabling if no holes are found");
        register(module.strict, "For some servers, probably best for Instant mode");
        register(module.factor, "Pulling factor");
        register(module.step, "Enables Step for you if you don't have it on");
        register(module.range, "Hole search range (Recommended 2-3 for most servers)");
        register(module.fov, "If you want to only snap into holes you can see");
        register(module.stop, "Disables some modules, to prevent inaccuracy");
        register(module.Stop, "Stops your motion in hole (2x1, 2x2)");
        register(module.wide, "2x1 holes, not recommended due to wrong centering possibility");
        register(module.wideAf, "2x2 holes, not recommended due to wrong centering possibility");
        register(module.notify, "Sends a message of current module stage");
    }



    @Override
    public String getDescription()
    {
        return "Anchor, but better.";
    }

}