package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R2.ai.targeters;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R2.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityTameableAnimal;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalOwnerHurtByTarget;
import org.bukkit.entity.Tameable;

@MythicAIGoal(
        name = "ownerHurtBy",
        aliases = {"ownerHurt", "ownerHurtByTarget", "ownerDamager", "ownerAttacker"},
        description = "Target something that attacks the mob's owner"
)
public class OwnerHurtByGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    public OwnerHurtByGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
    }

    @Override
    public boolean isValid() {
        return this.entity.getBukkitEntity() instanceof Tameable;
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalOwnerHurtByTarget((EntityTameableAnimal) PathfinderHolder.getNMSEntity(this.entity));
    }
}
