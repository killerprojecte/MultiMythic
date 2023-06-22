package io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.mobs.ai.PathfinderAdapter;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity;

public interface PathfinderHolder extends PathfinderAdapter {
    static EntityCreature getNMSEntity(AbstractEntity entity) {
        return (EntityCreature) ((CraftLivingEntity) entity.getBukkitEntity()).getHandle();
    }

    PathfinderGoal create();
}
