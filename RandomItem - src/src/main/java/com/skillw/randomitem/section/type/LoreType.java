package com.skillw.randomitem.section.type;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.type.BaseSectionType;
import com.skillw.randomitem.section.LoreSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName : com.skillw.randomitem.section.type.StringType
 * Created by Glom_ on 2021-02-09 09:37:48
 * Copyright  2020 user. All rights reserved.
 */
public class LoreType extends BaseSectionType {
    public LoreType() {
        super("lore", Arrays.asList("l", "lores"));
    }

    @Override
    protected BaseSection loadFromSection(ConfigurationSection section) {
        List<String> values = section.getStringList("values");
        return new LoreSection(section.getName(), values);
    }

    @Override
    protected BaseSection loadFromSectionSimply(String string) {
        if (string != null && !string.isEmpty()) {
            String id = string.split("-")[0];
            string = string.replace(id + "-", "");
            String[] splits = string.split(",", -1);
            if (splits.length >= 1) {
                List<String> values = new ArrayList<>(Arrays.asList(splits));
                return new LoreSection(id, values);
            }
        }
        return null;
    }
}
