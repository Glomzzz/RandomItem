package com.skillw.randomitem.api.event;

import com.skillw.randomitem.api.ComplexData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glom_
 * @date 2021-2-6 11:39:23
 */
public class RandomItemStartGeneratingEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String itemID;
    private ComplexData data;
    private ConcurrentHashMap<String, String> enchantmentMap;

    public RandomItemStartGeneratingEvent(String itemID, ComplexData data, ConcurrentHashMap<String, String> enchantmentMap) {
        super(true);
        this.itemID = itemID;
        this.data = data;
        this.player = data.getPlayer();
        this.enchantmentMap = enchantmentMap;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getItemID() {
        return this.itemID;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ComplexData getData() {
        return this.data;
    }

    public void setData(ComplexData data) {
        this.data = data;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public ConcurrentHashMap<String, String> getEnchantmentMap() {
        return this.enchantmentMap;
    }

    public void setEnchantmentMap(ConcurrentHashMap<String, String> enchantmentMap) {
        this.enchantmentMap = enchantmentMap;
    }
}
