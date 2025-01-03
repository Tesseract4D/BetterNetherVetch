package cn.tesseract.bnv.world.generator.map;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.function.Function;

public class MapChunk<T> {
    private final Reference2ByteMap<T> objToID = new Reference2ByteOpenHashMap<>();
    private final Byte2ObjectMap<T> idToObj = new Byte2ObjectOpenHashMap<>();
    private final byte[] data = new byte[4096];
    private byte id;

    public void set(int index, T value) {
        byte id = objToID.computeIfAbsent(value, k -> {
            byte newID = this.id++;
            idToObj.put(newID, value);
            return newID;
        });
        data[index] = id;
    }

    public T get(int index) {
        return idToObj.get(data[index]);
    }

    public void save(NBTTagCompound tag, Function<T, String> serializer) {
        NBTTagList list = new NBTTagList();
        tag.setTag("palette", list);
        objToID.forEach((value, id) -> {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setByte("id", id);
            entry.setString("value", serializer.apply(value));
            list.appendTag(entry);
        });
        tag.setByteArray("data", data);
    }

    public boolean load(NBTTagCompound tag, Function<String, T> deserializer) {
        if (!tag.hasKey("palette") || !tag.hasKey("data")) {
            return false;
        }
        byte[] preData = tag.getByteArray("data");
        if (preData.length != 4096) return false;
        System.arraycopy(preData, 0, data, 0, 4096);
        NBTTagList list = tag.getTagList("palette", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            byte id = entry.getByte("id");
            String name = entry.getString("value");
            T value = deserializer.apply(name);
            objToID.put(value, id);
            idToObj.put(id, value);
        }
        return true;
    }
}
