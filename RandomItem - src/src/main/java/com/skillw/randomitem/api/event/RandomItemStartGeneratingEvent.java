package com.skillw.randomitem.api.event;

import com.skillw.randomitem.api.object.SubString;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RandomItemStartGeneratingEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String itemID;
    private ConcurrentHashMap<String, String> computeMap;
    private ConcurrentHashMap<String, List<SubString>> subStringMap;
    private ConcurrentHashMap<String, String> numberMap;
    private ConcurrentHashMap<String, List<String>> alreadyStringMap;

    public RandomItemStartGeneratingEvent(String itemID, Player player, ConcurrentHashMap<String, String> computeMap, ConcurrentHashMap<String, List<SubString>> subStringMap, ConcurrentHashMap<String, String> numberMap, ConcurrentHashMap<String, List<String>> alreadyStringMap) {
        super(true);
        this.player = player;
        this.itemID = itemID;
        this.computeMap = computeMap;
        this.subStringMap = subStringMap;
        this.numberMap = numberMap;
        this.alreadyStringMap = alreadyStringMap;
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

    public ConcurrentHashMap<String, String> getComputeMap() {
        return this.computeMap;
    }

    public void setComputeMap(ConcurrentHashMap<String, String> computeMap) {
        this.computeMap = computeMap;
    }

    public ConcurrentHashMap<String, List<SubString>> getSubStringMap() {
        return this.subStringMap;
    }

    public void setSubStringMap(ConcurrentHashMap<String, List<SubString>> subStringMap) {
        this.subStringMap = subStringMap;
    }

    public ConcurrentHashMap<String, String> getNumberMap() {
        return this.numberMap;
    }

    public void setNumberMap(ConcurrentHashMap<String, String> numberMap) {
        this.numberMap = numberMap;
    }

    public ConcurrentHashMap<String, List<String>> getAlreadyStringMap() {
        return this.alreadyStringMap;
    }

    public void setAlreadyStringMap(ConcurrentHashMap<String, List<String>> alreadyStringMap) {
        this.alreadyStringMap = alreadyStringMap;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
