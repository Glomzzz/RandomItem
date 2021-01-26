package com.skillw.randomitem;

import com.google.common.base.Preconditions;
import javafx.util.Pair;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Glom_
 */
public class WeightRandom<K, V extends Number> {
    private final TreeMap<Double, K> weightMap = new TreeMap<>();

    public WeightRandom(List<Pair<K, V>> list) {
        Preconditions.checkNotNull(list, "List不能是null哦");
        for (Pair<K, V> pair : list) {
            double lastWeight = this.weightMap.size() == 0 ? 0 : this.weightMap.lastKey();
            this.weightMap.put(pair.getValue().doubleValue() + lastWeight, pair.getKey());
        }
    }

    public K random() {
        double randomWeight = this.weightMap.lastKey() * Math.random();
        SortedMap<Double, K> tailMap = this.weightMap.tailMap(randomWeight, false);
        return this.weightMap.get(tailMap.firstKey());
    }

}
