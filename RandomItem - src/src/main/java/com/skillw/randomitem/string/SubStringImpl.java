package com.skillw.randomitem.string;

import com.skillw.randomitem.api.object.SubString;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName : com.skillw.randomitem.api.object.SubString
 * Created by Glom_ on 2021-02-04 11:53:08
 * Copyright  2020 user. All rights reserved.
 */
public class SubStringImpl implements SubString {
    private final String id;
    private double weight;
    private List<String> strings;

    public SubStringImpl(String id, double weight, List<String> strings) {
        this.id = id;
        this.weight = weight;
        this.strings = strings;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public List<String> getStrings() {
        return this.strings;
    }

    @Override
    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public SubString clone() {
        return new SubStringImpl(this.id, this.weight, new ArrayList<>(this.strings));
    }
}
