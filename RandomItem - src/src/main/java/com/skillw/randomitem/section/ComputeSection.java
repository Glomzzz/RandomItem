package com.skillw.randomitem.section;

import com.skillw.randomitem.api.ComplexData;
import com.skillw.randomitem.api.debuggable.Debuggable;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.section.type.ComputeType;
import com.skillw.randomitem.utils.RandomItemUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName : com.skillw.randomitem.section.ComputeSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class ComputeSection extends BaseSection implements Debuggable {
    public ComputeSection(String id, String max, String fixed, String formula) {
        super(id, new ComputeType(), new HashMap<String, Object>() {{
            this.put("max", max == null ? -1 : max);
            this.put("fixed", fixed == null ? 0 : fixed);
            this.put("formula", formula);
        }});
    }

    @Override
    public String handleSection(String replaced, ComplexData data) {
        ConcurrentHashMap<String, List<String>> alreadySectionMap = data.getAlreadySectionMap();
        Player player = data.getPlayer();
        if (alreadySectionMap.containsKey(this.getId())) {
            return RandomItemUtils.listToStringWithNext(alreadySectionMap.get(this.getId()));
        }
        String value = RandomItemUtils.replacePAPI(this.get("max") + "," + this.get("fixed") + "," + this.get("formula"), player);
        value = RandomItemUtils.handleStringReplaced(value, data);
        double max = (RandomItemUtils.getResult(value.split(",")[0]));
        int fixed = (int) (RandomItemUtils.getResult(value.split(",")[1]));
        String formula = value.split(",")[2];
        double doubleResult = RandomItemUtils.getResult(formula);
        String result;
        double number = max == -1 ? doubleResult : Math.min(max, doubleResult);
        if (fixed == 0) {
            result = String.valueOf(Math.round(number));
        } else {
            result = String.valueOf(RandomItemUtils.format(number, fixed));
        }
        alreadySectionMap.put(this.getId(), Arrays.asList(result));
        return result;
    }

    @Override
    public List<String> getDebugMessages() {
        List<String> messages = new ArrayList<>();
        for (String key : this.getDataMap().keySet()) {
            messages.add(" &b" + key + " &5= &e" + this.get(key));
        }
        return messages;
    }

    @Override
    public BaseSection clone() {
        return new ComputeSection(this.id, String.valueOf(this.get("max")), String.valueOf(this.get("fixed")), String.valueOf(this.get("formula")));
    }
}
