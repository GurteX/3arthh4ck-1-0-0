package me.earth.earthhack.impl.modules.render.breakesp;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.helpers.render.BlockESPModule;
import me.earth.earthhack.impl.util.math.StopWatch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BreakESP extends BlockESPModule {
    protected final Setting<Double> range =
            register(new NumberSetting<>("Range", 10.0, 0.0, 100.0));
    protected final Setting<Color> growColor =
            register(new ColorSetting("Grow-Color", new Color(255, 255, 255, 255)));
    protected final Setting<Color> growColorOutline =
            register(new ColorSetting("Grow-Color-Outline", new Color(255, 255, 255, 255)));
    protected final Setting<Float> growLineWidth =
            register(new NumberSetting<>("Grow-Width", 1.5f, 0.0f, 3.0f));
    public final Setting<Boolean> reverse =
            register(new BooleanSetting("Reverse-Render", false));
    public final Setting<Boolean> reset =
            register(new BooleanSetting("Reset-Break", false));
    public final Setting<Boolean> dbl =
            register(new BooleanSetting("Double-Box", false));
    protected final Setting<Integer> time =
            register(new NumberSetting<>("Time", 2200, 0, 3000));
    protected final Setting<Integer> removeTime =
            register(new NumberSetting<>("Remove-Time", 2500, 0, 10000));

    protected final StopWatch timer = new StopWatch();
    protected final StopWatch renderTimer = new StopWatch();
    protected double distance;
    protected EntityPlayer player;
    protected final Map<BlockPos, Long> posList = new HashMap<>();
    public BreakESP() {
        super("BreakESP", Category.Render);
        this.listeners.add(new ListenerBreak(this));
        this.listeners.add(new ListenerPacket(this));
        this.listeners.add(new ListenerRender(this));
        this.setData(new BreakESPData(this));
    }

    @Override
    public void onDisable() {
        reset();
    }

    @Override
    public void onEnable() {
        reset();
    }

    public void reset() {
        posList.clear();
        timer.reset();
        renderTimer.reset();
    }
    @SuppressWarnings("all")
    public class BlockBreakData {
        private EntityPlayer player;

        private int slot;


        public BlockBreakData(EntityPlayer player, int slot) {
            this.player = player;
            this.slot = slot;
        }

        // this to reset the render if player's item slot is changed because its supposed to reset once that happens ex-> in Future.
        // im actually too lazy to do that so it might never actually be a thing (not really required, just like on ground check)
    }
}