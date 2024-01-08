package me.earth.earthhack.impl.modules.other.fasthole;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import net.minecraft.network.play.client.CPacketPlayer;

final class ListenerPacket extends ModuleListener<FastHole, PacketEvent.Send<?>>
{
    public ListenerPacket(FastHole module)
    {
        super(module, PacketEvent.Send.class);
    }

    @Override
    public void invoke(PacketEvent.Send<?> event)
    {
        if (module.onLeave.getValue() && !module.isInHole()) {
            return;
        }
        if (KeyBoardUtil.isKeyDown(module.activeKey)) {
            if (event.getPacket() instanceof CPacketPlayer) {
                mc.addScheduledTask(() -> module.packets.add(event.getPacket()));
                event.setCancelled(true);
            }
        }
    }

}