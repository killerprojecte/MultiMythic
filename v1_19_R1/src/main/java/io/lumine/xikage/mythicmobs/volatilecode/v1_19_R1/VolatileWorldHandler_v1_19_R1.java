package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R1;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractVector;
import io.lumine.xikage.mythicmobs.adapters.AbstractWorld;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.utils.Schedulers;
import io.lumine.xikage.mythicmobs.utils.lib.lang3.Validate;
import io.lumine.xikage.mythicmobs.utils.numbers.Numbers;
import io.lumine.xikage.mythicmobs.utils.reflection.Reflector;
import io.lumine.xikage.mythicmobs.volatilecode.VolatileCodeHandler;
import io.lumine.xikage.mythicmobs.volatilecode.handlers.VolatileWorldHandler;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.level.block.BlockBell;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.entity.Entity;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class VolatileWorldHandler_v1_19_R1 implements VolatileWorldHandler {
    public Thread resetServerThread;

    public VolatileWorldHandler_v1_19_R1(VolatileCodeHandler handler) {
    }

    @Override
    public void registerWorldAccess(World world) {
    }

    @Override
    public void unregisterWorldAccess(World world) {
    }

    @Override
    public void playSoundAtLocation(AbstractLocation location, String sound, float volume, float pitch, double radius) {
        Location l = BukkitAdapter.adapt(location);
        l.getWorld().playSound(l, sound, volume, pitch);
    }

    @Override
    public boolean isChunkLoaded(AbstractWorld world, int x, int z) {
        CraftWorld bukkitWorld = (CraftWorld) BukkitAdapter.adapt(world);
        WorldServer nmsWorld = bukkitWorld.getHandle();
        return null != nmsWorld.getChunkIfLoaded(x, z);
    }

    @Override
    public int getEntitiesInChunk(AbstractWorld world, int x, int z) {
        CraftWorld bukkitWorld = (CraftWorld) BukkitAdapter.adapt(world);
        WorldServer nmsWorld = bukkitWorld.getHandle();
        Chunk chunk = nmsWorld.getChunkIfLoaded(x, z);
        return chunk == null ? 0 : chunk.bukkitChunk.getEntities().length;
    }

    @Override
    public void doBlockTossEffect(AbstractLocation target, Material material, AbstractVector velocity, int duration, boolean hideSourceBlock) {
        Location location = BukkitAdapter.adapt(target);
        BlockPosition blockPosition = new BlockPosition(target.getBlockX(), target.getBlockY(), target.getBlockZ());
        CraftWorld bukkitWorld = (CraftWorld) location.getWorld();
        WorldServer nmsWorld = bukkitWorld.getHandle();
        IBlockData blockState = material == null ? nmsWorld.a_(blockPosition) : ((CraftBlockData) Bukkit.createBlockData(material)).getState();
        try {
            Constructor<EntityFallingBlock> ref = (Constructor<EntityFallingBlock>) Reflector.getConstructor(EntityFallingBlock.class, World.class, Double.class, Double.class, Double.class, IBlockData.class);
            ref.setAccessible(true);
            EntityFallingBlock block = ref.newInstance(nmsWorld, (double) target.getBlockX() + 0.5, target.getBlockY() + 1, (double) target.getBlockZ() + 0.5, blockState);
            PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(block, BlockBell.i(blockState), blockPosition);
            PacketPlayOutEntityVelocity packetV = new PacketPlayOutEntityVelocity(block.ae(), new Vec3D(velocity.getX(), velocity.getY(), velocity.getZ()));
            for (EntityPlayer p : nmsWorld.a(sp -> sp.x().equals(nmsWorld))) {
                if (hideSourceBlock) {
                    p.getBukkitEntity().sendBlockChange(location, Material.AIR, (byte) 0);
                }
                p.b.a(packet);
                p.b.a(packetV);
                Schedulers.async().runLater(() -> {
                    PacketPlayOutEntityDestroy packet2 = new PacketPlayOutEntityDestroy(block.ae());
                    p.b.a(packet2);
                    if (hideSourceBlock) {
                        p.getBukkitEntity().sendBlockChange(location, location.getBlock().getBlockData());
                    }
                }, duration);
            }
        } catch (Error | Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public float getDifficultyScale(AbstractLocation location) {
        BlockPosition pos = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        DifficultyDamageScaler scaler = ((CraftWorld) location.getWorld()).getHandle().d_(pos);
        return scaler.b();
    }

    @Override
    public Collection<AbstractEntity> getEntitiesNearLocation(AbstractLocation location, double radius, Predicate<AbstractEntity> predicate) {
        List<AbstractEntity> entities = new ArrayList<>();
        CraftWorld bukkitWorld = (CraftWorld) BukkitAdapter.adapt(location.getWorld());
        WorldServer nmsWorld = bukkitWorld.getHandle();
        int smallX = Numbers.floor((location.getX() - radius) / 16.0);
        int bigX = Numbers.floor((location.getX() + radius) / 16.0);
        int smallZ = Numbers.floor((location.getZ() - radius) / 16.0);
        int bigZ = Numbers.floor((location.getZ() + radius) / 16.0);

        for (int x = smallX; x <= bigX; ++x) {
            for (int z = smallZ; z <= bigZ; ++z) {
                Chunk chunk = nmsWorld.getChunkIfLoaded(x, z);
                if (null != chunk) {
                    for (Entity e : chunk.bukkitChunk.getEntities()) {
                        AbstractEntity entity = BukkitAdapter.adapt(e);
                        if (predicate == null || predicate.test(entity)) {
                            entities.add(entity);
                        }
                    }
                }
            }
        }

        return entities;
    }

    public RayTraceResult rayTraceEntities(Location start, Vector direction, double maxDistance, double raySize, Predicate<Entity> filter) {
        Validate.notNull(start, "Start location is null!");
        start.checkFinite();
        Validate.notNull(direction, "Direction is null!");
        direction.checkFinite();
        Validate.isTrue(direction.lengthSquared() > 0.0, "Direction's magnitude is 0!");
        if (maxDistance < 0.0) {
            return null;
        } else {
            Vector startPos = start.toVector();
            Vector dir = direction.clone().normalize().multiply(maxDistance);
            BoundingBox aabb = BoundingBox.of(startPos, startPos).expandDirectional(dir).expand(raySize);
            Collection<Entity> entities = start.getWorld().getNearbyEntities(aabb, filter);
            Entity nearestHitEntity = null;
            RayTraceResult nearestHitResult = null;
            double nearestDistanceSq = Double.MAX_VALUE;

            for (Entity entity : entities) {
                BoundingBox boundingBox = entity.getBoundingBox().expand(raySize);
                RayTraceResult hitResult = boundingBox.rayTrace(startPos, direction, maxDistance);
                if (hitResult != null) {
                    double distanceSq = startPos.distanceSquared(hitResult.getHitPosition());
                    if (distanceSq < nearestDistanceSq) {
                        nearestHitEntity = entity;
                        nearestHitResult = hitResult;
                        nearestDistanceSq = distanceSq;
                    }
                }
            }

            return nearestHitEntity == null ? null : new RayTraceResult(nearestHitResult.getHitPosition(), nearestHitEntity, nearestHitResult.getHitBlockFace());
        }
    }

    @Override
    public RayTraceResult rayTrace(
            Location start, Vector direction, double maxDistance, double raySize, Predicate<Entity> entityFilter, Predicate<Material> blockFilter
    ) {
        if (!(direction.lengthSquared() < 1.0E-5) && !(maxDistance <= 1.0E-5)) {
            RayTraceResult blockRayTrace = null;
            RayTraceResult entityRayTrace = start.getWorld().rayTraceEntities(start, direction, maxDistance, raySize, entityFilter);
            double distance;
            if (entityRayTrace != null && entityRayTrace.getHitEntity() != null) {
                distance = start.distance(entityRayTrace.getHitEntity().getLocation());
                if (distance == 0.0) {
                    distance = maxDistance;
                }
            } else {
                distance = maxDistance;
            }

            BlockIterator bIterator = new BlockIterator(start.getWorld(), start.toVector(), direction, 0.0, (int) Math.ceil(distance));
            Block block = null;

            while (bIterator.hasNext()) {
                block = bIterator.next();
                if (!block.isEmpty() && !blockFilter.test(block.getType())) {
                    RayTraceResult res = block.rayTrace(start, direction, distance, FluidCollisionMode.ALWAYS);
                    if (res != null) {
                        blockRayTrace = res;
                        break;
                    }
                }
            }

            if (entityRayTrace != null && entityRayTrace.getHitEntity() != null) {
                return blockRayTrace != null ? blockRayTrace : entityRayTrace;
            } else {
                return blockRayTrace != null ? blockRayTrace : new RayTraceResult(block.getLocation().toVector());
            }
        } else {
            return null;
        }
    }

    @Override
    public void changeWorldServerThread(World world) {
    }

    @Override
    public void restoreWorldServerThread(World world) {
    }

    @Override
    public Entity spawnInvisibleArmorStand(Location location) {
        WorldServer w = ((CraftWorld) location.getWorld()).getHandle();
        EntityArmorStand nmsEntity = new EntityArmorStand(w, location.getX(), location.getY(), location.getZ());
        nmsEntity.a(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        nmsEntity.j(true);
        w.b(nmsEntity);
        return nmsEntity.getBukkitEntity();
    }
}
