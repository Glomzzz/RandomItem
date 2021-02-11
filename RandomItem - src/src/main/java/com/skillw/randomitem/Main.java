package com.skillw.randomitem;

import com.skillw.randomitem.api.RandomItemApi;
import com.skillw.randomitem.api.manager.ItemManager;
import com.skillw.randomitem.api.randomitem.RandomItem;
import com.skillw.randomitem.manager.ItemManagerImpl;
import com.skillw.randomitem.manager.RandomItemApiImpl;
import com.skillw.randomitem.section.type.ComputeType;
import com.skillw.randomitem.section.type.NumberType;
import com.skillw.randomitem.section.type.StringType;
import com.skillw.randomitem.utils.ConfigUtils;
import com.skillw.randomitem.utils.RandomItemUtils;
import io.izzel.taboolib.internal.apache.lang3.concurrent.BasicThreadFactory;
import io.izzel.taboolib.loader.Plugin;
import io.izzel.taboolib.metrics.BStats;
import io.izzel.taboolib.module.command.lite.CommandBuilder;
import io.izzel.taboolib.module.config.TConfig;
import io.izzel.taboolib.module.config.TConfigWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.skillw.randomitem.utils.ConfigUtils.*;
import static org.bukkit.Material.AIR;

/**
 * @author Glom_
 */
public final class Main extends Plugin {
    private static final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(20,
            new BasicThreadFactory.Builder().namingPattern("random-item-schedule-pool-%d").daemon(true).build());
    public static int version;
    private static Main instance;
    private static RandomItemApi randomItemAPI;
    private static ItemManager itemManager;
    public boolean papi;
    public boolean mm;
    //    int pluginId = 10031;
    private File configFile;
    private TConfig config;
    private File messageFile;
    private TConfig message;
    private File itemsFile;
    private File globalSectionsFile;

    public static RandomItemApi getRandomItemAPI() {
        return randomItemAPI;
    }

    public static ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public static Main getInstance() {
        return instance;
    }

    public static void sendMessage(String text) {
        Bukkit.getConsoleSender().sendMessage(RandomItemUtils.getMessage(text));
    }

    public static boolean isDebug() {
        return ConfigUtils.getDebug();
    }

    public static void sendDebug(String message) {
        if (ConfigUtils.getDebug()) {
            sendMessage(getPrefix() + message);
        }
    }

    public static ItemManager getItemManager() {
        return itemManager;
    }

    private void createCommand() {
        CommandBuilder.create()
                .command(ConfigUtils.getCommandName())
                .aliases(ConfigUtils.getCommandAliases().toArray(new String[0]))
                .permission(ConfigUtils.getCommandPermission())
                .permissionMessage(getNoPermissionMessage())
                .execute(((sender, args) -> {
                    if (args.length >= 2) {
                        if (args.length == 7 && "drop".equalsIgnoreCase(args[0])) {
                            String name = args[1];
                            Player p = Bukkit.getServer().getPlayer(name);
                            String itemID = args[2];
                            RandomItem randomItem = Main.getItemManager().getRandomItemHashMap().get(itemID);
                            String worldName = args[3];
                            String xS = args[4];
                            String yS = args[5];
                            String zS = args[6];
                            double x = 0;
                            double y = 0;
                            double z = 0;
                            if (p != null) {
                                if (randomItem != null) {
                                    World world = Bukkit.getWorld(worldName);
                                    if (world != null) {
                                        try {
                                            x = Double.parseDouble(xS);
                                            y = Double.parseDouble(yS);
                                            z = Double.parseDouble(zS);
                                        } catch (Exception e) {
                                            sender.sendMessage(getValidXyzMessage());
                                        }
                                        if (x != 0 && y != 0 && z != 0) {
                                            Location location = new Location(world, x, y, z);
                                            world.dropItem(location, randomItem.getItemStack(p));
                                        }
                                    } else {
                                        sender.sendMessage(getValidWorldMessage(worldName));
                                        return;
                                    }
                                } else {
                                    sender.sendMessage(getValidItemMessage(itemID));
                                }
                                return;
                            } else {
                                sender.sendMessage(getValidPlayerMessage(name));
                            }
                            return;
                        }
                        if ("save".equalsIgnoreCase(args[0]) && args.length == 3) {
                            if (sender instanceof Player) {
                                String itemID = args[1];
                                String path = args[2];
                                Player p = (Player) sender;
                                ItemStack itemStack = p.getInventory().getItemInMainHand();
                                if (itemStack.getType() == AIR) {
                                    sender.sendMessage(getValidSaveMessage("空气"));
                                    return;
                                }
                                ItemMeta itemMeta = itemStack.getItemMeta();
                                String name = (itemMeta.hasDisplayName()) ? itemMeta.getDisplayName() : itemStack.getType().name();
                                if (Main.getItemManager().createItemStackConfig(itemStack, itemID, path)) {
                                    sender.sendMessage(getSaveItemMessage(name));
                                } else {
                                    sender.sendMessage(getValidSaveMessage(name));
                                }
                                return;
                            }
                            sender.sendMessage(getOnlyPlayerMessage());
                        }

                        if ("get".equalsIgnoreCase(args[0])) {
                            if (sender instanceof Player) {
                                String itemID = args[1];
                                Player player = (Player) sender;
                                String pointData = null;
                                if (args.length == 3) {
                                    pointData = args[2];
                                }
                                itemManager.giveRandomItem(player, itemID, player, pointData);
                            } else {
                                sender.sendMessage(getOnlyPlayerMessage());
                            }
                        }
                        if (args.length >= 3 &&
                                "give".equalsIgnoreCase(args[0])) {
                            String name = args[1];
                            String itemID = args[2];
                            String pointData = null;
                            if (args.length == 4) {
                                pointData = args[3];
                            }
                            Player player = Bukkit.getServer().getPlayer(name);
                            if (player != null) {
                                itemManager.giveRandomItem(player, itemID, player, pointData);
                                return;
                            } else {
                                sender.sendMessage(getValidPlayerMessage(name));
                            }
                            return;
                        }
                        return;
                    }
                    if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
                        Main.getInstance().loadConfig();
                        sender.sendMessage(getReloadMessage());
                        return;
                    }
                    for (String text : getCommandMessages()) {
                        sender.sendMessage(text);
                    }
                }))
                .tab((sender, args) -> {
                    final String[] itemSub = {"get", "give", "save", "drop", "reload"};
                    if (args.length == 1) {
                        return Arrays.stream(itemSub).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
                    }
                    if (args.length > 1) {
                        if ("get".equalsIgnoreCase(args[0])) {
                            if (args.length == 3) {
                                return this.getRandomItemIDs("[]");
                            }
                            return this.getRandomItemIDs(args);
                        } else if ("give".equalsIgnoreCase(args[0])) {
                            if (args.length == 2) {
                                return null;
                            }
                            if (args.length == 4) {
                                return this.getRandomItemIDs("[]");
                            }
                            return this.getRandomItemIDs(args);
                        }
                    }
                    return null;
                }).build();
    }

    private void initialize() {
        instance = this;
        randomItemAPI = new RandomItemApiImpl();
        itemManager = new ItemManagerImpl();
        this.firstLoad();
        this.createCommand();
        new StringType().register();
        new NumberType().register();
        new ComputeType().register();
    }

    private List<String> getRandomItemIDs(String... args) {
        if (args.length == 2) {
            ArrayList<String> stringArrayList = new ArrayList<>();
            for (RandomItem main : Main.getItemManager().getRandomItemHashMap().values()) {
                stringArrayList.add(main.getId());
            }
            String[] strings = stringArrayList.toArray(new String[0]);
            return Arrays.stream(strings).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
        }
        return null;
    }

    public File getItemsFile() {
        return this.itemsFile;
    }

    private void firstLoad() {
        version = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].replace("v", "").replace("_", "").replace("R", ""));
        this.configFile = new File(this.getPlugin().getDataFolder(), "config.yml");
        this.messageFile = new File(this.getPlugin().getDataFolder(), "message.yml");
        this.itemsFile = new File(this.getPlugin().getDataFolder() + "/Items");
        this.globalSectionsFile = new File(this.getPlugin().getDataFolder() + "/GlobalSections");
        if (!this.configFile.exists()) {
            this.getPlugin().saveResource("config.yml", true);
        }
        if (!this.messageFile.exists()) {
            this.getPlugin().saveResource("message.yml", true);
        }
        if (!this.itemsFile.exists()) {
            this.getPlugin().saveResource("Items/ExampleItem.yml", true);
        }
        if (!this.globalSectionsFile.exists()) {
            this.getPlugin().saveResource("GlobalSections/Basic.yml", true);
        }
        this.config = TConfig.create(this.configFile, this.getPlugin());
        this.message = TConfig.create(this.messageFile, this.getPlugin());
        TConfigWatcher.getInst().removeListener(this.configFile);
        TConfigWatcher.getInst().removeListener(this.messageFile);
        {
            TConfigWatcher.getInst().addSimpleListener(this.configFile, () -> {
                if (!this.configFile.exists()) {
                    this.getPlugin().saveResource("config.yml", true);
                }
                this.loadConfig();
                sendMessage(ConfigUtils.getConfigReloadMessage());
            });
            TConfigWatcher.getInst().addSimpleListener(this.messageFile, () -> {
                if (!this.messageFile.exists()) {
                    this.getPlugin().saveResource("message.yml", true);
                }
                try {
                    this.message.load(this.messageFile);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
                sendMessage(ConfigUtils.getMessageReloadMessage());
            });
        }
    }

    public File getConfigFile() {
        return this.configFile;
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public File getMessageFile() {
        return this.messageFile;
    }

    public YamlConfiguration getMessage() {
        return this.message;
    }

    public void loadConfig() {
        sendDebug("&aReloading:");
        try {
            this.config.load(this.configFile);
            this.message.load(this.messageFile);
        } catch (final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        ConfigUtils.loadGlobalSection();
        randomItemAPI.reloadRandomItems();
    }

    @Override
    public void onLoad() {
        this.initialize();
        sendMessage("&bRandomItem loaded successfully! &9Author: Glom_ &6QQ: 88595433");
    }

    @Override
    public void onEnable() {
        this.loadConfig();
        this.papi = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        this.mm = Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
        BStats stats = new BStats(this.getPlugin());
        stats.addCustomChart(new BStats.MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));
        sendMessage("&eRandomItem is starting...");
        sendMessage("&5Depends Info: ");
        sendMessage("  &b- &6PlaceholderAPI " + ((this.papi) ? "&2&l√" : "&4&l×"));
        sendMessage((this.papi) ? "   &aFound &6PlaceholderAPI&a, HOOK!" : "   &cNot found &6PlaceholderAPI&c, Skip!");
        sendMessage("  &b- &6MythicMobs " + ((this.mm) ? "&2&l√" : "&4&l×"));
        sendMessage((this.mm) ? "   &aFound &6MythicMobs&a, HOOK!" : "   &cNot found &6MythicMobs&c, Skip!");
        this.checkVersion();
        sendMessage("&2RandomItem is enable! &dAuthor: Glom_ &6QQ: 88595433");
    }

    private void checkVersion() {
        int newestVersion = RandomItemUtils.getNewestVersion();
        String newestVersionString = String.valueOf(newestVersion);
        if (ConfigUtils.getVersion() < newestVersion) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < newestVersionString.length(); i++) {
                stringBuffer.append(newestVersionString.charAt(i));
                if (i < newestVersionString.length() - 1) {
                    stringBuffer.append(".");
                }
            }
            sendMessage(getPrefix() + "&cRandomitem has a new version &6" + stringBuffer.toString() + "&c, please go to&e https://www.spigotmc.org/resources/%E2%9C%85randomitem%E2%9C%85%E2%80%94%E2%80%94a-random-plugin%E2%9A%9C%EF%B8%8F2-0-is-coming-%E2%AD%90-supports-nbt-enchantments.88226/ &cto download the latest version!");
        } else if (newestVersion != -114514) {
            sendMessage(getPrefix() + "&aYour RandomItem is the latest version!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        TConfigWatcher.getInst().removeListener(this.configFile);
        TConfigWatcher.getInst().removeListener(this.messageFile);
        sendMessage("&cRandomItem is disable! &5Author: Glom_ &6QQ: 88595433");
        instance = null;
        randomItemAPI = null;
        itemManager = null;
    }

    public File getGlobalSectionsFile() {
        return this.globalSectionsFile;
    }
}
