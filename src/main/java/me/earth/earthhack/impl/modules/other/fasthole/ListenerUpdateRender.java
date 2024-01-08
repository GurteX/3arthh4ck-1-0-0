package me.earth.earthhack.impl.modules.other.fasthole;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import net.minecraft.client.entity.AbstractClientPlayer;

final class ListenerUpdateRender extends ModuleListener<FastHole, MoveEvent> {
    public ListenerUpdateRender(FastHole module) {
        super(module, MoveEvent.class);
    }

    @Override
    public void invoke(MoveEvent event) {
        if (module.renderTimer.passed(module.renderDelay.getValue()) && KeyBoardUtil.isKeyDown(module.activeKey)) {
            module.renderTimer.reset();
            AbstractClientPlayer player = mc.player;

            module.getPosDataList().add(new FastHole.PosData(PlayerUtil.copyPlayer(player, true),
                    System.currentTimeMillis(),
                    player.posX,
                    player.posY,
                    player.posZ,
                    player.getSkinType().equals("slim")));
        }
    }
}
