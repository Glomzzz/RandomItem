package com.skillw.randomitem.section;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import com.skillw.randomitem.section.type.StringType;
import com.skillw.randomitem.util.NumberUtils;
import com.skillw.randomitem.util.ProcessUtils;
import com.skillw.randomitem.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.skillw.randomitem.util.Utils.checkNull;

/**
 * @ClassName : com.skillw.randomitem.section.StringSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class LoreSection extends BaseSection implements Debuggable {
    public LoreSection(String id, List<String> values) {
        super(id, StringType.class, new HashMap<String, Object>() {{
            this.put("id", id);
            this.put("values", values);
        }});
        if (checkNull(values, "lores can't be null!!!  &eLore section: &6" + id)) {
            this.put("values", Arrays.asList("Null lores!"));
        }
    }

    @Override
    public String handleSection(String replaced, ComplexData data) {
        List<String> values = ProcessUtils.replaceAll((List<String>) this.map.get("values"), data);
        String result;
        try {
            int random = NumberUtils.getRandom(0, values.size() - 1);
            result = values.get(random);
        } catch (Exception e) {
            result = null;
        }
        if (checkNull(result, "Please check the lore section: &6" + this.id)) {
            return "wrong lore section!";
        }
        data.getAlreadySectionMap().putIfAbsent(this.getId() + ".json", StringUtils.toJson(this.getDataMap()));
        return result;
    }

    @Override
    public BaseSection clone() {
        return new LoreSection(this.getId(), (List<String>) this.map.get("values"));
    }

    @Override
    public List<String> getDebugMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("&b " + "lores" + " &5:");
        for (String text : (List<String>) this.map.get("values")) {
            messages.add("&d     &e" + StringUtils.messageToText(text));
        }
        return messages;
    }
}
