package io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBowShoot;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.IRangedEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;

@MythicAIGoal(
        name = "bowAttack",
        aliases = {"skeletonBowAttack", "bowshoot", "bowmaster"},
        description = "An advanced bow attack"
)
public class BowAttackGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    protected double speedModifier;
    protected int attackIntervalMin;
    protected float attackRadius;

    public BowAttackGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
        this.speedModifier = mlc.getDouble(new String[]{"speed", "s"}, 1.0);
        this.attackIntervalMin = mlc.getInteger(new String[]{"attackspeedmax", "smax"}, 20);
        this.attackRadius = mlc.getFloat(new String[]{"attackradius", "radius", "r"}, 15.0F);
    }

    @Override
    public boolean isValid() {
        return ((CraftEntity) this.entity.getBukkitEntity()).getHandle() instanceof IRangedEntity;
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalBowShoot(
                (EntityMonster) PathfinderHolder.getNMSEntity(this.entity), this.speedModifier, this.attackIntervalMin, this.attackRadius
        );
    }
}
