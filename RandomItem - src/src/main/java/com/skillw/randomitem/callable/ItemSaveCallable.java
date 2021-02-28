package com.skillw.randomitem.callable;

import com.skillw.randomitem.Main;
import io.izzel.taboolib.module.nms.NMS;
import io.izzel.taboolib.module.nms.nbt.NBTAttribute;
import io.izzel.taboolib.module.nms.nbt.NBTCompound;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.util.NBTUtils.addCompoundToConfigurationSection;
import static com.skillw.randomitem.util.StringUtils.messageToOriginalText;

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

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public String getItemKey() {
        return this.itemKey;
    }

    @Override
    public Boolean call() {
        try {
            ItemStack itemStack = this.getItemStack();
            if (itemStack.hasItemMeta()) {
                String id = this.getItemKey();
                sendDebug("&aSaving item: &6" + id);
                long startTime = System.currentTimeMillis();
                ItemMeta itemMeta = itemStack.getItemMeta();

                File file = new File(Main.getInstance().getPlugin().getDataFolder() + "/Items/" + this.path);
                if (!file.exists() && !file.createNewFile()) {
                    return false;
                }
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.createSection(id);
                ConfigurationSection itemKeySection = config.getConfigurationSection(id);
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


                String name = (itemMeta.hasDisplayName()) ? messageToOriginalText(itemMeta.getDisplayName()) : itemStack.getType().name();
                sendDebug("&d- &aDisplay: &6" + name);
                itemKeySection.set("display", name);

                Material material = itemStack.getType();
                sendDebug("&d- &aMaterial: &6" + material);
                itemKeySection.set("material", material.toString());

                short data = 0;
                if (Main.gameVersion > 1141) {
                    if (itemMeta.hasCustomModelData()) {
                        data = (short) itemMeta.getCustomModelData();
                    }
                } else {
                    data = itemStack.getData().getData();
                }
                sendDebug("&d- &aData: &6" + data);
                itemKeySection.set("data", data);

                List<String> lores = new ArrayList<>();
                if (itemMeta.hasLore()) {
                    sendDebug("&d- &aLores: ");
                    for (String lore : itemMeta.getLore()) {
                        sendDebug("&d  - &f" + lore);
                        lores.add(messageToOriginalText(lore));
                    }
                }
                itemKeySection.set("lores", lores);

                if (itemMeta instanceof LeatherArmorMeta) {
                    Color color = ((LeatherArmorMeta) itemMeta).getColor();
                    String rgb = color.getRed() + "," + color.getGreen() + "," + color.getBlue();
                    sendDebug("&d- &aColor: &6" + rgb);
                    itemKeySection.set("color", rgb);
                }

                itemKeySection.set("unbreakable", itemMeta.isUnbreakable());

                Set<ItemFlag> itemFlagSet = itemMeta.getItemFlags();
                List<String> flags = new ArrayList<>();
                if (!itemMeta.getItemFlags().isEmpty()) {
                    sendDebug("&d- &aItemFlags: ");
                    itemKeySection.createSection("item-flags");
                    for (ItemFlag itemFlag : itemFlagSet) {
                        String flag = itemFlag.name();
                        sendDebug("&d  - &b" + flag);
                        flags.add(flag);
                    }
                    itemKeySection.set("item-flags", flags);
                }

                if (itemMeta.hasEnchants()) {
                    sendDebug("&d- &aEnchantments: ");
                    ConfigurationSection enchantSection = itemKeySection.getConfigurationSection("enchantments");
                    for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
                        if (enchantment != null) {
                            Integer level = itemMeta.getEnchantLevel(enchantment);
                            sendDebug("&d  - &b" + enchantment.getName() + " &5= &e" + level);
                            enchantSection.set(enchantment.getName(), level);
                        }
                    }
                }

                NBTCompound compound = NMS.handle().loadNBT(itemStack);
                if (compound != null && !compound.isEmpty()) {
                    ConfigurationSection nbtSection = itemKeySection.getConfigurationSection("nbt-keys");
                    sendDebug("&d- &aNBT-keys: &6");
                    addCompoundToConfigurationSection(nbtSection, compound, null);
                }

                List<NBTAttribute> attributes = NMS.handle().getAttribute(itemStack);
                if (!attributes.isEmpty()) {
                    sendDebug("&d- &aAttribute: ");
                    ConfigurationSection section = itemKeySection.getConfigurationSection("attributes");
                    for (NBTAttribute attribute : attributes) {
                        ConfigurationSection attAttSection = section.createSection(attribute.getDescription());
                        addCompoundToConfigurationSection(attAttSection, attribute.toNBT(), null);
                    }
                }

                config.save(file);

                long finishTime = System.currentTimeMillis();
                sendDebug("&2Done! &9Total time: &6" + (finishTime - startTime) + "&9ms");

                Main.getRandomItemAPI().createRandomItem(itemKeySection).register();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
