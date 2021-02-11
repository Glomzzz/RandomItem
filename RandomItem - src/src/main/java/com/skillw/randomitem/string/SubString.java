package com.skillw.randomitem.string;

import com.skillw.randomitem.api.data.BasicData;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName : com.skillw.randomitem.string.SubString
 * Created by Glom_ on 2021-02-04 11:53:08
 * Copyright  2020 user. All rights reserved.
 */
public class SubString extends BasicData<String> {

    public SubString(String id, double weight, List<String> strings) {
        super(id, weight, strings);
    }

    @Override
    public SubString clone() {
        return new SubString(this.getId(), this.getWeight(), new ArrayList<>(this.getObjects()));
    }
}
