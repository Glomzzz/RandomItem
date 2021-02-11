package com.skillw.randomitem.api.type;

import com.skillw.randomitem.api.debuggable.Debuggable;
import com.skillw.randomitem.api.section.BaseSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.utils.RandomItemUtils.debugSection;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.type.BaseSectionType
 * @date 2021-02-09 09:33:34
 * Copyright  2020 user. All rights reserved.
 */
public abstract class BaseSectionType {
    private static final Set<BaseSectionType> SECTION_TYPES = new HashSet<>();
    private final String name;
    private final List<String> aliases;

    protected BaseSectionType(String name, List<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public static Set<BaseSectionType> getSectionTypes() {
        return SECTION_TYPES;
    }

    public final void register() {
        getSectionTypes().add(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName(), this.getAliases());
    }

    protected String getName() {
        return this.name;
    }

    protected List<String> getAliases() {
        return this.aliases;
    }

    public final boolean isType(String type) {
        return type.equals(this.name) || this.aliases.contains(type);
    }

    protected void removeAliases(String text) {
        this.aliases.remove(text);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseSectionType)) {
            return false;
        }
        BaseSectionType that = (BaseSectionType) o;
        return this.getName().equals(that.getName()) && this.getAliases().equals(that.getAliases());
    }

    protected void addAliases(String text) {
        this.aliases.add(text);
    }

    public void loadIfSameType(String type, ConfigurationSection section, ConcurrentHashMap<String, BaseSection> map) {
        if (this.isType(type)) {
            BaseSection baseSection = this.loadFromSection(section);
            if (baseSection != null) {
                if (baseSection instanceof Debuggable) {
                    debugSection(baseSection);
                }
                map.put(section.getName(), baseSection);
            }
        }
    }

    /**
     * To return a BaseSection about the ConfigurationSection
     *
     * @param config the ConfigurationSection which has a Base Section.
     * @return the BaseSection about the ConfigurationSection
     */
    protected abstract BaseSection loadFromSection(ConfigurationSection config);
}
