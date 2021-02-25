package com.skillw.randomitem.util;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.randomitem.RandomItem;
import com.skillw.randomitem.api.section.BaseSection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.util.SectionUtils.addRandomsFromSection;
import static com.skillw.randomitem.util.SectionUtils.cloneBaseSectionMap;
import static com.skillw.randomitem.util.StringUtils.getMessage;
import static com.skillw.randomitem.util.Utils.getSubFilesFromFile;

/**
 * @author Glom_
 * @date 2020/10/25 20:16
 */
public final class ConfigUtils {
    private final static ConcurrentHashMap<String, BaseSection> GLOBAL_SECTION_MAP = new ConcurrentHashMap<>();

    private ConfigUtils() {
    }

    public static ConcurrentHashMap<String, BaseSection> getGlobalSectionMapClone() {
        return cloneBaseSectionMap(GLOBAL_SECTION_MAP);
    }

    public static boolean isCheckVersion() {
        return Main.getInstance().getConfig().getBoolean("options.check-version");
    }

    public static void loadGlobalSection() {
        GLOBAL_SECTION_MAP.clear();
        ConcurrentHashMap<String, BaseSection> sectionMap = new ConcurrentHashMap<>();
        sendDebug("&aLoading Global Sections:");
        List<File> fileList = getSubFilesFromFile(Main.getInstance().getGlobalSectionsFile());
        for (File file : fileList) {
            if (file == null) {
                continue;
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            //Some codes
            if (config.getKeys(false).isEmpty()) {
                continue;
            }
            addRandomsFromSection(sectionMap, config);
        }
        GLOBAL_SECTION_MAP.putAll(sectionMap);
    }

    public static boolean getDebug() {
        return Main.getInstance().getConfig().getBoolean("options.debug");
    }

    public static String getPrefix() {
        return getMessage(Main.getInstance().getMessage().getString("prefix"));
    }

    public static String getSaveItemMessage(String item) {
        return getMessage(Main.getInstance().getMessage().getString("item.save")).replace("{item}", item).replace("{prefix}", getPrefix());
    }


    public static String getGetItemMessage(String item, int amount) {
        return getMessage(Main.getInstance().getMessage().getString("item.get")).replace("{amount}", String.valueOf(amount)).replace("{item}", item).replace("{prefix}", getPrefix());
    }

    public static String getCommandName() {
        return Main.getInstance().getConfig().getString("command.name");
    }

    public static String getCommandPermission() {
        return Main.getInstance().getConfig().getString("command.permission");
    }

    public static List<String> getCommandAliases() {
        return Main.getInstance().getConfig().getStringList("command.aliases");
    }

    public static String getNoPermissionMessage() {
        return getMessage(Main.getInstance().getMessage().getString("no-permission")).replace("{prefix}", getPrefix());
    }

    public static String getOnlyPlayerMessage() {
        return getMessage(Main.getInstance().getMessage().getString("only-player")).replace("{prefix}", getPrefix());
    }

    public static String getValidXyzMessage() {
        return getMessage(Main.getInstance().getMessage().getString("valid.x-y-z")).replace("{prefix}", getPrefix());
    }

    public static String getValidWorldMessage(String world) {
        return getMessage(Main.getInstance().getMessage().getString("valid.world")).replace("{world}", world).replace("{prefix}", getPrefix());
    }

    public static String getValidItemMessage(String item) {
        return getMessage(Main.getInstance().getMessage().getString("valid.item")).replace("{item}", item).replace("{prefix}", getPrefix());
    }

    public static String getValidPlayerMessage(String player) {
        return getMessage(Main.getInstance().getMessage().getString("valid.player")).replace("{player}", player).replace("{prefix}", getPrefix());
    }

    public static String getValidSaveMessage(String item) {
        return getMessage(Main.getInstance().getMessage().getString("valid.save")).replace("{item}", item).replace("{prefix}", getPrefix());
    }

    public static String getValidIdMessage(String id) {
        return getMessage(Main.getInstance().getMessage().getString("valid.id")).replace("{section-id}", id).replace("{prefix}", getPrefix());
    }

    public static String getValidNumberMessage() {
        return getMessage(Main.getInstance().getMessage().getString("valid.number")).replace("{prefix}", getPrefix());
    }

    public static int getListNumber() {
        return Main.getInstance().getMessage().getInt("list.value-of-one-page");
    }

    public static String getListUpMessage() {
        return getMessage(Main.getInstance().getMessage().getString("list.up")).replace("{prefix}", getPrefix());
    }

    public static String getListLeftMessage() {
        return getMessage(Main.getInstance().getMessage().getString("list.left")).replace("{prefix}", getPrefix());
    }

    public static String getListRightMessage() {
        return getMessage(Main.getInstance().getMessage().getString("list.right")).replace("{prefix}", getPrefix());
    }

    public static String getListFormat(int number, RandomItem randomItem) {
        return getMessage(Main.getInstance().getMessage().getString("list.format")
                .replace("{prefix}", getPrefix())
                .replace("{number}", String.valueOf(number))
                .replace("{item-id}", randomItem.getId())
                .replace("{item-name}", randomItem.getDisplay()));
    }

    public static String getListPage(int now, int total) {
        return getMessage(Main.getInstance().getMessage().getString("list.page")
                .replace("{prefix}", getPrefix())
                .replace("{page1}", String.valueOf(now))
                .replace("{page2}", String.valueOf(total)));
    }

    public static String getListDownMessage() {
        return getMessage(Main.getInstance().getMessage().getString("list.down")).replace("{prefix}", getPrefix());
    }

    public static String getReloadMessage() {
        return getMessage(Main.getInstance().getMessage().getString("reload.default")).replace("{prefix}", getPrefix());
    }

    public static String getConfigReloadMessage() {
        return getMessage(Main.getInstance().getMessage().getString("reload.config")).replace("{prefix}", getPrefix());
    }

    public static int getVersion() {
        return Integer.parseInt(Main.getInstance().getPlugin().getDescription().getVersion().replace(".", ""));
    }

    public static String getMessageReloadMessage() {
        return getMessage(Main.getInstance().getMessage().getString("reload.message")).replace("{prefix}", getPrefix());
    }

    public static String getItemsReloadMessage() {
        return getMessage(Main.getInstance().getMessage().getString("reload.items")).replace("{prefix}", getPrefix());
    }

    public static String getGlobalSectionsReloadMessage() {
        return getMessage(Main.getInstance().getMessage().getString("reload.global-sections")).replace("{prefix}", getPrefix());
    }

    public static List<String> getCommandMessages() {
        List<String> texts = new ArrayList<>();
        for (String text : Main.getInstance().getMessage().getStringList("commands")) {
            texts.add(getMessage(text.replace("{prefix}", getPrefix())));
        }
        return texts;
    }

    public static HashMap<String, Object> getMapFromConfigSection(ConfigurationSection section, String superKey) {
        HashMap<String, Object> dataMap = new HashMap<>();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                Object object = section.get(key);
                key = (superKey != null && !superKey.isEmpty()) ? superKey + "." + key : key;
                if (object instanceof ConfigurationSection) {
                    dataMap.putAll(getMapFromConfigSection((ConfigurationSection) object, key));
                } else {
                    dataMap.put(key, object);
                }
            }
        }
        return dataMap;
    }

}
