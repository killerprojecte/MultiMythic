package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R2;

import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.logging.MythicLogger;
import io.lumine.xikage.mythicmobs.utils.Schedulers;
import io.lumine.xikage.mythicmobs.volatilecode.VolatileCodeHandler;
import io.lumine.xikage.mythicmobs.volatilecode.handlers.VolatileBlockHandler;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.piston.BlockPiston;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.type.Piston;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;

public class VolatileBlockHandler_v1_19_R2 implements VolatileBlockHandler {
    public VolatileBlockHandler_v1_19_R2(VolatileCodeHandler handler) {
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
    public void togglePowerable(AbstractLocation location) {
        this.togglePowerable(location, 0L);
    }

    @Override
    public void togglePowerable(AbstractLocation location, long duration) {
        Location l = BukkitAdapter.adapt(location);
        Block block = l.getWorld().getBlockAt(l);
        BlockData bd = block.getBlockData();
        if (bd instanceof Powerable pbd) {
           pbd.setPowered(true);
            block.setBlockData(pbd);
            this.applyPhysics(block);
            Schedulers.sync().runLater(() -> {
                pbd.setPowered(false);
                block.setBlockData(pbd);
                this.applyPhysics(block);
            }, duration);
        }
    }

    @Override
    public void togglePiston(AbstractLocation target) {
        Location location = BukkitAdapter.adapt(target);
        Block block = location.getBlock();
        if (block.getType() != Material.PISTON) {
            MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Location was not piston");
        } else {
            BlockData bd = block.getBlockData();
            Piston redstone = (Piston) bd;
            boolean extended = redstone.isExtended();
            BlockFace bf = ((Directional) bd).getFacing();
            if (extended) {
                redstone.setExtended(false);
                block.setBlockData(redstone);
            }

            redstone.setExtended(true);
            block.setBlockData(redstone);
            WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
            BlockPosition blockposition = new BlockPosition(location.getX(), location.getY(), location.getZ());
            IBlockData iblockdata = world.a_(blockposition);
            boolean bold = BlockPiston.a(iblockdata, world, blockposition, EnumDirection.c, true, EnumDirection.d);
            if (!bold) {
                redstone.setExtended(false);
                block.setBlockData(redstone);
            }
        }
    }
}
