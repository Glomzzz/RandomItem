package com.skillw.randomitem.callable;

import com.skillw.randomitem.Main;
import io.izzel.taboolib.module.nms.NMS;
import io.izzel.taboolib.module.nms.nbt.NBTBase;
import io.izzel.taboolib.module.nms.nbt.NBTCompound;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.skillw.randomitem.utils.RandomItemUtils.messageToOriginalText;

/**
 * @ClassName : com.skillw.randomitem.callable.ItemSaveRunnable
 * Created by Glom_ on 2021-02-05 11:15:31
 * Copyright  2020 user. All rights reserved.
 */
public class ItemSaveCallable implements Callable {
    private final ItemStack itemStack;
    private final String itemKey;

    public ItemSaveCallable(ItemStack itemStack, String itemKey) {
        this.itemStack = itemStack;
        this.itemKey = itemKey;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public String getItemKey() {
        return this.itemKey;
    }

    @Override
    public Object call() throws Exception {
        try {
            if (this.itemStack.hasItemMeta()) {
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

                File file = new File(Main.getInstance().getPlugin().getDataFolder() + "/Items", this.itemKey + ".yml");
                if (!file.createNewFile()) {
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
                itemKeySection.createSection("enchants");
                itemKeySection.createSection("randoms");
                itemKeySection.createSection("computes");
                itemKeySection.set("material", material.toString());
                itemKeySection.set("data", data);
                itemKeySection.set("display", name);
                itemKeySection.set("lores", lores);
                NBTCompound compound = NMS.handle().loadNBT(this.itemStack);
                ConfigurationSection nbtSection = itemKeySection.getConfigurationSection("nbt-keys");
                for (Object object : compound.keySet()) {
                    if (object instanceof String) {
                        if ((!"Damage".equals(object) && !"display".equals(object)) && !"Enchantments".equals(object) && compound.get(object) != null) {
                            NBTBase base = compound.get(object);
                            switch (base.getType()) {
                                case INT:
                                    nbtSection.set((String) object, base.asInt());
                                    break;
                                case BYTE:
                                    nbtSection.set((String) object, base.asByte());
                                    break;
                                case LONG:
                                    nbtSection.set((String) object, base.asLong());
                                    break;
                                case LIST:
                                    nbtSection.set((String) object, base.asList());
                                case FLOAT:
                                    nbtSection.set((String) object, base.asFloat());
                                    break;
                                case COMPOUND:
                                    nbtSection.set((String) object, base.asCompound());
                                    break;
                                case SHORT:
                                    nbtSection.set((String) object, base.asShort());
                                    break;
                                case DOUBLE:
                                    nbtSection.set((String) object, base.asDouble());
                                    break;
                                case STRING:
                                    nbtSection.set((String) object, base.asString());
                                    break;
                                case INT_ARRAY:
                                    nbtSection.set((String) object, base.asIntArray());
                                    break;
                                case BYTE_ARRAY:
                                    nbtSection.set((String) object, base.asByteArray());
                                    break;
                                default:
                                    break;
                            }

                        }
                    }
                }
                if (itemMeta.hasEnchants()) {
                    for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
                        if (enchantment != null) {
                            Integer level = itemMeta.getEnchantLevel(enchantment);
                            itemKeySection.getConfigurationSection("enchants").set(enchantment.getKey().getKey(), level);
                        }
                    }
                }
                config.save(file);
                Main.getInstance().loadConfig();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
