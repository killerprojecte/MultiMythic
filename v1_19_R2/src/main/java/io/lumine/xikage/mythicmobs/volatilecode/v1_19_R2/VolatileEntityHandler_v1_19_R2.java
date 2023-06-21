package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R2;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.adapters.AbstractVector;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.ConfigManager;
import io.lumine.xikage.mythicmobs.logging.MythicLogger;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.SkillCaster;
import io.lumine.xikage.mythicmobs.skills.damage.DamageMetadata;
import io.lumine.xikage.mythicmobs.util.jnbt.CompoundTag;
import io.lumine.xikage.mythicmobs.util.jnbt.Tag;
import io.lumine.xikage.mythicmobs.utils.Schedulers;
import io.lumine.xikage.mythicmobs.utils.adventure.text.serializer.gson.GsonComponentSerializer;
import io.lumine.xikage.mythicmobs.utils.items.ItemFactory;
import io.lumine.xikage.mythicmobs.utils.numbers.Numbers;
import io.lumine.xikage.mythicmobs.utils.reflection.Reflector;
import io.lumine.xikage.mythicmobs.utils.text.Text;
import io.lumine.xikage.mythicmobs.volatilecode.VolatileCodeHandler;
import io.lumine.xikage.mythicmobs.volatilecode.handlers.VolatileEntityHandler;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeMapBase;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftItem;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class VolatileEntityHandler_v1_19_R2 implements VolatileEntityHandler {
    private static final String ENTITY_DIMENSIONS = "aZ";
    private static final String ENTITY_EYE_HEIGHT = "ba";
    private static final String ATTRIBUTES = "b";
    private static final String BYPASS_ARMOR = "x";
    private static final String FOOD_EXHAUSTION = "B";
    private static final String BYPASS_ENCHANTS = "A";
    private static final Reflector<AttributeMapBase> refAttributeMap = new Reflector<AttributeMapBase>(AttributeMapBase.class, "b");
    private static final Reflector<DamageSource> refDamageSource = new Reflector<DamageSource>(DamageSource.class, "x", "B", "A");
    private static final Reflector<net.minecraft.world.entity.Entity> refEntity = new Reflector<net.minecraft.world.entity.Entity>(net.minecraft.world.entity.Entity.class, "aZ", "ba");

    public VolatileEntityHandler_v1_19_R2(VolatileCodeHandler handler) {
    }

    @Override
    public void setCustomName(AbstractEntity entity, String name) {
        net.minecraft.world.entity.Entity le = ((CraftEntity) entity.getBukkitEntity()).getHandle();
        le.b(CraftChatMessage.fromJSON(GsonComponentSerializer.gson().serialize(Text.parse(name))));
    }

    @Override
    public void doDamage(DamageMetadata data, AbstractEntity aTarget) {
        SkillCaster caster = data.getDamager();
        double damage = data.getAmount();
        DamageSource reason = this.getDamageSource(data);
        if (aTarget == null) {
            return;
        }
        if (!aTarget.isDamageable()) {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Damage cancelled: target is not damageable");
            return;
        }
        caster.setUsingDamageSkill(true);
        caster.getEntity().setMetadata("doing-skill-damage", true);
        aTarget.setMetadata("skill-damage", data);
        SkillCaster skillCaster = caster;
        if (skillCaster instanceof ActiveMob am) {
           am.setLastDamageSkillAmount(damage);
        }
        EntityLiving target = ((CraftLivingEntity) aTarget.getBukkitEntity()).getHandle();
        AttributeModifier mod = new AttributeModifier(UUID.randomUUID(), "mythic$kbresist", 1.0, AttributeModifier.Operation.ADD_NUMBER);
        try {
            if (data.getPreventsKnockback().booleanValue()) {
                ((CraftLivingEntity) aTarget.getBukkitEntity()).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).addModifier(mod);
            }
            target.a(reason, (float) damage);
        } catch (Exception ex) {
            if (ConfigManager.debugLevel > 0) {
                ex.printStackTrace();
            }
        } finally {
            if (data.getPreventsImmunity().booleanValue()) {
                aTarget.setNoDamageTicks(0);
            }
            ((CraftLivingEntity) aTarget.getBukkitEntity()).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).removeModifier(mod);
            caster.getEntity().removeMetadata("doing-skill-damage");
            caster.setUsingDamageSkill(false);
            aTarget.removeMetadata("skill-damage");
        }
    }

    private DamageSource getDamageSource(DamageMetadata data) {
        String var3 = data.getDamageCause().toLowerCase();
        DamageSource reason;
        switch (var3) {
            case "entity_attack":
                if (data.getDamager().getEntity().isPlayer()) {
                    reason = DamageSource.a(((CraftPlayer) data.getDamager().getEntity().getBukkitEntity()).getHandle());
                } else {
                    reason = DamageSource.c((EntityLiving) ((CraftEntity) data.getDamager().getEntity().getBukkitEntity()).getHandle());
                }
                break;
            case "magic":
                reason = DamageSource.o;
                break;
            case "thorns":
                reason = DamageSource.d(((CraftEntity) data.getDamager().getEntity().getBukkitEntity()).getHandle());
                break;
            case "fire":
                reason = DamageSource.a;
                break;
            case "fire_tick":
                reason = DamageSource.c;
                break;
            case "dragon_breath":
                reason = DamageSource.q;
                break;
            case "lava":
                reason = DamageSource.d;
                break;
            case "hot_floor":
                reason = DamageSource.e;
                break;
            case "void":
                reason = DamageSource.m;
                break;
            case "freeze":
                reason = DamageSource.t;
                break;
            case "fall":
                reason = DamageSource.k;
                break;
            default:
                reason = DamageSource.n;
        }

        return reason;
    }

    @Override
    public float getEntityAbsorptionHearts(AbstractEntity entity) {
        if (!entity.isLiving()) {
            return 0.0F;
        } else {
            EntityLiving el = (EntityLiving) ((CraftEntity) entity.getBukkitEntity()).getHandle();
            return el.eW();
        }
    }

    @Override
    public void setEntityAbsorptionHearts(AbstractEntity entity, float value) {
        if (entity.isLiving()) {
            EntityLiving el = (EntityLiving) ((CraftEntity) entity.getBukkitEntity()).getHandle();
            el.u(value);
        }
    }

    @Override
    public void setLocation(AbstractEntity entity, AbstractLocation location) {
    }

    @Override
    public void setLocation(AbstractEntity entity, double x, double y, double z, float yaw, float pitch) {
    }

    @Override
    public void setLocation(AbstractEntity entity, double x, double y, double z, float yaw, float pitch, boolean noRotation, boolean noGravity) {
        net.minecraft.world.entity.Entity e = ((CraftEntity) entity.getBukkitEntity()).getHandle();
        e.a(x, y, z, yaw, pitch);
        if (entity.isPlayer()) {
            this.playerConnectionTeleport(entity, x, y, z, yaw, pitch, noRotation, noGravity);
        }
        if (e.s instanceof WorldServer) {
            // empty if block
        }
    }

    @Override
    public void setPlayerRotation(AbstractPlayer entity, float yaw, float pitch) {
        EntityPlayer me = ((CraftPlayer) entity.getBukkitEntity()).getHandle();
        HashSet<PacketPlayOutPosition.EnumPlayerTeleportFlags> set = new HashSet<>();
        set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.a);
        set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.b);
        set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.c);
        set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.e);
        set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.d);
        me.b.a(new PacketPlayOutPosition(0.0, 0.0, 0.0, yaw, pitch, set, 0, false));
    }

    private void playerConnectionTeleport(AbstractEntity entity, double x, double y, double z, float yaw, float pitch, boolean noRotation, boolean noGravity) {
        EntityPlayer me = ((CraftPlayer) entity.getBukkitEntity()).getHandle();
        HashSet<PacketPlayOutPosition.EnumPlayerTeleportFlags> set = new HashSet<PacketPlayOutPosition.EnumPlayerTeleportFlags>();
        boolean dismountVehicle = false;
        if (noRotation) {
            pitch = 0.0f;
            yaw = 0.0f;
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.e);
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.d);
        }
        if (noGravity) {
            set.add(PacketPlayOutPosition.EnumPlayerTeleportFlags.b);
            y = 0.0;
        }
        me.b.a(new PacketPlayOutPosition(x, y, z, yaw, pitch, set, 0, dismountVehicle));
    }

    @Override
    public boolean isEntityInMotion(AbstractEntity entity, boolean exact) {
        if (entity.isLiving()) {
            EntityLiving e = (EntityLiving) ((CraftEntity) entity.getBukkitEntity()).getHandle();
            Vec3D position = e.dd();
            if (exact) {
               return e.t != position.a() || e.u != position.b() || e.v != position.c();
            } else {
                int x = Numbers.floor(e.t);
                int y = Numbers.floor(e.u);
                int z = Numbers.floor(e.v);
                int pX = Numbers.floor(position.a());
                int pY = Numbers.floor(position.b());
                int pZ = Numbers.floor(position.c());
               return x != pX || y != pY || z != pZ;
            }
        }
        return false;
    }

    @Override
    public AbstractVector getEntityMotion(AbstractEntity entity) {
        if (entity.isLiving()) {
            EntityLiving e = (EntityLiving) ((CraftEntity) entity.getBukkitEntity()).getHandle();
            Vec3D position = e.dd();
            double x = position.a() - e.t;
            double y = position.b() - e.u;
            double z = position.c() - e.v;
            return new AbstractVector(x, y, z);
        }
        return new AbstractVector(0, 0, 0);
    }

    @Override
    public void setHitBox(AbstractEntity target, double a0, double a1, double a2) {
        org.bukkit.entity.Entity entity = BukkitAdapter.adapt(target);
        net.minecraft.world.entity.Entity ent = ((CraftEntity) entity).getHandle();
        AxisAlignedBB bb = new AxisAlignedBB(ent.dk() - a0 / 2.0, ent.dk(), ent.dl() - a0 / 2.0, ent.dk() + a0 / 2.0, ent.dm() + a0, ent.dq() + a0 / 2.0);
        ent.a(bb);
        refEntity.set(ent, "aZ", new EntitySize((float) a0, (float) a1, true));
        refEntity.set(ent, "ba", (float) (a1 * 0.8));
    }

    @Override
    public void setItemPosition(AbstractEntity target, AbstractLocation ol) {
        Entity entity = BukkitAdapter.adapt(target);
        if (entity instanceof Item item) {
           EntityItem ei = (EntityItem) ((CraftItem) item).getHandle();
            ei.h(ol.getX(), ol.getY(), ol.getZ());
        }
    }

    @Override
    public void sendEntityTeleportPacket(AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);
        net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
        PacketPlayOutEntityTeleport tp = new PacketPlayOutEntityTeleport(me);
        entity.getLocation().getWorld().getNearbyEntities(entity.getLocation(), 32.0, 32.0, 32.0).forEach(e -> {
            if (e instanceof Player) {
                ((CraftPlayer) e).getHandle().b.a(tp);
            }
        });
    }

    @Override
    public void setEntityRotation(AbstractEntity target, float pitch, float yaw) {
        Entity entity = BukkitAdapter.adapt(target);
        net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
        me.p(yaw);
        me.q(pitch);
    }

    @Override
    public void setArmorStandNoGravity(AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);
        if (entity.getType() == EntityType.ARMOR_STAND) {
            EntityArmorStand as = (EntityArmorStand) ((CraftEntity) entity).getHandle();
            as.e(true);
        }
    }

    @Override
    public void sendGameStateChange(AbstractPlayer target, int state, int skybox) {
        Player player = BukkitAdapter.adapt(target);
        PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(new PacketPlayOutGameStateChange.a(state), (float) skybox);
        ((CraftPlayer) player).getHandle().b.a(packet);
    }

    @Override
    public void forcePlayCredits(AbstractPlayer target, float f) {
        Player player = BukkitAdapter.adapt(target);
        EntityPlayer me = ((CraftPlayer) player).getHandle();
        me.b.a(new PacketPlayOutGameStateChange(new PacketPlayOutGameStateChange.a(4), f));
    }

    @Override
    public void forceCloseWindow(AbstractPlayer target) {
        Player player = BukkitAdapter.adapt(target);
        EntityPlayer me = ((CraftPlayer) player).getHandle();
        me.b.a(new PacketPlayOutCloseWindow(0));
    }

    @Override
    public void setPlayerWorldBorder(AbstractPlayer target, AbstractLocation center, int radius) {
        WorldBorder border;
        Player player = BukkitAdapter.adapt(target);
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        if (radius == -1) {
            border = ep.y().q_();
        } else {
            border = new WorldBorder();
            border.world = ep.y().q_().world;
            border.c(center.getX(), center.getZ());
            border.a((double) radius);
            border.c(1);
        }
        ep.b.a(new ClientboundSetBorderCenterPacket(border));
        ep.b.a(new ClientboundSetBorderWarningDistancePacket(border));
        ep.b.a(new ClientboundSetBorderSizePacket(border));
    }

    @Override
    public void sendPlayerFakeInventoryItem(AbstractPlayer target, ItemStack stack, int slot) {
        if (slot < 9) {
            slot += 36;
        }
        Player player = BukkitAdapter.adapt(target);
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(stack);
        PacketPlayOutSetSlot packet = new PacketPlayOutSetSlot(0, 0, slot, item);
        entityPlayer.b.a(packet);
    }

    public void hideEntityModel(AbstractEntity target) {
        Entity entity = BukkitAdapter.adapt(target);
        net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
        DataWatcher w = me.al();
        w.b(new DataWatcherObject(0, DataWatcherRegistry.a), (Object) 32);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(me.ah(), w.c());
    }

    @Override
    public void playTotemEffect(AbstractPlayer target, int model) {
        this.sendPlayerFakeInventoryItem(target, ItemFactory.of(Material.TOTEM_OF_UNDYING).model(model).build(), 45);
        Player player = BukkitAdapter.adapt(target);
        EntityPlayer me = ((CraftPlayer) player).getHandle();
        PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(me, (byte) 35);
        me.b.a(packet);
        player.stopSound(Sound.ITEM_TOTEM_USE, SoundCategory.MASTER);
        Schedulers.async().runLater(() -> this.sendPlayerFakeInventoryItem(target, player.getInventory().getItemInOffHand(), 45), 1L);
    }

    @Override
    public void setEntitySpawnReason(AbstractEntity target, SpawnReason reason) {
        Entity entity = BukkitAdapter.adapt(target);
        World world = BukkitAdapter.adapt(target.getWorld());
        net.minecraft.world.entity.Entity entityHandle = ((CraftEntity) entity).getHandle();
        WorldServer worldHandle = ((CraftWorld) world).getHandle();
        worldHandle.addFreshEntity(entityHandle, reason);
    }

    @Override
    public void playFreezeEffect(AbstractPlayer target, int ticks) {
        Player player = (Player) target.getBukkitEntity();
        EntityPlayer me = ((CraftPlayer) player).getHandle();
        me.j(ticks);
        me.b.a(new PacketPlayOutEntityMetadata(me.ah(), me.al().c()));
    }

    @Override
    public void playEntityAnimation(AbstractEntity target, byte effect, Collection<AbstractEntity> audience) {
        net.minecraft.world.entity.Entity e = ((CraftEntity) target.getBukkitEntity()).getHandle();
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation(e, effect);
        for (AbstractEntity entity : audience) {
            if (!entity.isPlayer()) continue;
            Player player = (Player) entity.getBukkitEntity();
            EntityPlayer me = ((CraftPlayer) player).getHandle();
            me.b.a(packet);
        }
    }

    @Override
    public void spawnFakeLightning(AbstractLocation target, double radius) {
        Location location = BukkitAdapter.adapt(target);
        World world = location.getWorld();
        WorldServer worldHandle = ((CraftWorld) world).getHandle();
        EntityLightning entitylightning = EntityTypes.Y.a(worldHandle);
        entitylightning.a_(Vec3D.a(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ())));
        entitylightning.a(true);
        PacketPlayOutSpawnEntity spawnItem = new PacketPlayOutSpawnEntity(entitylightning);
        double distanceSquared = radius * radius;
        Schedulers.async().run(() -> {
            for (Player player : location.getWorld().getPlayers()) {
                if (!(location.distanceSquared(player.getLocation()) <= distanceSquared)) continue;
                ((CraftPlayer) player).getHandle().b.a(spawnItem);
                player.playSound(location, "entity.lightning_bolt.impact", 1.0f, 1.0f);
                player.playSound(location, "entity.lightning_bolt.thunder", 10000.0f, 63.0f);
            }
        });
    }

    @Override
    public AbstractEntity addNBTData(AbstractEntity entity, String key, Tag value) {
        CompoundTag compound = this.getNBTData(entity).createBuilder().put(key, value).build();
        this.setNBTData(entity, compound);
        return entity;
    }

    @Override
    public CompoundTag getNBTData(AbstractEntity entity) {
        org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
        NBTTagCompound compound = new NBTTagCompound();
        return CompoundTag_v1_19_R2.fromNMSTag(compound);
    }

    @Override
    public AbstractEntity setNBTData(AbstractEntity entity, CompoundTag compoundTag) {
        org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
        return entity;
    }

    @Override
    public void sendActionBarMessageToPlayer(AbstractPlayer target, String message) {
        Player player = BukkitAdapter.adapt(target);
    }
}
