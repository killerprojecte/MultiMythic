package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;

@MythicAIGoal(
        name = "randomStroll",
        aliases = {},
        description = "Walk around randomly"
)
public class RandomStrollGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    public RandomStrollGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalRandomStroll(PathfinderHolder.getNMSEntity(this.entity), 1.0);
    }
}
