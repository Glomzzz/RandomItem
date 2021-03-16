package com.skillw.randomitem.api.section;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.data.BaseData;
import com.skillw.randomitem.api.section.type.BaseSectionType;
import com.skillw.randomitem.api.section.weight.Weighable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.util.ConfigUtils.getPrefix;
import static com.skillw.randomitem.util.Utils.checkNull;

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
        checkNull(this.type, "The section type: &6" + typeClass.getName() + "&c doesn't exist! &7Maybe you haven't registered it yet?");
        this.map = new ConcurrentHashMap<>();
        if (map != null && !map.isEmpty()) {
            this.map.putAll(map);
        }
        if (this instanceof Weighable<?>) {
            boolean right = true;
            if (!this.getDataMap().containsKey("value-map")) {
                right = false;
            } else if (this.get("value-map") instanceof ConcurrentHashMap) {
                try {
                    ConcurrentHashMap<String, BaseData<?>> map1 = ((ConcurrentHashMap<String, BaseData<?>>) this.get("value-map"));
                    if (map1.isEmpty()) {
                        right = false;
                    }
                    for (String key : map1.keySet()) {
                        right = map1.get(key) instanceof BaseData<?>;
                        break;
                    }
                } catch (Exception e) {
                    right = false;
                }
            } else {
                right = false;
            }
            if (!right) {
                Main.sendMessage(getPrefix() + "&cThe map in the BaseSection must have a key \"value-map\" and its value ConcurrentHashMap<String,BaseData<?>> (can't be empty)! &ein class&6 " + this.getClass().getName());
                this.type = null;
            }
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
    protected abstract String handleSection(String replaced, ComplexData data);

    /**
     * To load a BaseSection
     *
     * @param replaced the replaced string
     * @param data     the complex data
     */
    public void load(String replaced, ComplexData data) {
        String result = this.handleSection(replaced, data);
        data.getAlreadySectionMap().putIfAbsent(this.getId(), result);
    }

    /**
     * To get the clone of this BaseSection.
     *
     * @return the clone of this BaseSection
     */
    @Override
    public abstract BaseSection clone();
}
