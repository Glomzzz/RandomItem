package com.skillw.randomitem.item;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.randomitem.ItemData;
import com.skillw.randomitem.api.randomitem.RandomItem;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.callable.RandomItemCallable;
import io.izzel.taboolib.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.skillw.randomitem.util.StringUtils.getMessage;

/**
 * @author Glom_
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class RandomItemImpl implements RandomItem {
    private final ItemData itemData;

    public RandomItemImpl(ItemData itemData) {
        this.itemData = itemData;
    }


    @Override
    public String getData() {
        return this.itemData.getData();
    }

    @Override
    public ConcurrentHashMap<String, String> getEnchantMapClone() {
        return this.itemData.getEnchantMapClone();
    }


    @Override
    public ConcurrentHashMap<String, BaseSection> getSectionMapClone() {
        return this.itemData.getSectionMapClone();
    }

    @Override
    public List<String> getUsedGlobalSectionsClone() {
        return this.itemData.getUsedGlobalSectionsClone();
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return this.getItemStack(player, null);
    }

    public ItemData getItemData() {
        return this.itemData;
    }

    @Override
    public ItemStack getItemStack() {
        ItemBuilder builder = new ItemBuilder(Material.STONE);
        String display = (this.itemData.getDisplay() == null) ? this.itemData.getMaterial() : this.itemData.getDisplay();
        Material matchMaterial = Material.matchMaterial(this.itemData.getMaterial());
        Material material = matchMaterial == null ? Material.STONE : matchMaterial;
        builder.name(getMessage(display));
        builder.material(material);
        List<String> lores = this.getItemData().getLoresClone();
        for (int i = 0; i < lores.size(); i++) {
            lores.set(i, getMessage("&f" + lores.get(i)));
        }
        builder.lore(lores);
        builder.flags(ItemFlag.HIDE_ATTRIBUTES);
        return builder.build();
    }


    @Override
    public ItemStack getItemStack(Player player, String pointData) {
        RandomItemCallable callable = new RandomItemCallable(player, this.itemData, pointData);
        Future<ItemStack> future = Main.getScheduledExecutorService().submit(callable);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String getUnbreakableFormula() {
        return this.itemData.getUnbreakableFormula();
    }

    @Override
    public List<String> getItemFlagsClone() {
        return this.itemData.getItemFlagsClone();
    }

    @Override
    public ConfigurationSection getItemSection() {
        return this.itemData.getItemSection();
    }

    @Override
    public String getId() {
        return this.itemData.getId();
    }


    @Override
    public String getDisplay() {
        return this.itemData.getDisplay();
    }


    @Override
    public String getMaterial() {
        return this.itemData.getMaterial();
    }


    @Override
    public List<String> getLoresClone() {
        return this.itemData.getLoresClone();
    }


    @Override
    public void register() {
        Main.getItemManager().getRandomItemHashMap().put(this.itemData.getId(), this);
    }

}