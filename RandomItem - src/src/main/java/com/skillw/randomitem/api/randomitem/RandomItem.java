package com.skillw.randomitem.api.randomitem;

import com.skillw.randomitem.api.section.BaseSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.randomitem.RandomItem
 * @date 2021-02-11 02:34:46
 * Copyright  2020 user. All rights reserved.
 */
public interface RandomItem {
    /**
     * To get the data of the ItemStack
     *
     * @return the data of the ItemStack (1.14+ CustomModelData)
     */
    String getData();

    /**
     * To get the Enchantment Map (Enchantment's name,Level formula)
     *
     * @return The Enchantment Map (Enchantment's name,Level formula)
     */
    ConcurrentHashMap<String, String> getEnchantMap();

    /**
     * To get the BaseSection Map (BaseSection's id,BaseSection)
     *
     * @return the BaseSection Map (BaseSection's id,BaseSection)
     */
    ConcurrentHashMap<String, BaseSection> getSectionMap();

    /**
     * To get the ItemStack created by the player
     *
     * @param player the player
     * @return the ItemStack created by the player
     */
    ItemStack getItemStack(Player player);

    /**
     * To get the ItemStack created by the player with the Point Data
     *
     * @param player    the player
     * @param pointData the Point Data
     * @return the ItemStack created by the player
     */
    ItemStack getItemStack(Player player, String pointData);

    /**
     * To get the id
     *
     * @return the id
     */
    String getId();

    /**
     * To get the display
     *
     * @return the display
     */
    String getDisplay();

    /**
     * To get the material
     *
     * @return the material
     */
    String getMaterial();

    /**
     * To get the lores
     *
     * @return the lores
     */
    List<String> getLores();

    /**
     * To register the RandomItem to the plugin
     */
    void register();
}
