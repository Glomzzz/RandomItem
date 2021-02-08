package com.skillw.randomitem.api.object;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName : com.skillw.randomitem.item.RandomItemI
 * Created by Glom_ on 2021-02-04 16:38:22
 * Copyright  2020 user. All rights reserved.
 */
public interface RandomItem {
    short getData();

    ConcurrentHashMap<String, String> getEnchantMap();

    ItemStack getItemStack(Player player);

    ItemStack getItemStack(Player player, String pointData);

    String getId();

    String getDisplay();

    String getMaterial();

    List<String> getLores();

    ConcurrentHashMap<String, List<SubString>> getSubStringMap();

    ConcurrentHashMap<String, String> getNumberMap();

    ItemCompute getItemCompute();

    void register();
}
