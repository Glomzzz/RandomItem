package com.skillw.randomitem.section;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import com.skillw.randomitem.section.type.NumberType;
import com.skillw.randomitem.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.skillw.randomitem.util.CalculationUtils.getResult;
import static com.skillw.randomitem.util.NumberUtils.format;
import static com.skillw.randomitem.util.NumberUtils.getRandom;
import static com.skillw.randomitem.util.ProcessUtils.replaceAll;
import static com.skillw.randomitem.util.Utils.checkNull;

/**
 * @ClassName : com.skillw.randomitem.section.NumberSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class NumberSection extends BaseSection implements Debuggable {
    public NumberSection(String id, String start, String bound, String fixedIntegerMax, String fixedIntegerMin, String fixedDecimalMax, String fixedDecimalMin, String decimal) {
        super(id, NumberType.class, new HashMap<String, Object>() {{
            this.put("id", id);
            this.put("start", start);
            this.put("bound", bound);
            this.put("fixed.integer.max", (fixedIntegerMax == null || fixedIntegerMax.isEmpty() || "null".equals(fixedIntegerMax)) ? "-1" : fixedIntegerMax);
            this.put("fixed.integer.min", (fixedIntegerMin == null || fixedIntegerMin.isEmpty() || "null".equals(fixedIntegerMin)) ? "0" : fixedIntegerMin);
            this.put("fixed.decimal.max", (fixedDecimalMax == null || fixedDecimalMax.isEmpty() || "null".equals(fixedDecimalMax)) ? "0" : fixedDecimalMax);
            this.put("fixed.decimal.min", (fixedDecimalMin == null || fixedDecimalMin.isEmpty() || "null".equals(fixedDecimalMin)) ? "0" : fixedDecimalMin);
            this.put("decimal", (decimal == null || decimal.isEmpty() || "null".equals(decimal)) ? "0" : decimal);
        }});
        if (checkNull(this.get("start"), "start can't be null!! &eNumber section: &6" + id)) {
            this.put("start", "0");
        }
        if (checkNull(this.get("bound"), "bound can't be null!! &eNumber section: &6" + id)) {
            this.put("bound", "0");
        }
    }

    public NumberSection(String id, HashMap<String, Object> dataMap) {
        super(id, NumberType.class, dataMap);
        this.put("id", id);
        String fixedIntegerMax = String.valueOf(this.get("fixed.integer.max"));
        String fixedIntegerMin = String.valueOf(this.get("fixed.integer.min"));
        String fixedDecimalMax = String.valueOf(this.get("fixed.decimal.max"));
        String fixedDecimalMin = String.valueOf(this.get("fixed.decimal.min"));
        String decimal = String.valueOf(this.get("decimal"));
        this.put("fixed.integer.max", (fixedIntegerMax == null || fixedIntegerMax.isEmpty() || "null".equals(fixedIntegerMax)) ? "-1" : fixedIntegerMax);
        this.put("fixed.integer.min", (fixedIntegerMin == null || fixedIntegerMin.isEmpty() || "null".equals(fixedIntegerMin)) ? "0" : fixedIntegerMin);
        this.put("fixed.decimal.max", (fixedDecimalMax == null || fixedDecimalMax.isEmpty() || "null".equals(fixedDecimalMax)) ? "0" : fixedDecimalMax);
        this.put("fixed.decimal.min", (fixedDecimalMin == null || fixedDecimalMin.isEmpty() || "null".equals(fixedDecimalMin)) ? "0" : fixedDecimalMin);
        this.put("decimal", (decimal == null || decimal.isEmpty() || "null".equals(decimal)) ? "0" : decimal);
    }

    @Override
    public String handleSection(String replaced, ComplexData data) {
        String value = replaceAll(
                StringUtils.toJson(this.getDataMap())
                , data);
        HashMap<String, Object> dataMap = Main.getGson().fromJson(value, HashMap.class);
        double start = getResult(String.valueOf(dataMap.get("start")));
        double bound = getResult(String.valueOf(dataMap.get("bound")));
        int fixedIntegerMax = (int) getResult(String.valueOf(dataMap.get("fixed.integer.max")));
        int fixedIntegerMin = (int) getResult(String.valueOf(dataMap.get("fixed.integer.min")));
        int fixedDecimalMax = (int) getResult(String.valueOf(dataMap.get("fixed.decimal.max")));
        int fixedDecimalMin = (int) getResult(String.valueOf(dataMap.get("fixed.decimal.min")));
        int decimal = (int) getResult(String.valueOf(dataMap.get("decimal")));
        String result;
        fixedDecimalMax = Math.max(fixedDecimalMax, fixedDecimalMin);
        if (fixedIntegerMax != -1) {
            int temp = Math.max(fixedIntegerMax, fixedIntegerMin);
            fixedIntegerMax = temp == 0 ? fixedIntegerMax : temp;
        }
        if (fixedDecimalMax == 0) {
            result = String.valueOf((int) format(getRandom((int) Math.round(start), (int) Math.round(bound)), fixedIntegerMax, fixedIntegerMin, 0, 0));
        } else {
            result = String.valueOf(getRandom(start, bound, fixedIntegerMax, fixedIntegerMin, fixedDecimalMax, fixedDecimalMin, decimal));
        }
        data.getAlreadySectionMap().putIfAbsent(this.getId() + ".json", value);
        return result;
    }

    @Override
    public BaseSection clone() {
        return new NumberSection(this.id,
                String.valueOf(this.get("start")),
                String.valueOf(this.get("bound")),
                String.valueOf(this.get("fixed.integer.max")),
                String.valueOf(this.get("fixed.integer.min")),
                String.valueOf(this.get("fixed.decimal.max")),
                String.valueOf(this.get("fixed.decimal.min")),
                String.valueOf(this.get("decimal")));
    }

    @Override
    public List<String> getDebugMessages() {
        List<String> messages = new ArrayList<>();
        this.getDataMap().keySet().stream()
                .sorted(Comparator.reverseOrder())
                .sorted((s1, s2) -> {
                    int num = Integer.compare(s1.length(), s2.length());
                    if (num == 0) {
                        return s1.compareTo(s2);
                    }
                    return num;
                })
                .forEach(key -> messages.add(" &b" + key + " &5= &e" + this.get(key)));
        return messages;
    }
}
