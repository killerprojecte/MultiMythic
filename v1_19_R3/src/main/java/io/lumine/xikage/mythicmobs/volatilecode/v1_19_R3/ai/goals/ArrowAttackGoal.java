package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.PathfinderHolder;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalArrowAttack;
import net.minecraft.world.entity.monster.IRangedEntity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;

@MythicAIGoal(
        name = "arrowAttack",
        aliases = {"rangedAttack"},
        description = "A basic bow attack"
)
public class ArrowAttackGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    protected double speedModifier;
    protected int attackIntervalMin;
    protected int attackIntervalMax;
    protected float attackRadius;

    public ArrowAttackGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
        this.speedModifier = mlc.getDouble(new String[]{"speed", "s"}, 1.0);
        this.attackIntervalMin = mlc.getInteger(new String[]{"attackspeedmax", "smax"}, 20);
        this.attackIntervalMax = mlc.getInteger(new String[]{"attackspeedmin", "amin"}, 60);
        this.attackRadius = mlc.getFloat(new String[]{"attackradius", "radius", "r"}, 15.0F);
    }

    @Override
    public boolean isValid() {
        return ((CraftEntity) this.entity.getBukkitEntity()).getHandle() instanceof IRangedEntity;
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalArrowAttack(
                (IRangedEntity) PathfinderHolder.getNMSEntity(this.entity), this.speedModifier, this.attackIntervalMin, this.attackIntervalMax, this.attackRadius
        );
    }
}
