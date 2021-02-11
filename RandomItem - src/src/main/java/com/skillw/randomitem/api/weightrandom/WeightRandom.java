package com.skillw.randomitem.api.weightrandom;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.weightrandom.WeightRandom
 * @date 2021-02-11 02:35:28
 * Copyright  2020 user. All rights reserved.
 */
public interface WeightRandom<K, V extends Number> {
    /**
     * To get the result of weight random
     *
     * @return the result of weight random
     */
    K random();
}
