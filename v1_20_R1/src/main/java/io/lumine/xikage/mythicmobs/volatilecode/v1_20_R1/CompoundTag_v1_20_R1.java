package io.lumine.xikage.mythicmobs.volatilecode.v1_20_R1;

import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.util.jnbt.*;
import net.minecraft.nbt.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompoundTag_v1_20_R1 extends CompoundTag {
    public CompoundTag_v1_20_R1(Map<String, Tag> value) {
        super(value);
    }

    public static CompoundTag fromNMSTag(NBTTagCompound tag) {
        HashMap<String, Tag> tags = new HashMap<String, Tag>();
        for (String key : tag.e()) {
            NBTTagList list;
            NBTBase base = tag.c(key);
            if (base instanceof NBTTagInt) {
                tags.put(key, new IntTag(((NBTTagInt) base).g()));
                continue;
            }
            if (base instanceof NBTTagByte) {
                tags.put(key, new ByteTag(((NBTTagByte) base).i()));
                continue;
            }
            if (base instanceof NBTTagFloat) {
                tags.put(key, new FloatTag(((NBTTagFloat) base).k()));
                continue;
            }
            if (base instanceof NBTTagDouble) {
                tags.put(key, new DoubleTag(((NBTTagDouble) base).j()));
                continue;
            }
            if (base instanceof NBTTagByteArray) {
                tags.put(key, new ByteArrayTag(((NBTTagByteArray) base).e()));
                continue;
            }
            if (base instanceof NBTTagIntArray) {
                tags.put(key, new IntArrayTag(((NBTTagIntArray) base).g()));
                continue;
            }
            if (base instanceof NBTTagCompound) {
                tags.put(key, CompoundTag_v1_20_R1.fromNMSTag((NBTTagCompound) base));
                continue;
            }
            if (base instanceof NBTTagEnd) {
                tags.put(key, new EndTag());
                continue;
            }
            if (base instanceof NBTTagLong) {
                tags.put(key, new LongTag(((NBTTagLong) base).f()));
                continue;
            }
            if (base instanceof NBTTagShort) {
                tags.put(key, new ShortTag(((NBTTagShort) base).h()));
                continue;
            }
            if (base instanceof NBTTagString) {
                tags.put(key, new StringTag(base.m_()));
                continue;
            }
            if (!(base instanceof NBTTagList) || (list = (NBTTagList) base).size() <= 0) continue;
            NBTBase nbase = list.k(0);
            NBTTagCompound comp = new NBTTagCompound();
            comp.a("test", nbase);
            ListTagBuilder ltb = new ListTagBuilder(CompoundTag_v1_20_R1.fromNMSTag(comp).getValue().get("test").getClass());
            for (int i = 0; i < list.size(); ++i) {
                NBTBase nbase2 = list.k(i);
                NBTTagCompound comp2 = new NBTTagCompound();
                comp2.a("test", nbase2);
                ltb.add(CompoundTag_v1_20_R1.fromNMSTag(comp2).getValue().get("test"));
            }
            tags.put(key, ltb.build());
        }
        return new CompoundTag_v1_20_R1(tags);
    }

    public NBTTagCompound toNMSTag() {
        NBTTagCompound tag = new NBTTagCompound();
        for (Map.Entry entry : this.value.entrySet()) {
            if (entry.getValue() instanceof IntTag) {
                tag.a((String) entry.getKey(), ((IntTag) entry.getValue()).getValue().intValue());
                continue;
            }
            if (entry.getValue() instanceof ByteTag) {
                tag.a((String) entry.getKey(), ((ByteTag) entry.getValue()).getValue().byteValue());
                continue;
            }
            if (entry.getValue() instanceof ByteArrayTag) {
                tag.a((String) entry.getKey(), ((ByteArrayTag) entry.getValue()).getValue());
                continue;
            }
            if (entry.getValue() instanceof CompoundTag) {
                tag.a((String) entry.getKey(), ((CompoundTag_v1_20_R1) entry.getValue()).toNMSTag());
                continue;
            }
            if (entry.getValue() instanceof DoubleTag) {
                tag.a((String) entry.getKey(), ((DoubleTag) entry.getValue()).getValue().doubleValue());
                continue;
            }
            if (entry.getValue() instanceof FloatTag) {
                tag.a((String) entry.getKey(), ((FloatTag) entry.getValue()).getValue().floatValue());
                continue;
            }
            if (entry.getValue() instanceof IntArrayTag) {
                tag.a((String) entry.getKey(), ((IntArrayTag) entry.getValue()).getValue());
                continue;
            }
            if (entry.getValue() instanceof ListTag) {
                NBTTagList list = new NBTTagList();
                List<Tag> tags = ((ListTag) entry.getValue()).getValue();
                for (Tag btag : tags) {
                    HashMap<String, Tag> btags = new HashMap<String, Tag>();
                    btags.put("test", btag);
                    CompoundTag_v1_20_R1 comp = new CompoundTag_v1_20_R1(btags);
                    list.add(comp.toNMSTag().c("test"));
                }
                tag.a((String) entry.getKey(), list);
                continue;
            }
            if (entry.getValue() instanceof LongTag) {
                tag.a((String) entry.getKey(), ((LongTag) entry.getValue()).getValue().longValue());
                continue;
            }
            if (entry.getValue() instanceof ShortTag) {
                tag.a((String) entry.getKey(), ((ShortTag) entry.getValue()).getValue().shortValue());
                continue;
            }
            if (entry.getValue() instanceof StringTag) {
                tag.a((String) entry.getKey(), ((StringTag) entry.getValue()).getValue());
                continue;
            }
            if (entry.getValue() instanceof PlaceholderDoubleTag) {
                tag.a((String) entry.getKey(), ((PlaceholderDoubleTag) entry.getValue()).getValue().get());
                continue;
            }
            if (entry.getValue() instanceof PlaceholderFloatTag) {
                tag.a((String) entry.getKey(), ((PlaceholderFloatTag) entry.getValue()).getValue().get());
                continue;
            }
            if (entry.getValue() instanceof PlaceholderIntTag) {
                tag.a((String) entry.getKey(), ((PlaceholderIntTag) entry.getValue()).getValue().get());
                continue;
            }
            if (!(entry.getValue() instanceof PlaceholderStringTag)) continue;
            tag.a((String) entry.getKey(), ((PlaceholderStringTag) entry.getValue()).getValue().get());
        }
        return tag;
    }

    public NBTTagCompound toNMSTag(DropMetadata meta) {
        NBTTagCompound tag = new NBTTagCompound();
        for (Map.Entry entry : this.value.entrySet()) {
            if (entry.getValue() instanceof IntTag) {
                tag.a((String) entry.getKey(), ((IntTag) entry.getValue()).getValue().intValue());
                continue;
            }
            if (entry.getValue() instanceof ByteTag) {
                tag.a((String) entry.getKey(), ((ByteTag) entry.getValue()).getValue().byteValue());
                continue;
            }
            if (entry.getValue() instanceof ByteArrayTag) {
                tag.a((String) entry.getKey(), ((ByteArrayTag) entry.getValue()).getValue());
                continue;
            }
            if (entry.getValue() instanceof CompoundTag) {
                tag.a((String) entry.getKey(), ((CompoundTag_v1_20_R1) entry.getValue()).toNMSTag(meta));
                continue;
            }
            if (entry.getValue() instanceof DoubleTag) {
                tag.a((String) entry.getKey(), ((DoubleTag) entry.getValue()).getValue().doubleValue());
                continue;
            }
            if (entry.getValue() instanceof FloatTag) {
                tag.a((String) entry.getKey(), ((FloatTag) entry.getValue()).getValue().floatValue());
                continue;
            }
            if (entry.getValue() instanceof IntArrayTag) {
                tag.a((String) entry.getKey(), ((IntArrayTag) entry.getValue()).getValue());
                continue;
            }
            if (entry.getValue() instanceof ListTag) {
                NBTTagList list = new NBTTagList();
                List<Tag> tags = ((ListTag) entry.getValue()).getValue();
                for (Tag btag : tags) {
                    HashMap<String, Tag> btags = new HashMap<String, Tag>();
                    btags.put("test", btag);
                    CompoundTag_v1_20_R1 comp = new CompoundTag_v1_20_R1(btags);
                    list.add(comp.toNMSTag(meta).c("test"));
                }
                tag.a((String) entry.getKey(), list);
                continue;
            }
            if (entry.getValue() instanceof LongTag) {
                tag.a((String) entry.getKey(), ((LongTag) entry.getValue()).getValue().longValue());
                continue;
            }
            if (entry.getValue() instanceof ShortTag) {
                tag.a((String) entry.getKey(), ((ShortTag) entry.getValue()).getValue().shortValue());
                continue;
            }
            if (entry.getValue() instanceof StringTag) {
                tag.a((String) entry.getKey(), ((StringTag) entry.getValue()).getValue());
                continue;
            }
            if (entry.getValue() instanceof PlaceholderDoubleTag) {
                tag.a((String) entry.getKey(), ((PlaceholderDoubleTag) entry.getValue()).getValue().get(meta));
                continue;
            }
            if (entry.getValue() instanceof PlaceholderFloatTag) {
                tag.a((String) entry.getKey(), ((PlaceholderFloatTag) entry.getValue()).getValue().get(meta));
                continue;
            }
            if (entry.getValue() instanceof PlaceholderIntTag) {
                tag.a((String) entry.getKey(), ((PlaceholderIntTag) entry.getValue()).getValue().get(meta));
                continue;
            }
            if (!(entry.getValue() instanceof PlaceholderStringTag)) continue;
            tag.a((String) entry.getKey(), ((PlaceholderStringTag) entry.getValue()).getValue().get(meta));
        }
        return tag;
    }
}
