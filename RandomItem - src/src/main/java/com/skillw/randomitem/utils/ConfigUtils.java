package com.skillw.randomitem.utils;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.object.ItemCompute;
import com.skillw.randomitem.api.object.SubString;
import com.skillw.randomitem.compute.ItemComputeImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glom_
 * @date 2020/10/25 20:16
 */
public class ConfigUtils {
    private static ConcurrentHashMap<String, List<SubString>> globalStringsMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> globalNumbersMap = new ConcurrentHashMap<>();
    private static ItemCompute globalItemCompute = new ItemComputeImpl();

    public static ItemCompute getGlobalItemCompute() {
        return globalItemCompute.clone();
    }

    public static void setGlobalItemCompute(ItemCompute globalItemCompute) {
        ConfigUtils.globalItemCompute = globalItemCompute;
    }

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

    public static String getValidSaveMessage(String item) {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("valid.save")).replace("{item}", item).replace("{prefix}", getPrefix());
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

    public static ConcurrentHashMap<String, List<SubString>> getGlobalStringsMap() {
        ConcurrentHashMap<String, List<SubString>> stringsMap = new ConcurrentHashMap<>();
        for (String key : globalStringsMap.keySet()) {
            List<SubString> subStrings = new ArrayList<>();
            List<SubString> values = globalStringsMap.get(key);
            values.forEach(subString -> subStrings.add(subString.clone()));
            stringsMap.put(key, subStrings);
        }
        return stringsMap;
    }

    public static void setGlobalStringsMap(ConcurrentHashMap<String, List<SubString>> globalStringsMap) {
        ConfigUtils.globalStringsMap = globalStringsMap;
    }

    public static ConcurrentHashMap<String, String> getGlobalNumbersMap() {
        ConcurrentHashMap<String, String> numbersMap1 = new ConcurrentHashMap<>();
        for (String key : globalNumbersMap.keySet()) {
            String value = globalNumbersMap.get(key);
            numbersMap1.put(key, value);
        }
        return numbersMap1;
    }

    public static void setGlobalNumbersMap(ConcurrentHashMap<String, String> globalNumbersMap) {
        ConfigUtils.globalNumbersMap = globalNumbersMap;
    }


}
