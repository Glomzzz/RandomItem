package com.skillw.randomitem.callable;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.event.RandomItemFinishGeneratingEvent;
import com.skillw.randomitem.api.event.RandomItemStartGeneratingEvent;
import com.skillw.randomitem.api.object.ItemCompute;
import com.skillw.randomitem.api.object.SubString;
import com.skillw.randomitem.utils.RandomItemUtils;
import io.izzel.taboolib.module.nms.NMS;
import io.izzel.taboolib.util.item.ItemBuilder;
import io.izzel.taboolib.util.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.utils.RandomItemUtils.*;

/**
 * @ClassName : com.skillw.randomitem.future.RandomItemCallable
 * Created by Glom_ on 2021-02-04 19:37:20
 * Copyright  2020 user. All rights reserved.
 */
public class RandomItemCallable implements Callable {
    private final Player player;
    private final String id;
    private final String display;
    private final String material;
    private final short data;
    private final List<String> lores;
    private final ConfigurationSection nbtSection;
    private final ItemCompute itemCompute;
    private final ConcurrentHashMap<String, List<SubString>> subStringMap;
    private final ConcurrentHashMap<String, String> numberMap;
    private final ConcurrentHashMap<String, String> enchantMap;
    private final String pointData;

    public RandomItemCallable(Player player,
                              String id,
                              String display,
                              String material,
                              short data,
                              List<String> lores,
                              ConfigurationSection nbtSection,
                              ItemCompute itemCompute,
                              ConcurrentHashMap<String, List<SubString>> subStringMap,
                              ConcurrentHashMap<String, String> numberMap,
                              ConcurrentHashMap<String, String> enchantMap,
                              String pointData) {
        this.player = player;
        this.id = id;
        this.display = display;
        this.material = material;
        this.data = data;
        this.lores = lores;
        this.nbtSection = nbtSection;
        this.itemCompute = itemCompute;
        this.subStringMap = subStringMap;
        this.numberMap = numberMap;
        this.enchantMap = enchantMap;
        this.pointData = pointData;
    }

    @Override
    public Object call() throws Exception {
        ItemBuilder builder;
        if (Main.version > 1121) {
            builder = new ItemBuilder(Material.IRON_AXE, 1);
        } else {
            builder = new ItemBuilder(Material.IRON_AXE, 1, this.data);
        }

        String display = (this.display == null) ? this.material : this.display;
        ConcurrentHashMap<String, String> numberMap = new ConcurrentHashMap<>();
        for (String key : this.numberMap.keySet()) {
            String value = RandomItemUtils.doPAPIReplace(this.numberMap.get(key), this.player);
            double start = (RandomItemUtils.getResult(value.split(",")[0]));
            double bound = (RandomItemUtils.getResult(value.split(",")[1]));
            int fixed = Integer.parseInt(value.split(",")[2]);
            numberMap.put(key, String.valueOf(RandomItemUtils.getRandom(start, bound, fixed)));
        }
        ItemCompute itemCompute = this.itemCompute.clone();
        ConcurrentHashMap<String, List<SubString>> subStringMap = new ConcurrentHashMap<>();
        for (String key : this.subStringMap.keySet()) {
            List<SubString> subStrings = new ArrayList<>();
            List<SubString> values = this.subStringMap.get(key);
            values.forEach(subString -> subStrings.add(subString.clone()));
            subStringMap.put(key, subStrings);
        }
        ConcurrentHashMap<String, String> computeMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, List<String>> alreadyStringsMap = itemCompute.getAlreadyStringMap();
        if (this.pointData != null) {
            pointHandle(this.pointData, subStringMap, numberMap, computeMap, alreadyStringsMap);
        }
        itemCompute.setNumberMap(numberMap);
        itemCompute.setSubStringMap(subStringMap);
        ConcurrentHashMap<String, String> computeMap2 = itemCompute.calculateCompute(this.player.getUniqueId());
        for (String key : computeMap2.keySet()) {
            if (!computeMap.containsKey(key)) {
                computeMap.put(key, computeMap2.get(key));
            }
        }
        RandomItemStartGeneratingEvent startEvent = new RandomItemStartGeneratingEvent(this.id, this.player, computeMap, subStringMap, numberMap, alreadyStringsMap);
        Bukkit.getPluginManager().callEvent(startEvent);
        subStringMap = startEvent.getSubStringMap();
        numberMap = startEvent.getNumberMap();
        computeMap = startEvent.getComputeMap();
        alreadyStringsMap = startEvent.getAlreadyStringMap();
        Material material = Items.asMaterial(doReplace(this.material, subStringMap, computeMap, numberMap, alreadyStringsMap, this.player));
        builder.material(material);
        display = doReplace(display, subStringMap, computeMap, numberMap, alreadyStringsMap, this.player);
        builder.name(display);
        if (Main.version >= 1141) {
            builder.customModelData((int) this.data);
        }
        List<String> newLores = new ArrayList<>();
        List<String> loresClone = new ArrayList<>(this.lores);
        for (String lore : loresClone) {
            lore = doReplace(lore, subStringMap, computeMap, numberMap, alreadyStringsMap, this.player);
            if (lore.contains("\n")) {
                Collections.addAll(newLores, lore.split("\n"));
            } else {
                newLores.add(lore);
            }
        }
        builder.lore(newLores);
        for (String enchant : this.enchantMap.keySet()) {
            String value = doReplace(this.enchantMap.get(enchant), subStringMap, computeMap, numberMap, alreadyStringsMap, this.player);
            int level = (int) Math.round(getResult(value));
            if (level > 0) {
                builder.enchant(Enchantment.getByKey(NamespacedKey.minecraft(enchant)), level);
            }
        }
        RandomItemFinishGeneratingEvent finishEvent = new RandomItemFinishGeneratingEvent(this.id, this.player, builder);
        Bukkit.getPluginManager().callEvent(finishEvent);
        builder = finishEvent.getItemBuilder();
        ItemStack itemStack = builder.build();
        translateSection(NMS.handle().loadNBT(itemStack), this.nbtSection, subStringMap, computeMap, numberMap, alreadyStringsMap, this.player).saveTo(itemStack);
        return itemStack;
    }


    public String getPointData() {
        return this.pointData;
    }
}
