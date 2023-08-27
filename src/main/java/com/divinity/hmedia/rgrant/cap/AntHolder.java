package com.divinity.hmedia.rgrant.cap;

import com.divinity.hmedia.rgrant.network.NetworkHandler;
import dev._100media.capabilitysyncer.core.EntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediamorphs.morph.Morph;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.simple.SimpleChannel;

public class AntHolder extends EntityCapability {

    private int mindControlTicks;

    protected AntHolder(Entity entity) {
        super(entity);
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("mindControlTicks", this.mindControlTicks);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.mindControlTicks = nbt.getInt("mindControlTicks");
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.entity.getId(), AntHolderAttacher.EXAMPLE_RL, this);
    }
/*    public void tick() {
        if (masterId != null) {
            Player master = searchAllLevels(masterId);
            if (master != null) {
                if (master.level != player.level || master.distanceToSqr(player) > TETHER_DISTANCE * TETHER_DISTANCE) {
                    ChunkPos chunkpos = new ChunkPos(new BlockPos(master.getX(), master.getY(), master.getZ()));
                    ((ServerLevel) master.level).getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, player.getId());
                    player.stopRiding();
                    if (player.isSleeping()) {
                        player.stopSleepInBed(true, true);
                    }

                    if (master.level == player.level) {
                        ((ServerPlayer) player).connection.teleport(master.getX(), master.getY(), master.getZ(), player.getYRot(), player.getXRot(), EnumSet.noneOf(ClientboundPlayerPositionPacket.RelativeArgument.class));
                    } else {
                        ((ServerPlayer) player).teleportTo((ServerLevel) master.level, master.getX(), master.getY(), master.getZ(), player.getYRot(), player.getXRot());
                    }
                }
                if (player.tickCount % 20 == 0) {
                    List<Player> targets = player.level.getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(15), e -> e != player && e != getMaster() && e.distanceToSqr(player) <= KILL_AURA_DISTANCE * KILL_AURA_DISTANCE);
                    if (!targets.isEmpty()) {
                        Player target = targets.get(0);
                        player.attack(target);
                        player.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
                        player.swing(InteractionHand.MAIN_HAND);
                    }
                }
            }
        }
        boolean day = player.level.isDay();
        if (!wasDay && day) {
            readyToMummify = true;
        }
        wasDay = day;
    }*/

    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }

    public int getMindControlTicks() {
        return mindControlTicks;
    }

    public void decreaseMindControlTicks() {
        setMindControlTicks(getMindControlTicks() - 1);
    }

    public void setMindControlTicks(int mindControlTicks) {
        int temp = this.mindControlTicks;
        this.mindControlTicks = mindControlTicks;
        if (this.mindControlTicks < 0) this.mindControlTicks = 0;
        if (temp != this.mindControlTicks) updateTracking();

    }

    public boolean isMindControlled() {
        return mindControlTicks > 0;
    }

/*    public Size getSize() {

    }

    public EntityDimensions getDimensionsForSize() {
        return switch (getSize()) {
            case NONE -> {
                if (this.entity instanceof LivingEntity e) {
                    Morph morph = MorphHolderAttacher.getCurrentMorphUnwrap(e);
                    if (morph != null && morph.getDimensions())
                }
            }
        }
    }*/

    public enum Size {
        NONE,
        SMALLEST,
        SMALL,
        MEDIUM,
        LARGE,
        XLARGE
    }
}
