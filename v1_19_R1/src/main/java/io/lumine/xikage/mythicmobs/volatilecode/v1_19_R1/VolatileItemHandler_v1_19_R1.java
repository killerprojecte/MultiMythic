package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R1;

import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractPlayer;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.util.jnbt.CompoundTag;
import io.lumine.xikage.mythicmobs.util.jnbt.Tag;
import io.lumine.xikage.mythicmobs.utils.Schedulers;
import io.lumine.xikage.mythicmobs.utils.reflection.Reflector;
import io.lumine.xikage.mythicmobs.volatilecode.VolatileCodeHandler;
import io.lumine.xikage.mythicmobs.volatilecode.handlers.VolatileItemHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayOutCollect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.item.EntityItem;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftTrident;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class VolatileItemHandler_v1_19_R1 implements VolatileItemHandler {
    private final Reflector refItemStack = new Reflector(CraftItemStack.class, "handle");

    public VolatileItemHandler_v1_19_R1(VolatileCodeHandler handler) {
    }

    @Override
    public void destroyItem(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack realStack = (net.minecraft.world.item.ItemStack) this.refItemStack.get(itemStack, "handle");
        if (realStack != null) {
            realStack.e(0);
        }
    }

    @Override
    public ItemStack addNBTData(ItemStack itemStack, String key, Tag value) {
        net.minecraft.world.item.ItemStack nmsItemStack = (net.minecraft.world.item.ItemStack) this.refItemStack.get(itemStack, "handle");
        NBTTagCompound tag = nmsItemStack.t() ? nmsItemStack.u() : new NBTTagCompound();
        CompoundTag compound = CompoundTag_v1_19_R1.fromNMSTag(tag).createBuilder().put(key, value).build();
        nmsItemStack.c(((CompoundTag_v1_19_R1) compound).toNMSTag());
        return itemStack;
    }

    @Override
    public CompoundTag getNBTData(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack != null && nmsItemStack.t() ? CompoundTag_v1_19_R1.fromNMSTag(nmsItemStack.u()) : new CompoundTag_v1_19_R1(new HashMap());
    }

    @Override
    public ItemStack setNBTData(ItemStack itemStack, CompoundTag compoundTag) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        nmsItemStack.c(((CompoundTag_v1_19_R1) compoundTag).toNMSTag());
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public ItemStack setNBTData(ItemStack itemStack, CompoundTag compoundTag, DropMetadata meta) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        nmsItemStack.c(((CompoundTag_v1_19_R1) compoundTag).toNMSTag(meta));
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public int spawnFakeItem(Player player, ItemStack itemStack, AbstractLocation location) {
        EntityItem item = new EntityItem(((CraftWorld) BukkitAdapter.adapt(location.getWorld())).getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(itemStack));
        item.a(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
        PacketPlayOutSpawnEntity spawnItem = new PacketPlayOutSpawnEntity(item);
        PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(item.ae(), item.ai(), true);
        Schedulers.async().run(() -> {
            ((CraftPlayer) player).getHandle().b.a(spawnItem);
            ((CraftPlayer) player).getHandle().b.a(data);
        });
        return item.ae();
    }

    @Override
    public void collectFakeItem(Player player, int id) {
        int playerID = ((CraftPlayer) player).getHandle().ae();
        PacketPlayOutCollect spawnItem = new PacketPlayOutCollect(id, playerID, 1);
        Schedulers.async().run(() -> {
            ((CraftPlayer) player).getHandle().b.a(spawnItem);
        });
    }

    public void updateFakeItem(Player player, int id, ItemStack itemStack, AbstractLocation location) {
        EntityItem item = new EntityItem(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(itemStack));
        item.a(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
        PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(item.ae(), item.ai(), true);
        Schedulers.async().run(() -> {
            ((CraftPlayer) player).getHandle().b.a(data);
        });
    }

    @Override
    public void destroyFakeItem(Player player, int id) {
        int playerID = ((CraftPlayer) player).getHandle().ae();
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(id);
        Schedulers.async().run(() -> {
            ((CraftPlayer) player).getHandle().b.a(destroyPacket);
        });
    }

    public void playTotemEffect(AbstractPlayer player, int modelId) {
    }

    @Override
    public float getItemRecharge(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        return ep.v(0.0F);
    }

    @Override
    public boolean getItemRecharging(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        return ep.v(0.0F) < 1.0F;
    }

    @Override
    public void resetItemRecharge(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.fV();
    }

    @Override
    public void setTridentItem(Trident entity, ItemStack item) {
        ((CraftTrident) entity).getHandle().aq = CraftItemStack.asNMSCopy(item);
    }
}
