package com.skillw.randomitem.section.type;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.type.BaseSectionType;
import com.skillw.randomitem.section.ScriptSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

import static com.skillw.randomitem.utils.Utils.checkNull;

/**
 * @ClassName : com.skillw.randomitem.section.type.StringType
 * Created by Glom_ on 2021-02-09 09:37:48
 * Copyright  2020 user. All rights reserved.
 */
public class ScriptType extends BaseSectionType {
    public ScriptType() {
        super("javascript", Arrays.asList("script", "js"));
    }

    @Override
    protected BaseSection loadFromSection(ConfigurationSection section) {
        String script = section.getString("script");
        checkNull(script, "script can't be null!!!");
        return new ScriptSection(section.getName(), script);
    }

    @Override
    protected BaseSection loadFromSectionSimply(String string) {
        if (string != null && !string.isEmpty()) {
            String id = string.split("-")[0];
            string = string.replace(id + "-", "");
            checkNull(string, "script can't be null!!!");
            return new ScriptSection(id, string);
        }
        return null;
    }
}
