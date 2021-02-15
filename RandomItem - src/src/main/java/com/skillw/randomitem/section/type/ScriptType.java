package com.skillw.randomitem.section.type;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.type.BaseSectionType;
import com.skillw.randomitem.section.ScriptSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        List<String> scripts = null;
        Object object = section.get("script");
        if (object instanceof String) {
            scripts = new ArrayList<>();
            Collections.addAll(scripts, ((String) object).split("\n"));
        } else if (object instanceof List<?>) {
            if (((List<?>) object).get(0) instanceof String) {
                scripts = (List<String>) object;
            }
        }
        checkNull(scripts, "script can't be null!!!");
        return new ScriptSection(section.getName(), scripts);
    }

    @Override
    protected BaseSection loadFromSectionSimply(String string) {
        if (string != null && !string.isEmpty()) {
            String id = string.split("-")[0];
            string = string.replace(id + "-", "");
            List<String> scripts = new ArrayList<>();
            Collections.addAll(scripts, string.split("\n"));
            checkNull(string, "script can't be null!!!");
            return new ScriptSection(id, scripts);
        }
        return null;
    }
}
