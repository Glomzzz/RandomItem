package com.skillw.randomitem.item;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.object.ItemCompute;
import com.skillw.randomitem.api.object.RandomItem;
import com.skillw.randomitem.api.object.SubString;
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
    private final short data;
    private final List<String> lores;
    private final ConfigurationSection nbtSection;
    private final ItemCompute itemCompute;
    private final ConcurrentHashMap<String, List<SubString>> subStringMap;
    private final ConcurrentHashMap<String, String> numberMap;
    private final ConcurrentHashMap<String, String> enchantMap;

    public RandomItemImpl(String id,
                          String display,
                          String material,
                          short data,
                          List<String> lores,
                          ConfigurationSection nbtSection,
                          ItemCompute itemCompute,
                          ConcurrentHashMap<String, List<SubString>> subStringMap,
                          ConcurrentHashMap<String, String> numberMap,
                          ConcurrentHashMap<String, String> enchantMap) {
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
    }

    @Override
    public short getData() {
        return this.data;
    }

    @Override
    public ConcurrentHashMap<String, String> getEnchantMap() {
        return this.enchantMap;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return this.getItemStack(player, null);
    }

    @Override
    public ItemStack getItemStack(Player player, String pointData) {
        RandomItemCallable callable = new RandomItemCallable(player, this.id, this.display, this.material, this.data, this.lores, this.nbtSection, this.itemCompute, this.subStringMap, this.numberMap, this.enchantMap, pointData);
        Future future = Main.getScheduledExecutorService().submit(callable);
        try {
            return (ItemStack) future.get();
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
    public ConcurrentHashMap<String, List<SubString>> getSubStringMap() {
        return this.subStringMap;
    }

    @Override
    public ConcurrentHashMap<String, String> getNumberMap() {
        return this.numberMap;
    }

    @Override
    public ItemCompute getItemCompute() {
        return this.itemCompute;
    }

    @Override
    public void register() {
        Main.getItemManager().getRPGItemHashMap().put(this.id, this);
    }
}