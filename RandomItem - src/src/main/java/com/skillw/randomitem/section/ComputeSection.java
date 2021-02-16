package com.skillw.randomitem.section;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import com.skillw.randomitem.section.type.ComputeType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.utils.CalculationUtils.getResult;
import static com.skillw.randomitem.utils.NumberUtils.format;
import static com.skillw.randomitem.utils.StringUtils.replacePAPI;
import static com.skillw.randomitem.utils.Utils.handleStringReplaced;

/**
 * @ClassName : com.skillw.randomitem.section.ComputeSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class ComputeSection extends BaseSection implements Debuggable {
    public ComputeSection(String id, String formula, String max, String fixed) {
        super(id, new ComputeType(), new HashMap<String, Object>() {{
            this.put("formula", formula);
            this.put("max", max == null || max.isEmpty() ? -1 : max);
            this.put("fixed", fixed == null || fixed.isEmpty() ? 0 : fixed);
        }});
    }

    @Override
    public List<String> handleSection(String replaced, ComplexData data) {
        ConcurrentHashMap<String, List<String>> alreadySectionMap = data.getAlreadySectionMap();
        Player player = data.getPlayer();
        if (alreadySectionMap.containsKey(this.getId())) {
            return alreadySectionMap.get(this.getId());
        }
        String value = replacePAPI(this.get("formula") + "," + this.get("max") + "," + this.get("fixed"), player);
        value = handleStringReplaced(value, data);
        String[] splits = value.split(",");
        String formula = splits[0];
        double max = (getResult(splits[1]));
        int fixed = (int) (getResult(splits[2]));
        double doubleResult = getResult(formula);
        String result;
        double number = max == -1 ? doubleResult : Math.min(max, doubleResult);
        if (fixed == 0) {
            result = String.valueOf((int) Math.round(number));
        } else {
            result = String.valueOf(format(number, fixed));
        }
        return Arrays.asList(result);
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
        return new ComputeSection(this.id, String.valueOf(this.get("formula")), String.valueOf(this.get("max")), String.valueOf(this.get("fixed")));
    }
}
