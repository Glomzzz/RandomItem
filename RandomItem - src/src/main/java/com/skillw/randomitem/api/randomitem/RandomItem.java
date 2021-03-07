package com.skillw.randomitem.api.randomitem;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.randomitem.RandomItem
 * @date 2021-02-11 02:34:46
 * Copyright  2020 user. All rights reserved.
 */
public interface RandomItem extends ItemData {


    /**
     * To get the ItemStack created by the player
     *
     * @param player the player
     * @return the ItemStack created by the player
     */
    ItemStack getItemStack(Player player, boolean isDebug);

    /**
     * To get the ItemStack without calling random sections
     *
     * @return the ItemStack without calling random sections
     */
    ItemStack getItemStack();

    /**
     * To get the ItemStack created by the player with the Point Data
     *
     * @param player    the player
     * @param pointData the Point Data
     * @return the ItemStack created by the player
     */
    ItemStack getItemStack(Player player, String pointData, boolean isDebug);

    /**
     * To register the RandomItem to the plugin
     */
    void register();

}
