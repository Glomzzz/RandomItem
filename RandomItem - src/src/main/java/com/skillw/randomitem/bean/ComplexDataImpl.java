package com.skillw.randomitem.bean;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import org.bukkit.entity.Player;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName : com.skillw.randomitem.api.section.ComplexData
 * Created by Glom_ on 2021-02-09 21:19:40
 * Copyright  2020 user. All rights reserved.
 */
public class ComplexDataImpl implements ComplexData {
    private ConcurrentHashMap<String, BaseSection> sectionMap;
    private ConcurrentHashMap<String, String> alreadySectionMap;
    private Set<String> debugReplacingMessages = new LinkedHashSet<>();
    private Player player;

    public ComplexDataImpl(ConcurrentHashMap<String, BaseSection> sectionMap, ConcurrentHashMap<String, String> alreadySectionMap, Player player) {
        this.sectionMap = sectionMap;
        this.alreadySectionMap = alreadySectionMap;
        this.player = player;
    }

    @Override
    public Set<String> getDebugReplacingMessages() {
        return this.debugReplacingMessages;
    }

    @Override
    public void setDebugReplacingMessages(Set<String> messages) {
        this.debugReplacingMessages = messages;
    }

    @Override
    public void addDebugReplacingMessages(String message) {
        this.debugReplacingMessages.add(message);
    }

    @Override
    public ConcurrentHashMap<String, BaseSection> getSectionMap() {
        return this.sectionMap;
    }

    @Override
    public void setSectionMap(ConcurrentHashMap<String, BaseSection> sectionMap) {
        this.sectionMap = sectionMap;
    }

    @Override
    public ConcurrentHashMap<String, String> getAlreadySectionMap() {
        return this.alreadySectionMap;
    }

    @Override
    public void setAlreadySectionMap(ConcurrentHashMap<String, String> alreadySectionMap) {
        this.alreadySectionMap = alreadySectionMap;
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

}
