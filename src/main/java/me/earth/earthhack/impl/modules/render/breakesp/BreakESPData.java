package me.earth.earthhack.impl.modules.render.breakesp;

import me.earth.earthhack.api.module.data.DefaultData;

public class BreakESPData extends DefaultData<BreakESP> {
    public BreakESPData(BreakESP module) {
        super(module);
        register(module.dbl, "Render additional box, without animation");
        register(module.removeTime, "Time to remove the block breaking position");
        register(module.reset, "Remove break position on block break");
        register(module.reverse, "Reverse render");
        register(module.time, "Break time (we can only assume, but usually its 2.2)");
    }
}
