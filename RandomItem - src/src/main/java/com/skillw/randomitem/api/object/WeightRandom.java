package com.skillw.randomitem.api.object;

/**
 * @ClassName : com.skillw.randomitem.api.object.WeightRandom
 * Created by Glom_ on 2021-02-04 17:24:58
 * Copyright  2020 user. All rights reserved.
 */
public interface WeightRandom<K, V extends Number> {
    K random();
}
