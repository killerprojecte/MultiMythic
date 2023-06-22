package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.targeters;

import com.google.common.collect.Sets;
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
import net.minecraft.world.entity.monster.EntityMonster;

import java.util.Optional;
import java.util.Set;

@MythicAIGoal(
        name = "nearestSpecificFactionMonsters",
        aliases = {"specificFactionMonsters"},
        description = "Target a nearby monster that is in a specific faction"
)
public class SpecificFactionMonstersGoal extends WrappedPathfindingGoal implements PathfinderHolder {
    private final Set<String> faction = Sets.newHashSet();

    public SpecificFactionMonstersGoal(AbstractEntity entity, String line, MythicLineConfig mlc) {
        super(entity, line, mlc);
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
        return new PathfinderGoalNearestAttackableTarget(PathfinderHolder.getNMSEntity(this.entity), EntityMonster.class, 0, true, false, targetEntity -> {
            try {
                ActiveMob mob = getPlugin().getMobManager().getMythicMobInstance(this.getEntity());
                AbstractEntity target = BukkitAdapter.adapt(((EntityLiving) targetEntity).getBukkitEntity());
                if (mob == null) {
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
        });
    }
}
