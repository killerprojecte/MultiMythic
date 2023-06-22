package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.ConfigManager;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.logging.MythicLogger;
import io.lumine.xikage.mythicmobs.mobs.ai.Pathfinder;
import io.lumine.xikage.mythicmobs.mobs.ai.PathfinderAdapter;
import io.lumine.xikage.mythicmobs.mobs.ai.WrappedPathfinder;
import io.lumine.xikage.mythicmobs.util.annotations.MythicAIGoal;
import io.lumine.xikage.mythicmobs.util.reflections.VersionCompliantReflections;
import io.lumine.xikage.mythicmobs.utils.reflection.Reflector;
import io.lumine.xikage.mythicmobs.volatilecode.VolatileCodeHandler;
import io.lumine.xikage.mythicmobs.volatilecode.handlers.VolatileAIHandler;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.CustomAIAdapter;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.PathfinderHolder;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class VolatileAIHandler_v1_19_R3 implements VolatileAIHandler {
    private static final String OBF_MOB_NAV = "bM";
    private static final String OBF_MOB_GOAL = "bN";
    private static final String OBF_MOB_TARG = "bO";
    private static final String OBF_PATHFINDER_1 = "c";
    private static final String OBF_PATHFINDER_2 = "d";
    private final Map<String, Class<? extends PathfinderAdapter>> AI_GOALS = new ConcurrentHashMap<>();
    private final Map<String, Class<? extends PathfinderAdapter>> AI_TARGETS = new ConcurrentHashMap<>();
    private final Reflector<EntityInsentient> refEntityInsentient = new Reflector<EntityInsentient>(EntityInsentient.class, "bM", "bN", "bO");
    private final Reflector<PathfinderGoalSelector> refGoalSelector = new Reflector<PathfinderGoalSelector>(PathfinderGoalSelector.class, "c", "d");

    public VolatileAIHandler_v1_19_R3(VolatileCodeHandler handler) {
        for (Class<?> clazz : new VersionCompliantReflections("io.lumine.xikage.mythicmobs.mobs.ai.goals").getTypesAnnotatedWith(MythicAIGoal.class)) {
            try {
                String name = clazz.getAnnotation(MythicAIGoal.class).name();
                String[] aliases = clazz.getAnnotation(MythicAIGoal.class).aliases();
                if (PathfinderAdapter.class.isAssignableFrom(clazz)) {
                    this.AI_GOALS.put(name.toUpperCase(), (Class<? extends PathfinderAdapter>) clazz);

                    for (String alias : aliases) {
                        this.AI_GOALS.put(alias.toUpperCase(), (Class<? extends PathfinderAdapter>) clazz);
                    }
                }
            } catch (Exception var17) {
                MythicLogger.error("Failed to load custom AI goal {0}", clazz.getCanonicalName());
            }
        }

        for (Class<?> clazz : new VersionCompliantReflections("io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.goals")
                .getTypesAnnotatedWith(MythicAIGoal.class)) {
            try {
                String name = clazz.getAnnotation(MythicAIGoal.class).name();
                String[] aliases = clazz.getAnnotation(MythicAIGoal.class).aliases();
                if (PathfinderAdapter.class.isAssignableFrom(clazz)) {
                    this.AI_GOALS.put(name.toUpperCase(), (Class<? extends PathfinderAdapter>) clazz);

                    for (String alias : aliases) {
                        this.AI_GOALS.put(alias.toUpperCase(), (Class<? extends PathfinderAdapter>) clazz);
                    }
                }
            } catch (Exception var16) {
                MythicLogger.error("Failed to load wrapped AI goal {0}", clazz.getCanonicalName());
            }
        }

        for (Class<?> clazz : new VersionCompliantReflections("io.lumine.xikage.mythicmobs.mobs.ai.targeters").getTypesAnnotatedWith(MythicAIGoal.class)) {
            try {
                String name = clazz.getAnnotation(MythicAIGoal.class).name();
                String[] aliases = clazz.getAnnotation(MythicAIGoal.class).aliases();
                if (PathfinderAdapter.class.isAssignableFrom(clazz)) {
                    this.AI_TARGETS.put(name.toUpperCase(), (Class<? extends PathfinderAdapter>) clazz);

                    for (String alias : aliases) {
                        this.AI_TARGETS.put(alias.toUpperCase(), (Class<? extends PathfinderAdapter>) clazz);
                    }
                }
            } catch (Exception var15) {
                MythicLogger.error("Failed to load custom AI targeter {0}", clazz.getCanonicalName());
            }
        }

        for (Class<?> clazz : new VersionCompliantReflections("io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai.targeters")
                .getTypesAnnotatedWith(MythicAIGoal.class)) {
            try {
                String name = clazz.getAnnotation(MythicAIGoal.class).name();
                String[] aliases = clazz.getAnnotation(MythicAIGoal.class).aliases();
                if (PathfinderAdapter.class.isAssignableFrom(clazz)) {
                    this.AI_TARGETS.put(name.toUpperCase(), (Class<? extends PathfinderAdapter>) clazz);

                    for (String alias : aliases) {
                        this.AI_TARGETS.put(alias.toUpperCase(), (Class<? extends PathfinderAdapter>) clazz);
                    }
                }
            } catch (Exception var14) {
                MythicLogger.error("Failed to load wrapped AI targeter {0}", clazz.getCanonicalName());
            }
        }
    }

    @Override
    public void setTarget(LivingEntity entity, LivingEntity target) {
        if (entity instanceof Creature) {
            try {
                ((Creature) entity).setTarget(target);
            } catch (Exception exception) {
            }
        } else {
            try {
                ((EntityInsentient) ((CraftLivingEntity) entity).getHandle()).setTarget(((CraftLivingEntity) target).getHandle(), TargetReason.CUSTOM, true);
            } catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Override
    public void navigateToLocation(AbstractEntity entity, AbstractLocation destination, double maxDistance) {
        if (entity.isLiving()) {
            EntityInsentient e = (EntityInsentient) ((CraftLivingEntity) BukkitAdapter.adapt(entity)).getHandle();
            e.G().a(destination.getX(), destination.getY(), destination.getZ(), maxDistance);
        }
    }

    public void clearPathfinderGoals(AbstractEntity entity) {
        if (entity.isLiving()) {
            EntityInsentient e = (EntityInsentient) ((CraftLivingEntity) BukkitAdapter.adapt(entity)).getHandle();
            PathfinderGoalSelector goals = (PathfinderGoalSelector) this.refEntityInsentient.get(e, OBF_MOB_GOAL);
            ((Map) this.refGoalSelector.get(goals, OBF_PATHFINDER_1)).clear();
            ((Set) this.refGoalSelector.get(goals, OBF_PATHFINDER_2)).clear();
        }
    }

    public void clearPathfinderTargets(AbstractEntity entity) {
        if (entity.isLiving()) {
            EntityInsentient e = (EntityInsentient) ((CraftLivingEntity) BukkitAdapter.adapt(entity)).getHandle();
            PathfinderGoalSelector goals = (PathfinderGoalSelector) this.refEntityInsentient.get(e, OBF_MOB_TARG);
            ((Map) this.refGoalSelector.get(goals, OBF_PATHFINDER_1)).clear();
            ((Set) this.refGoalSelector.get(goals, OBF_PATHFINDER_2)).clear();
        }
    }

    public void addPathfindersGoal(int index, AbstractEntity entity, PathfinderGoal goal, Predicate<AbstractEntity> validator) {
        if (validator == null || validator.test(entity)) {
            EntityInsentient nmsEntity = (EntityInsentient) ((CraftLivingEntity) BukkitAdapter.adapt(entity)).getHandle();
            PathfinderGoalSelector goals = (PathfinderGoalSelector) this.refEntityInsentient.get(nmsEntity, "");

            try {
                goals.a(index, goal);
            } catch (Exception var8) {
                MythicLogger.error("Failed to apply pathfinder goal");
                if (ConfigManager.debugLevel > 0) {
                    var8.printStackTrace();
                }
            }
        }
    }

    @Override
    public void addPathfinderGoals(LivingEntity entity, List<String> aiMods) {
        try {
            EntityInsentient e = (EntityInsentient) ((CraftLivingEntity) entity).getHandle();
            PathfinderGoalSelector goals = (PathfinderGoalSelector) this.refEntityInsentient.get(e, OBF_MOB_GOAL);
            int i = 0;
            int j = 0;

            for (String str : aiMods) {
                ++i;
                String[] split = str.split(" ");
                String goal;
                if (split[0].matches("[0-9]*")) {
                    j = Integer.parseInt(split[0]);
                    goal = split[1];
                    if (split.length > 2) {
                        String data = split[2];
                    } else {
                        String data = null;
                    }

                    if (split.length > 3) {
                        String data2 = split[3];
                    } else {
                        String data2 = null;
                    }
                } else {
                    j = i;
                    goal = split[0];
                    if (split.length > 1) {
                        String data = split[1];
                    } else {
                        String data = null;
                    }

                    if (split.length > 2) {
                        String data2 = split[2];
                    } else {
                        String data2 = null;
                    }
                }

                MythicLineConfig mlc = new MythicLineConfig(MythicLineConfig.unparseBlock(goal));
                goal = mlc.getKey();
                if (this.AI_GOALS.containsKey(goal.toUpperCase())) {
                    Class<? extends PathfinderAdapter> clazz = this.AI_GOALS.get(goal.toUpperCase());

                    try {
                        if (Pathfinder.class.isAssignableFrom(clazz)) {
                            Pathfinder pathfinder = (Pathfinder) clazz.getConstructor(AbstractEntity.class, String.class, MythicLineConfig.class)
                                    .newInstance(BukkitAdapter.adapt(entity), str, mlc);
                            goals.a(j, CustomAIAdapter.create(pathfinder));
                        } else {
                            WrappedPathfinder wrappedPathfinder = (WrappedPathfinder) clazz.getConstructor(AbstractEntity.class, String.class, MythicLineConfig.class)
                                    .newInstance(BukkitAdapter.adapt(entity), str, mlc);
                            PathfinderHolder holder = (PathfinderHolder) wrappedPathfinder;
                            if (holder.isValid()) {
                                PathfinderGoal pathfinder = holder.create();
                                goals.a(j, pathfinder);
                            } else {
                                MythicLogger.error("AI pathfinder {0} is not valid for this mob type", goal);
                            }
                        }
                        continue;
                    } catch (Error | Exception var19) {
                        MythicLogger.error("Failed to construct AI pathfinder {0}", goal);
                        var19.printStackTrace();
                    }
                }

                String var28 = goal.toLowerCase();
                switch (var28) {
                    case "reset":
                    case "clear":
                        ((Map) this.refGoalSelector.get(goals, OBF_PATHFINDER_1)).clear();
                        ((Set) this.refGoalSelector.get(goals, OBF_PATHFINDER_2)).clear();
                }
            }
        } catch (Exception var20) {
            MythicLogger.error("An error occurred while adding an AIGoalSelector, enable debugging for a stack trace.");
            if (ConfigManager.debugLevel > 0) {
                var20.printStackTrace();
            }
        }
    }

    @Override
    public void addTargetGoals(LivingEntity entity, List<String> aiMods) {
        try {
            EntityInsentient e = (EntityInsentient) ((CraftLivingEntity) entity).getHandle();
            PathfinderGoalSelector goals = (PathfinderGoalSelector) this.refEntityInsentient.get(e, OBF_MOB_TARG);
            int i = 0;
            int j = 0;

            for (String str : aiMods) {
                ++i;
                String[] split = str.split(" ");
                String goal;
                if (split[0].matches("[0-9]*")) {
                    j = Integer.parseInt(split[0]);
                    goal = split[1];
                    if (split.length > 2) {
                        String data = split[2];
                    } else {
                        String data = "";
                    }
                } else {
                    j = i;
                    goal = split[0];
                    if (split.length > 1) {
                        String data = split[1];
                    } else {
                        String data = "";
                    }
                }

                MythicLineConfig mlc = new MythicLineConfig(goal);
                goal = mlc.getKey();
                if (this.AI_TARGETS.containsKey(goal.toUpperCase())) {
                    Class<? extends PathfinderAdapter> clazz = this.AI_TARGETS.get(goal.toUpperCase());

                    try {
                        if (clazz.isAssignableFrom(Pathfinder.class)) {
                            Pathfinder pathfinder = (Pathfinder) clazz.getConstructor(AbstractEntity.class, String.class, MythicLineConfig.class)
                                    .newInstance(BukkitAdapter.adapt(entity), str, mlc);
                            goals.a(j, CustomAIAdapter.create(pathfinder));
                        } else {
                            WrappedPathfinder wrappedPathfinder = (WrappedPathfinder) clazz.getConstructor(AbstractEntity.class, String.class, MythicLineConfig.class)
                                    .newInstance(BukkitAdapter.adapt(entity), str, mlc);
                            PathfinderHolder holder = (PathfinderHolder) wrappedPathfinder;
                            if (holder.isValid()) {
                                PathfinderGoal pathfinder = holder.create();
                                goals.a(j, pathfinder);
                            } else {
                                MythicLogger.error("AI pathfinder {0} is not valid for this mob type", goal);
                            }
                        }
                        continue;
                    } catch (Error | Exception var19) {
                        MythicLogger.error("Failed to construct AI pathfinder {0}", goal);
                        var19.printStackTrace();
                    }
                }

                String var25 = goal.toLowerCase();
                switch (var25) {
                    case "reset":
                    case "clear":
                        ((Map) this.refGoalSelector.get(goals, OBF_PATHFINDER_1)).clear();
                        ((Set) this.refGoalSelector.get(goals, OBF_PATHFINDER_2)).clear();
                        e.h((EntityLiving) null);
                }
            }
        } catch (Exception var20) {
            MythicLogger.error("An error has occurred, enable debugging for a stack trace.");
            if (ConfigManager.debugLevel > 0) {
                var20.printStackTrace();
            }
        }
    }
}
