package com.skillw.randomitem.api.section.type;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.util.SectionUtils.debugSection;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.section.type.BaseSectionType
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
        if (type.contains(":")) {
            String[] splits = type.split("-")[1].split(":");
            if (splits.length > 0) {
                return splits[0].equals(this.name) || this.aliases.contains(splits[0]);
            }
        } else {
            return type.equals(this.name) || this.aliases.contains(type);
        }
        return false;
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
            BaseSection baseSection;
            if (type.contains(":")) {
                String[] splits = type.split(":");
                String string = type.split("-")[0] + "-" + splits[1];
                baseSection = this.loadFromSectionSimply(string);
                if (baseSection != null) {
                    if (baseSection instanceof Debuggable) {
                        debugSection(baseSection);
                    }
                    map.put(type.split("-")[0], baseSection);
                }
            } else {
                baseSection = this.loadFromSection(section);
                if (baseSection != null) {
                    if (baseSection instanceof Debuggable) {
                        debugSection(baseSection);
                    }
                    map.put(section.getName(), baseSection);
                }
            }

        }
    }

    /**
     * To return a BaseSection about the ConfigurationSection
     *
     * @param section the ConfigurationSection which has a Base Section.
     * @return the BaseSection about the ConfigurationSection
     */
    protected abstract BaseSection loadFromSection(ConfigurationSection section);

    /**
     * To return a BaseSection about the simple formula
     *
     * @param string a simple formula of BaseSection.
     * @return the BaseSection about the simple formula
     */
    protected abstract BaseSection loadFromSectionSimply(String string);
}
