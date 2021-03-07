package com.skillw.randomitem.section.type;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.type.BaseSectionType;
import com.skillw.randomitem.section.NumberSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.HashMap;

import static com.skillw.randomitem.util.ConfigUtils.getMapFromConfigurationSection;
import static com.skillw.randomitem.util.Utils.checkNull;

/**
 * @ClassName : com.skillw.randomitem.section.type.NumberType
 * Created by Glom_ on 2021-02-09 09:37:48
 * Copyright  2020 user. All rights reserved.
 */
public class NumberType extends BaseSectionType {
    public NumberType() {
        super("number", Arrays.asList("n"));
    }

    @Override
    protected BaseSection loadFromSection(ConfigurationSection section) {
        if (section != null) {
            HashMap<String, Object> dataMap = getMapFromConfigurationSection(section, null);
            return new NumberSection(section.getName(), dataMap);
        }
        return null;
    }

    @Override
    protected BaseSection loadFromSectionSimply(String string) {
        if (string != null && !string.isEmpty()) {
            String id = string.split("-")[0];
            string = string.replace(id + "-", "");
            String[] splits = string.split(",", -1);
            if (splits.length >= 7) {
                String start = splits[0];
                if (checkNull(start, "start can't be null!!")) {
                    return null;
                }
                String bound = splits[1];
                String fixedIntegerMax = splits[2];
                String fixedIntegerMin = splits[3];
                String fixedDecimalMax = splits[4];
                String fixedDecimalMin = splits[5];
                String decimal = splits[6];
                return new NumberSection(id, start, bound, fixedIntegerMax, fixedIntegerMin, fixedDecimalMax, fixedDecimalMin, decimal);
            }
        }
        return null;
    }
}
