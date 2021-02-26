package com.skillw.randomitem.section.type;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.type.BaseSectionType;
import com.skillw.randomitem.section.StringSection;
import com.skillw.randomitem.string.SubString;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
    protected BaseSection loadFromSection(ConfigurationSection section) {
        ConfigurationSection subSection = section.getConfigurationSection("strings");
        if (subSection != null) {
            ConcurrentHashMap<String, SubString> valueMap = new ConcurrentHashMap<>();
            for (String subStringKey : subSection.getKeys(false)) {
                ConfigurationSection subStringSection = subSection.getConfigurationSection(subStringKey);
                String weight = subStringSection.getString("weight");
                List<String> strings = subStringSection.getStringList("values");
                if (!strings.isEmpty()) {
                    valueMap.put(subStringKey, Main.getRandomItemAPI().createSubString(subStringKey, weight, strings));
                }
            }
            return new StringSection(section.getName(), valueMap);
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
                ConcurrentHashMap<String, SubString> valueMap = new ConcurrentHashMap<>();
                for (String sub : splits) {
                    String[] subSplits = sub.split(";");
                    if (subSplits.length > 0) {
                        String subId = subSplits[0];
                        String weight = subSplits[1];
                        String value = subSplits[2];
                        valueMap.put(subId, new SubString(subId, weight, value));
                    }
                }
                return new StringSection(id, valueMap);
            }
        }
        return null;
    }
}
