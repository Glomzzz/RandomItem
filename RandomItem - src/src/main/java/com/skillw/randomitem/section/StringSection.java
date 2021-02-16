package com.skillw.randomitem.section;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import com.skillw.randomitem.api.section.weight.Weighable;
import com.skillw.randomitem.api.section.weight.WeightRandom;
import com.skillw.randomitem.section.type.StringType;
import com.skillw.randomitem.string.SubString;
import com.skillw.randomitem.utils.Utils;
import com.skillw.randomitem.weight.WeightRandomImpl;
import io.izzel.taboolib.internal.gson.Gson;
import io.izzel.taboolib.util.Pair;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName : com.skillw.randomitem.section.StringSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class StringSection extends BaseSection implements Weighable<SubString>, Debuggable {
    public StringSection(String id, List<SubString> subStringList) {
        super(id, new StringType(), new HashMap<String, Object>() {{
            this.put("values", subStringList);
        }});
    }

    public static SubString getWeightRandomString(List<SubString> subStrings, Player player) {
        List<Pair<SubString, Double>> pairs = new ArrayList<>();
        for (SubString subString : subStrings) {
            pairs.add(new Pair<>(subString, subString.getWeightValue(player)));
        }
        WeightRandom<SubString, Double> stringDoubleWeightRandom = new WeightRandomImpl<>(pairs);
        return stringDoubleWeightRandom.random();
    }

    @Override
    public List<String> handleSection(String replaced, ComplexData data) {
        ConcurrentHashMap<String, List<String>> alreadySectionMap = data.getAlreadySectionMap();
        if (alreadySectionMap.containsKey(this.getId())) {
            return alreadySectionMap.get(this.getId());
        }
        SubString subString = this.getWeightRandom(data.getPlayer());
        List<String> results = Utils.handleStringsReplaced(subString.getObjects(), data);
        results.add(new Gson().toJson(subString));
        return results;
    }

    @Override
    public BaseSection clone() {
        List<SubString> subStrings = new ArrayList<>();
        for (SubString subString : this.getObjects()) {
            subStrings.add(subString.clone());
        }
        return new StringSection(this.getId(), subStrings);
    }

    @Override
    public List<SubString> getObjects() {
        return (List<SubString>) this.get("values");
    }

    @Override
    public SubString getWeightRandom(Player player) {
        List<SubString> subStrings = this.getObjects();
        if (subStrings.size() == 1) {
            return subStrings.get(0);
        } else {
            return getWeightRandomString(subStrings, player);
        }
    }

    @Override
    public List<String> getDebugMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("&b " + "values" + " &5:");
        for (SubString subString : (List<SubString>) this.get("values")) {
            messages.add("  &d> &b" + subString.getId());
            for (String string : subString.getObjects()) {
                messages.add("   &d- &e" + string);
            }
        }
        return messages;
    }
}
