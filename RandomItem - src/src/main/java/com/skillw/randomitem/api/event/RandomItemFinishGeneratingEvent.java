package com.skillw.randomitem.api.event;

import io.izzel.taboolib.util.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RandomItemFinishGeneratingEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String itemID;
    private ItemBuilder itemBuilder;

    public RandomItemFinishGeneratingEvent(String itemID, Player player, ItemBuilder itemBuilder) {
        super(true);
        this.player = player;
        this.itemID = itemID;
        this.itemBuilder = itemBuilder;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getItemID() {
        return this.itemID;
    }

    public ItemBuilder getItemBuilder() {
        return this.itemBuilder;
    }

    public void setItemBuilder(ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
