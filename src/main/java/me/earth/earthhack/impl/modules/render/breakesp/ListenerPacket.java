package me.earth.earthhack.impl.modules.render.breakesp;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
public class ListenerPacket extends ModuleListener<BreakESP, PacketEvent.Receive<SPacketBlockBreakAnim>> {
    public ListenerPacket(BreakESP module) {
        super(module, PacketEvent.Receive.class, -10000, SPacketBlockBreakAnim.class);
    }


    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockBreakAnim> event) {
        if (event.getPacket().getBreakerId() != mc.player.getEntityId()) {
            SPacketBlockBreakAnim packet = event.getPacket();
            module.reset();
            IBlockState state = mc.world.getBlockState(packet.getPosition());
            module.player = (EntityPlayer) mc.world.getEntityByID(packet.getBreakerId());
            if (module.player != null) {
                module.distance = module.player.getDistanceSq(event.getPacket().getPosition());
                if (module.player.getDistanceSq(packet.getPosition()) <= module.range.getValue()) {
                    if (state != Blocks.AIR.getDefaultState() && state != Blocks.BEDROCK.getDefaultState()) {
                        module.posList.put(packet.getPosition(), System.currentTimeMillis());
                    }
                }
            }
        }
    }
}
