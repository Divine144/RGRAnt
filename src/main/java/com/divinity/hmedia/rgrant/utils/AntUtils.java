package com.divinity.hmedia.rgrant.utils;

import dev._100media.hundredmediaquests.cap.QuestHolderAttacher;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AntUtils {

    public static boolean hasItemEitherHands(Player player, Item item) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == item || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == item;
    }

    public static <T extends QuestGoal> void addToGenericQuestGoal(ServerPlayer player, Class<T> clazz) {
        QuestHolderAttacher.checkAllGoals(player, goal -> {
            if (goal.getClass() == clazz) {
                goal.addProgress(1);
                return true;
            }
            return false;
        });
    }

    public static EntityHitResult rayTraceEntities(Level level, Entity origin, float range, Predicate<Entity> filter) {
        Vec3 look = origin.getViewVector(0);
        Vec3 startVec = origin.getEyePosition(0);
        Vec3 endVec = startVec.add(look.x * range, look.y * range, look.z * range);
        AABB box = new AABB(startVec, endVec);
        return rayTraceEntities(level,origin,startVec,endVec,box,filter);
    }

    public static EntityHitResult rayTraceEntities(Level level, @Nullable Entity origin, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter) {
        double d0 = Double.MAX_VALUE;
        Entity entity = null;
        for (Entity entity1 : level.getEntities(origin, boundingBox, filter)) {
            if (entity1.isSpectator()) {
                continue;
            }
            AABB aabb = entity1.getBoundingBox();
            if (aabb.getSize() < 0.3) {
                aabb = aabb.inflate(0.3);
            }
            Optional<Vec3> optional = aabb.clip(startVec, endVec);
            if (optional.isPresent()) {
                double d1 = startVec.distanceToSqr(optional.get());
                if (d1 < d0) {
                    entity = entity1;
                    d0 = d1;
                }
            }
        }
        return entity == null ? null : new EntityHitResult(entity);
    }

    /**
     * Returns a list of entities (targets) from a relative entity within the specified x, y, and z bounds.
     */
    public static <T extends LivingEntity> List<T> getEntitiesInRange(LivingEntity relativeEntity, Class<T> targets, double xBound, double yBound, double zBound, Predicate<T> filter) {
        return relativeEntity.level().getEntitiesOfClass(targets,
                        new AABB(relativeEntity.getX() - xBound, relativeEntity.getY() - yBound, relativeEntity.getZ() - zBound,
                                relativeEntity.getX() + xBound, relativeEntity.getY() + yBound, relativeEntity.getZ() + zBound))
                .stream().sorted(getEntityComparator(relativeEntity)).filter(filter).collect(Collectors.toList());
    }

    /**
     * Returns a comparator which compares entities' distances to a given LivingEntity
     */
    private static Comparator<Entity> getEntityComparator(LivingEntity other) {
        return Comparator.comparing(entity -> entity.distanceToSqr(other.getX(), other.getY(), other.getZ()));
    }
}