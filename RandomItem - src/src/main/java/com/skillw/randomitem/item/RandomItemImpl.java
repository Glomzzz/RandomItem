package com.skillw.randomitem.item;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.randomitem.RandomItem;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.callable.RandomItemCallable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Glom_
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class RandomItemImpl implements RandomItem {
    private final String id;
    private final String display;
    private final String material;
    private final String data;
    private final List<String> lores;
    private final ConfigurationSection nbtSection;
    private final ConcurrentHashMap<String, String> enchantMap;
    private final ConcurrentHashMap<String, BaseSection> sectionMap;

    public RandomItemImpl(String id,
                          String display,
                          String material,
                          String data,
                          List<String> lores,
                          ConfigurationSection nbtSection,
                          ConcurrentHashMap<String, String> enchantMap,
                          ConcurrentHashMap<String, BaseSection> sectionMap) {
        this.id = id;
        this.display = display;
        this.material = material;
        this.data = data;
        this.lores = lores;
        this.nbtSection = nbtSection;
        this.enchantMap = enchantMap;
        this.sectionMap = sectionMap;
    }


    @Override
    public String getData() {
        return this.data;
    }

    @Override
    public ConcurrentHashMap<String, String> getEnchantMap() {
        return this.enchantMap;
    }


    @Override
    public ConcurrentHashMap<String, BaseSection> getSectionMap() {
        return this.sectionMap;
    }


    @Override
    public ItemStack getItemStack(Player player) {
        return this.getItemStack(player, null);
    }


    @Override
    public ItemStack getItemStack(Player player, String pointData) {
        RandomItemCallable callable = new RandomItemCallable(player, this.id, this.display, this.material, this.data, this.lores, this.nbtSection, this.enchantMap, this.sectionMap, pointData);
        Future<ItemStack> future = Main.getScheduledExecutorService().submit(callable);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String getId() {
        return this.id;
    }


    @Override
    public String getDisplay() {
        return this.display;
    }


    @Override
    public String getMaterial() {
        return this.material;
    }


    @Override
    public List<String> getLores() {
        return this.lores;
    }


    @Override
    public void register() {
        Main.getItemManager().getRandomItemHashMap().put(this.id, this);
    }
}