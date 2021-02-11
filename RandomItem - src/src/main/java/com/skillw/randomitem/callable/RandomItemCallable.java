package com.skillw.randomitem.callable;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.ComplexData;
import com.skillw.randomitem.api.event.RandomItemFinishGeneratingEvent;
import com.skillw.randomitem.api.event.RandomItemStartGeneratingEvent;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.utils.ComplexDataImpl;
import com.skillw.randomitem.utils.RandomItemUtils;
import io.izzel.taboolib.module.nms.NMS;
import io.izzel.taboolib.util.item.ItemBuilder;
import io.izzel.taboolib.util.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.isDebug;
import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.utils.RandomItemUtils.*;

/**
 * @ClassName : com.skillw.randomitem.callable.RandomItemCallable
 * Created by Glom_ on 2021-02-04 19:37:20
 * Copyright  2020 user. All rights reserved.
 */
public class RandomItemCallable implements Callable<ItemStack> {
    private final Player player;
    private final String id;
    private final String display;
    private final String material;
    private final String data;
    private final List<String> lores;
    private final ConfigurationSection nbtSection;
    private final ConcurrentHashMap<String, String> enchantmentMap;
    private final ConcurrentHashMap<String, BaseSection> sectionMap;
    private final String pointData;

    public RandomItemCallable(Player player,
                              String id,
                              String display,
                              String material,
                              String data,
                              List<String> lores,
                              ConfigurationSection nbtSection,
                              ConcurrentHashMap<String, String> enchantmentMap,
                              ConcurrentHashMap<String, BaseSection> sectionMap,
                              String pointData) {
        this.player = player;
        this.id = id;
        this.display = display;
        this.material = material;
        this.data = data;
        this.lores = lores;
        this.nbtSection = nbtSection;
        this.sectionMap = sectionMap;
        this.enchantmentMap = enchantmentMap;
        this.pointData = pointData;
    }

    @Override
    public ItemStack call() {
        sendDebug("&aGenerating item: &6" + this.id);
        long startTime = System.currentTimeMillis();
        ConcurrentHashMap<String, BaseSection> sectionMap = RandomItemUtils.cloneBaseSectionMap(this.sectionMap);
        ConcurrentHashMap<String, String> enchantmentMap = new ConcurrentHashMap<>(this.enchantmentMap);
        ConcurrentHashMap<String, List<String>> alreadySectionMap = new ConcurrentHashMap<>();
        ComplexData complexData = new ComplexDataImpl(sectionMap, alreadySectionMap, this.player);
        ItemBuilder builder;
        if (Main.version > 1121) {
            builder = new ItemBuilder(Material.IRON_AXE, 1);
        } else {
            builder = new ItemBuilder(Material.IRON_AXE, 1);
        }
        String display = (this.display == null) ? this.material : this.display;
        if (this.pointData != null) {
            pointHandle(this.pointData, complexData);
        }
        sendDebug("&d- &aOriginal display: &6" + display);

        sendDebug("&d- &aOriginal material: &6" + this.material);

        sendDebug("&d- &aOriginal data: &6" + this.data);

        if (isDebug() && !this.lores.isEmpty()) {
            sendDebug("&d- &aOriginal lores: ");
            for (String lore : this.lores) {
                sendDebug("&d  - &f" + lore);
            }
        }
        if (this.pointData != null) {
            sendDebug("&d- &aPoint Data: &6" + this.pointData);
        }
        if (!alreadySectionMap.isEmpty()) {
            if (isDebug()) {
                sendDebug("&d- &aAlready Sections: &6");
                for (String key : alreadySectionMap.keySet()) {
                    sendDebug("&d  -> &b" + key + " &5= &e" + alreadySectionMap.get(key));
                }
            }
        }
        if (this.nbtSection != null && !this.nbtSection.getKeys(false).isEmpty()) {
            sendDebug("&d- &aOriginal NBT-keys: ");
            for (String key : this.nbtSection.getKeys(false)) {
                Object object = this.nbtSection.get(key);
                if (object instanceof ConfigurationSection) {
                    ConfigurationSection section = (ConfigurationSection) object;
                    if (!section.getKeys(false).isEmpty()) {
                        sendDebug("&d   -> &b" + key + " &5= &e" + section.get(key));
                    }
                } else {
                    sendDebug("&d  -> &b" + key + " &5= &e" + this.nbtSection.get(key));
                }
            }
        }
        if (!enchantmentMap.isEmpty()) {
            sendDebug("&d- &aOriginal enchantments: ");
            for (String key : enchantmentMap.keySet()) {
                sendDebug("&d  -> &b" + key + " &5= &e" + enchantmentMap.get(key));
            }
        }
        if (!sectionMap.isEmpty()) {
            sendDebug("&d- &aOriginal sections: ");
            for (BaseSection baseSection : sectionMap.values()) {
                RandomItemUtils.debugSection(baseSection);
            }
        }

        RandomItemStartGeneratingEvent startEvent = new RandomItemStartGeneratingEvent(this.id, complexData, enchantmentMap);
        Bukkit.getPluginManager().callEvent(startEvent);
        enchantmentMap = startEvent.getEnchantmentMap();
        complexData = startEvent.getData();
        sendDebug("&d- &aReplacing sections:");
        String materialString = (replaceAll(this.material, complexData));
        Material material = Material.matchMaterial(materialString) == null ? Material.STONE : Material.matchMaterial(materialString);
        builder.material(material);
        display = replaceAll(display, complexData);
        builder.name(display);
        String data = replaceAll(this.data, complexData);
        if (Main.version >= 1141) {
            builder.customModelData(Integer.parseInt(data));
        } else {
            builder.damage(Integer.parseInt(data));
        }
        List<String> newLores = new ArrayList<>();
        List<String> loresClone = new ArrayList<>(this.lores);
        for (String lore : loresClone) {
            lore = replaceAll(lore, complexData);
            if (lore.contains("\n")) {
                Collections.addAll(newLores, lore.split("\n"));
            } else {
                newLores.add(lore);
            }
        }
        builder.lore(newLores);
        for (String enchant : enchantmentMap.keySet()) {
            String value = replaceAll(enchantmentMap.get(enchant), complexData);
            int level = (int) Math.round(getResult(value));
            if (level > 0) {
                builder.enchant(Items.asEnchantment(enchant), level);
            }
        }
        sendDebug("&d- &aFinal material: &6" + materialString);

        sendDebug("&d- &aFinal data: &6" + data);

        if (isDebug() && !newLores.isEmpty()) {
            sendDebug("&d- &aFinal lores: ");
            for (String lore : newLores) {
                sendDebug("&d  - &f" + lore);
            }
        }
        RandomItemFinishGeneratingEvent finishEvent = new RandomItemFinishGeneratingEvent(this.id, this.player, builder);
        Bukkit.getPluginManager().callEvent(finishEvent);
        builder = finishEvent.getItemBuilder();
        ItemStack itemStack = builder.build();
        if (this.nbtSection != null && !this.nbtSection.getKeys(false).isEmpty()) {
            sendDebug("&d- &aLoaded NBT-keys: ");
        }
        translateSection(NMS.handle().loadNBT(itemStack), this.nbtSection, complexData).saveTo(itemStack);

        if (!enchantmentMap.isEmpty()) {
            sendDebug("&d- &aLoaded enchantments: ");
            for (String key : enchantmentMap.keySet()) {
                sendDebug("&d  -> &b" + key + " &5= &e" + enchantmentMap.get(key));
            }
        }
        if (!alreadySectionMap.isEmpty()) {
            if (isDebug()) {
                sendDebug("&d- &aLoaded Sections: &6");
                for (String key : alreadySectionMap.keySet()) {
                    sendDebug("&d  -> &b" + key + " &5= &e" + alreadySectionMap.get(key));
                }
            }
        }
        long finishTime = System.currentTimeMillis();
        sendDebug("&2Done! &9Total time: &6" + (finishTime - startTime) + "&9ms");
        return itemStack;
    }


    public String getPointData() {
        return this.pointData;
    }
}
