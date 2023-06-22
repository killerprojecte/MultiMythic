package io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRestrictSun;

@MythicAIGoal(
        name = "restrictSun",
        aliases = {"avoidSun", "avoidSunlight"},
        description = "Actively try to avoid sunlight"
)
public class RestrictSunGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    public RestrictSunGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalRestrictSun(PathfinderHolder.getNMSEntity(this.entity));
    }
}
