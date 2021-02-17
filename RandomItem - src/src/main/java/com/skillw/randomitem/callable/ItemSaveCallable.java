package com.skillw.randomitem.callable;

import com.skillw.randomitem.Main;
import io.izzel.taboolib.module.nms.NMS;
import io.izzel.taboolib.module.nms.nbt.NBTAttribute;
import io.izzel.taboolib.module.nms.nbt.NBTBase;
import io.izzel.taboolib.module.nms.nbt.NBTCompound;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.skillw.randomitem.Main.isDebug;
import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.utils.StringUtils.messageToOriginalText;

/**
 * @ClassName : com.skillw.randomitem.callable.ItemSaveCallable
 * Created by Glom_ on 2021-02-05 11:15:31
 * Copyright  2020 user. All rights reserved.
 */
public final class ItemSaveCallable implements Callable<Boolean> {
    private final ItemStack itemStack;
    private final String itemKey;
    private final String path;

    public ItemSaveCallable(ItemStack itemStack, String itemKey, String path) {
        this.itemStack = itemStack;
        this.itemKey = itemKey;
        this.path = path;
    }

    private static void addCompound(ConfigurationSection nbtSection, NBTCompound compound) {
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
                    addBase(nbtSection, String.valueOf(object), compound.get(object));
                }
            }
        }
    }

    private static void addBase(ConfigurationSection nbtSection, String key, NBTBase base) {
        if (base instanceof List) {
            ConfigurationSection listSection = nbtSection.createSection(key);
            int i = 0;
            for (NBTBase nbtBase : base.asList()) {
                addBase(listSection, String.valueOf(i++), nbtBase);
            }
        } else {
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
                    addCompound(compoundSection, nbtCompound);
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

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public String getItemKey() {
        return this.itemKey;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            if (this.itemStack.hasItemMeta()) {
                sendDebug("&aSaving item: &6" + this.itemKey);
                long startTime = System.currentTimeMillis();
                ItemMeta itemMeta = this.itemStack.getItemMeta();

                Material material = this.itemStack.getType();

                String name = (itemMeta.hasDisplayName()) ? messageToOriginalText(itemMeta.getDisplayName()) : material.name();

                List<String> lores = new ArrayList<>();
                if (itemMeta.hasLore()) {
                    for (String lore : itemMeta.getLore()) {
                        lores.add(messageToOriginalText(lore));
                    }
                }

                short data = 0;
                if (Main.version > 1141) {
                    if (itemMeta.hasCustomModelData()) {
                        data = (short) itemMeta.getCustomModelData();
                    }
                } else {
                    data = this.itemStack.getData().getData();
                }
                sendDebug("&d- &adisplay: &6" + name);

                sendDebug("&d- &amaterial: &6" + material);

                sendDebug("&d- &adata: &6" + data);

                sendDebug("&d- &alores: ");
                if (isDebug() && itemMeta.hasLore()) {
                    for (String lore : itemMeta.getLore()) {
                        sendDebug("&d  - &f" + lore);
                    }
                }
                File file = new File(Main.getInstance().getPlugin().getDataFolder() + "/Items/" + this.path);
                if (!file.exists() && !file.createNewFile()) {
                    return false;
                }
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.createSection(this.itemKey);
                ConfigurationSection itemKeySection = config.getConfigurationSection(this.itemKey);
                itemKeySection.createSection("material");
                itemKeySection.createSection("data");
                itemKeySection.createSection("display");
                itemKeySection.createSection("lores");
                itemKeySection.createSection("nbt-keys");
                itemKeySection.createSection("enchantments");
                itemKeySection.createSection("unbreakable");
                itemKeySection.createSection("randoms");
                itemKeySection.createSection("computes");
                itemKeySection.createSection("attributes");
                itemKeySection.set("material", material.toString());
                itemKeySection.set("data", data);
                itemKeySection.set("display", name);
                itemKeySection.set("lores", lores);
                itemKeySection.set("unbreakable", itemMeta.isUnbreakable());
                NBTCompound compound = NMS.handle().loadNBT(this.itemStack);
                ConfigurationSection nbtSection = itemKeySection.getConfigurationSection("nbt-keys");
                addCompound(nbtSection, compound);
                if (itemMeta.hasEnchants()) {
                    sendDebug("&d- &aEnchantments: ");
                    for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
                        if (enchantment != null) {
                            Integer level = itemMeta.getEnchantLevel(enchantment);
                            sendDebug("&d  - &b" + enchantment.getName() + " &5= &e" + level);
                            itemKeySection.getConfigurationSection("enchantments").set(enchantment.getName(), level);
                        }
                    }
                }
                List<NBTAttribute> attributes = NMS.handle().getAttribute(this.itemStack);
                if (!attributes.isEmpty()) {
                    ConfigurationSection section = itemKeySection.getConfigurationSection("attributes");
                    for (NBTAttribute attribute : attributes) {
                        ConfigurationSection attAttSection = section.createSection(attribute.getDescription());
                        addCompound(attAttSection, attribute.toNBT());
                    }
                }
                Set<ItemFlag> itemFlagSet = itemMeta.getItemFlags();
                List<String> flags = new ArrayList<>();
                if (!itemMeta.getItemFlags().isEmpty()) {
                    itemKeySection.createSection("item-flags");
                    for (ItemFlag itemFlag : itemFlagSet) {
                        flags.add(itemFlag.name());
                    }
                    itemKeySection.set("item-flags", flags);
                }
                config.save(file);
                long finishTime = System.currentTimeMillis();
                sendDebug("&2Done! &9Total time: &6" + (finishTime - startTime) + "&9ms");
                Main.getInstance().loadConfig();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
