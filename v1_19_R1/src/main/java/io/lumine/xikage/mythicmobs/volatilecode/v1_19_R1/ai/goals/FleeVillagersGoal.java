package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R1.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.npc.EntityVillager;

@MythicAIGoal(
        name = "fleeVillagers",
        aliases = {"runfromvillagers"},
        description = "Run away from nearby wolves"
)
public class FleeVillagersGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    protected float distance;
    protected double speed;
    protected double safeSpeed;

    public FleeVillagersGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
        this.distance = mlc.getFloat(new String[]{"distance", "d"}, 16.0F);
        this.speed = mlc.getDouble(new String[]{"speed", "s"}, 1.2F);
        this.safeSpeed = mlc.getDouble(new String[]{"safespeed", "ss"}, 1.0);
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalAvoidTarget(PathfinderHolder.getNMSEntity(this.entity), EntityVillager.class, this.distance, this.safeSpeed, this.speed);
    }
}
