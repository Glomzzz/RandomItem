package com.skillw.randomitem.section.type;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.type.BaseSectionType;
import com.skillw.randomitem.section.ComputeSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

import static com.skillw.randomitem.utils.Utils.checkNull;

/**
 * @ClassName : com.skillw.randomitem.section.type.ComputeType
 * Created by Glom_ on 2021-02-09 09:37:48
 * Copyright  2020 user. All rights reserved.
 */
public class ComputeType extends BaseSectionType {
    public ComputeType() {
        super("compute", Arrays.asList("c"));
    }

    @Override
    protected BaseSection loadFromSection(ConfigurationSection section) {
        if (section != null) {
            String max = section.getString("max");
            String fixed = section.getString("fixed");
            String formula = section.getString("formula");
            if (checkNull(formula, "formula can't be null!!")) {
                return null;
            }
            return new ComputeSection(section.getName(), formula, max, fixed);
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
                String fixed = splits[2];
                return new ComputeSection(id, formula, max, fixed);
            }
        }
        return null;
    }
}
