package com.skillw.randomitem.section;

import com.skillw.randomitem.api.ComplexData;
import com.skillw.randomitem.api.debuggable.Debuggable;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.section.type.NumberType;
import com.skillw.randomitem.utils.RandomItemUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName : com.skillw.randomitem.section.NumberSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class NumberSection extends BaseSection implements Debuggable {
    public NumberSection(String id, String fixed, String start, String bound) {
        super(id, new NumberType(), new HashMap<String, Object>() {{
            this.put("start", start);
            this.put("bound", bound);
            this.put("fixed", fixed == null ? 0 : fixed);
        }});
    }

    @Override
    public String handleSection(String replaced, ComplexData data) {
        ConcurrentHashMap<String, List<String>> alreadySectionMap = data.getAlreadySectionMap();
        Player player = data.getPlayer();
        if (alreadySectionMap.containsKey(this.getId())) {
            return RandomItemUtils.listToStringWithNext(alreadySectionMap.get(this.getId()));
        }
        String value = RandomItemUtils.replacePAPI(this.get("start") + "," + this.get("bound") + "," + this.get("fixed"), player);
        value = RandomItemUtils.handleStringReplaced(value, data);
        double start = (RandomItemUtils.getResult(value.split(",")[0]));
        double bound = (RandomItemUtils.getResult(value.split(",")[1]));
        int fixed = Integer.parseInt(value.split(",")[2]);
        String result = String.valueOf(RandomItemUtils.getRandom(start, bound, fixed));
        alreadySectionMap.put(this.getId(), Arrays.asList(result));
        return result;
    }

    @Override
    public BaseSection clone() {
        return new NumberSection(this.id, String.valueOf(this.get("fixed")), String.valueOf(this.get("start")), String.valueOf(this.get("bound")));
    }

    @Override
    public List<String> getDebugMessages() {
        List<String> messages = new ArrayList<>();
        for (String key : this.getDataMap().keySet()) {
            messages.add(" &b" + key + " &5= &e" + this.get(key));
        }
        return messages;
    }
}
