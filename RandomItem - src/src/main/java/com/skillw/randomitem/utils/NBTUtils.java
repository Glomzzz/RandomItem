package com.skillw.randomitem.utils;

import com.skillw.randomitem.api.section.ComplexData;
import io.izzel.taboolib.TabooLib;
import io.izzel.taboolib.module.nms.nbt.NBTBase;
import io.izzel.taboolib.module.nms.nbt.NBTCompound;
import io.izzel.taboolib.module.nms.nbt.NBTList;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.utils.Utils.replaceAll;

/**
 * @ClassName : com.skillw.randomitem.utils.NBTUtils
 * Created by Glom_ on 2021-02-15 17:42:04
 * Copyright  2020 user. All rights reserved.
 */
public class NBTUtils {
    protected static Pattern PATTERN = Pattern.compile("\\d+s");

    //From https://github.com/TabooLib/TabooLib/
    public static NBTCompound translateSection(NBTCompound nbt,
                                               ConfigurationSection section,
                                               ComplexData data) {
        for (String key : section.getKeys(false)) {
            Object obj = section.get(key);
            NBTBase base;
            if (obj instanceof ConfigurationSection) {
                base = translateSection(new NBTCompound(), section.getConfigurationSection(key), data);
            } else if ((base = toNBT(obj, data)) == null) {
                TabooLib.getLogger().warn("Invalid Type: " + obj + " [" + obj.getClass().getSimpleName() + "]");
                continue;
            }
            sendDebug("&d  -> &b" + key + " &5= &e" + base.toString());
            nbt.put(key, base);
        }
        return nbt;
    }

    //From https://github.com/TabooLib/TabooLib/
    private static NBTList translateList(NBTList nbtListBase,
                                         List<?> list,
                                         ComplexData data) {
        for (Object object : list) {
            NBTBase base = toNBT(object, data);
            if (base == null) {
                TabooLib.getLogger().warn("Invalid Type: " + object + " [" + object.getClass().getSimpleName() + "]");
            } else {
                nbtListBase.add(base);
            }
        }
        return nbtListBase;
    }

    //From https://github.com/TabooLib/TabooLib/
    private static NBTBase toNBT(Object object, ComplexData data) {
        String string = String.valueOf(object);
        if (object instanceof NBTBase) {
            return (NBTBase) object;
        } else if (object instanceof String
                && !string.contains("double:")
                && !string.contains("integer:")
                && !string.contains("float:")
                && !string.contains("short:")
                && !string.contains("long:")
                && !string.contains("byte:")
                && !string.contains("compound:")) {
            return PATTERN.matcher(object.toString()).matches() ? toNBT(Short.valueOf(object.toString().substring(0, object.toString().length() - 1)), data) : new NBTBase(replaceAll((String) object, data));
        } else if (object instanceof Integer || string.contains("integer:")) {
            if (string.contains(":")) {
                string = string.split(":")[1];
                //这里先用parseDouble取处double值 再强转为int的原因是防止parseInteger读小数点报错
                return new NBTBase((int) (Double.parseDouble(replaceAll(string, data))));
            } else {
                return new NBTBase((int) object);
            }
        } else if (object instanceof Double || string.contains("double:")) {
            if (string.contains(":")) {
                string = string.split(":")[1];
                return new NBTBase(Double.parseDouble(replaceAll(string, data)));
            } else {
                return new NBTBase((double) object);
            }
        } else if (object instanceof Float || string.contains("float:")) {
            if (string.contains(":")) {
                string = string.split(":")[1];
                return new NBTBase(Float.parseFloat(replaceAll(string, data)));
            } else {
                return new NBTBase((float) object);
            }
        } else if (object instanceof Short || string.contains("short:")) {
            if (string.contains(":")) {
                string = string.split(":")[1];
                //这里先用parseDouble取处double值 再强转为short的原因是防止parseShort读小数点报错
                return new NBTBase((short) Double.parseDouble(replaceAll(string, data)));
            } else {
                return new NBTBase((short) object);
            }
        } else if (object instanceof Long || string.contains("long:")) {
            if (string.contains(":")) {
                string = string.split(":")[1];
                //这里先用parseDouble取处double值 再强转为long的原因是防止parseLong读小数点报错
                return new NBTBase((long) Double.parseDouble(replaceAll(string, data)));
            } else {
                return new NBTBase((long) object);
            }
        } else if (object instanceof Byte || string.contains("byte:")) {
            if (string.contains(":")) {
                string = string.split(":")[1];
                //这里先用parseDouble取处double值 再强转为byte的原因是防止parseByte读小数点报错
                return new NBTBase((byte) Double.parseDouble(replaceAll(string, data)));
            } else {
                return new NBTBase((byte) object);
            }
        } else if (object instanceof byte[]) {
            return new NBTBase((byte[]) object);
        } else if (object instanceof int[]) {
            return new NBTBase((int[]) object);
        } else if (object instanceof List) {
            return translateList(new NBTList(), (List<?>) object, data);
        } else {
            NBTCompound nbtCompound;
            if (object instanceof Map) {
                nbtCompound = new NBTCompound();
                ((Map<?, ?>) object).forEach((key, value) -> nbtCompound.put(key.toString(), toNBT(value, data)));
                return nbtCompound;
            } else if (object instanceof ConfigurationSection) {
                nbtCompound = new NBTCompound();
                ((ConfigurationSection) object).getValues(false).forEach((key, value) -> nbtCompound.put(key, toNBT(value, data)));
                return nbtCompound;
            } else {
                return new NBTBase("error: " + object);
            }
        }
    }
}
