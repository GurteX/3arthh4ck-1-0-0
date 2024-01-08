package me.earth.earthhack.impl.modules.other.autokit;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.impl.event.events.render.GuiScreenEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.client.gui.GuiGameOver;

final class ListenerScreens extends
        ModuleListener<AutoKit, GuiScreenEvent<GuiGameOver>>
{
    public ListenerScreens(AutoKit module)
    {
        super(module, GuiScreenEvent.class, GuiGameOver.class);
    }

    @Override
    public void invoke(GuiScreenEvent<GuiGameOver> event)
    {
        if (mc.player != null)
        {
            event.setCancelled(true);
            mc.player.respawnPlayer();
            module.timer.reset();
            module.shouldKit = true;
        }
    }

}
