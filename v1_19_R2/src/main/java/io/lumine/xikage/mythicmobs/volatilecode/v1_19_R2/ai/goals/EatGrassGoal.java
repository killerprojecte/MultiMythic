package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R2.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R2.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalEatTile;

@MythicAIGoal(
        name = "eatGrass",
        description = "Break down doors"
)
public class EatGrassGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    public EatGrassGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalEatTile(PathfinderHolder.getNMSEntity(this.entity));
    }
}
