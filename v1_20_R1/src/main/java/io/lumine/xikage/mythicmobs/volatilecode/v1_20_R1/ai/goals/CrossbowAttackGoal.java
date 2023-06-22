package io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalCrossbowAttack;
import net.minecraft.world.entity.monster.EntityMonster;

@MythicAIGoal(
        name = "crossbowAttack",
        aliases = {},
        description = "Attack with a crossbow"
)
public class CrossbowAttackGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    public CrossbowAttackGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
    }

    @Override
    public boolean isValid() {
        return this.entity.isMonster();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalCrossbowAttack((EntityMonster) PathfinderHolder.getNMSEntity(this.entity), 1.0, 8.0F);
    }
}
