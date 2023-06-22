package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveThroughVillage;

@MythicAIGoal(
        name = "moveThroughVillage",
        aliases = {},
        description = "Makes the mob move through villages"
)
public class MoveThroughVillageGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    private final double speed;
    private final boolean openDoors = false;

    public MoveThroughVillageGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
        this.speed = mlc.getDouble(new String[]{"speed", "s"}, 0.6);
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalMoveThroughVillage(PathfinderHolder.getNMSEntity(this.entity), this.speed, false, 4, () -> this.openDoors);
    }
}
