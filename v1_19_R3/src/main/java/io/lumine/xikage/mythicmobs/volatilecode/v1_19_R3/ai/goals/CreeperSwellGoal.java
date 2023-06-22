package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSwell;
import net.minecraft.world.entity.monster.EntityCreeper;
import org.bukkit.entity.EntityType;

@MythicAIGoal(
        name = "creeperswell",
        aliases = {"creeperexplode"},
        description = "Cause a creeper to want to blow up."
)
public class CreeperSwellGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    public CreeperSwellGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
    }

    @Override
    public boolean isValid() {
        return this.entity.getBukkitEntity().getType() == EntityType.CREEPER;
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalSwell((EntityCreeper) PathfinderHolder.getNMSEntity(this.entity));
    }
}
