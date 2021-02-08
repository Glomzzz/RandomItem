package com.skillw.randomitem.api.manager;

import com.skillw.randomitem.api.object.RandomItem;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * @ClassName : com.skillw.randomitem.item.ItemConfigI
 * Created by Glom_ on 2021-02-04 17:10:19
 * Copyright  2020 user. All rights reserved.
 */
public interface ItemManager {
    HashMap<String, RandomItem> getRPGItemHashMap();

    ItemStack getItemStack(String itemID, Player player);

    void giveRandomItem(Player player, String itemID);

    void giveRandomItem(Player player, String itemID, CommandSender sender);

    void giveRandomItem(Player player, String itemID, CommandSender sender, String pointData);

    void dropRandomItemRandomly(String itemID, Location location, String amountString, String chanceString, boolean isSame, Player player);

    boolean createItemStackConfig(ItemStack itemStack, String itemKey);
}
