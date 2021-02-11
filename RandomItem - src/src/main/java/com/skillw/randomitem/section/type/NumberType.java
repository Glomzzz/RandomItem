package com.skillw.randomitem.section.type;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.type.BaseSectionType;
import com.skillw.randomitem.section.NumberSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

import static com.skillw.randomitem.utils.RandomItemUtils.checkNull;

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
    protected BaseSection loadFromSection(ConfigurationSection config) {
        if (config != null) {
            String start = config.getString("start");
            if (checkNull(start, "start can't be null!!")) {
                return null;
            }
            String bound = config.getString("bound");
            if (checkNull(bound, "bound can't be null!!")) {
                return null;
            }
            String fixed = config.getString("fixed");
            return new NumberSection(config.getName(), fixed, start, bound);
        }
        return null;
    }
}
