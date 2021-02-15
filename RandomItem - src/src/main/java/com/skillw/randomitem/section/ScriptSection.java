package com.skillw.randomitem.section;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import com.skillw.randomitem.section.type.StringType;
import com.skillw.randomitem.utils.StringUtils;
import com.skillw.randomitem.utils.Utils;
import io.izzel.taboolib.util.Features;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.utils.StringUtils.listToStringWithNext;

/**
 * @ClassName : com.skillw.randomitem.section.StringSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class ScriptSection extends BaseSection implements Debuggable {
    public ScriptSection(String id, List<String> script) {
        super(id, new StringType(), new HashMap<String, Object>() {{
            this.put("script", script);
        }});
    }

    @Override
    public String handleSection(String replaced, ComplexData data) {
        ConcurrentHashMap<String, List<String>> alreadySectionMap = data.getAlreadySectionMap();
        if (alreadySectionMap.containsKey(this.getId())) {
            return listToStringWithNext(alreadySectionMap.get(this.getId()));
        }
        List<String> results = Utils.handleStringsReplaced(new ArrayList<>((List<String>) this.map.get("script")), data);
        String scriptString = "function run(){" + StringUtils.listToStringWithNext(results) + "}" + "run();";
        CompiledScript script = Features.compileScript(scriptString);
        try {
            return String.valueOf(script.eval());
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BaseSection clone() {
        return new ScriptSection(this.getId(), (List<String>) this.map.get("script"));
    }

    @Override
    public List<String> getDebugMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("&b " + "script" + " &5:");
        for (String text : (List<String>) this.get("script")) {
            messages.add("&d     &e" + text);
        }
        return messages;
    }
}
