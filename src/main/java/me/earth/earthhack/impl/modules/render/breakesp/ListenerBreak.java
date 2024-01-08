package me.earth.earthhack.impl.modules.render.breakesp;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;

final class ListenerBreak extends ModuleListener<BreakESP, PacketEvent.Receive<SPacketBlockChange>> {
    public ListenerBreak(BreakESP module) {
        super(module, PacketEvent.Receive.class, -1000, SPacketBlockChange.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<SPacketBlockChange> event) {
        SPacketBlockChange change = event.getPacket();

        if (change.getBlockState() == Blocks.AIR.getDefaultState() && module.posList.containsKey(change.getBlockPosition())) {
            module.reset();
            if (module.reset.getValue()) {
                module.posList.remove(change.getBlockPosition());
            }
        }
    }
}
