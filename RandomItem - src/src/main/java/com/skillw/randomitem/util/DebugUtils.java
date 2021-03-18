package com.skillw.randomitem.util;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.sendMessage;
import static com.skillw.randomitem.util.ConfigUtils.getPrefix;
import static com.skillw.randomitem.util.StringUtils.messageToText;

/**
 * @ClassName : com.skillw.randomitem.util.DebugUtils
 * Created by Glom_ on 2021-02-26 21:26:24
 * Copyright  2020 user. All rights reserved.
 */
public class DebugUtils {

    public static void sendDebug(String message, boolean isDebug) {
        if (ConfigUtils.isDebug() && isDebug) {
            message = message.replace("\\n", "\n").replace("\\", "");
            if (message.contains("\n")) {
                String[] strings = message.split("\n");
                for (int i = 0; i < strings.length; i++) {
                    String text = strings[i];
                    sendMessage(getPrefix() + "&e" + (i == 0 ? "" : "   ") + text);
                }
            } else {
                sendMessage(getPrefix() + "&e" + message);
            }
        }
    }

    public static void debugSection(BaseSection baseSection) {
        if (baseSection instanceof Debuggable) {
            sendDebug("&d -> &b" + baseSection.getId() + " &5: ", true);
            for (String string : ((Debuggable) baseSection).getDebugMessages()) {
                sendDebug("&d     " + string, true);
            }
        } else {
            sendDebug("&d -> &b" + baseSection.getId(), true);
        }
    }

    public static void debugConfigurationSection(ConfigurationSection section, String superKey, Set<String> debugs) {
        superKey = superKey != null ? superKey : "";
        boolean notNBT = !section.getName().equalsIgnoreCase("nbt-keys");
        if (notNBT) {
            sendDebug(superKey + "&d  -> &b" + section.getName() + " &5: &e", true);
            superKey = superKey + "  ";
        }
        for (String key : section.getKeys(false)) {
            Object object = section.get(key);
            if (object instanceof ConfigurationSection) {
                ConfigurationSection subSection = (ConfigurationSection) object;
                sendDebug(superKey + "&d  -> &b" + key + " &5: &e", true);
                debugConfigurationSection(subSection, superKey + "  ", debugs);
            } else {
                String debug = superKey + "&d  -" + (!notNBT ? ">" : "") + " &b" + key + " &5= &e" + section.get(key);
                if (debugs == null) {
                    sendDebug(debug, true);
                } else {
                    debugs.add(debug);
                }
            }
        }
    }

    public static void debugStringsMap(ConcurrentHashMap<String, String> alreadySectionMap) {
        alreadySectionMap.keySet().stream().sorted().forEach(key -> {
            String str = alreadySectionMap.get(key);
            sendDebug("&d  -> &b" + key + " &5: &e" + (str.contains("\n") || str.contains("\\n") ? "\n" : "") + messageToText(str), true);
        });
    }

}
