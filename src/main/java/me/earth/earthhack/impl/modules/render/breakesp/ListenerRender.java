package me.earth.earthhack.impl.modules.render.breakesp;

import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.player.speedmine.Speedmine;
import me.earth.earthhack.impl.util.minecraft.PlayerUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.earthhack.impl.util.render.mutables.BBRender;
import me.earth.earthhack.impl.util.render.mutables.MutableBB;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class ListenerRender extends ModuleListener<BreakESP, Render3DEvent> {

    public ListenerRender(BreakESP module)
    {
        super(module, Render3DEvent.class);
    }

    private final MutableBB bb = new MutableBB();



    @Override
    public void invoke(Render3DEvent event)
    {
        if (mc.player != null && mc.world != null) {
            for (HashMap.Entry<BlockPos, Long> set : module.posList.entrySet()) {
                final Color boxColor = module.color.getValue();
                final Color outlineColor = module.outline.getValue();
                if (module.dbl.getValue()) {
                    RenderUtil.renderBox(
                            Interpolation.interpolatePos(set.getKey(), module.height.getValue()),
                            new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), boxColor.getAlpha()),
                            new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), outlineColor.getAlpha()),
                            module.lineWidth.getValue());
                }

                double grow = (-1.0 - Math.signum(-1.0) * module.renderTimer.getTime() / Math.max(1.0, module.time.getValue())) / 2.0;
                if (grow >= 0.0) {
                    renderBoxMutable(set.getKey());
                } else {
                    if (!module.reverse.getValue()) {
                        bb.setFromBlockPos(set.getKey());
                        bb.growMutable(grow, grow, grow);
                        Interpolation.interpolateMutable(bb);
                        BBRender.renderBox(bb, module.growColor.getValue(), module.growColorOutline.getValue(), module.growLineWidth.getValue());
                    } else {
                        bb.setFromBlockPos(set.getKey());
                        bb.shrinkMutable(0.5, 0.5, 0.5);
                        bb.shrinkMutable(grow, grow, grow);
                        Interpolation.interpolateMutable(bb);
                        BBRender.renderBox(bb, module.growColor.getValue(), module.growColorOutline.getValue(), module.growLineWidth.getValue());
                    }
                }
            }


            module.posList.entrySet().removeIf(e ->
                    e.getValue() + module.removeTime.getValue()
                            < System.currentTimeMillis());

        }
    }

    private void renderBoxMutable(BlockPos pos) {
        bb.setFromBlockPos(pos);
        Interpolation.interpolateMutable(bb);
        BBRender.renderBox(
                bb,
                module.growColor.getValue(),
                module.growColorOutline.getValue(),
                module.growLineWidth.getValue());
    }
}
