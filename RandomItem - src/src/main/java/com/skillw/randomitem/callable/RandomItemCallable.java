package com.skillw.randomitem.callable;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.event.RandomItemFinishGeneratingEvent;
import com.skillw.randomitem.api.event.RandomItemStartGeneratingEvent;
import com.skillw.randomitem.api.randomitem.ItemData;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.bean.ComplexDataImpl;
import io.izzel.taboolib.module.nms.NMS;
import io.izzel.taboolib.module.nms.nbt.NBTCompound;
import io.izzel.taboolib.module.nms.nbt.NBTList;
import io.izzel.taboolib.util.item.ItemBuilder;
import io.izzel.taboolib.util.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.isDebug;
import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.util.CalculationUtils.getResult;
import static com.skillw.randomitem.util.NBTUtils.translateSection;
import static com.skillw.randomitem.util.ProcessUtils.replaceAll;
import static com.skillw.randomitem.util.SectionUtils.debugSection;
import static com.skillw.randomitem.util.StringUtils.addStrings;
import static com.skillw.randomitem.util.StringUtils.messageToText;
import static com.skillw.randomitem.util.Utils.handlePointData;

/**
 * @ClassName : com.skillw.randomitem.callable.RandomItemCallable
 * Created by Glom_ on 2021-02-04 19:37:20
 * Copyright  2020 user. All rights reserved.
 */
public final class RandomItemCallable implements Callable<ItemStack> {
    private final Player player;
    private final ItemData itemData;
    private final String pointData;

    public RandomItemCallable(Player player,
                              ItemData itemData,
                              String pointData) {
        this.player = player;
        this.itemData = itemData;
        this.pointData = pointData;
    }

    @Override
    public ItemStack call() {
        sendDebug("&aGenerating item: &6" + this.getItemData().getId());
        long startTime = System.currentTimeMillis();
        ConcurrentHashMap<String, BaseSection> sectionMap = this.getItemData().getSectionMapClone();
        ConcurrentHashMap<String, String> enchantmentMap = this.getItemData().getEnchantMapClone();
        ConcurrentHashMap<String, String> alreadySectionMap = new ConcurrentHashMap<>();
        ComplexData complexData = new ComplexDataImpl(sectionMap, alreadySectionMap, this.player);
        ItemBuilder builder = new ItemBuilder(Material.IRON_AXE, 1);
        String display = (this.getItemData().getDisplay() == null) ? this.getItemData().getMaterial() : this.getItemData().getDisplay();
        if (this.pointData != null) {
            handlePointData(this.pointData, complexData);
        }
        sendDebug("&d- &aOriginal display: &6" + display);

        sendDebug("&d- &aOriginal material: &6" + this.getItemData().getMaterial());

        sendDebug("&d- &aOriginal data: &6" + this.getItemData().getData());

        if (isDebug() && !this.getItemData().getLoresClone().isEmpty()) {
            sendDebug("&d- &aOriginal lores: ");
            for (String lore : this.getItemData().getLoresClone()) {
                sendDebug("&d  - &f" + lore);
            }
        }
        boolean unbreakable = false;
        if (this.getItemData().getUnbreakableFormula() != null) {
            sendDebug("&d- &aOriginal unbreakable: " + this.getItemData().getUnbreakableFormula());
            if ("true".equals(this.getItemData().getUnbreakableFormula()) || "false".equals(this.getItemData().getUnbreakableFormula())) {
                unbreakable = Boolean.parseBoolean(this.getItemData().getUnbreakableFormula());
            } else {
                unbreakable = getResult(replaceAll(this.getItemData().getUnbreakableFormula(), complexData)) != 0;
            }
        }
        List<String> itemFlags = this.getItemData().getItemFlagsClone();
        if (itemFlags != null && !itemFlags.isEmpty()) {
            sendDebug("&d- &aOriginal ItemFlags: ");
            for (int i = 0; i < itemFlags.size(); i++) {
                String itemFlag = itemFlags.get(i);
                sendDebug("&d  - &f" + itemFlag);
                itemFlag = replaceAll(itemFlag, complexData);
                itemFlags.set(i, itemFlag);
            }
        }

        if (this.pointData != null) {
            sendDebug("&d- &aPoint Data: &6" + this.pointData);
        }
        if (!alreadySectionMap.isEmpty()) {
            if (isDebug()) {
                sendDebug("&d- &aAlready Sections: &6");
                this.debugStringsMap(alreadySectionMap);
            }
        }
        if (this.getItemData().getNbtSection() != null && !this.getItemData().getNbtSection().getKeys(false).isEmpty()) {
            sendDebug("&d- &aOriginal NBT-keys: ");
            for (String key : this.getItemData().getNbtSection().getKeys(false)) {
                Object object = this.getItemData().getNbtSection().get(key);
                if (object instanceof ConfigurationSection) {
                    ConfigurationSection section = (ConfigurationSection) object;
                    if (!section.getKeys(false).isEmpty()) {
                        sendDebug("&d   -> &b" + key + " &5= &e" + section.get(key));
                    }
                } else {
                    sendDebug("&d  -> &b" + key + " &5= &e" + this.getItemData().getNbtSection().get(key));
                }
            }
        }
        if (!enchantmentMap.isEmpty()) {
            sendDebug("&d- &aOriginal enchantments: ");
            for (String key : enchantmentMap.keySet()) {
                sendDebug("&d  -> &b" + key + " &5= &e" + enchantmentMap.get(key));
            }
        }
        if (!sectionMap.isEmpty()) {
            sendDebug("&d- &aOriginal sections: ");
            for (BaseSection baseSection : sectionMap.values()) {
                debugSection(baseSection);
            }
        }

        RandomItemStartGeneratingEvent startEvent = new RandomItemStartGeneratingEvent(this.getItemData().getId(), complexData, enchantmentMap);
        Bukkit.getPluginManager().callEvent(startEvent);
        enchantmentMap = startEvent.getEnchantmentMap();
        complexData = startEvent.getData();
        sendDebug("&d- &aReplacing sections:");
        String materialString = (replaceAll(this.getItemData().getMaterial(), complexData));
        Material matchMaterial = Material.matchMaterial(materialString);
        Material material = matchMaterial == null ? Material.STONE : matchMaterial;
        builder.material(material);
        display = replaceAll(display, complexData);
        builder.name(display);
        String data = replaceAll(this.getItemData().getData(), complexData);
        if (Main.version >= 1141) {
            builder.customModelData(Integer.parseInt(data));
        } else {
            builder.damage(Integer.parseInt(data));
        }
        List<String> newLores = new ArrayList<>();
        List<String> loresClone = this.getItemData().getLoresClone();
        for (String lore : loresClone) {
            lore = replaceAll(lore, complexData);
            addStrings(newLores, lore);
        }
        builder.lore(newLores);
        for (String enchant : enchantmentMap.keySet()) {
            String value = replaceAll(enchantmentMap.get(enchant), complexData);
            int level = (int) Math.round(getResult(value));
            if (level > 0) {
                builder.enchant(Items.asEnchantment(enchant), level, true);
            }
        }
        builder.unbreakable(unbreakable);
        sendDebug("&d- &aFinal material: &6" + materialString);

        sendDebug("&d- &aFinal data: &6" + data);

        if (isDebug() && !newLores.isEmpty()) {
            sendDebug("&d- &aFinal lores: ");
            for (String lore : newLores) {
                sendDebug("&d  - &f" + lore);
            }
        }
        sendDebug("&d- &aFinal unbreakable: " + unbreakable);

        if (itemFlags != null && !itemFlags.isEmpty()) {
            sendDebug("&d- &aLoaded ItemFlags: ");
            for (String itemFlag : itemFlags) {
                sendDebug("&d  - &f" + itemFlag);
                ItemFlag flag = ItemFlag.valueOf(itemFlag);
                builder.flags(flag);
            }
        }
        if (this.itemData.getItemSection().contains("color")) {
            builder.color(Color.fromRGB(Integer.parseInt(replaceAll(this.itemData.getItemSection().getString("color"), complexData))));
        }

        ItemStack itemStack = builder.build();
        if (this.getItemData().getNbtSection() != null && !this.getItemData().getNbtSection().getKeys(false).isEmpty()) {
            sendDebug("&d- &aFinal NBT-keys: ");
            translateSection(NMS.handle().loadNBT(itemStack), this.getItemData().getNbtSection(), complexData).saveTo(itemStack);
        }
        ConfigurationSection attributeSection = this.itemData.getAttributeSection();
        if (attributeSection != null && !attributeSection.getKeys(false).isEmpty()) {
            NBTList nbtList = new NBTList();
            for (String att : attributeSection.getKeys(false)) {
                ConfigurationSection attSection = attributeSection.getConfigurationSection(att);
                NBTCompound compound = new NBTCompound();
                compound = translateSection(compound, attSection, complexData);
                nbtList.add(compound);
            }
            NBTCompound compound = NMS.handle().loadNBT(itemStack);
            compound.put("AttributeModifiers", nbtList);
            compound.saveTo(itemStack);
        }
        RandomItemFinishGeneratingEvent finishEvent = new RandomItemFinishGeneratingEvent(this.getItemData().getId(), this.player, itemStack);
        Bukkit.getPluginManager().callEvent(finishEvent);
        itemStack = finishEvent.getItemStack();
        if (!enchantmentMap.isEmpty()) {
            sendDebug("&d- &aFinal enchantments: ");
            for (String key : enchantmentMap.keySet()) {
                sendDebug("&d  -> &b" + key + " &5= &e" + enchantmentMap.get(key));
            }
        }
        if (!alreadySectionMap.isEmpty()) {
            if (isDebug()) {
                sendDebug("&d- &aFinal Sections: &6");
                this.debugStringsMap(alreadySectionMap);
            }
        }
        long finishTime = System.currentTimeMillis();
        sendDebug("&2Done! &9Total time: &6" + (finishTime - startTime) + "&9ms");
        return itemStack;
    }

    private void debugStringsMap(ConcurrentHashMap<String, String> alreadySectionMap) {
        for (String key : alreadySectionMap.keySet()) {
            String str = alreadySectionMap.get(key);
            sendDebug("&d  -> &b" + key + " &5: &e" + (str.contains("\n") || str.contains("\\n") ? "\n" : "") + messageToText(str));
        }
    }


    public String getPointData() {
        return this.pointData;
    }

    public ItemData getItemData() {
        return this.itemData;
    }
}
