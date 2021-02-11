package com.skillw.randomitem.section.type;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.type.BaseSectionType;
import com.skillw.randomitem.section.StringSection;
import com.skillw.randomitem.string.SubString;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName : com.skillw.randomitem.section.type.StringType
 * Created by Glom_ on 2021-02-09 09:37:48
 * Copyright  2020 user. All rights reserved.
 */
public class StringType extends BaseSectionType {
    public StringType() {
        super("strings", Arrays.asList("s", "string", "text"));
    }

    @Override
    protected BaseSection loadFromSection(ConfigurationSection config) {
        ConfigurationSection subSection = config.getConfigurationSection("strings");
        if (subSection != null) {
            List<SubString> subStrings = new ArrayList<>();
            for (String subStringKey : subSection.getKeys(false)) {
                ConfigurationSection subStringSection = subSection.getConfigurationSection(subStringKey);
                int weight = subStringSection.getInt("weight");
                List<String> strings = subStringSection.getStringList("values");
                if (!strings.isEmpty()) {
                    subStrings.add(Main.getRandomItemAPI().createSubString(subStringKey, weight, strings));
                }
            }
            return new StringSection(config.getName(), subStrings);
        }
        return null;
    }
}
