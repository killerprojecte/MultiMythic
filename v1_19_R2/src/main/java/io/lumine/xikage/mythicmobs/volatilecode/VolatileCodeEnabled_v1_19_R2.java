package io.lumine.xikage.mythicmobs.volatilecode;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.SkillCaster;
import io.lumine.xikage.mythicmobs.util.VectorUtils;
import io.lumine.xikage.mythicmobs.util.jnbt.CompoundTag;
import io.lumine.xikage.mythicmobs.util.jnbt.Tag;
import io.lumine.xikage.mythicmobs.volatilecode.handlers.*;
import io.lumine.xikage.mythicmobs.volatilecode.v1_19_R2.*;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftChicken;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class VolatileCodeEnabled_v1_19_R2 implements VolatileCodeHandler {
    private static final Set<EntityType> BAD_CONTROLLER_LOOK = EnumSet.of(
            EntityType.ENDERMITE,
            EntityType.ENDER_DRAGON,
            EntityType.GHAST,
            EntityType.HORSE,
            EntityType.MAGMA_CUBE,
            EntityType.POLAR_BEAR,
            EntityType.SILVERFISH,
            EntityType.SLIME
    );
    private final VolatileAIHandler AIHandler = new VolatileAIHandler_v1_19_R2(this);
    private final VolatileBlockHandler blockHandler = new VolatileBlockHandler_v1_19_R2(this);
    private final VolatileEntityHandler entityHandler = new VolatileEntityHandler_v1_19_R2(this);
    private final VolatileItemHandler itemHandler = new VolatileItemHandler_v1_19_R2(this);
    private final VolatileWorldHandler worldHandler = new VolatileWorldHandler_v1_19_R2(this);

    @Override
    public CompoundTag createCompoundTag(Map<String, Tag> value) {
        return new CompoundTag_v1_19_R2(value);
    }

    @Override
    public Set<AbstractEntity> getEntitiesBySelector(SkillCaster am, String targetSelector) {
        return null;
    }

    @Override
    public void CreateFireworksExplosion(Location location, boolean flicker, boolean trail, int type, int[] colors, int[] fadeColors, int flightDuration) {
    }

    @Override
    public void setChickenHostile(Chicken c) {
        EntityChicken chicken = ((CraftChicken) c).getHandle();
        chicken.u(true);
    }

    @Override
    public void doDamage(ActiveMob mob, AbstractEntity t, float amount) {
        CraftLivingEntity caster = (CraftLivingEntity) mob.getEntity().getBukkitEntity();
        CraftLivingEntity target = (CraftLivingEntity) t.getBukkitEntity();
        target.getHandle().a(DamageSource.c(caster.getHandle()), amount);
    }

    @Override
    public double getAbsorptionHearts(LivingEntity entity) {
        return ((CraftLivingEntity) entity).getHandle().eW();
    }

    @Override
    public void saveSkinData(Player player, String name) {
        GameProfile profile = ((CraftPlayer) player).getHandle().fD();
        Collection<Property> props = profile.getProperties().get("textures");
        Iterator var5 = props.iterator();
        if (var5.hasNext()) {
            Property prop = (Property) var5.next();
            String skin = prop.getValue();
            String sig = prop.getSignature();
            File folder = new File(MythicMobs.inst().getDataFolder(), "PlayerSkins");
            if (!folder.exists()) {
                folder.mkdir();
            }

            File skinFile = new File(folder, name + ".skin.txt");
            File sigFile = new File(folder, name + ".sig.txt");

            try {
                FileWriter writer = new FileWriter(skinFile);
                writer.write(skin);
                writer.flush();
                writer.close();
                writer = new FileWriter(sigFile);
                writer.write(sig);
                writer.flush();
                writer.close();
            } catch (Exception var13) {
                var13.printStackTrace();
            }
        }
    }

    @Override
    public float getItemRecharge(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        return ep.w(0.0f);
    }

    @Override
    public boolean getItemRecharging(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        return ep.w(0.0f) < 1.0f;
    }

    @Override
    public void doEffectArmSwing(AbstractEntity entity) {
        Entity e = ((CraftEntity) entity.getBukkitEntity()).getHandle();
        e.cH().a(e, (byte) 0);
    }

    @Override
    public void lookAt(AbstractEntity entity, float yaw, float pitch) {
        Entity handle = ((CraftEntity) BukkitAdapter.adapt(entity)).getHandle();
        yaw = VectorUtils.clampYaw(yaw);
        handle.q(yaw);
        this.setHeadYaw(entity, yaw);
        handle.l(pitch);
    }

    @Override
    public void lookAtLocation(AbstractEntity entity, AbstractLocation to, boolean headOnly, boolean immediate) {
        Entity handle = ((CraftEntity) BukkitAdapter.adapt(entity)).getHandle();
        if (immediate || headOnly || BAD_CONTROLLER_LOOK.contains(handle.getBukkitEntity().getType()) || !(handle instanceof EntityInsentient)) {
            AbstractLocation fromLocation = entity.getLocation();
            double xDiff = to.getX() - fromLocation.getX();
            double yDiff = to.getY() - fromLocation.getY();
            double zDiff = to.getZ() - fromLocation.getZ();
            double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
            double distanceY = Math.sqrt(distanceXZ * distanceXZ + yDiff * yDiff);
            double yaw = Math.toDegrees(Math.acos(xDiff / distanceXZ));
            double pitch = Math.toDegrees(Math.acos(yDiff / distanceY)) - 90.0;
            if (zDiff < 0.0) {
                yaw += Math.abs(180.0 - yaw) * 2.0;
            }
            yaw -= 90.0;
            if (headOnly) {
                this.setHeadYaw(entity, (float) yaw);
            } else {
                this.lookAt(entity, (float) yaw, (float) pitch);
            }
        } else {
            ((EntityInsentient) handle).A().a(to.getX(), to.getY(), to.getZ());
        }
    }

    @Override
    public void lookAtEntity(AbstractEntity entity, AbstractEntity to, boolean headOnly, boolean immediate) {
        Entity handle = ((CraftEntity) BukkitAdapter.adapt(entity)).getHandle();
        Entity target = ((CraftEntity) BukkitAdapter.adapt(to)).getHandle();
        if (BAD_CONTROLLER_LOOK.contains(handle.getBukkitEntity().getType())) {
            if (to.isLiving()) {
                this.lookAtLocation(entity, to.getEyeLocation(), headOnly, immediate);
            } else {
                this.lookAtLocation(entity, to.getLocation(), headOnly, immediate);
            }
        } else if (handle instanceof EntityInsentient insentient) {
           insentient.A().a(target);
        }
    }

    @Override
    public void sendResourcePack(AbstractPlayer player, String url, String hash) {
        Player p = (Player) player.getBukkitEntity();
        byte[] h = hash.getBytes();
        p.setResourcePack(url, h);
    }

    @Override
    public void applyPhysics(Block target) {
        Location location = target.getLocation();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition blockposition = new BlockPosition(location.getX(), location.getY(), location.getZ());
        IBlockData iblockdata = world.a_(blockposition);
        net.minecraft.world.level.block.Block block = iblockdata.b();
        world.b(blockposition, block);
    }

    @Override
    public VolatileAIHandler getAIHandler() {
        return this.AIHandler;
    }

    @Override
    public VolatileBlockHandler getBlockHandler() {
        return this.blockHandler;
    }

    @Override
    public VolatileEntityHandler getEntityHandler() {
        return this.entityHandler;
    }

    @Override
    public VolatileItemHandler getItemHandler() {
        return this.itemHandler;
    }

    @Override
    public VolatileWorldHandler getWorldHandler() {
        return this.worldHandler;
    }
}
