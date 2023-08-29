package com.divinity.hmedia.rgrant.network.serverbound;

import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import com.divinity.hmedia.rgrant.init.EffectInit;
import dev._100media.capabilitysyncer.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class EscapeNetPacket implements IPacket {
    public EscapeNetPacket() {
    }

    @Override
    public void write(FriendlyByteBuf packetBuf) {
    }

    public static EscapeNetPacket read(FriendlyByteBuf buffer) {
        return new EscapeNetPacket();
    }

    @Override
    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            AntHolderAttacher.getAntHolder(player).ifPresent(cap -> {
                cap.decrementToSwing();
                if (cap.getToSwing() <= 0) {
                    player.removeEffect(EffectInit.NETTED.get());
                }
            });
        });
        context.setPacketHandled(true);
    }

    public static void register(SimpleChannel channel, int id) {
        IPacket.register(channel, id, NetworkDirection.PLAY_TO_SERVER, EscapeNetPacket.class, EscapeNetPacket::read);
    }
}
