package com.skillw.randomitem.util;

import com.skillw.randomitem.api.section.ComplexData;
import io.izzel.taboolib.TabooLib;
import io.izzel.taboolib.module.nms.nbt.NBTBase;
import io.izzel.taboolib.module.nms.nbt.NBTCompound;
import io.izzel.taboolib.module.nms.nbt.NBTList;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.skillw.randomitem.util.DebugUtils.sendDebug;
import static com.skillw.randomitem.util.ProcessUtils.replaceAll;
import static com.skillw.randomitem.util.StringUtils.messageToText;

/**
 * @ClassName : com.skillw.randomitem.utils.NBTUtils
 * Created by Glom_ on 2021-02-15 17:42:04
 * Copyright  2020 user. All rights reserved.
 */
//From https://github.com/TabooLib/TabooLib/
public final class NBTUtils {
    protected static Pattern PATTERN = Pattern.compile("\\d+s");

    private NBTUtils() {
    }

    //From https://github.com/TabooLib/TabooLib/
    public static NBTCompound translateSection(NBTCompound nbt,
                                               ConfigurationSection section,
                                               ComplexData data,
                                               String superKey,
                                               boolean isDebug,
                                               Set<String> debugs) {
        superKey = superKey != null ? superKey : "";
        boolean notNBT = !section.getName().equalsIgnoreCase("nbt-keys");
        if (notNBT) {
            sendDebug(superKey + "&d  -> &b" + section.getName() + " &5: &e", isDebug);
            superKey = superKey + "  ";
        }
        for (String key : section.getKeys(false)) {
            Object obj = section.get(key);
            NBTBase base;
            if (obj instanceof ConfigurationSection) {
                base = translateSection(new NBTCompound(), section.getConfigurationSection(key), data, superKey + "  ", isDebug, debugs);
            } else if ((base = toNBT(obj, data)) == null) {
                TabooLib.getLogger().warn("Invalid Type: " + obj + " [" + obj.getClass().getSimpleName() + "]");
                continue;
            }
            String debug = superKey + "&d  -" + (!notNBT ? ">" : "") + " &b" + key + " &5= &e" + base.toString();
            if (debugs == null) {
                sendDebug(debug, isDebug);
            } else {
                debugs.add(debug);
            }
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

    public static void addCompoundToConfigurationSection(ConfigurationSection nbtSection, NBTCompound compound, String superKey, boolean isDebug) {
        superKey = superKey != null ? superKey : "";
        sendDebug(superKey + "&d  -> &b" + nbtSection.getName() + " &5: ", isDebug);
        for (Object object : compound.keySet()) {
            if (object instanceof String) {
                if (("Damage".equals(object) ||
                        "display".equals(object) ||
                        "Enchantments".equals(object) ||
                        "ench".equals(object) ||
                        "Unbreakable".equals(object) ||
                        "HideFlags".equals(object) ||
                        "AttributeModifiers".equals(object))) {
                    continue;
                }
                if (compound.get(object) != null) {
                    addBaseToConfigurationSection(nbtSection, String.valueOf(object), compound.get(object), superKey + "  ", isDebug);
                }
            }
        }
    }

    public static void addBaseToConfigurationSection(ConfigurationSection nbtSection, String key, NBTBase base, String superKey, boolean isDebug) {
        superKey = superKey != null ? superKey : "";
        if (base == null) {
            return;
        }
        if (base instanceof List) {
            sendDebug(superKey + "&d  -> &b" + key + " &5: &e" + messageToText(base.toString()), isDebug);
            ConfigurationSection listSection = nbtSection.createSection(key);
            int i = 0;
            for (NBTBase nbtBase : base.asList()) {
                addBaseToConfigurationSection(listSection, String.valueOf(i++), nbtBase, superKey + "  ", isDebug);
            }
        } else {
            sendDebug(superKey + "&d  - &b" + key + " &5: &e" + messageToText(base.toString()), isDebug);
            switch (base.getType()) {
                case INT:
                    nbtSection.set(key, base.asInt());
                    break;
                case BYTE:
                    nbtSection.set(key, base.asByte());
                    break;
                case LONG:
                    nbtSection.set(key, base.asLong());
                    break;
                case FLOAT:
                    nbtSection.set(key, base.asFloat());
                    break;
                case COMPOUND:
                    NBTCompound nbtCompound = base.asCompound();
                    ConfigurationSection compoundSection = nbtSection.createSection(key);
                    addCompoundToConfigurationSection(compoundSection, nbtCompound, superKey + "  ", isDebug);
                    break;
                case SHORT:
                    nbtSection.set(key, base.asShort());
                    break;
                case DOUBLE:
                    nbtSection.set(key, base.asDouble());
                    break;
                case STRING:
                    nbtSection.set(key, base.asString());
                    break;
                case INT_ARRAY:
                    nbtSection.set(key, base.asIntArray());
                    break;
                case BYTE_ARRAY:
                    nbtSection.set(key, base.asByteArray());
                    break;
                default:
                    break;
            }
        }
    }


}
