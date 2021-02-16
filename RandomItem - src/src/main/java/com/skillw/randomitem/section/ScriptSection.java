package com.skillw.randomitem.section;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import com.skillw.randomitem.section.type.StringType;
import com.skillw.randomitem.utils.Utils;
import io.izzel.taboolib.util.Features;

import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.skillw.randomitem.utils.Utils.checkNull;

/**
 * @ClassName : com.skillw.randomitem.section.StringSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class ScriptSection extends BaseSection implements Debuggable {
    public ScriptSection(String id, String script) {
        super(id, new StringType(), new HashMap<String, Object>() {{
            this.put("script", script);
        }});
    }

    @Override
    public List<String> handleSection(String replaced, ComplexData data) {
        String code = Utils.handleStringReplaced((String) this.map.get("script"), data);
        String scriptString = code.contains("function") ? code : ("function run(){" + code + "}" + "run();");
        CompiledScript script = Features.compileScript(scriptString);
        List<String> result = new ArrayList<>();
        String eval = null;
        try {
            eval = String.valueOf(script.eval());
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        checkNull(eval, "&cPlease check the javascript section: &6" + this.id);
        if (eval.contains("\n")) {
            Collections.addAll(result, eval.split("\n"));
        } else {
            result.add(eval);
        }
        return result.isEmpty() ? null : result;
    }

    @Override
    public BaseSection clone() {
        return new ScriptSection(this.getId(), (String) this.map.get("script"));
    }

    @Override
    public List<String> getDebugMessages() {
        List<String> messages = new ArrayList<>();
        messages.add("&b " + "script" + " &5:");
        for (String text : ((String) this.get("script")).split("\n")) {
            messages.add("&d     &e" + text);
        }
        return messages;
    }
}
