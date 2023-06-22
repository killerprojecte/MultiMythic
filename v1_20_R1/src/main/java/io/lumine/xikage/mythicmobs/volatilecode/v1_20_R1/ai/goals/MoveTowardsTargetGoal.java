package io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsTarget;

@MythicAIGoal(
        name = "moveTowardsTarget",
        aliases = {},
        description = "Path to the current target"
)
public class MoveTowardsTargetGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    private final double speed;
    private final float maxRange;

    public MoveTowardsTargetGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
        this.speed = mlc.getDouble(new String[]{"speed", "s"}, 0.9);
        this.maxRange = mlc.getFloat(new String[]{"maxrange", "range", "r"}, 32.0F);
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalMoveTowardsTarget(PathfinderHolder.getNMSEntity(this.entity), this.speed, this.maxRange);
    }
}
