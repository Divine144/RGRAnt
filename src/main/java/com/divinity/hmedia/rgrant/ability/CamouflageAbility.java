package com.divinity.hmedia.rgrant.ability;

import com.divinity.hmedia.rgrant.cap.AntHolder;
import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CamouflageAbility extends Ability {

    @Override
    public void executeToggle(ServerLevel level, ServerPlayer player, boolean toggledOn) {
        super.executeToggle(level, player, toggledOn);
        var holder = AntHolderAttacher.getAntHolderUnwrap(player);
        if (holder != null) {
            if (toggledOn) {
                BlockHitResult result = AntUtils.blockTrace(player, ClipContext.Fluid.NONE, 10, false);
                if (result != null) {
                    BlockState blockState = level.getBlockState(result.getBlockPos());
                    if (!blockState.isAir()) {
                        holder.setCurrentSize(AntHolder.Size.SMALLEST);
                        holder.setCamouflagedBlock(blockState.getBlock());
                    }
                }
            }
            else {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false, false));
                holder.resetCurrentSize();
                holder.setCamouflagedBlock(Blocks.AIR);
            }
        }
    }

    @Override
    public boolean isToggleAbility() {
        return true;
    }
}
