package me.earth.earthhack.impl.modules.other.fasthole;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.*;
import me.earth.earthhack.api.util.bind.Bind;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.holesnap.HoleSnap;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.Hole;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.HoleManager;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.SimpleHoleManager;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.minecraft.KeyBoardUtil;
import me.earth.earthhack.impl.util.misc.collections.CollectionUtil;
import me.earth.earthhack.impl.util.render.entity.StaticModelPlayer;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;

import java.awt.*;
import java.util.*;
import java.util.List;

public class FastHole extends Module {
    protected final HoleManager holeManager = new SimpleHoleManager();

    protected final Setting<Bind> activeKey =
            register(new BindSetting("Key", Bind.none()));
    protected final Setting<Boolean> step =
            register(new BooleanSetting("Step", false));
    protected final Setting<Boolean> useTimer =
            register(new BooleanSetting("Timer", false));
    protected final Setting<Boolean> onLeave =
            register(new BooleanSetting("OnHoleLeave", false));
    protected final Setting<Boolean> tpPost =
            register(new BooleanSetting("PostSnap", false));
    protected final Setting<Float> timerAmount =
            register(new NumberSetting<>("Timer-Amount", 1.1f, 1.0f, 2.0f));
    protected final Setting<Boolean> render =
            register(new BooleanSetting("Render", false));
    protected final Setting<Integer> fadeTime =
            register(new NumberSetting<>("Fade-Time", 500, 0, 5000));
    public final ColorSetting selfColor =
            register(new ColorSetting("Render-Color", new Color(80, 80, 255, 80)));
    public final ColorSetting selfOutline =
            register(new ColorSetting("Render-Outline", new Color(80, 80, 255, 255)));
    protected final Setting<Integer> renderDelay =
            register(new NumberSetting<>("RenderDelay", 500, 0, 8000));

    protected boolean shouldDisable;

    public FastHole() {
        super("FastHole", Category.Other);
        this.listeners.add(new ListenerLoop(this));
        this.listeners.add(new ListenerPacket(this));
        this.listeners.add(new ListenerRender(this));
        this.listeners.add(new ListenerUpdateRender(this));
        this.setData(new FastHoleData(this));
    }
    protected final StopWatch timer = new StopWatch();
    protected final StopWatch enabledTimer = new StopWatch();

    protected final StopWatch renderTimer = new StopWatch();
    protected final ModuleCache<Step> STEP =
            Caches.getModule(Step.class);
    private final List<FastHole.PosData> posDataList = new ArrayList<>();
    @SuppressWarnings("all")
    protected final ModuleCache<HoleSnap> HOLESNAP =
            Caches.getModule(HoleSnap.class);
    protected final Queue<Packet<?>> packets = new LinkedList<>();
    @Override
    protected void onEnable() {
        shouldDisable = false;
        enabledTimer.reset();
        reset();
    }

    private boolean isKey() {
        return KeyBoardUtil.isKeyDown(activeKey);
    }
    @Override
    protected void onDisable() {
        shouldDisable = false;
        reset();
        Managers.TIMER.reset();
        if (mc.getConnection() != null)
        {
            CollectionUtil.emptyQueue(packets, p -> mc.getConnection()
                    .sendPacket(p));
        }
        else
        {
            packets.clear();
        }
        enabledTimer.reset();
    }

    public void lC92N82() {
        shouldDisable = false;
        reset();
        if (step.getValue()) {
            STEP.disable();
        }
        if (useTimer.getValue()) {
            Managers.TIMER.reset();
        }
        if (mc.getConnection() != null)
        {
            CollectionUtil.emptyQueue(packets, p -> mc.getConnection()
                    .sendPacket(p));
        }
        else
        {
            packets.clear();
        }
    }
    public void reset() {
        timer.reset();
        // yea
    }
    public boolean isInHole() {
        return holeManager
                .getHoles()
                .values()
                .stream()
                .anyMatch(h -> h.contains(mc.player.posX, mc.player.posY, mc.player.posZ));
    }

    public double getClosestHoleRange() {
        Hole hole =  holeManager
                .getHoles()
                .values()
                .stream()
                .min(Comparator.comparingDouble(this::getDistance))
                .orElse(null);

        return hole != null ? getDistance(hole) : 0.0;

    }
    private double getDistance(Hole hole)
    {
        double holeX = hole.getX() + (hole.getMaxX() - hole.getX()) / 2.0;
        double holeY = hole.getY();
        double holeZ = hole.getZ() + (hole.getMaxZ() - hole.getZ()) / 2.0;
        return mc.player.getDistanceSq(holeX, holeY, holeZ);
    }
    @Override
    public String getDisplayInfo() {
        return isKey() ? getInfoTimer() + getInfoRange() : TextColor.GRAY + "Hold";
    }
    private String getInfoColor() {
        long time = enabledTimer.getTime();
        return time > 500
                ? TextColor.RED
                : time < 100
                ? TextColor.GREEN
                : TextColor.YELLOW;
    }

    private String getInfoTimer() {
        return getInfoColor() + enabledTimer.getTime() + "ms" + TextColor.GRAY + ", " + TextColor.RESET;
    }
    private String getInfoRange() {
        double range = getClosestHoleRange();
        return range == 0.0
                ? TextColor.RED + "None"
                : range > 6
                ? TextColor.RED + range
                : range < 3
                ? TextColor.GREEN + range
                : TextColor.YELLOW + range;
    }
    protected Color getColor() {
        return selfColor.getValue();
    }
    protected Color getOutlineColor() {
        return selfOutline.getValue();
    }
    public List<FastHole.PosData> getPosDataList() {
        return posDataList;
    }
    public static class PosData {
        private final EntityPlayer player;
        private final StaticModelPlayer model;
        private final long time;
        private final double x;
        private final double y;
        private final double z;

        public PosData(EntityPlayer player, long time, double x, double y, double z, boolean slim) {
            this.player = player;
            this.time = time;
            this.x = x;
            this.y = y - (player.isSneaking() ? 0.125 : 0);
            this.z = z;
            this.model = new StaticModelPlayer(player, slim, 0);
            this.model.disableArmorLayers();
        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public long getTime() {
            return time;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public StaticModelPlayer getModel() {
            return model;
        }
    }
}
