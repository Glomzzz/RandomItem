package com.skillw.randomitem.util;

import com.skillw.randomitem.api.data.BasicData;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.weight.WeightRandom;
import com.skillw.randomitem.weight.WeightRandomImpl;
import io.izzel.taboolib.util.Pair;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
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

    public static BasicData<?> getRandomBasicDataBaseOnWeight(ConcurrentHashMap<String, BasicData<?>> valueMap, Player player) {
        if (valueMap.size() == 1) {
            return new ArrayList<>(valueMap.values()).get(0);
        }
        List<Pair<BasicData<?>, Double>> pairs = new ArrayList<>();
        for (BasicData<?> basicData : valueMap.values()) {
            pairs.add(new Pair<>(basicData, basicData.getWeightValue(player)));
        }
        WeightRandom<BasicData<?>, Double> stringDoubleWeightRandom = new WeightRandomImpl<>(pairs);
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
