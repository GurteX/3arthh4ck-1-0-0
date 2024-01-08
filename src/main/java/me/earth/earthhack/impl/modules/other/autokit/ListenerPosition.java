package me.earth.earthhack.impl.modules.other.autokit;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerPosition extends ModuleListener<AutoKit, TickEvent> {
    public ListenerPosition(AutoKit module) {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event) {
        if (event.isSafe()) {
            if (module.shouldKit && module.height.getValue() && module.mode.getValue() == AutoKit.Mode.AUTO) {
                if (mc.player.posY <= 125 && module.timer.passed(300)) {
                    module.timer.reset();
                    mc.player.sendChatMessage("/kit " + module.kit.getValue());
                    module.shouldKit = false;
                }
            }
        }
    }
}
