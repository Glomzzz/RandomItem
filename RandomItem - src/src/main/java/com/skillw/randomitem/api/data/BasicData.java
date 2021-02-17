package com.skillw.randomitem.api.data;

import org.bukkit.entity.Player;

import java.util.List;

import static com.skillw.randomitem.utils.CalculationUtils.getResult;
import static com.skillw.randomitem.utils.StringUtils.replacePAPI;

/**
 * @author Glom_
 * @ClassName : com.skillw.randomitem.api.data.BasicData
 * @date 2021-02-09 16:18:38
 * Copyright  2020 user. All rights reserved.
 */
public abstract class BasicData<T> {
    private final String id;
    private final List<T> objects;
    private String weight;

    protected BasicData(String id, String weight, List<T> objects) {
        this.id = id;
        this.weight = weight;
        this.objects = objects;
    }

    public String getWeight() {
        return this.weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public double getWeightValue(Player player) {
        return getResult(replacePAPI(this.weight, player));
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
