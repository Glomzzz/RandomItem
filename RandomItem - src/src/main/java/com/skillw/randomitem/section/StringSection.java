package com.skillw.randomitem.section;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import com.skillw.randomitem.api.section.weight.Weighable;
import com.skillw.randomitem.section.type.StringType;
import com.skillw.randomitem.string.SubString;
import com.skillw.randomitem.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.util.ProcessUtils.replaceAll;

/**
 * @ClassName : com.skillw.randomitem.section.StringSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class StringSection extends BaseSection implements Weighable<SubString>, Debuggable {
    public StringSection(String id, ConcurrentHashMap<String, SubString> valueMap) {
        super(id, StringType.class, new HashMap<String, Object>() {{
            this.put("id", id);
            this.put("value-map", valueMap);
        }});
    }

    @Override
    public String handleSection(String replaced, ComplexData data) {
        SubString subString = (SubString) Weighable.getWeightRandom(this, data.getPlayer());
        String result = replaceAll(StringUtils.listToStringWithNext(subString.getObjects()), data);
        data.getAlreadySectionMap().putIfAbsent(this.getId() + ".json", StringUtils.toJson(this.getDataMap()));
        data.getAlreadySectionMap().putIfAbsent(this.getId() + ".data-json", StringUtils.toJson(subString));
        return result;
    }

    @Override
    public BaseSection clone() {
        return new StringSection(this.getId(), this.getValueMapClone());
    }

    @Override
    public ConcurrentHashMap<String, SubString> getValueMapClone() {
        ConcurrentHashMap<String, SubString> valueMap = new ConcurrentHashMap<>();
        for (SubString subString : this.getOriginalValueMap().values()) {
            valueMap.put(subString.getId(), subString.clone());
        }
        return valueMap;
    }

    private ConcurrentHashMap<String, SubString> getOriginalValueMap() {
        return ((ConcurrentHashMap<String, SubString>) this.get("value-map"));
    }

    @Override
    public List<String> getDebugMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("&b " + "values" + " &5:");
        for (SubString subString : this.getOriginalValueMap().values()) {
            messages.add("  &d> &b" + subString.getId());
            for (String string : subString.getObjects()) {
                messages.add("   &d- &e" + string);
            }
        }
        return messages;
    }
}
