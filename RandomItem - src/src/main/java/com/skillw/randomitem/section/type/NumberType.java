package com.skillw.randomitem.section.type;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.type.BaseSectionType;
import com.skillw.randomitem.section.NumberSection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.skillw.randomitem.utils.Utils.checkNull;

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
            String start = section.getString("start");
            if (checkNull(start, "start can't be null!!")) {
                return null;
            }
            String bound = section.getString("bound");
            if (checkNull(bound, "bound can't be null!!")) {
                return null;
            }
            String fixed = section.getString("fixed");
            String decimal = section.getString("decimal");
            return new NumberSection(section.getName(), start, bound, fixed, decimal);
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
                String start = splits[0];
                if (checkNull(start, "start can't be null!!")) {
                    return null;
                }
                String bound = splits[1];
                String fixed = splits[2];
                String decimal = splits[3];
                if (checkNull(bound, "bound can't be null!!")) {
                    return null;
                }
                return new NumberSection(id, start, bound, fixed, decimal);
            }
        }
        return null;
    }
}
