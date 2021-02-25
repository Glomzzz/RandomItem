package com.skillw.randomitem.util;

import com.skillw.randomitem.api.data.BasicData;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.weight.Weighable;
import com.skillw.randomitem.weight.WeightRandom;
import io.izzel.taboolib.util.Pair;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.util.StringUtils.*;

/**
 * @ClassName : com.skillw.randomitem.util.ProcessUtils
 * Created by Glom_ on 2021-02-25 14:54:24
 * Copyright  2020 user. All rights reserved.
 */
public final class ProcessUtils {
    private ProcessUtils() {

    }

    public static void handlePointData(String pointData,
                                       ComplexData data) {
        ConcurrentHashMap<String, BaseSection> sectionMap = data.getSectionMap();
        ConcurrentHashMap<String, String> alreadySectionMap = data.getAlreadySectionMap();
        Player player = data.getPlayer();
        pointData = pointData.replace("[", "").replace("]", "").replace(" ", "");
        for (String text : pointData.split(",")) {
            if (text != null && !text.isEmpty()) {
                if (text.contains(":")) {
                    String id = text.split(":")[0];
                    String value = text.split(":")[1];
                    Set<String> values = new HashSet<>();
                    if (value.contains(";")) {
                        values.addAll(Arrays.asList(value.split(";")));
                    } else {
                        values.add(value);
                    }
                    if (sectionMap.containsKey(id)) {
                        List<BaseSection> sections = new ArrayList<>(sectionMap.values());
                        sections.forEach(section -> {
                            if (section.getId().equals(id)) {
                                if (section instanceof Weighable) {
                                    //在这里硬性规定了
                                    // 实现Weighable的BaseSection子类
                                    // 其实例的DataMap中必须存在一个"value-map",HashMap<String,BasicData<?>>
                                    // 所以会直接强转
                                    ConcurrentHashMap<String, BasicData<?>> basicDataMap = new ConcurrentHashMap<>();
                                    {
                                        ConcurrentHashMap<String, BasicData<?>> originalMap = (ConcurrentHashMap<String, BasicData<?>>) section.get("value-map");
                                        for (String key : originalMap.keySet()) {
                                            basicDataMap.put(key, originalMap.get(key).clone());
                                        }
                                    }
                                    for (String key : basicDataMap.keySet()) {
                                        if (!values.contains(key)) {
                                            basicDataMap.remove(key);
                                        }
                                    }
                                    section.put("value-map", basicDataMap);
                                    section.load(id, data);
                                } else {
                                    //因为singletonList返回的列表容量始终为1
                                    //且需保证后面对此列表的操作可以正常进行
                                    // 故用asList
                                    alreadySectionMap.put(id, value);
                                }
                            }
                        });
                    } else {
                        ConfigUtils.sendValidIdMessage(player, id);
                    }
                }
            }
        }
    }

    public static BasicData<?> getRandomBasicDataBaseOnWeight(ConcurrentHashMap<String, BasicData<?>> valueMap, Player player) {
        if (valueMap.size() == 1) {
            return new ArrayList<>(valueMap.values()).get(0);
        }
        List<Pair<BasicData<?>, Double>> pairs = new ArrayList<>();
        for (BasicData<?> basicData : valueMap.values()) {
            pairs.add(new Pair<>(basicData, basicData.getWeightValue(player)));
        }
        WeightRandom<BasicData<?>, Double> stringDoubleWeightRandom = new WeightRandom<>(pairs);
        return stringDoubleWeightRandom.random();
    }

    public static List<String> replaceAll(List<String> strings, ComplexData data) {
        for (int i = 0; i < strings.size(); i++) {
            String value = replaceAll(strings.get(i), data);
            strings.set(i, value);
        }
        return strings;
    }

    public static String replaceAll(String string, ComplexData data) {
        string = handleStringReplaced(string, data);
        return deleteSlashes(getMessage(replacePAPI(string, data.getPlayer())));
    }


}
