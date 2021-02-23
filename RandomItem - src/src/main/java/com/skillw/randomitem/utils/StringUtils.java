package com.skillw.randomitem.utils;

import com.skillw.randomitem.Main;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName : com.skillw.randomitem.utils.StringHandler
 * Created by Glom_ on 2021-02-15 17:37:47
 * Copyright  2020 user. All rights reserved.
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * To replace the & to §.
     *
     * @param msg text
     * @return the txt after replacing
     */
    public static String getMessage(String msg) {
        if (msg != null) {
            return msg.replace("&", "§");
        } else {
            return null;
        }
    }

    /**
     * To replace the § to &.
     *
     * @param msg text
     * @return the txt after replacing
     */
    public static String messageToOriginalText(String msg) {
        if (msg != null) {
            return msg.replace("§", "&");
        } else {
            return null;
        }
    }

    public static String listToStringWithNext(List<String> strings) {
        return strings.toString().replace(", ", "\n").replace("[", "").replace("]", "");
    }

    public static String replacePAPI(String text, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !Main.getInstance().papi) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    public static String replacePAPI(String text, Player player) {
        if (!Main.getInstance().papi || player == null) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    public static List<String> interceptRedundantEscaped(String text) {
        ArrayList<String> strings = new ArrayList<>();
        int start = -114514, end = 0;
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != '\\') {
                continue;
            }
            if (start == -114514) {
                if (count == 0) {
                    start = i;
                }
                count++;
            } else {
                count--;
                if (count == 0) {
                    end = i;
                    String already = text.substring(start, end + 1);
                    strings.add(already);
                    start = -114514;
                }
            }

        }
        return strings;
    }

    /**
     * To get the List of the strings between "<" and ">"
     *
     * @param text the text
     * @return the List of the strings between "<" and ">"
     */
    public static List<String> intercept(String text) {
        ArrayList<String> strings = new ArrayList<>();
        for (String redundant : interceptRedundantEscaped(text)) {
            text = text.replace(redundant, "");
        }
        int start = 0, end = 0;
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '<') {
                if (count == 0) {
                    start = i;
                }
                count++;
            }
            if (text.charAt(i) == '>') {
                count--;
                if (count == 0) {
                    end = i;
                    strings.add(text.substring(start + 1, end));
                }
            }
        }
        return strings;
    }

    public static String deleteSlashes(String value) {
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\\' && chars[i + 1] != 'n') {
                chars[i] = '&';
            }
        }
        value = new String(chars).replace("&", "");
        return value;
    }
}
