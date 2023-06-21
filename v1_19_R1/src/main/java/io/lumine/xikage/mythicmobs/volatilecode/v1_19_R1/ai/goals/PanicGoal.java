package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R1.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;

@MythicAIGoal(
        name = "panic",
        aliases = {"panicWhenOnFire"},
        description = "Run around panicing when on fire and look for water"
)
public class PanicGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    public PanicGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalPanic(PathfinderHolder.getNMSEntity(this.entity), 1.25);
    }
}
