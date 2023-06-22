package io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.targeters;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityTameableAnimal;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalOwnerHurtTarget;
import org.bukkit.entity.Tameable;

@MythicAIGoal(
        name = "ownerTarget",
        aliases = {"ownerHurt", "ownerAttack"},
        description = "Target the mob's owner's target"
)
public class OwnerHurtGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    public OwnerHurtGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
    }

    @Override
    public boolean isValid() {
        return this.entity.getBukkitEntity() instanceof Tameable;
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalOwnerHurtTarget((EntityTameableAnimal) PathfinderHolder.getNMSEntity(this.entity));
    }
}
