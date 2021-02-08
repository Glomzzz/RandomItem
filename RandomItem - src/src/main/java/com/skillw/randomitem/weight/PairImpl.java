package com.skillw.randomitem.weight;

import com.skillw.randomitem.api.object.Pair;

import java.util.Objects;

/**
 * @ClassName : com.skillw.randomitem.api.object.Pair
 * Created by Glom_ on 2021-02-04 00:31:53
 * Copyright  2020 user. All rights reserved.
 */
public class PairImpl<K, V> implements Pair<K, V> {
    private K key;
    private V value;

    public PairImpl(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Pair{" + this.key + "," + this.value + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PairImpl)) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return this.getKey().equals(pair.getKey()) && this.getValue().equals(pair.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getKey(), this.getValue());
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public void setKey(K key) {
        this.key = key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public void setValue(V value) {
        this.value = value;
    }
}
