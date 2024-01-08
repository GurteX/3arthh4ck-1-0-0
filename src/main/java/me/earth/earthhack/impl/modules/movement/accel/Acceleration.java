package me.earth.earthhack.impl.modules.movement.accel;

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
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.EnumSetting;
import me.earth.earthhack.impl.event.events.movement.MoveEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.flight.Flight;
import me.earth.earthhack.impl.modules.movement.longjump.LongJump;
import me.earth.earthhack.impl.modules.movement.speed.Speed;
import me.earth.earthhack.impl.modules.player.timer.Timer;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.math.StopWatch;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import me.earth.earthhack.impl.util.text.TextColor;

public class Acceleration extends Module {

    private final ModuleCache<Speed> SPEED =
            Caches.getModule(Speed.class);
    public Acceleration() {
        super("Acceleration", Category.Movement);
        this.setData(new AccelerationData(this));
        this.listeners.add(new LambdaListener<>(MoveEvent.class, event -> {
            if (!LONG_JUMP.isEnabled() && !mc.player.isCreative() && !FLIGHT.isEnabled() && !PositionUtil.inLiquid() && !mc.player.isOnLadder() && !SPEED.isEnabled()) {
                if (!strict.getValue()) {
                    if (!MovementUtil.noMovementKeys()) {
                        if (inAir.getValue() && !mc.player.onGround) {
                            MovementUtil.strafe(event, MovementUtil.getSpeed(accountSlow.getValue()));
                        } else if (mc.player.onGround) {
                            MovementUtil.strafe(event, MovementUtil.getSpeed(accountSlow.getValue()));
                        }
                    } else {
                        if (fastStop.getValue()) {
                            if (MovementUtil.noMovementKeys()) {
                                setFastStop();
                            }
                        }
                    }
                } else {
                    if (MovementUtil.noMovementKeys() && !PositionUtil.inLiquid() && !mc.player.isOnLadder()) {
                        Managers.TIMER.reset();
                        if (fastStop.getValue()) {
                            setFastStop();
                        }
                    }
                    if (mc.player.onGround && !PositionUtil.inLiquid() && !mc.player.isOnLadder()) {
                        if (!superStrict.getValue()) {
                            if (timer.getTime() < 6) {
                                Managers.TIMER.setTimer(1.0998844444422287774f);
                                timer.reset();
                            } else {
                                Managers.TIMER.reset();
                            }
                        } else Managers.TIMER.reset();
                    }
                }
            }
        }));
    }

    protected final ModuleCache<LongJump> LONG_JUMP = Caches.getModule(LongJump.class);
    protected final ModuleCache<Flight> FLIGHT = Caches.getModule(Flight.class);

    protected static final ModuleCache<Timer> TIMER =
            Caches.getModule(Timer.class);

    // TODO: Some kinda packet thingy

    protected final Setting<infoMode> info =
            register(new EnumSetting<>("Info", infoMode.Accel));
    protected final Setting<Boolean> fastStop =
            register(new BooleanSetting("FastStop", true));
    protected final Setting<Boolean> accountSlow =
            register(new BooleanSetting("Slow Check", true));
    protected final Setting<Boolean> inAir =
            register(new BooleanSetting("Air", true));
    protected final Setting<Boolean> strict =
            register(new BooleanSetting("Strict", false));
    protected final Setting<Boolean> superStrict =
            register(new BooleanSetting("SuperStrict", false));

    protected final StopWatch timer = new StopWatch();

    protected void setFastStop() {
        mc.player.motionX = 0.0f;
        mc.player.motionZ = 0.0f;
        MovementUtil.setMoveSpeed(0.0f);
        MovementUtil.strafe(0.0f);
    }

    private enum infoMode {
        Accel,
        Speed
    }


    private String getSpeedColor(double speed) {
        if (speed > 20) {
            return TextColor.GREEN;
        }
        if (speed > 10) {
            return TextColor.YELLOW;
        }
        return TextColor.RED;
    }

    @Override
    @SuppressWarnings("All")
    public String getDisplayInfo() {
        return strict.getValue()
                ? (superStrict.getValue()
                ? "Strict: Grim" : "Strict")
                : info.getValue() == infoMode.Accel
                ? Math.round(MovementUtil.getSpeed()) + ""
                : getSpeedColor(MathUtil.round(Managers.SPEED.getSpeed(), 2)) + "" + MathUtil.round(Managers.SPEED.getSpeed(), 2) + " km/h";
    }

}
