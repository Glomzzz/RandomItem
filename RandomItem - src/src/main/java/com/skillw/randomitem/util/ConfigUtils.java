package com.skillw.randomitem.util;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.randomitem.RandomItem;
import com.skillw.randomitem.api.section.BaseSection;
import io.izzel.taboolib.module.locale.TLocale;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.Main.sendWrong;
import static com.skillw.randomitem.util.FileUtils.getSubFilesFromFile;
import static com.skillw.randomitem.util.SectionUtils.addRandomsFromSection;
import static com.skillw.randomitem.util.SectionUtils.cloneBaseSectionMap;
import static com.skillw.randomitem.util.StringUtils.getMessage;
import static io.izzel.taboolib.module.locale.TLocaleLoader.getLocalPriorityFirst;

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


    public static String getLanguage() {
        if (Main.getInstance().getConfig() != null) {
            String lang = getLocalPriorityFirst(Main.getInstance().getPlugin());
            return "languages/" + lang + "/";
        }
        return null;
    }

    public static List<String> getLocalePriority() {
        return Main.getInstance().getConfig().getStringList("options.locale-priority");
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

    public static void loadRandomItems() {
        Main.getItemManager().getRandomItemHashMap().clear();
        sendDebug("&aLoading items:");
        List<File> fileList = getSubFilesFromFile(Main.getInstance().getItemsFile());
        for (File file : fileList) {
            if (file == null) {
                continue;
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            //Some codes
            if (config.getKeys(false).isEmpty()) {
                continue;
            }
            for (String key : config.getKeys(false)) {
                ConfigurationSection objectSection = config.getConfigurationSection(key);
                RandomItem randomItem = Main.getRandomItemAPI().createRandomItem(objectSection);
                randomItem.register();
            }
        }
    }

    public static boolean getDebug() {
        try {
            return Main.getInstance().getConfig().getBoolean("options.debug");
        } catch (Exception e) {
            return false;
        }
    }

    public static String getPrefix() {
        return TLocale.asString("prefix");
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


    public static String getListFormat(int number, RandomItem randomItem) {
        return TLocale.asString("list.format", getPrefix(), String.valueOf(number), randomItem.getId(), getMessage(randomItem.getDisplay()));
    }

    public static String getListPage(int now, int total) {
        return TLocale.asString("list.page", getPrefix(), String.valueOf(now), String.valueOf(total));
    }

    public static void sendSaveItemMessage(CommandSender sender, String item) {
        TLocale.sendTo(sender, "item.save", getPrefix(), item);
    }

    public static void sendGetItemMessage(CommandSender sender, String item, int amount) {
        TLocale.sendTo(sender, "item.get", getPrefix(), item, String.valueOf(amount));
    }

    public static String getNoPermissionMessage() {
        return TLocale.asString("no-permission", getPrefix());
    }

    public static void sendOnlyPlayerMessage(CommandSender sender) {
        TLocale.sendTo(sender, "only-player", getPrefix());
    }

    public static void sendValidXyzMessage(CommandSender sender) {
        TLocale.sendTo(sender, "valid.x-y-z", getPrefix());
    }

    public static void sendValidWorldMessage(CommandSender sender, String world) {
        TLocale.sendTo(sender, "valid.world", getPrefix(), world);
    }

    public static void sendValidItemMessage(CommandSender sender, String item) {
        TLocale.sendTo(sender, "valid.item", getPrefix(), item);
    }

    public static void sendValidPlayerMessage(CommandSender sender, String player) {
        TLocale.sendTo(sender, "valid.player", getPrefix(), player);
    }

    public static void sendValidSaveMessage(CommandSender sender, String item) {
        TLocale.sendTo(sender, "valid.save", getPrefix(), item);
    }

    public static void sendValidIdMessage(CommandSender sender, String id) {
        TLocale.sendTo(sender, "valid.id", getPrefix(), id);
    }

    public static void sendValidNumberMessage(CommandSender sender) {
        TLocale.sendTo(sender, "valid.number", getPrefix());
    }

    public static String getListUpMessage() {
        return TLocale.asString("list.up", getPrefix());
    }

    public static String getListLeftMessage() {
        return TLocale.asString("list.left", getPrefix());
    }

    public static String getListRightMessage() {
        return TLocale.asString("list.right", getPrefix());
    }

    public static String getListDownMessage() {
        return TLocale.asString("list.down", getPrefix());
    }

    public static String getVersionLegacyMessage(String latestVersion) {
        return TLocale.asString("check-version.legacy", getPrefix(), latestVersion);
    }

    public static String getVersionLatestMessage() {
        return TLocale.asString("check-version.latest", getPrefix());
    }

    public static String getValidNetWorkMessage() {
        return TLocale.asString("valid.network", getPrefix());
    }

    public static void sendReloadMessage(CommandSender sender) {
        TLocale.sendTo(sender, "reload.default", getPrefix());
    }

    public static void sendConfigReloadMessage(CommandSender sender) {
        TLocale.sendTo(sender, "reload.config", getPrefix());
    }

    public static int getVersion() {
        return Integer.parseInt(Main.getInstance().getPlugin().getDescription().getVersion().replace(".", ""));
    }

    public static List<String> getCommandMessages() {
        List<String> texts = new ArrayList<>();
        for (String text : TLocale.asStringList("commands")) {
            texts.add(getMessage(text));
        }
        return texts;
    }

    public static int getListNumber() {
        try {
            return Integer.parseInt(TLocale.asString("list.value-of-one-page"));
        } catch (Exception e) {
            sendWrong("Wrong format: &6list.value-of-one-page &cin the lang file &b" + Main.getInstance().getLangFile().getPath() + "/" + getLocalPriorityFirst(Main.getInstance().getPlugin()) + ".yml");
            return 10;
        }
    }

    public static HashMap<String, Object> getMapFromConfigurationSection(ConfigurationSection section, String superKey) {
        HashMap<String, Object> dataMap = new HashMap<>();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                Object object = section.get(key);
                key = (superKey != null && !superKey.isEmpty()) ? superKey + "." + key : key;
                if (object instanceof ConfigurationSection) {
                    dataMap.putAll(getMapFromConfigurationSection((ConfigurationSection) object, key));
                } else {
                    dataMap.put(key, object);
                }
            }
        }
        return dataMap;
    }

}
