package com.skillw.randomitem.api.data;

import java.util.List;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.data.BasicData
 * @date 2021-02-09 16:18:38
 * Copyright  2020 user. All rights reserved.
 */
public abstract class BasicData<T> {
    private final String id;
    private final List<T> objects;
    private double weight;

    protected BasicData(String id, double weight, List<T> objects) {
        this.id = id;
        this.weight = weight;
        this.objects = objects;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getId() {
        return this.id;
    }

    public List<T> getObjects() {
        return this.objects;
    }

    /**
     * To get a clone of this.
     *
     * @return clone
     */
    @Override
    public abstract BasicData<T> clone();
}
