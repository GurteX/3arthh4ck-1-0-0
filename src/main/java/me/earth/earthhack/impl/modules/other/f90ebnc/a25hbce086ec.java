package me.earth.earthhack.impl.modules.other.f90ebnc;

/*
 *
 * The following file (class) is part of 3arthh4ck (Earthhack) Client and cannot be modified or distributed without this annotation.
 *
 * Copyright (C) 2023 Phobos.eu LLC
 *
 */

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.RayTraceUtil;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.minecraft.blocks.BlockUtil;
import me.earth.earthhack.impl.util.minecraft.entity.EntityUtil;
import me.earth.earthhack.impl.util.network.PacketUtil;
import me.earth.earthhack.impl.util.text.ChatUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public class a25hbce086ec extends Module
{
    protected final BooleanSetting rotate =
            register (new BooleanSetting("Rotate", false));
    protected final BooleanSetting smart =
            register (new BooleanSetting("Smart", false));
    protected final Setting<Integer> range      =
            register(new NumberSetting<>("Range", 3, 0, 6));

    public a25hbce086ec()
    {
        super("SelfWeb", Category.Combat);
        this.listeners.add(new LambdaListener<>(MotionUpdateEvent.class, event -> {
            if (!smart.getValue()) {
                placeWeb();
                disable();
            } else {
                EntityPlayer enemy = EntityUtil.getClosestEnemy();

                if (enemy != null) {
                    if (mc.player.getDistance(enemy) <= range.getValue()) {
                        placeWeb();
                        disable();
                    }
                } else {
                    return;
                }
            }
        }));
    }


    private void placeWeb() {
        BlockPos pos = mc.player.getPosition();

        int slot = InventoryUtil.findHotbarBlock(Blocks.WEB);
        if (slot == -1) ChatUtil.sendMessage(TextColor.RED + "<SelfWeb> Unable to find cobweb!");
        EnumFacing f = BlockUtil.getFacing(pos);

        BlockPos on = pos.offset(f);
        float[] r =
                RotationUtil.getRotations(on, f.getOpposite(), mc.player);
        RayTraceResult result =
                RayTraceUtil.getRayTraceResultWithEntity(r[0], r[1], mc.player);

        float[] vec = RayTraceUtil.hitVecToPlaceVec(on, result.hitVec);

        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
        {
            int lastSlot = mc.player.inventory.currentItem;

            if (rotate.getValue()) {
                if (mc.player.getPositionVector()
                        .equals(Managers.POSITION.getVec())) {
                    PacketUtil.doRotation(r[0], r[1], true);
                } else {
                    PacketUtil.doPosRot(mc.player.posX,
                            mc.player.posY,
                            mc.player.posZ,
                            r[0],
                            r[1],
                            true);
                }
            }
            InventoryUtil.switchTo(slot);
            PacketUtil.place(on, f.getOpposite(), slot, vec[0], vec[1], vec[2]);
            PacketUtil.swing(slot);
            InventoryUtil.switchTo(lastSlot);
        });


    }


}
