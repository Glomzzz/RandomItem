package com.skillw.randomitem.string;

import com.skillw.randomitem.api.data.BaseData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName : com.skillw.randomitem.string.SubString
 * Created by Glom_ on 2021-02-04 11:53:08
 * Copyright  2020 user. All rights reserved.
 */
public class SubString extends BaseData<String> {

    public SubString(String id, String weight, List<String> strings) {
        super(id, weight == null || weight.isEmpty() ? "1" : weight, strings);
    }

    public SubString(String id, String weight, String stringWithNext) {
        super(id, weight == null || weight.isEmpty() ? "1" : weight, Arrays.asList(stringWithNext.split("\n")));
    }

    @Override
    public SubString clone() {
        return new SubString(this.getId(), this.getWeight(), new ArrayList<>(this.getObjects()));
    }
}
