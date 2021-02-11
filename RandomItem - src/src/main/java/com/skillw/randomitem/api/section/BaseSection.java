package com.skillw.randomitem.api.section;

import com.skillw.randomitem.api.ComplexData;
import com.skillw.randomitem.api.type.BaseSectionType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.section.BaseSection
 * @date 2021-02-09 09:28:31
 * Copyright  2020 user. All rights reserved.
 */
public abstract class BaseSection {
    protected final String id;
    protected final BaseSectionType type;
    protected ConcurrentHashMap<String, Object> map;

    public BaseSection(String id, BaseSectionType type, Map<String, Object> map) {
        this.id = id;
        this.type = type;
        this.map = new ConcurrentHashMap<>();
        if (map != null) {
            this.map.putAll(map);
        }
    }

    public String getId() {
        return this.id;
    }

    public BaseSectionType getType() {
        return this.type;
    }

    public ConcurrentHashMap<String, Object> getDataMap() {
        return this.map;
    }

    public void setMap(ConcurrentHashMap<String, Object> map) {
        this.map = map;
    }

    public Object get(String key) {
        return this.getDataMap().get(key);
    }

    public Object put(String key, Object object) {
        return this.getDataMap().put(key, object);
    }

    /**
     * To return a string to replace the replaced string
     *
     * @param replaced the replaced string
     * @param data     the complex data
     * @return The string which replace the replaced string
     */
    public abstract String handleSection(String replaced, ComplexData data);

    @Override
    public abstract BaseSection clone();
}
