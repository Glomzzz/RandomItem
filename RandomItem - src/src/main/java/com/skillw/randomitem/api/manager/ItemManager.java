package com.skillw.randomitem.api.manager;

import com.skillw.randomitem.api.randomitem.RandomItem;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.manager.ItemManager
 * @date 2021-02-04 17:10:19
 * Copyright  2020 user. All rights reserved.
 */
public interface ItemManager {
    /**
     * To get the map about RandomItem (RandomItem's id , RandomItem)
     *
     * @return the map about RandomItem
     */
    HashMap<String, RandomItem> getRandomItemHashMap();

    /**
     * To give the player a RandomItem with point data.
     *
     * @param player    the player
     * @param itemID    RandomItem's id
     * @param sender    the command sender
     * @param pointData the point Data (point section's value)
     */
    void giveRandomItem(Player player, String itemID, CommandSender sender, String pointData);

    /**
     * To drop some RandomItems in a location randomly.
     *
     * @param itemID        RandomItem's id
     * @param location      the location where to drop items
     * @param amountFormula how many items do it drop (support PAPI)
     * @param chanceFormula how many chance do it drop (support PAPI) (0~1)
     * @param isSame        weather or not generating same item.
     * @param player        the player
     * @param commandSender the command sender (nullable)
     */
    void dropRandomItemRandomly(String itemID, Location location, String amountFormula, String chanceFormula, boolean isSame, Player player, CommandSender commandSender);

    /**
     * To create a new file to save the ItemStack.
     *
     * @param itemStack ItemStack
     * @param itemKey   the new id you want
     * @param path      the path where to save this item stack
     * @return success
     */
    boolean createItemStackConfig(ItemStack itemStack, String itemKey, String path);
}
