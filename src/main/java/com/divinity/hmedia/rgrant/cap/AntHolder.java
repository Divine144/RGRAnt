package com.divinity.hmedia.rgrant.cap;

import com.divinity.hmedia.rgrant.item.EchoLocationItem;
import com.divinity.hmedia.rgrant.network.NetworkHandler;
import dev._100media.capabilitysyncer.core.EntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediamorphs.morph.Morph;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AntHolder extends EntityCapability {

    private int mindControlTicks;
    private Size currentSize = Size.SMALL;
    private static final int ESCAPE_REQUIRED_SWINGS = 5;
    private UUID captured = null;

    private Block camouflagedBlock = Blocks.AIR;

    private int toSwing = 0;

    protected AntHolder(Entity entity) {
        super(entity);
    }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("mindControlTicks", this.mindControlTicks);
        tag.putInt("currentSize", currentSize.ordinal());
        tag.putInt("toSwing", toSwing);
        if (captured != null) {
            tag.putUUID("captured", captured);
        }
        tag.putInt("block", Block.getId(camouflagedBlock.defaultBlockState()));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.mindControlTicks = nbt.getInt("mindControlTicks");
        this.currentSize = Size.values()[nbt.getInt("currentSize")];
        toSwing = nbt.getInt("toSwing");
        captured = null;
        if (nbt.contains("captured")) {
            captured = nbt.getUUID("captured");
        }
        this.camouflagedBlock = Block.stateById(nbt.getInt("block")).getBlock();
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

    public void updateToSwing() {
        toSwing = ESCAPE_REQUIRED_SWINGS;
        updateTracking();
    }

    public void decrementToSwing() {
        --toSwing;
        updateTracking();
    }

    public int getToSwing() {
        return toSwing;
    }

    public void capture(LivingEntity living) {
        captured = living.getUUID();
        living.setNoGravity(true);
    }

    public void unCapture() {
        if (getCaptured() instanceof LivingEntity living) {
            living.setNoGravity(false);
        }
        captured = null;
    }

    public Entity getCaptured() {
        if (captured != null && entity.level() instanceof ServerLevel level) {
            return level.getEntity(captured);
        }
        return null;
    }

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

    public Size getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(Size currentSize) {
        this.currentSize = currentSize;
        if (entity instanceof ServerPlayer player) {
            var holder = MorphHolderAttacher.getMorphHolderUnwrap(player);
            if (holder != null) {
                holder.setDimensionsOverride(currentSize.dimensions, true);
            }
        }
        updateTracking();
    }

    public void setCurrentSizeNoDimensionUpdate(Size currentSize) {
        this.currentSize = currentSize;
        updateTracking();
    }

    public Block getCamouflagedBlock() {
        return camouflagedBlock;
    }

    public void setCamouflagedBlock(Block camouflagedBlock) {
        this.camouflagedBlock = camouflagedBlock;
        updateTracking();
    }

    public enum Size {
        SMALLEST(EntityDimensions.scalable(0.65f, 0.65f)),
        SMALL(EntityDimensions.scalable(1f, 1f)),
        MEDIUM(EntityDimensions.scalable(1.5f, 2f)),
        LARGE(EntityDimensions.scalable(2f, 3f)),
        X_LARGE(EntityDimensions.scalable(2f, 6f));

        private final EntityDimensions dimensions;

        Size(EntityDimensions dimensions) {
            this.dimensions = dimensions;
        }

        public boolean isCurrentSize(Morph morph) {
            var otherDimensions = morph.getDimensions(null, Pose.STANDING);
            return Morph.dimensionsEqual(dimensions, otherDimensions);
        }

        public Size next() {
            if (ordinal() + 1 >= values().length) {
                return this;
            }
            return values()[(ordinal() + 1) % values().length];
        }

        public Size previous() {
            if (ordinal() - 1 < 0) {
                return this;
            }
            return values()[(ordinal() - 1) % values().length];
        }
    }
}
