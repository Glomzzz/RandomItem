package com.skillw.randomitem.section;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import com.skillw.randomitem.section.type.CalculationType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.utils.CalculationUtils.getResult;
import static com.skillw.randomitem.utils.NumberUtils.format;
import static com.skillw.randomitem.utils.Utils.replaceAll;

/**
 * @ClassName : com.skillw.randomitem.section.CalculationSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class CalculationSection extends BaseSection implements Debuggable {
    public CalculationSection(String id, String formula, String max, String fixedIntegerMax, String fixedIntegerMin, String fixedDecimalMax, String fixedDecimalMin) {
        super(id, CalculationType.class, new HashMap<String, Object>() {{
            this.put("formula", formula);
            this.put("max", max == null || max.isEmpty() ? -1 : max);
            this.put("fixed.integer.max", (fixedIntegerMax == null || fixedIntegerMax.isEmpty() || "null".equals(fixedIntegerMax)) ? "-1" : fixedIntegerMax);
            this.put("fixed.integer.min", (fixedIntegerMin == null || fixedIntegerMin.isEmpty() || "null".equals(fixedIntegerMin)) ? "0" : fixedIntegerMin);
            this.put("fixed.decimal.max", (fixedDecimalMax == null || fixedDecimalMax.isEmpty() || "null".equals(fixedDecimalMax)) ? "0" : fixedDecimalMax);
            this.put("fixed.decimal.min", (fixedDecimalMin == null || fixedDecimalMin.isEmpty() || "null".equals(fixedDecimalMin)) ? "0" : fixedDecimalMin);
        }});
    }

    public CalculationSection(String id, HashMap<String, Object> dataMap) {
        super(id, CalculationType.class, dataMap);
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
    public List<String> handleSection(String replaced, ComplexData data) {
        ConcurrentHashMap<String, LinkedList<String>> alreadySectionMap = data.getAlreadySectionMap();
        if (alreadySectionMap.containsKey(this.getId())) {
            return alreadySectionMap.get(this.getId());
        }
        String value = replaceAll(
                this.get("formula") + "," +
                        this.get("max") + "," +
                        this.get("fixed.integer.max") + "," +
                        this.get("fixed.integer.min") + "," +
                        this.get("fixed.decimal.max") + "," +
                        this.get("fixed.decimal.min")
                , data);
        String[] splits = value.split(",");
        String formula = splits[0];
        double max = getResult(splits[1]);
        int fixedIntegerMax = (int) getResult(splits[2]);
        int fixedIntegerMin = (int) getResult(splits[3]);
        int fixedDecimalMax = (int) getResult(splits[4]);
        int fixedDecimalMin = (int) getResult(splits[5]);
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
        return Arrays.asList(result);
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
