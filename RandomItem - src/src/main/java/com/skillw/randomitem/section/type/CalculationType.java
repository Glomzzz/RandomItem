package com.skillw.randomitem.section.type;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.type.BaseSectionType;
import com.skillw.randomitem.section.CalculationSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.HashMap;

import static com.skillw.randomitem.utils.ConfigUtils.getMapFromConfigSection;
import static com.skillw.randomitem.utils.Utils.checkNull;

/**
 * @ClassName : com.skillw.randomitem.section.type.CalculationType
 * Created by Glom_ on 2021-02-09 09:37:48
 * Copyright  2020 user. All rights reserved.
 */
public class CalculationType extends BaseSectionType {
    public CalculationType() {
        super("calculation", Arrays.asList("compute", "c"));
    }

    @Override
    protected BaseSection loadFromSection(ConfigurationSection section) {
        if (section != null) {
            HashMap<String, Object> dataMap = getMapFromConfigSection(section, null);
            if (checkNull(dataMap.get("formula"), "formula can't be null!!")) {
                return null;
            }
            return new CalculationSection(section.getName(), dataMap);
        }
        return null;
    }

    @Override
    protected BaseSection loadFromSectionSimply(String string) {
        if (string != null && !string.isEmpty()) {
            String id = string.split("-")[0];
            string = string.replace(id + "-", "");
            String[] splits = string.split(",");
            if (splits.length > 0) {
                String formula = splits[0];
                String max = splits[1];
                String fixedIntegerMax = splits[2];
                String fixedIntegerMin = splits[3];
                String fixedDecimalMax = splits[4];
                String fixedDecimalMin = splits[5];
                return new CalculationSection(id, formula, max, fixedIntegerMax, fixedIntegerMin, fixedDecimalMax, fixedDecimalMin);
            }
        }
        return null;
    }
}
