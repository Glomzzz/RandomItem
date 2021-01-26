package com.skillw.randomitem.utils;

import com.skillw.randomitem.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glom_
 * @date 2020/10/25 20:16
 */
public class ConfigUtils {
    private static ConcurrentHashMap<String, List<String>> stringsMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> numbersMap = new ConcurrentHashMap<>();

    public static String getPrefix() {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("prefix"));
    }

    public static String getSaveItemMessage(String item) {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("item.save")).replace("{item}", item).replace("{prefix}", getPrefix());
    }


    public static String getGetItemMessage(String item, int amount) {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("item.get")).replace("{amount}", String.valueOf(amount)).replace("{item}", item).replace("{prefix}", getPrefix());
    }

    public static String getNoPermissionMessage() {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("no-permission")).replace("{prefix}", getPrefix());
    }

    public static String getOnlyPlayerMessage() {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("only-player")).replace("{prefix}", getPrefix());
    }

    public static String getValidXyzMessage() {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("valid.x-y-z")).replace("{prefix}", getPrefix());
    }

    public static String getValidWorldMessage(String world) {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("valid.world")).replace("{world}", world).replace("{prefix}", getPrefix());
    }

    public static String getValidItemMessage(String item) {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("valid.item")).replace("{item}", item).replace("{prefix}", getPrefix());
    }

    public static String getValidPlayerMessage(String player) {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("valid.player")).replace("{player}", player).replace("{prefix}", getPrefix());
    }

    public static String getReloadMessage() {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("reload")).replace("{prefix}", getPrefix());
    }

    public static List<String> getCommandMessages() {
        List<String> texts = new ArrayList<>();
        for (String text : Main.getInstance().getMessage().getStringList("commands")) {
            texts.add(RandomItemUtils.getMessage(text.replace("{prefix}", getPrefix())));
        }
        return texts;
    }

    public static ConcurrentHashMap<String, List<String>> getStringsMap() {
        ConcurrentHashMap<String, List<String>> stringsMap1 = new ConcurrentHashMap<>();
        for (String key : stringsMap.keySet()) {
            List<String> value = stringsMap.get(key);
            List<String> value1 = new ArrayList<>();
            for (String string : value) {
                value1.add(new String(string));
            }
            stringsMap1.put(new String(key), value1);
        }
        return stringsMap1;
    }

    public static void setStringsMap(ConcurrentHashMap<String, List<String>> stringsMap) {
        ConfigUtils.stringsMap = stringsMap;
    }

    public static ConcurrentHashMap<String, String> getNumbersMap() {
        ConcurrentHashMap<String, String> numbersMap1 = new ConcurrentHashMap<>();
        for (String key : numbersMap.keySet()) {
            String value = numbersMap.get(key);
            numbersMap1.put(new String(key), new String(value));
        }
        return numbersMap1;
    }

    public static void setNumbersMap(ConcurrentHashMap<String, String> numbersMap) {
        ConfigUtils.numbersMap = numbersMap;
    }


}
