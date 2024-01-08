package me.earth.earthhack.impl.modules.movement.holesnap;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.event.bus.EventListener;
import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.ducks.world.IChunk;
import me.earth.earthhack.impl.event.events.client.PostInitEvent;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.gui.visibility.Visibilities;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.flight.Flight;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.movement.speed.SpeedMode;
import me.earth.earthhack.impl.modules.movement.step.Step;
import me.earth.earthhack.impl.modules.movement.step.StepMode;
import me.earth.earthhack.impl.modules.movement.tickshift.TickShift;
import me.earth.earthhack.impl.modules.player.blink.Blink;
import me.earth.earthhack.impl.modules.player.timer.Timer;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.AirHoleFinder;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.Hole;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.HoleManager;
import me.earth.earthhack.impl.modules.render.holeesp.invalidation.SimpleHoleManager;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import java.util.Comparator;


// TODO: Maybe it shouldn't be this complicated? And, a better 2x1 facing check. (Hopefully 2x2 works fine)
public class HoleSnap extends Module {

    protected final Setting<Page> page =
            register(new EnumSetting<>("Page", Page.Main));
    protected final Setting<Mode> mode =
            register(new EnumSetting<>("Mode", Mode.Motion));
    protected final Setting<Integer> range =
            register(new NumberSetting<>("Range", 2, 0, 10));
    protected final Setting<Float> factor =
            register(new NumberSetting<>("Factor", 2.f, 0.f, 15.f));
    protected final Setting<Boolean> step =
            register(new BooleanSetting("Step", false));
    protected final Setting<Boolean> Stop =
            register(new BooleanSetting("Stop", false));
    protected final Setting<Boolean> notify =
            register(new BooleanSetting("Notify", false));
    protected final Setting<Boolean> wide =
            register(new BooleanSetting("2x1", false));
    protected final Setting<Boolean> wideAf =
            register(new BooleanSetting("2x2", false));
    protected final Setting<Boolean> strict =
            register(new BooleanSetting("Y-Strict", false));
    protected final Setting<Boolean> stop =
            register(new BooleanSetting("Sync", false));
    protected final Setting<Boolean> fov =
            register(new BooleanSetting("Facing", false));
    protected final Setting<Boolean> wait =
            register(new BooleanSetting("Wait", false));

    protected enum Mode {
        Motion,
        Instant
    }
    protected enum Page {
        Main,
        Filter
    }
    protected final ModuleCache<Step> STEP =
            Caches.getModule(Step.class);
    protected final ModuleCache<Speed> SPEED =
            Caches.getModule(Speed.class);
    protected final ModuleCache<LongJump> LONG_JUMP =
            Caches.getModule(LongJump.class);
    protected final ModuleCache<Flight> FLIGHT =
            Caches.getModule(Flight.class);
    protected final ModuleCache<Timer> TIMER =
            Caches.getModule(Timer.class);
    protected final ModuleCache<TickShift> TICKSHIFT =
            Caches.getModule(TickShift.class);
    protected final ModuleCache<Blink> BLINK =
            Caches.getModule(Blink.class);
    protected final StopWatch timer = new StopWatch();
    private final HoleManager holeManager = new SimpleHoleManager();
    private final AirHoleFinder holeFinder = new AirHoleFinder(holeManager);
    public HoleSnap() {
        super("HoleSnap", Category.Movement);
        this.setData(new HoleSnapData(this));
        this.listeners.add(new LambdaListener<>(MotionUpdateEvent.class, event -> {
            if (mc.player != null && mc.world != null) {
                BlockPos pos = mc.player.getPosition();
                holeFinder.setChunk((IChunk) mc.world.getChunk(pos));
                int value = range.getValue();
                holeFinder.setMaxX(pos.getX() + value);
                holeFinder.setMinX(pos.getX() - value);
                holeFinder.setMaxY(pos.getY() + value);
                holeFinder.setMinY(pos.getY() - value);
                holeFinder.setMaxZ(pos.getZ() + value);
                holeFinder.setMinZ(pos.getZ() - value);
                holeFinder.calcHoles();
                Hole hole = holeManager.getHoles()
                        .values()
                        .stream()
                        .filter(h -> !wide.getValue()
                                ? !h.is2x1()
                                : h.isValid())
                        .filter(h -> !wideAf.getValue()
                                ? !h.is2x2()
                                : h.isValid())
                        .filter(h -> !fov.getValue()
                                || RotationUtil
                                .inFov(h.getX(), h.getY(), h.getZ()))
                        .min(Comparator.comparingDouble(this::getDistance))
                        .orElse(null);
                if (hole == null) {
                    if (!wait.getValue()) {
                        if (notify.getValue()) {
                            ChatUtil.sendMessage("<HoleSnap> Unable to find a hole");
                            return;
                        }
                        disable();
                    }
                    return;
                }
                if (isInHole()) {
                    if (Stop.getValue() && mode.getValue() == Mode.Instant) {
                        mc.player.motionY = 0;
                        mc.player.motionX = 0;
                        mc.player.motionZ = 0;
                    }
                    if (notify.getValue()) {
                        ChatUtil.sendMessage("<HoleSnap> Finished");
                        return;
                    }
                    disable();
                    return;
                }
                if (PositionUtil.inLiquid() || mc.player.isOnLadder()) {
                    disable();
                    return;
                }
                Vec3d player;
                Vec3d targetPos;
                if (!hole.is2x1() && !hole.is2x2()) {
                    if (mode.getValue() == Mode.Motion) {
                        player = mc.player.getPositionVector();
                        targetPos = new Vec3d(hole.getX() + 0.5f, mc.player.posY, hole.getZ() + 0.5f);
                        double yawRad = Math.toRadians(RotationUtil.getRotationTo(player, targetPos).x);
                        double dist = player.distanceTo(targetPos);
                        double speed = HoleSnap.mc.player.onGround ? -Math.min(0.2805, dist / 2.0) : -MovementUtil.getSpeed() + 0.02;
                        Managers.TIMER.setTimer(factor.getValue());
                        mc.player.motionX = -Math.sin(yawRad) * speed;
                        mc.player.motionZ = Math.cos(yawRad) * speed;
                    } else {
                        if (strict.getValue() && !isVertical(hole)) {
                            mc.player.setPosition(hole.getX() + 0.5f, hole.getY(), hole.getZ() + 0.5f);
                        } else {
                            mc.player.setPosition(hole.getX() + 0.5f, hole.getY(), hole.getZ() + 0.5f);
                        }
                        disable();
                    }
                }
                if (hole.is2x1() && wide.getValue()) {
                    if (mode.getValue() == Mode.Motion) {
                        player = mc.player.getPositionVector();
                        targetPos = new Vec3d(hole.getX() + 0.5f, mc.player.posY, hole.getZ() + 0.5f);  // 0.5 // 1
                        double yawRad = Math.toRadians(RotationUtil.getRotationTo(player, targetPos).x);
                        double dist = player.distanceTo(targetPos);
                        double speed = HoleSnap.mc.player.onGround ? -Math.min(0.2805, dist / 2.0) : -MovementUtil.getSpeed() + 0.02;
                        Managers.TIMER.setTimer(factor.getValue());
                        mc.player.motionX = -Math.sin(yawRad) * speed;
                        mc.player.motionZ = Math.cos(yawRad) * speed;
                    } else {
                        if (strict.getValue() && !isVertical(hole)) {
                            mc.player.setPosition(hole.getX() + 0.5f, hole.getY(), hole.getZ() + 0.5f);
                        } else {
                            mc.player.setPosition(hole.getX() + 0.5f, hole.getY(), hole.getZ() + 0.5f);
                        }
                        disable();
                    }
                }
                if (hole.is2x2() && wideAf.getValue()) {
                    if (mode.getValue() == Mode.Motion) {
                        player = mc.player.getPositionVector();
                        targetPos = new Vec3d(hole.getX() + 1f, mc.player.posY, hole.getZ() + 1f);  // 0.5 // 1
                        double yawRad = Math.toRadians(RotationUtil.getRotationTo(player, targetPos).x);
                        double dist = player.distanceTo(targetPos);
                        double speed = HoleSnap.mc.player.onGround ? -Math.min(0.2805, dist / 2.0) : -MovementUtil.getSpeed() + 0.02;
                        Managers.TIMER.setTimer(factor.getValue());
                        mc.player.motionX = -Math.sin(yawRad) * speed;
                        mc.player.motionZ = Math.cos(yawRad) * speed;
                    } else {
                        if (strict.getValue() && !isVertical(hole)) {
                            mc.player.setPosition(hole.getX() + 1f, hole.getY(), hole.getZ() + 1f);
                        } else {
                            mc.player.setPosition(hole.getX() + 1f, hole.getY(), hole.getZ() + 1f);
                        }
                        disable();
                    }
                }
            }
        }));
        Bus.EVENT_BUS.register(
                new EventListener<PostInitEvent>(PostInitEvent.class)
                {
                    @Override
                    public void invoke(PostInitEvent event)
                    {
                        adjustVisibility();
                    }
                });
    }
    private void adjustVisibility() {
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(mode, () -> page.getValue() == Page.Main);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(range, () -> page.getValue() == Page.Main);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(strict, () -> mode.getValue() == Mode.Instant && page.getValue() == Page.Main);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(stop, () -> page.getValue() == Page.Main);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(wideAf, () -> page.getValue() == Page.Filter);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(wide, () -> page.getValue() == Page.Filter);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(factor, () -> mode.getValue() == Mode.Motion && page.getValue() == Page.Main);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(step, () -> mode.getValue() == Mode.Motion && page.getValue() == Page.Main);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(fov, () -> page.getValue() == Page.Filter);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(strict, () -> page.getValue() == Page.Filter && mode.getValue() == Mode.Instant);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(Stop, () -> page.getValue() == Page.Main && mode.getValue() == Mode.Instant);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(wait, () -> page.getValue() == Page.Main);
        Visibilities.VISIBILITY_MANAGER
                .registerVisibility(notify, () -> page.getValue() == Page.Main);
    }
    @Override
    public void onEnable() {
        if (step.getValue() && !STEP.isEnabled() && STEP.get().mode.getValue() != StepMode.Slow) {
            STEP.enable();
        }
        if (stop.getValue()) {
            LONG_JUMP.disable();
            FLIGHT.disable();
            TICKSHIFT.disable();
            TIMER.disable();
            BLINK.disable();
            SPEED.disable();
        }
        holeManager.reset();
    }
    @Override
    public void onDisable() {
        if (step.getValue() && STEP.isEnabled() && STEP.get().mode.getValue() != StepMode.Slow) {
            STEP.disable();
        }
        timer.reset();
        Managers.TIMER.reset();
        if (SPEED.get().getMode() != SpeedMode.Instant) {
            SPEED.disable();
        }
    }
    private boolean isInHole() {
        return holeManager
                .getHoles()
                .values()
                .stream()
                .anyMatch(h -> h.contains(mc.player.posX, mc.player.posY, mc.player.posZ));
    }
    private double getDistance(Hole hole)
    {
        double holeX = hole.getX() + (hole.getMaxX() - hole.getX()) / 2.0;
        double holeY = hole.getY();
        double holeZ = hole.getZ() + (hole.getMaxZ() - hole.getZ()) / 2.0;
        return mc.player.getDistanceSq(holeX, holeY, holeZ);
    }

    @SuppressWarnings("all")
    private boolean isVertical(Hole hole) {
        double motionY = mc.player.motionY;
        return motionY >= 0.0;
    }
}

