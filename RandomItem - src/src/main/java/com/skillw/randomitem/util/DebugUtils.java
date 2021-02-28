package com.skillw.randomitem.util;

import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.debuggable.Debuggable;
import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.util.StringUtils.messageToText;

/**
 * @ClassName : com.skillw.randomitem.util.DebugUtils
 * Created by Glom_ on 2021-02-26 21:26:24
 * Copyright  2020 user. All rights reserved.
 */
public class DebugUtils {
    public static void debugSection(BaseSection baseSection) {
        if (baseSection instanceof Debuggable) {
            sendDebug("&d -> &b" + baseSection.getId() + " &5: ");
            for (String string : ((Debuggable) baseSection).getDebugMessages()) {
                sendDebug("&d     " + string);
            }
        } else {
            sendDebug("&d -> &b" + baseSection.getId());
        }
    }

    public static void debugConfigurationSection(ConfigurationSection section, String superKey) {
        superKey = superKey != null ? superKey : "";
        boolean notNBT = !section.getName().equalsIgnoreCase("nbt-keys");
        if (notNBT) {
            sendDebug(superKey + "&d  -> &b" + section.getName() + " &5: &e");
            superKey = superKey + "  ";
        }
        for (String key : section.getKeys(false)) {
            Object object = section.get(key);
            if (object instanceof ConfigurationSection) {
                ConfigurationSection subSection = (ConfigurationSection) object;
                sendDebug(superKey + "&d  -> &b" + key + " &5: &e");
                debugConfigurationSection(subSection, superKey + "  ");
            } else {
                sendDebug(superKey + "&d  -" + (!notNBT ? ">" : "") + " &b" + key + " &5= &e" + section.get(key));
            }
        }
    }

    public static void debugStringsMap(ConcurrentHashMap<String, String> alreadySectionMap) {
        alreadySectionMap.keySet().stream().sorted().forEach(key -> {
            String str = alreadySectionMap.get(key);
            sendDebug("&d  -> &b" + key + " &5: &e" + (str.contains("\n") || str.contains("\\n") ? "\n" : "") + messageToText(str));
        });
    }

}
