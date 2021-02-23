package com.skillw.randomitem.section;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import com.skillw.randomitem.section.type.NumberType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.skillw.randomitem.utils.CalculationUtils.getResult;
import static com.skillw.randomitem.utils.NumberUtils.getRandom;
import static com.skillw.randomitem.utils.StringUtils.replacePAPI;
import static com.skillw.randomitem.utils.Utils.replaceAll;

/**
 * @ClassName : com.skillw.randomitem.section.NumberSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class NumberSection extends BaseSection implements Debuggable {
    public NumberSection(String id, String start, String bound, String fixed, String decimal) {
        super(id, new NumberType(), new HashMap<String, Object>() {{
            this.put("start", start);
            this.put("bound", bound);
            this.put("fixed", (fixed == null || fixed.isEmpty()) ? "0" : fixed);
            this.put("decimal", (decimal == null || decimal.isEmpty()) ? "0" : decimal);
        }});
    }

    @Override
    public List<String> handleSection(String replaced, ComplexData data) {
        Player player = data.getPlayer();
        String value = replacePAPI(this.get("start") + "," + this.get("bound") + "," + this.get("fixed") + "," + this.get("decimal"), player);
        value = replaceAll(value, data);
        String[] splits = value.split(",");
        double start = (getResult(splits[0]));
        double bound = (getResult(splits[1]));
        int fixed = (int) getResult(splits[2]);
        int decimal = (int) getResult(splits[3]);
        String result;
        if (fixed == 0) {
            result = String.valueOf(getRandom((int) Math.round(start), (int) Math.round(bound)));
        } else {
            result = String.valueOf(getRandom(start, bound, fixed, decimal));
        }
        return Arrays.asList(result);
    }

    @Override
    public BaseSection clone() {
        return new NumberSection(this.id, String.valueOf(this.get("start")), String.valueOf(this.get("bound")), String.valueOf(this.get("fixed")), String.valueOf(this.get("decimal")));
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
