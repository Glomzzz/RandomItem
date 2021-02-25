package com.skillw.randomitem.api;

import com.skillw.randomitem.api.randomitem.ItemData;
import com.skillw.randomitem.api.randomitem.RandomItem;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.string.SubString;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.RandomItemAPI
 * @date 2021-02-04 18:01:15
 * Copyright  2020 user. All rights reserved.
 */
public interface RandomItemApi {

    /**
     * To get the results of strings after calling random sections
     *
     * @param strings the strings that call the random sections
     * @param data    bean
     * @return the results of strings after calling random sections
     */
    List<String> replaceAll(List<String> strings, ComplexData data);

    /**
     * To get the results of string after calling random sections
     *
     * @param string the string that call the random sections
     * @param data   bean
     * @return the results of string after calling random sections
     */
    String replaceAll(String string, ComplexData data);

    /**
     * To create a new RandomItem by a ConfigurationSection
     *
     * @param section the ConfigurationSection.
     * @return A new RandomItem about the ConfigurationSection
     */
    RandomItem createRandomItem(ConfigurationSection section);

    /**
     * To get the RandomItem created form the ItemData
     *
     * @param itemData the ItemData
     * @return the RandomItem created form the ItemData
     */
    RandomItem createRandomItem(ItemData itemData);

    /**
     * To get the ItemData created form the ConfigurationSection
     *
     * @param section the ConfigurationSection of a RandomItem
     * @return the ItemData created form the ConfigurationSection
     */
    ItemData createItemData(ConfigurationSection section);

    /**
     * To create a new RandomItem with some params.
     *
     * @param id                 the new id you want
     * @param display            the display
     * @param material           the material
     * @param data               the data
     * @param lores              the lores
     * @param usedGlobalSections the global sections used
     * @param unbreakable        the formula of unbreakable (0 = false , other number = true)
     * @param itemFlags          the ItemFlags of item.
     * @param enchantmentMap     the Enchantment Map (Enchantment's name,Level formula)
     * @param sectionMap         the BaseSection Map (BaseSection's id,BaseSection)
     * @param itemSection        the ConfigurationSection of this RandomItem
     * @return A new RandomItem
     */
    ItemData createItemData(String id,
                            String display,
                            String material,
                            String data,
                            List<String> lores,
                            List<String> usedGlobalSections,
                            String unbreakable,
                            List<String> itemFlags,
                            ConcurrentHashMap<String, String> enchantmentMap,
                            ConcurrentHashMap<String, BaseSection> sectionMap,
                            ConfigurationSection itemSection);

    /**
     * To create a new SubString.
     *
     * @param id      the new id you want
     * @param weight  the weight
     * @param strings the strings
     * @return A new SubString
     */
    SubString createSubString(String id, String weight, List<String> strings);

    /**
     * To reload the all config of the plugin
     */
    void reload();

    /**
     * To reload the RandomItems
     */
    void reloadRandomItems();
}
