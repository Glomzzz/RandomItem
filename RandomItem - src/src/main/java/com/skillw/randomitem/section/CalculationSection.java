package com.skillw.randomitem.section;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import com.skillw.randomitem.section.type.CalculationType;
import com.skillw.randomitem.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.skillw.randomitem.util.CalculationUtils.getResult;
import static com.skillw.randomitem.util.NumberUtils.format;
import static com.skillw.randomitem.util.ProcessUtils.replaceAll;
import static com.skillw.randomitem.util.Utils.checkNull;

/**
 * @ClassName : com.skillw.randomitem.section.CalculationSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class CalculationSection extends BaseSection implements Debuggable {
    public CalculationSection(String id, String formula, String max, String fixedIntegerMax, String fixedIntegerMin, String fixedDecimalMax, String fixedDecimalMin) {
        super(id, CalculationType.class, new HashMap<String, Object>() {{
            this.put("id", id);
            this.put("formula", formula);
            this.put("max", max == null || max.isEmpty() ? -1 : max);
            this.put("fixed.integer.max", (fixedIntegerMax == null || fixedIntegerMax.isEmpty() || "null".equals(fixedIntegerMax)) ? "-1" : fixedIntegerMax);
            this.put("fixed.integer.min", (fixedIntegerMin == null || fixedIntegerMin.isEmpty() || "null".equals(fixedIntegerMin)) ? "0" : fixedIntegerMin);
            this.put("fixed.decimal.max", (fixedDecimalMax == null || fixedDecimalMax.isEmpty() || "null".equals(fixedDecimalMax)) ? "0" : fixedDecimalMax);
            this.put("fixed.decimal.min", (fixedDecimalMin == null || fixedDecimalMin.isEmpty() || "null".equals(fixedDecimalMin)) ? "0" : fixedDecimalMin);
        }});
        if (checkNull(this.get("formula"), "formula can't be null!! &eCalculation section: &6" + id)) {
            this.put("formula", "114514");
        }
    }

    public CalculationSection(String id, HashMap<String, Object> dataMap) {
        super(id, CalculationType.class, dataMap);
        if (checkNull(this.get("formula"), "formula can't be null!! &eCalculation section: &6" + id)) {
            this.put("formula", "114514");
        }
        this.put("id", id);
        String fixedIntegerMax = String.valueOf(this.get("fixed.integer.max"));
        String fixedIntegerMin = String.valueOf(this.get("fixed.integer.min"));
        String fixedDecimalMax = String.valueOf(this.get("fixed.decimal.max"));
        String fixedDecimalMin = String.valueOf(this.get("fixed.decimal.min"));
        this.put("fixed.integer.max", (fixedIntegerMax == null || fixedIntegerMax.isEmpty() || "null".equals(fixedIntegerMax)) ? "-1" : fixedIntegerMax);
        this.put("fixed.integer.min", (fixedIntegerMin == null || fixedIntegerMin.isEmpty() || "null".equals(fixedIntegerMin)) ? "0" : fixedIntegerMin);
        this.put("fixed.decimal.max", (fixedDecimalMax == null || fixedDecimalMax.isEmpty() || "null".equals(fixedDecimalMax)) ? "0" : fixedDecimalMax);
        this.put("fixed.decimal.min", (fixedDecimalMin == null || fixedDecimalMin.isEmpty() || "null".equals(fixedDecimalMin)) ? "0" : fixedDecimalMin);
    }

    @Override
    public String handleSection(String replaced, ComplexData data) {
        String value = replaceAll(
                StringUtils.toJson(this.getDataMap())
                , data);
        HashMap<String, Object> dataMap = Main.getGson().fromJson(value, HashMap.class);
        String formula = String.valueOf(dataMap.get("formula"));
        double max = getResult(String.valueOf(dataMap.get("max")));
        int fixedIntegerMax = (int) getResult(String.valueOf(dataMap.get("fixed.integer.max")));
        int fixedIntegerMin = (int) getResult(String.valueOf(dataMap.get("fixed.integer.min")));
        int fixedDecimalMax = (int) getResult(String.valueOf(dataMap.get("fixed.decimal.max")));
        int fixedDecimalMin = (int) getResult(String.valueOf(dataMap.get("fixed.decimal.min")));
        double doubleResult = getResult(formula);
        String result;
        double number = max == -1 ? doubleResult : Math.min(max, doubleResult);
        fixedDecimalMax = Math.max(fixedDecimalMax, fixedDecimalMin);
        if (fixedIntegerMax != -1) {
            int temp = Math.max(fixedIntegerMax, fixedIntegerMin);
            fixedIntegerMax = temp == 0 ? fixedIntegerMax : temp;
        }
        if (fixedDecimalMax == 0) {
            result = String.valueOf((int) Math.round(format(number, fixedIntegerMax, fixedIntegerMin, 0, 0)));
        } else {
            result = String.valueOf(format(number, fixedIntegerMax, fixedIntegerMin, fixedDecimalMax, fixedDecimalMin));
        }
        data.getAlreadySectionMap().putIfAbsent(this.getId() + ".json", value);
        return result;
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

    @Override
    public BaseSection clone() {
        return new CalculationSection(this.id,
                String.valueOf(this.get("formula")),
                String.valueOf(this.get("max")),
                String.valueOf(this.get("fixed.integer.max")),
                String.valueOf(this.get("fixed.integer.min")),
                String.valueOf(this.get("fixed.decimal.max")),
                String.valueOf(this.get("fixed.decimal.min")));
    }
}
