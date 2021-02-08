package com.skillw.randomitem.api.object;

import java.util.List;

/**
 * @ClassName : com.skillw.randomitem.api.object.SubString
 * Created by Glom_ on 2021-02-04 17:20:35
 * Copyright  2020 user. All rights reserved.
 */
public interface SubString {
    String getID();

    double getWeight();

    void setWeight(double weight);

    List<String> getStrings();

    void setStrings(List<String> strings);

    SubString clone();
}
