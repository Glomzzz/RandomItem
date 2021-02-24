package com.skillw.randomitem.api.section;

import com.skillw.randomitem.api.section.type.BaseSectionType;
import com.skillw.randomitem.api.section.weight.Weighable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.utils.Utils.checkNull;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.section.BaseSection
 * @date 2021-02-09 09:28:31
 * Copyright  2020 user. All rights reserved.
 */
public abstract class BaseSection {
    protected final String id;
    protected BaseSectionType type;
    protected ConcurrentHashMap<String, Object> map;

    public BaseSection(String id, Class<? extends BaseSectionType> typeClass, Map<String, Object> map) {
        this.id = id;
        this.type = null;
        for (BaseSectionType sectionType : BaseSectionType.getSectionTypes()) {
            if (sectionType.getClass().equals(typeClass)) {
                this.type = sectionType;
                break;
            }
        }
        checkNull(this.type, "&cThe section type: &6" + typeClass.getName() + "&c doesn't exist! &7Maybe you haven't registered it yet?");
        this.map = new ConcurrentHashMap<>();
        if (map != null && !map.isEmpty()) {
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
    protected abstract List<String> handleSection(String replaced, ComplexData data);

    /**
     * To load a BaseSection
     *
     * @param replaced the replaced string
     * @param data     the complex data
     */
    public void load(String replaced, ComplexData data) {
        List<String> result = this.handleSection(replaced, data);
        if (this instanceof Weighable) {
            data.getAlreadySectionMap().putIfAbsent(this.getId() + ".data", new LinkedList<>(Arrays.asList(result.get(result.size() - 1))));
            result.remove(result.size() - 1);
        }
        data.getAlreadySectionMap().putIfAbsent(this.getId(), new LinkedList<>(result));
    }

    /**
     * To get the clone of this BaseSection.
     *
     * @return the clone of this BaseSection
     */
    @Override
    public abstract BaseSection clone();
}
