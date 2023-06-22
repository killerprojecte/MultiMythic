package io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLeapAtTarget;

@MythicAIGoal(
        name = "leapTowardsTarget",
        aliases = {"leapAtTarget"},
        description = "Leap towards the target"
)
public class LeapTowardsTargetGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    protected float speed;

    public LeapTowardsTargetGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
        this.speed = (float) mlc.getDouble(new String[]{"speed", "s"}, 1.2F);
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalLeapAtTarget(PathfinderHolder.getNMSEntity(this.entity), this.speed);
    }
}
