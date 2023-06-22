package io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.goals;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;

import java.util.List;

@MythicAIGoal(
        name = "fleeConditional",
        aliases = {"fleeIf"},
        description = "Run away from nearby entities that meets the conditions",
        premium = true
)
public class FleeConditionalGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    protected float distance;
    protected double speed;
    protected double safeSpeed;
    protected String conditionString;
    protected List<SkillCondition> conditions = null;

    public FleeConditionalGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
        this.distance = mlc.getFloat(new String[]{"distance", "d"}, 4.0F);
        this.speed = mlc.getDouble(new String[]{"speed", "s"}, 1.2F);
        this.safeSpeed = mlc.getDouble(new String[]{"safespeed", "ss"}, 1.0);
        this.conditionString = mlc.getString(new String[]{"fleeconditions", "conditions", "cond", "c"}, "null");
        if (this.conditionString != null) {
            this.conditions = getPlugin().getSkillManager().getConditions(this.conditionString);
        }
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalAvoidTarget(
                PathfinderHolder.getNMSEntity(this.entity), EntityLiving.class, this.distance, this.safeSpeed, this.speed, targetEntity -> {
            try {
                AbstractEntity absEntity = BukkitAdapter.adapt(((EntityLiving) targetEntity).getBukkitEntity());

                for (SkillCondition cond : this.conditions) {
                    if (!cond.evaluateToEntity(this.entity, absEntity)) {
                        return false;
                    }
                }
            } catch (Error | Exception var5) {
                var5.printStackTrace();
            }

            return true;
        }
        );
    }
}
