package com.skillw.randomitem;

import com.skillw.randomitem.bstats.Metrics;
import com.skillw.randomitem.command.RandomItemCommand;
import com.skillw.randomitem.utils.ConfigUtils;
import com.skillw.randomitem.utils.RandomItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Glom_
 */
public final class Main extends JavaPlugin {
    private static Main instance;
    int pluginId = 10031;
    private File configFile;
    private YamlConfiguration config;
    private File messageFile;
    private YamlConfiguration message;
    private File itemsFile;

    public static Main getInstance() {
        return instance;
    }

    public static void sendMessage(String text) {
        Bukkit.getConsoleSender().sendMessage(RandomItemUtils.getMessage(text));
    }

    public static void getRandomsBySection(ConcurrentHashMap<String, List<String>> stringsMap, ConcurrentHashMap<String, String> numbersMap, ConfigurationSection randomsSection) {
        for (String randomKey : randomsSection.getKeys(false)) {
            ConfigurationSection randomSection = randomsSection.getConfigurationSection(randomKey);
            String type = randomSection.getString("type");
            switch (type) {
                case "number": {
                    String start = (randomSection.getString("start"));
                    String bound = (randomSection.getString("bound"));
                    numbersMap.put(randomKey, start + "," + bound);
                }
                break;
                case "strings": {
                    ConcurrentHashMap<String, String> stringMap = new ConcurrentHashMap<>();
                    ConfigurationSection section = randomSection.getConfigurationSection("strings");
                    if (section != null) {
                        for (String strKey : section.getKeys(false)) {
                            List<String> strings = section.getStringList(strKey);
                            if (!strings.isEmpty()) {
                                stringMap.put(strKey, RandomItemUtils.listToStringWithNext(strings));
                            }
                        }
                    }
                    List<String> strings = randomSection.getStringList("value");
                    if (!strings.isEmpty()) {
                        for (String string : strings) {
                            int index = strings.indexOf(string);
                            for (String key : stringMap.keySet()) {
                                string = string.replace("{" + key + "}", stringMap.get(key));
                                strings.set(index, string);
                            }
                        }
                        stringsMap.put(randomKey, strings);
                    }
                }
                break;
                default:
                    break;
            }
        }
    }

    public File getItemsFile() {

        return this.itemsFile;
    }

    private void firstLoad() {
        this.configFile = new File(getDataFolder(), "Config.yml");
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        this.messageFile = new File(getDataFolder(), "Message.yml");
        this.message = YamlConfiguration.loadConfiguration(this.messageFile);
        this.itemsFile = new File(getDataFolder() + "/Items");
    }

    public File getConfigFile() {
        return this.configFile;
    }

    public YamlConfiguration getPluginConfig() {
        return this.config;
    }

    public File getMessageFile() {
        return this.messageFile;
    }

    public YamlConfiguration getMessage() {
        return this.message;
    }

    public void loadConfig() {
        if (!this.configFile.exists()) {
            saveResource("Config.yml", true);
        }
        if (!this.messageFile.exists()) {
            saveResource("Message.yml", true);
        }
        if (!this.itemsFile.exists()) {
            saveResource("Items/ExampleItem.yml", true);
        }
        try {
            this.config.load(this.configFile);
            this.message.load(this.messageFile);
        } catch (final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        ConcurrentHashMap<String, List<String>> stringsMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> numbersMap = new ConcurrentHashMap<>();
        {
            ConfigurationSection randomsSection = getPluginConfig().getConfigurationSection("options.global-randoms");
            getRandomsBySection(stringsMap, numbersMap, randomsSection);
        }
        ConfigUtils.setNumbersMap(numbersMap);
        ConfigUtils.setStringsMap(stringsMap);
        RandomItem.loadObjectsFromFiles();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        firstLoad();
        loadConfig();
        Bukkit.getPluginCommand("randomitem").setExecutor(new RandomItemCommand());
        Bukkit.getPluginCommand("randomitem").setTabCompleter(new RandomItemCommand());
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));
        sendMessage("§eRandomItem正在启用! §a作者: Glom_ §6QQ: 88595433");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        sendMessage("§cRandomItem正在卸载! §a作者: Glom_ §6QQ: 88595433");
        instance = null;
    }
}
