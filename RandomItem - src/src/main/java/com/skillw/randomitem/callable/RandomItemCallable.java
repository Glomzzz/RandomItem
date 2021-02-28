package com.skillw.randomitem.callable;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.event.RandomItemFinishGeneratingEvent;
import com.skillw.randomitem.api.event.RandomItemStartGeneratingEvent;
import com.skillw.randomitem.api.randomitem.ItemData;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.bean.ComplexDataImpl;
import io.izzel.taboolib.module.nms.NMS;
import io.izzel.taboolib.module.nms.nbt.NBTCompound;
import io.izzel.taboolib.module.nms.nbt.NBTList;
import io.izzel.taboolib.util.item.ItemBuilder;
import io.izzel.taboolib.util.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.*;
import static com.skillw.randomitem.util.CalculationUtils.getResult;
import static com.skillw.randomitem.util.DebugUtils.*;
import static com.skillw.randomitem.util.NBTUtils.translateSection;
import static com.skillw.randomitem.util.ProcessUtils.handlePointData;
import static com.skillw.randomitem.util.ProcessUtils.replaceAll;
import static com.skillw.randomitem.util.StringUtils.addStrings;
import static com.skillw.randomitem.util.Utils.checkNull;

/**
 * @ClassName : com.skillw.randomitem.callable.RandomItemCallable
 * Created by Glom_ on 2021-02-04 19:37:20
 * Copyright  2020 user. All rights reserved.
 */
public final class RandomItemCallable implements Callable<ItemStack> {
    private final Player player;
    private final ItemData itemData;
    private final String pointData;

    public RandomItemCallable(Player player,
                              ItemData itemData,
                              String pointData) {
        this.player = player;
        this.itemData = itemData;
        this.pointData = pointData;
    }

    @Override
    public ItemStack call() {
        sendDebug("&aGenerating item: &6" + this.getItemData().getId());
        long startTime = System.currentTimeMillis();
        ConcurrentHashMap<String, BaseSection> sectionMap = this.getItemData().getSectionMapClone();
        ConcurrentHashMap<String, String> enchantmentMap = this.getItemData().getEnchantMapClone();
        ConcurrentHashMap<String, String> alreadySectionMap = new ConcurrentHashMap<>();
        ComplexData complexData = new ComplexDataImpl(sectionMap, alreadySectionMap, this.player);
        ItemBuilder builder = new ItemBuilder(Material.IRON_AXE, 1);

        String id = this.getItemData().getId();
        if (this.pointData != null && !this.pointData.isEmpty()) {
            sendDebug("&d- &aPoint Data: &6" + this.pointData);
            handlePointData(this.pointData, complexData);
        }
        RandomItemStartGeneratingEvent startEvent = new RandomItemStartGeneratingEvent(id, complexData, enchantmentMap);
        Bukkit.getPluginManager().callEvent(startEvent);
        enchantmentMap = startEvent.getEnchantmentMap();
        complexData = startEvent.getData();

        // 这玩意就是为了让用户知道 指向数据 和 其它插件 对 alreadySectionMap 的影响
        if (isDebug() && !alreadySectionMap.isEmpty()) {
            sendDebug("&d- &aAlready Random Sections: &6");
            debugStringsMap(alreadySectionMap);
        }

        String display = this.getItemData().getDisplay();
        sendDebug("&d- &aOriginal Display: &6" + display);
        display = replaceAll(display, complexData);
        sendDebug("&d- &aFinal Display: &6" + display);
        builder.name(display);

        String materialString = this.getItemData().getMaterial();
        sendDebug("&d- &aOriginal Material: &6" + materialString);
        materialString = (replaceAll(materialString, complexData));
        Material matchMaterial = Material.matchMaterial(materialString);
        Material material = matchMaterial == null ? Material.STONE : matchMaterial;
        sendDebug("&d- &aFinal Material: &6" + materialString + "&5 - > &6" + material);
        builder.material(material);

        String data = this.getItemData().getData();
        sendDebug("&d- &aOriginal Data: &6" + data);
        data = replaceAll(data, complexData);
        if (!"0".equals(data)) {
            int dataInteger = 0;
            try {
                dataInteger = Integer.parseInt(data);
            } catch (Exception e) {
                sendWrong("RandomItem's data must be a integer!");
            }
            sendDebug("&d- &aFinal Data: &6" + data);
            if (Main.gameVersion >= 1141) {
                builder.customModelData(dataInteger);
            } else {
                builder.damage(dataInteger);
            }
        }

        List<String> newLores = new ArrayList<>();
        List<String> loresClone = this.getItemData().getLoresClone();
        if (!this.getItemData().getLoresClone().isEmpty()) {
            sendDebug("&d- &aOriginal lores: ");
            for (String lore : loresClone) {
                sendDebug("&d  - &f" + lore);
                lore = replaceAll(lore, complexData);
                addStrings(newLores, lore);
            }
        }
        if (isDebug() && !newLores.isEmpty()) {
            sendDebug("&d- &aFinal Lores: ");
            for (String lore : newLores) {
                sendDebug("&d  - &f" + lore);
            }
        }
        builder.lore(newLores);

        if (this.itemData.getItemSection().contains("color")) {
            String rgbData = replaceAll(this.itemData.getItemSection().getString("color"), complexData);
            sendDebug("&d- &aOriginal RBG Color: &6" + rgbData);
            String[] rgbs = rgbData.split(",");
            int red = 255;
            int green = 255;
            int blue = 255;
            try {
                red = Integer.parseInt(rgbs[0]);
                green = Integer.parseInt(rgbs[1]);
                blue = Integer.parseInt(rgbs[2]);
            } catch (Exception e) {
                sendWrong("R G B in RandomItem's color must be integer!");
            }
            Color color = Color.fromRGB(red, green, blue);
            sendDebug("&d- &aFinal RBG Color: &6" + red + "," + green + "," + blue + "&5 - > &6" + color.asRGB());
            if (color != null) {
                builder.color(color);
            }
        }

        boolean unbreakable = false;
        String unbreakableFormula = this.getItemData().getUnbreakableFormula();
        if (unbreakableFormula != null) {
            sendDebug("&d- &aOriginal unbreakable: " + unbreakableFormula);
            unbreakableFormula = replaceAll(unbreakableFormula, complexData);
            if ("true".equals(unbreakableFormula) || "false".equals(unbreakableFormula)) {
                unbreakable = Boolean.parseBoolean(unbreakableFormula);
            } else {
                unbreakable = getResult(unbreakableFormula) != 0;
            }
        }
        sendDebug("&d- &aFinal unbreakable: &6" + unbreakableFormula + "&5 - > &6" + unbreakable);
        builder.unbreakable(unbreakable);

        List<String> itemFlags = this.getItemData().getItemFlagsClone();
        if (!itemFlags.isEmpty()) {
            sendDebug("&d- &aOriginal ItemFlags: ");
            for (int i = 0; i < itemFlags.size(); i++) {
                String itemFlag = itemFlags.get(i);
                sendDebug("&d  - &f" + itemFlag);
                itemFlag = replaceAll(itemFlag, complexData);
                itemFlags.set(i, itemFlag);
            }
            sendDebug("&d- &aFinal ItemFlags: ");
            for (String itemFlag : itemFlags) {
                sendDebug("&d  - &f" + itemFlag);
                ItemFlag flag = ItemFlag.valueOf(itemFlag);
                if (!checkNull(flag, "ItemFlag can't be null! in &6" + id + ".item-flags &c: &b" + itemFlag)) {
                    builder.flags(flag);
                }
            }
        }

        ConfigurationSection nbtSection = this.itemData.getItemSection().getConfigurationSection("nbt-keys");
        //储存原物品的NBT
        NBTCompound nbtCompound = NMS.handle().loadNBT(builder.build());
        if (nbtSection != null && !nbtSection.getKeys(false).isEmpty()) {
            sendDebug("&d- &aOriginal NBT-keys: ");
            debugConfigurationSection(nbtSection, null);
            sendDebug("&d- &aFinal NBT-keys: ");
            nbtCompound = translateSection(nbtCompound, nbtSection, complexData, null);
        }

        ConfigurationSection attributeSection = this.getItemData().getItemSection().getConfigurationSection("attributes");
        if (attributeSection != null && !attributeSection.getKeys(false).isEmpty()) {
            NBTList nbtList = new NBTList();
            sendDebug("&d- &aOriginal Attributes: ");
            for (String att : attributeSection.getKeys(false)) {
                ConfigurationSection attSection = attributeSection.getConfigurationSection(att);
                debugConfigurationSection(attSection, null);
            }
            for (String att : attributeSection.getKeys(false)) {
                ConfigurationSection attSection = attributeSection.getConfigurationSection(att);
                NBTCompound compound = new NBTCompound();
                sendDebug("&d- &aFinal Attributes: ");
                compound = translateSection(compound, attSection, complexData, null);
                nbtList.add(compound);
            }
            nbtCompound.put("AttributeModifiers", nbtList);
        }

        if (enchantmentMap != null && !enchantmentMap.isEmpty()) {
            sendDebug("&d- &aOriginal enchantments: ");
            for (String enchant : enchantmentMap.keySet()) {
                sendDebug("&d  -> &b" + enchant + " &5= &e" + enchantmentMap.get(enchant));
                String value = replaceAll(enchantmentMap.get(enchant), complexData);
                enchantmentMap.put(enchant, value);
            }
            sendDebug("&d- &aFinal enchantments: ");
            for (String enchant : enchantmentMap.keySet()) {
                String value = enchantmentMap.get(enchant);
                sendDebug("&d  -> &b" + enchant + " &5= &e" + value);
                int level = (int) Math.round(getResult(value));
                if (level > 0) {
                    Enchantment enchantment = Items.asEnchantment(enchant);
                    if (!checkNull(enchantment, "Enchantment can't be null! in &6" + id + ".enchantments &c: &b" + enchant)) {
                        builder.enchant(enchantment, level, true);
                    }
                }
            }
        }
        if (isDebug() && !sectionMap.isEmpty()) {
            sendDebug("&d- &aOriginal Random Sections: ");
            sectionMap.keySet().stream().sorted().forEach(key -> {
                debugSection(sectionMap.get(key));
            });
        }
        if (isDebug() && !complexData.getDebugMessages().isEmpty()) {
            sendDebug("&aReplacing Random Sections:");
            complexData.sendDebugMessages();
        }


        if (isDebug() && !alreadySectionMap.isEmpty()) {
            sendDebug("&d- &aFinal Random Sections: &6");
            debugStringsMap(alreadySectionMap);
        }

        ItemStack itemStack = builder.build();
        if (nbtCompound != null && !nbtCompound.isEmpty()) {
            nbtCompound.saveTo(itemStack);
        }

        RandomItemFinishGeneratingEvent finishEvent = new RandomItemFinishGeneratingEvent(id, this.player, itemStack);
        Bukkit.getPluginManager().callEvent(finishEvent);
        itemStack = finishEvent.getItemStack();

        long finishTime = System.currentTimeMillis();
        sendDebug("&2Done! &9Total time: &6" + (finishTime - startTime) + "&9ms");
        return itemStack;
    }

    public String getPointData() {
        return this.pointData;
    }

    public ItemData getItemData() {
        return this.itemData;
    }
}
