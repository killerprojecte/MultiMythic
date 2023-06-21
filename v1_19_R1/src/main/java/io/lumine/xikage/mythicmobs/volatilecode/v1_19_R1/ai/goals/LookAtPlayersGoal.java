package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R1.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.player.EntityHuman;

@MythicAIGoal(
        name = "lookAtPlayer",
        aliases = {"lookAtPlayers"},
        description = "Float on water"
)
public class LookAtPlayersGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    public LookAtPlayersGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalLookAtPlayer(PathfinderHolder.getNMSEntity(this.entity), EntityHuman.class, 5.0F, 1.0F);
    }
}
