package com.skillw.randomitem.api.object;

/**
 * @ClassName : com.skillw.randomitem.api.object.Pair
 * Created by Glom_ on 2021-02-04 17:23:26
 * Copyright  2020 user. All rights reserved.
 */
public interface Pair<K, V> {
    @Override
    String toString();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    K getKey();

    void setKey(K key);

    V getValue();

    void setValue(V value);
}
