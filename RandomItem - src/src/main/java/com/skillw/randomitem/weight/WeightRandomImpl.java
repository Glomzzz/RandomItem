package com.skillw.randomitem.weight;

import com.skillw.randomitem.api.section.weight.WeightRandom;
import com.skillw.randomitem.utils.Utils;
import io.izzel.taboolib.util.Pair;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Glom_
 */
public class WeightRandomImpl<K, V extends Number> implements WeightRandom<K, V> {
    private final TreeMap<Double, K> weightMap = new TreeMap<>();

    public WeightRandomImpl(List<Pair<K, V>> list) {
        if (Utils.checkNull(list, "Weight List can't be null!")) {
            return;
        }
        for (Pair<K, V> pair : list) {
            double lastWeight = this.weightMap.size() == 0 ? 0 : this.weightMap.lastKey();
            this.weightMap.put(pair.getValue().doubleValue() + lastWeight, pair.getKey());
        }
    }

    @Override
    public K random() {
        double randomWeight = this.weightMap.lastKey() * Math.random();
        SortedMap<Double, K> tailMap = this.weightMap.tailMap(randomWeight, false);
        return this.weightMap.get(tailMap.firstKey());
    }

}
