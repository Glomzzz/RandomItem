package com.skillw.randomitem.section.type;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.type.BaseSectionType;
import com.skillw.randomitem.section.ComputeSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

import static com.skillw.randomitem.utils.RandomItemUtils.checkNull;

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
    protected BaseSection loadFromSection(ConfigurationSection config) {
        if (config != null) {
            String max = config.getString("max");
            String fixed = config.getString("fixed");
            String formula = config.getString("formula");
            if (checkNull(formula, "formula can't be null!!")) {
                return null;
            }
            return new ComputeSection(config.getName(), max, fixed, formula);
        }
        return null;
    }
}
