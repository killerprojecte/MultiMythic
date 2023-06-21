package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R2.ai.goals;

import com.google.common.collect.Sets;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfindingGoal;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R2.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;

import java.util.Optional;
import java.util.Set;

@MythicAIGoal(
        name = "fleeFaction",
        aliases = {"runFromFaction"},
        description = "Runs away from nearby entities that is in a specific faction"
)
public class FleeFactionGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    protected float distance;
    protected double speed;
    protected double safeSpeed;
    private final Set<String> faction = Sets.newHashSet();

    public FleeFactionGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
        this.distance = mlc.getFloat(new String[]{"distance", "d"}, 16.0F);
        this.speed = mlc.getDouble(new String[]{"speed", "s"}, 1.2F);
        this.safeSpeed = mlc.getDouble(new String[]{"safespeed", "ss"}, 1.0);
        String factions = mlc.getString(new String[]{"faction", "f"}, this.dataVar1);
        if (factions != null) {
            String[] split = factions.split(",");

            for (String s : split) {
                this.faction.add(s.toUpperCase());
            }
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
                ActiveMob am = getPlugin().getMobManager().getMythicMobInstance(this.getEntity());
                AbstractEntity target = BukkitAdapter.adapt(((EntityLiving) targetEntity).getBukkitEntity());
                if (am == null) {
                    return false;
                }

                if (target.isPlayer()) {
                    for (String faction : this.faction) {
                        if (getPlugin().getPlayerManager().getFactionProvider().isInFaction(target.asPlayer(), faction)) {
                            return true;
                        }
                    }
                } else {
                    Optional<ActiveMob> maybeTargetAM = getPlugin().getMobManager().getActiveMob(target.getUniqueId());
                    if (!maybeTargetAM.isPresent()) {
                        return false;
                    }

                    ActiveMob targetAM = maybeTargetAM.get();
                    if (targetAM.hasFaction()) {
                        String faction = targetAM.getFaction().toUpperCase();
                        return this.faction.contains(faction);
                    }
                }
            } catch (Exception var7) {
                var7.printStackTrace();
            }

            return false;
        }
        );
    }
}
