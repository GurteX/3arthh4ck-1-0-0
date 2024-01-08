package me.earth.earthhack.impl.modules.other.autokill;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.potion.Potion;

import java.util.ArrayList;

final class ListenerLoop extends ModuleListener<AutoKill, GameLoopEvent> {
    public ListenerLoop(AutoKill module) {
        super(module, GameLoopEvent.class, -1);
    }


    @Override
    public void invoke(GameLoopEvent event) {
        if (mc.player != null && mc.world != null) {
            final ArrayList<Potion> sorted = new ArrayList<>(); // registering all potions arraylist
            for (final Potion potion : Potion.REGISTRY) {
                if (potion != null) {
                    if (mc.player.isPotionActive(potion)) {
                        sorted.add(potion);
                    }
                }
            }

            module.willDo = sorted.isEmpty();

            if (module.timer.passed(20000)) {
                module.enabledTimer.reset();
                module.timer.reset();
                if (sorted.isEmpty()) {
                    if (module.onSpawn.getValue() && Math.round(mc.player.posY) > 130) {
                        mc.addScheduledTask(() ->
                                mc.player.sendChatMessage("/kill"));
                    } else {
                        mc.addScheduledTask(() ->
                                mc.player.sendChatMessage("/kill"));
                    }
                }
                module.timer.reset();
            }
        }
    }
}
