package me.earth.earthhack.impl.modules.other.autokit;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerMove extends ModuleListener<AutoKit, MoveEvent> {
    public ListenerMove(AutoKit module) {
        super(module, MoveEvent.class);
    }
    @Override
    public void invoke(MoveEvent event) {
        if (mc.player != null && mc.world != null) {
            if (module.shouldKit && !module.height.getValue() && module.timer.passed(300) && module.mode.getValue() == AutoKit.Mode.AUTO) {
                mc.player.sendChatMessage("/kit " + module.kit.getValue());
                module.shouldKit = false;
            }
        }
    }
}
