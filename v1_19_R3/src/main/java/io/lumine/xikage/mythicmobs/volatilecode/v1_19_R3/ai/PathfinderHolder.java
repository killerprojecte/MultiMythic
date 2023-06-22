package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.mobs.ai.PathfinderAdapter;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftLivingEntity;

public interface PathfinderHolder extends PathfinderAdapter {
    static EntityCreature getNMSEntity(AbstractEntity entity) {
        return (EntityCreature) ((CraftLivingEntity) entity.getBukkitEntity()).getHandle();
    }

    PathfinderGoal create();
}
