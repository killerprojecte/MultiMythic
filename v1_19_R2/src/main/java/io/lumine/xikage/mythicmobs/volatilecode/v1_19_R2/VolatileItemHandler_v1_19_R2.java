package io.lumine.xikage.mythicmobs.volatilecode.v1_19_R2;

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
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutCollect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.level.World;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftTrident;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class VolatileItemHandler_v1_19_R2 implements VolatileItemHandler {
    private final Reflector refItemStack = new Reflector(CraftItemStack.class, "handle");

    public VolatileItemHandler_v1_19_R2(VolatileCodeHandler handler) {
    }

    @Override
    public void destroyItem(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack realStack = (net.minecraft.world.item.ItemStack) this.refItemStack.get(itemStack, "handle");
        if (realStack != null) {
            realStack.f(0);
        }
    }

    @Override
    public ItemStack addNBTData(ItemStack itemStack, String key, Tag value) {
        net.minecraft.world.item.ItemStack nmsItemStack = (net.minecraft.world.item.ItemStack) this.refItemStack.get(itemStack, "handle");
        NBTTagCompound tag = nmsItemStack.t() ? nmsItemStack.u() : new NBTTagCompound();
        CompoundTag compound = CompoundTag_v1_19_R2.fromNMSTag(tag).createBuilder().put(key, value).build();
        nmsItemStack.c(((CompoundTag_v1_19_R2) compound).toNMSTag());
        return itemStack;
    }

    @Override
    public CompoundTag getNBTData(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItemStack != null && nmsItemStack.t()) {
            return CompoundTag_v1_19_R2.fromNMSTag(nmsItemStack.u());
        }
        return new CompoundTag_v1_19_R2(new HashMap<String, Tag>());
    }

    @Override
    public ItemStack setNBTData(ItemStack itemStack, CompoundTag compoundTag) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        nmsItemStack.c(((CompoundTag_v1_19_R2) compoundTag).toNMSTag());
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public ItemStack setNBTData(ItemStack itemStack, CompoundTag compoundTag, DropMetadata meta) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        nmsItemStack.c(((CompoundTag_v1_19_R2) compoundTag).toNMSTag(meta));
        return CraftItemStack.asBukkitCopy(nmsItemStack);
    }

    @Override
    public int spawnFakeItem(Player player, ItemStack itemStack, AbstractLocation location) {
        EntityItem item = new EntityItem(((CraftWorld) BukkitAdapter.adapt(location.getWorld())).getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(itemStack));
        item.a(location.getX(), location.getY(), location.getZ(), 0.0f, 0.0f);
        PacketPlayOutSpawnEntity spawnItem = new PacketPlayOutSpawnEntity(item);
        PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(item.ah(), item.al().c());
        Schedulers.async().run(() -> {
            ((CraftPlayer) player).getHandle().b.a(spawnItem);
            ((CraftPlayer) player).getHandle().b.a(data);
        });
        return item.ah();
    }

    @Override
    public void collectFakeItem(Player player, int id) {
        int playerID = ((CraftPlayer) player).getHandle().ah();
        PacketPlayOutCollect spawnItem = new PacketPlayOutCollect(id, playerID, 1);
        Schedulers.async().run(() -> ((CraftPlayer) player).getHandle().b.a(spawnItem));
    }

    public void updateFakeItem(Player player, int id, ItemStack itemStack, AbstractLocation location) {
        EntityItem item = new EntityItem(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(itemStack));
        item.a(location.getX(), location.getY(), location.getZ(), 0.0f, 0.0f);
        PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(item.ah(), item.al().c());
        Schedulers.async().run(() -> ((CraftPlayer) player).getHandle().b.a(data));
    }

    @Override
    public void destroyFakeItem(Player player, int id) {
        int playerID = ((CraftPlayer) player).getHandle().ah();
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(id);
        Schedulers.async().run(() -> ((CraftPlayer) player).getHandle().b.a(destroyPacket));
    }

    public void playTotemEffect(AbstractPlayer player, int modelId) {
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
    public void resetItemRecharge(Player player) {
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        ep.fY();
    }

    @Override
    public void setTridentItem(Trident entity, ItemStack item) {
        ((CraftTrident) entity).getHandle().aq = CraftItemStack.asNMSCopy(item);
    }
}
