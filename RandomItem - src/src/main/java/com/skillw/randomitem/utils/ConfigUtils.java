package com.skillw.randomitem.utils;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.section.BaseSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.utils.RandomItemUtils.addRandomsFromSection;

/**
 * @author Glom_
 * @date 2020/10/25 20:16
 */
public class ConfigUtils {
    private final static ConcurrentHashMap<String, BaseSection> GLOBAL_SECTION_MAP = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, BaseSection> getGlobalSectionMap() {
        return GLOBAL_SECTION_MAP;
    }

    public static boolean isCheckVersion() {
        return Main.getInstance().getConfig().getBoolean("options.check-version");
    }

    public static void loadGlobalSection() {
        getGlobalSectionMap().clear();
        ConcurrentHashMap<String, BaseSection> sectionMap = new ConcurrentHashMap<>();
        sendDebug("&aLoading Global Sections:");
        List<File> fileList = RandomItemUtils.getSubFilesFromFile(Main.getInstance().getGlobalSectionsFile());
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
        getGlobalSectionMap().putAll(sectionMap);
    }

    public static boolean getDebug() {
        return Main.getInstance().getConfig().getBoolean("options.debug");
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

    public static String getValidIdMessage(String id) {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("valid.id")).replace("{section-id}", id).replace("{prefix}", getPrefix());
    }

    public static String getReloadMessage() {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("reload.default")).replace("{prefix}", getPrefix());
    }

    public static String getConfigReloadMessage() {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("reload.config")).replace("{prefix}", getPrefix());
    }

    public static int getVersion() {
        return Main.getInstance().getConfig().getInt("version");
    }

    public static String getMessageReloadMessage() {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("reload.message")).replace("{prefix}", getPrefix());
    }

    public static String getItemsReloadMessage() {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("reload.items")).replace("{prefix}", getPrefix());
    }

    public static String getGlobalSectionsReloadMessage() {
        return RandomItemUtils.getMessage(Main.getInstance().getMessage().getString("reload.global-sections")).replace("{prefix}", getPrefix());
    }

    public static List<String> getCommandMessages() {
        List<String> texts = new ArrayList<>();
        for (String text : Main.getInstance().getMessage().getStringList("commands")) {
            texts.add(RandomItemUtils.getMessage(text.replace("{prefix}", getPrefix())));
        }
        return texts;
    }

}
