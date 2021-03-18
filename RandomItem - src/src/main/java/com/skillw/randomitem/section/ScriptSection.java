package com.skillw.randomitem.section;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import com.skillw.randomitem.section.type.StringType;
import com.skillw.randomitem.util.CommandUtils;
import com.skillw.randomitem.util.StringUtils;
import io.izzel.taboolib.util.Features;

import javax.script.CompiledScript;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.skillw.randomitem.util.ProcessUtils.replaceAll;
import static com.skillw.randomitem.util.Utils.checkNull;

/**
 * @ClassName : com.skillw.randomitem.section.StringSection
 * Created by Glom_ on 2021-02-09 09:31:04
 * Copyright  2020 user. All rights reserved.
 */
public class ScriptSection extends BaseSection implements Debuggable {
    public ScriptSection(String id, String script) {
        super(id, StringType.class, new HashMap<String, Object>() {{
            this.put("id", id);
            this.put("script", script);
        }});
        if (checkNull(script, "script can't be null!!!  &eJavaScript section: &6" + id)) {
            this.put("script", "return \"Empty script!\";");
        }
    }

/*    public String run(Player player) {
        String code = (String) this.map.get("script");
        List<String> strings = StringUtils.intercept$(code);
        for (String replacedInCode : strings) {
            if (replacedInCode.contains(":")) {
                CommandUtils.perform(replacedInCode, data);
                code = code.replace("$" + replacedInCode + "$", "");
            }
        }
        boolean containsFunction = code.contains("function");
        code = replaceAll(code, data);
        String scriptString = containsFunction ? code : ("function run(){" + code + "}" + "run();");
        CompiledScript script = Features.compileScript(scriptString);
        String eval;
        try {
            eval = String.valueOf(script.eval());
        } catch (Exception e) {
            Main.sendWrong("Please check the javascript section: &6" + this.id);
            return "wrong script!";
        }
        if (checkNull(eval, "Please check the javascript section: &6" + this.id)) {
            return "wrong script!";
        }
        data.getAlreadySectionMap().putIfAbsent(this.getId() + ".json", StringUtils.toJson(this.getDataMap()));
        data.getAlreadySectionMap().putIfAbsent(this.getId() + ".script", code);
        return eval.isEmpty() ? null : eval;
    }*/

    @Override
    public String handleSection(String replaced, ComplexData data) {
        String code = (String) this.map.get("script");
        List<String> strings = StringUtils.intercept$(code);
        for (String replacedInCode : strings) {
            if (replacedInCode.contains(":")) {
                CommandUtils.perform(replacedInCode, data);
                code = code.replace("$" + replacedInCode + "$", "");
            }
        }
        boolean containsFunction = code.contains("function");
        code = replaceAll(code, data);
        String scriptString = containsFunction ? code : ("function run(){" + code + "}" + "run();");
        CompiledScript script = Features.compileScript(scriptString);
        String eval;
        try {
            eval = String.valueOf(script.eval());
        } catch (Exception e) {
            Main.sendWrong("Please check the javascript section: &6" + this.id);
            return "wrong script!";
        }
        if (checkNull(eval, "Please check the javascript section: &6" + this.id)) {
            return "wrong script!";
        }
        data.getAlreadySectionMap().putIfAbsent(this.getId() + ".json", StringUtils.toJson(this.getDataMap()));
        data.getAlreadySectionMap().putIfAbsent(this.getId() + ".script", code);
        return eval.isEmpty() ? null : eval;
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
            messages.add("&d     &e" + StringUtils.messageToText(text));
        }
        return messages;
    }
}
