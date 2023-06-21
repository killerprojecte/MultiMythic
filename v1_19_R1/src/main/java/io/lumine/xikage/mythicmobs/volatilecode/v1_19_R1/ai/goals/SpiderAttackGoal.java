package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R1.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import org.bukkit.entity.Spider;

@MythicAIGoal(
        name = "spiderAttack",
        aliases = {},
        description = "Spider melee attack"
)
public class SpiderAttackGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    public SpiderAttackGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
    }

    @Override
    public boolean isValid() {
        return this.entity.getBukkitEntity() instanceof Spider;
    }

    @Override
    public PathfinderGoal create() {
        return new SpiderMeleeAttackGoal(PathfinderHolder.getNMSEntity(this.entity));
    }

    protected static class SpiderMeleeAttackGoal extends PathfinderGoalMeleeAttack {
        public SpiderMeleeAttackGoal(EntityCreature entity) {
            super(entity, 1.0, true);
        }

        public boolean a() {
            return super.a();
        }

        protected double getAttackReachSqr(EntityInsentient entityliving) {
            return 4.0F + entityliving.cW();
        }
    }
}
