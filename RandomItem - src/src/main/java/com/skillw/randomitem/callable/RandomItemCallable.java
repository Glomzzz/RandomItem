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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.isDebug;
import static com.skillw.randomitem.Main.sendWrong;
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
    private final boolean isDebug;

    public RandomItemCallable(Player player,
                              ItemData itemData,
                              String pointData,
                              boolean isDebug) {
        this.player = player;
        this.itemData = itemData;
        this.pointData = pointData;
        this.isDebug = isDebug;
    }

    private void add(Set<String> strings, String value) {
        if (strings != null && this.isDebug) {
            strings.add(value);
        }
    }

    @Override
    public ItemStack call() {
        long startTime = System.currentTimeMillis();
        ConcurrentHashMap<String, BaseSection> sectionMap = this.getItemData().getSectionMapClone();
        ConcurrentHashMap<String, String> enchantmentMap = this.getItemData().getEnchantMapClone();
        ConcurrentHashMap<String, String> alreadySectionMap = new ConcurrentHashMap<>();
        ComplexData complexData = new ComplexDataImpl(sectionMap, alreadySectionMap, this.player);
        Set<String> debugMessages = this.isDebug ? new LinkedHashSet<>() : null;
        this.add(debugMessages, "&aGenerating item: &6" + this.getItemData().getId());
        ItemBuilder builder = new ItemBuilder(Material.IRON_AXE, 1);
        String id = this.getItemData().getId();
        if (this.pointData != null && !this.pointData.isEmpty()) {
            this.add(debugMessages, "&d- &aPoint Data: &6" + this.pointData);
            handlePointData(this.pointData, complexData);
        }
        RandomItemStartGeneratingEvent startEvent = new RandomItemStartGeneratingEvent(id, complexData, enchantmentMap);
        Bukkit.getPluginManager().callEvent(startEvent);
        enchantmentMap = startEvent.getEnchantmentMap();
        complexData = startEvent.getData();

        // 这玩意就是为了让用户知道 指向数据 和 其它插件 对 alreadySectionMap 的影响
        if (isDebug() && !alreadySectionMap.isEmpty()) {
            this.add(debugMessages, "&d- &aAlready Random Sections: &6");
            debugStringsMap(alreadySectionMap);
        }

        String display = this.getItemData().getDisplay();
        this.add(debugMessages, "&d- &aOriginal Display: &6" + display);
        display = replaceAll(display, complexData);
        this.add(debugMessages, "&d- &aFinal Display: &6" + display);
        builder.name(display);

        String materialString = this.getItemData().getMaterial();
        this.add(debugMessages, "&d- &aOriginal Material: &6" + materialString);
        materialString = (replaceAll(materialString, complexData));
        Material matchMaterial = Material.matchMaterial(materialString);
        Material material = matchMaterial == null ? Material.STONE : matchMaterial;
        this.add(debugMessages, "&d- &aFinal Material: &6" + materialString + "&5 - > &6" + material);
        builder.material(material);

        String data = this.getItemData().getData();
        this.add(debugMessages, "&d- &aOriginal Data: &6" + data);
        data = replaceAll(data, complexData);
        if (!"0".equals(data)) {
            int dataInteger = 0;
            try {
                dataInteger = Integer.parseInt(data);
            } catch (Exception e) {
                sendWrong("RandomItem's data must be a integer!");
            }
            this.add(debugMessages, "&d- &aFinal Data: &6" + data);
            if (Main.gameVersion >= 1141) {
                builder.customModelData(dataInteger);
            } else {
                builder.damage(dataInteger);
            }
        }

        List<String> newLores = new ArrayList<>();
        List<String> loresClone = this.getItemData().getLoresClone();
        if (!this.getItemData().getLoresClone().isEmpty()) {
            this.add(debugMessages, "&d- &aOriginal lores: ");
            for (String lore : loresClone) {
                this.add(debugMessages, "&d  - &f" + lore);
                lore = replaceAll(lore, complexData);
                addStrings(newLores, lore);
            }
        }
        if (isDebug() && !newLores.isEmpty()) {
            this.add(debugMessages, "&d- &aFinal Lores: ");
            for (String lore : newLores) {
                this.add(debugMessages, "&d  - &f&f" + lore);
            }
        }
        builder.lore(newLores);

        if (this.itemData.getItemSection().contains("color")) {
            String rgbData = replaceAll(this.itemData.getItemSection().getString("color"), complexData);
            this.add(debugMessages, "&d- &aOriginal RBG Color: &6" + rgbData);
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
            this.add(debugMessages, "&d- &aFinal RBG Color: &6" + red + "," + green + "," + blue + "&5 - > &6" + color.asRGB());
            if (color != null) {
                builder.color(color);
            }
        }

        boolean unbreakable = false;
        String unbreakableFormula = this.getItemData().getUnbreakableFormula();
        if (unbreakableFormula != null) {
            this.add(debugMessages, "&d- &aOriginal unbreakable: " + unbreakableFormula);
            unbreakableFormula = replaceAll(unbreakableFormula, complexData);
            if ("true".equals(unbreakableFormula) || "false".equals(unbreakableFormula)) {
                unbreakable = Boolean.parseBoolean(unbreakableFormula);
            } else {
                unbreakable = getResult(unbreakableFormula) != 0;
            }
        }
        this.add(debugMessages, "&d- &aFinal unbreakable: &6" + unbreakableFormula + "&5 - > &6" + unbreakable);
        builder.unbreakable(unbreakable);

        List<String> itemFlags = this.getItemData().getItemFlagsClone();
        if (!itemFlags.isEmpty()) {
            this.add(debugMessages, "&d- &aOriginal ItemFlags: ");
            for (int i = 0; i < itemFlags.size(); i++) {
                String itemFlag = itemFlags.get(i);
                this.add(debugMessages, "&d  - &f" + itemFlag);
                itemFlag = replaceAll(itemFlag, complexData);
                itemFlags.set(i, itemFlag);
            }
            this.add(debugMessages, "&d- &aFinal ItemFlags: ");
            for (String itemFlag : itemFlags) {
                this.add(debugMessages, "&d  - &f&f" + itemFlag);
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
            this.add(debugMessages, "&d- &aOriginal NBT-keys: ");
            debugConfigurationSection(nbtSection, null, debugMessages);
            this.add(debugMessages, "&d- &aFinal NBT-keys: ");
            nbtCompound = translateSection(nbtCompound, nbtSection, complexData, null, this.isDebug, debugMessages);
        }

        ConfigurationSection attributeSection = this.getItemData().getItemSection().getConfigurationSection("attributes");
        if (attributeSection != null && !attributeSection.getKeys(false).isEmpty()) {
            NBTList nbtList = new NBTList();
            this.add(debugMessages, "&d- &aOriginal Attributes: ");
            for (String att : attributeSection.getKeys(false)) {
                ConfigurationSection attSection = attributeSection.getConfigurationSection(att);
                debugConfigurationSection(attSection, null, debugMessages);
            }
            for (String att : attributeSection.getKeys(false)) {
                ConfigurationSection attSection = attributeSection.getConfigurationSection(att);
                NBTCompound compound = new NBTCompound();
                this.add(debugMessages, "&d- &aFinal Attributes: ");
                compound = translateSection(compound, attSection, complexData, null, this.isDebug, debugMessages);
                nbtList.add(compound);
            }
            nbtCompound.put("AttributeModifiers", nbtList);
        }

        if (enchantmentMap != null && !enchantmentMap.isEmpty()) {
            this.add(debugMessages, "&d- &aOriginal enchantments: ");
            for (String enchant : enchantmentMap.keySet()) {
                this.add(debugMessages, "&d  -> &b" + enchant + " &5= &e" + enchantmentMap.get(enchant));
                String value = replaceAll(enchantmentMap.get(enchant), complexData);
                enchantmentMap.put(enchant, value);
            }
            this.add(debugMessages, "&d- &aFinal enchantments: ");
            for (String enchant : enchantmentMap.keySet()) {
                String value = enchantmentMap.get(enchant);
                this.add(debugMessages, "&d  -> &b&b" + enchant + " &5= &e" + value);
                int level = (int) Math.round(getResult(value));
                if (level > 0) {
                    Enchantment enchantment = Items.asEnchantment(enchant);
                    if (!checkNull(enchantment, "Enchantment can't be null! in &6" + id + ".enchantments &c: &b" + enchant)) {
                        builder.enchant(enchantment, level, true);
                    }
                }
            }
        }

        ItemStack itemStack = builder.build();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (nbtCompound != null && !nbtCompound.isEmpty()) {
            nbtCompound.saveTo(itemStack);
        }
        itemStack.setItemMeta(itemMeta);

        RandomItemFinishGeneratingEvent finishEvent = new RandomItemFinishGeneratingEvent(id, this.player, itemStack);
        Bukkit.getPluginManager().callEvent(finishEvent);
        itemStack = finishEvent.getItemStack();

        long finishTime = System.currentTimeMillis();
        if (debugMessages != null && !debugMessages.isEmpty() && this.isDebug) {
            debugMessages.forEach(message -> sendDebug(message, true));
            if (!sectionMap.isEmpty()) {
                sendDebug("&d- &aOriginal Random Sections: ", true);
                sectionMap.keySet().stream().sorted().forEach(key -> debugSection(sectionMap.get(key)));
            }
            if (!complexData.getDebugReplacingMessages().isEmpty()) {
                sendDebug("&aReplacing Random Sections:", true);
                complexData.getDebugReplacingMessages().forEach(message -> sendDebug(message, true));
            }
            if (!alreadySectionMap.isEmpty()) {
                sendDebug("&d- &aFinal Random Sections: &6", true);
                debugStringsMap(alreadySectionMap);
            }
        }
        sendDebug("&2Done! &9Total time: &6" + (finishTime - startTime) + "&9ms", this.isDebug);

        return itemStack;
    }

    public String getPointData() {
        return this.pointData;
    }

    public ItemData getItemData() {
        return this.itemData;
    }
}
