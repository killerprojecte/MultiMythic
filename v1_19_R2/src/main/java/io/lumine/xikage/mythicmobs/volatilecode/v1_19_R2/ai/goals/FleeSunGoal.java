package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R2.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R2.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFleeSun;

@MythicAIGoal(
        name = "fleeSun",
        description = "Run away from sunlight"
)
public class FleeSunGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    protected double speed;

    public FleeSunGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
        this.speed = mlc.getDouble(new String[]{"speed", "s"}, 1.0);
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalFleeSun(PathfinderHolder.getNMSEntity(this.entity), this.speed);
    }
}
