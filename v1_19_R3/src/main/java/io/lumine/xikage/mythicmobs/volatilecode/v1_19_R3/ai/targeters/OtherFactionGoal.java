package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.targeters;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;

import java.util.Optional;

@MythicAIGoal(
        name = "nearestOtherFaction",
        aliases = {"otherFaction"},
        description = "Target a nearby entity that is in a different faction"
)
public class OtherFactionGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    public OtherFactionGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
    }

    @Override
    public boolean isValid() {
        return this.entity.isCreature();
    }

    @Override
    public PathfinderGoal create() {
        return new PathfinderGoalNearestAttackableTarget(PathfinderHolder.getNMSEntity(this.entity), EntityLiving.class, 0, true, false, targetEntity -> {
            try {
                ActiveMob mob = getPlugin().getMobManager().getMythicMobInstance(this.getEntity());
                AbstractEntity target = BukkitAdapter.adapt(((EntityLiving) targetEntity).getBukkitEntity());
                if (mob == null || !mob.hasFaction()) {
                    return true;
                } else if (target.isPlayer()) {
                    return !getPlugin().getPlayerManager().getFactionProvider().isInFaction(target.asPlayer(), mob.getFaction());
                } else {
                    Optional<ActiveMob> maybeTargetAM = getPlugin().getMobManager().getActiveMob(target.getUniqueId());
                    if (!maybeTargetAM.isPresent()) {
                        return true;
                    } else {
                        ActiveMob targetAM = maybeTargetAM.get();
                        if (targetAM.hasFaction()) {
                            return !mob.getFaction().equals(targetAM.getFaction());
                        } else {
                            return true;
                        }
                    }
                }
            } catch (Exception var6) {
                var6.printStackTrace();
                return false;
            }
        });
    }
}
