package com.skillw.randomitem.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * @author Glom_
 * @date 2021-2-6 11:43:31
 */
public class RandomItemFinishGeneratingEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String itemID;
    private ItemStack itemStack;

    public RandomItemFinishGeneratingEvent(String itemID, Player player, ItemStack itemStack) {
        super(true);
        this.player = player;
        this.itemID = itemID;
        this.itemStack = itemStack;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getItemID() {
        return this.itemID;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
