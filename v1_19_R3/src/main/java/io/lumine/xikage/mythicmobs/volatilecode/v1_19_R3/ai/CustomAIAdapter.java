package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R3.ai;

import io.lumine.xikage.mythicmobs.mobs.ai.Pathfinder;
import io.lumine.xikage.mythicmobs.mobs.ai.PathfinderAdapter;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;

import java.util.EnumSet;

public class CustomAIAdapter extends PathfinderGoal implements PathfinderAdapter {
    private final Pathfinder goal;

    public CustomAIAdapter(Pathfinder goal) {
        this.goal = goal;
        switch (goal.getGoalType()) {
            case MOVE:
                this.a(EnumSet.of(Type.a));
                break;
            case MOVE_LOOK:
                this.a(EnumSet.of(Type.a, Type.b));
                break;
            case TARGET:
                this.a(EnumSet.of(Type.d));
        }
    }

    public static PathfinderGoal create(Pathfinder goal) {
        return new CustomAIAdapter(goal);
    }

    @Override
    public boolean isValid() {
        return this.goal.isValid();
    }

    public boolean a() {
        try {
            return this.goal.shouldStart();
        } catch (Error | Exception var2) {
            var2.printStackTrace();
            return false;
        }
    }

    public void c() {
        try {
            this.goal.start();
        } catch (Error | Exception var2) {
            var2.printStackTrace();
        }
    }

    public void e() {
        try {
            this.goal.tick();
        } catch (Error | Exception var2) {
            var2.printStackTrace();
        }
    }

    public boolean b() {
        try {
            return !this.goal.shouldEnd();
        } catch (Error | Exception var2) {
            var2.printStackTrace();
            return true;
        }
    }

    public void d() {
        try {
            this.goal.end();
        } catch (Error | Exception var2) {
            var2.printStackTrace();
        }
    }

    public Pathfinder getGoal() {
        return this.goal;
    }
}
