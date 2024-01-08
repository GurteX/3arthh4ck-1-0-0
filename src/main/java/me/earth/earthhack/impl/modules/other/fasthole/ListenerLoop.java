package me.earth.earthhack.impl.modules.other.fasthole;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.impl.event.events.misc.GameLoopEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;

final class ListenerLoop extends ModuleListener<FastHole, GameLoopEvent> {
    public ListenerLoop(FastHole module) {
        super(module, GameLoopEvent.class);
    }
    @Override
    public void invoke(GameLoopEvent event) {
        if (mc.player != null && mc.world != null) {
            if (!KeyBoardUtil.isKeyDown(module.activeKey) && module.shouldDisable) {
                if (module.tpPost.getValue() && !module.HOLESNAP.isEnabled()) {
                    module.HOLESNAP.enable();
                }
                module.enabledTimer.reset();
                module.lC92N82();
            }
            if (module.isInHole()) {
                module.lC92N82();
            }
            if (KeyBoardUtil.isKeyDown(module.activeKey)) {
                module.shouldDisable = true;
                module.holeManager.reset();
                module.reset();
                if (module.step.getValue()) {
                    module.STEP.enable();
                }
                if (module.useTimer.getValue()) {
                    Managers.TIMER.setTimer(module.timerAmount.getValue());
                }
            } else {
                module.enabledTimer.reset();
            }
        }
    }
}
