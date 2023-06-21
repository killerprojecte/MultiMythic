package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R1.ai.targeters;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R1.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;

import java.util.List;

@MythicAIGoal(
        name = "nearestConditionalTarget",
        aliases = {"nearestConditional", "nearestIf"},
        description = "Target a nearby attacker that meets the conditions",
        premium = true
)
public class NearestConditionalTargetGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    protected String targetConditionString;
    protected List<SkillCondition> targetConditions = null;

    public NearestConditionalTargetGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
        this.targetConditionString = mlc.getString(new String[]{"targetconditions", "conditions", "cond", "c"}, "null");
        if (this.targetConditionString != null) {
            this.targetConditions = getPlugin().getSkillManager().getConditions(this.targetConditionString);
        }
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalNearestAttackableTarget(PathfinderHolder.getNMSEntity(this.entity), EntityLiving.class, 0, true, false, targetEntity -> {
            try {
                AbstractEntity absEntity = BukkitAdapter.adapt(((EntityLiving) targetEntity).getBukkitEntity());

                for (SkillCondition cond : this.targetConditions) {
                    if (!cond.evaluateToEntity(this.entity, absEntity)) {
                        return false;
                    }
                }
            } catch (Error | Exception var5) {
                var5.printStackTrace();
            }

            return true;
        });
    }
}
