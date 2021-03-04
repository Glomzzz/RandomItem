package com.skillw.randomitem;

import com.skillw.randomitem.api.RandomItemApi;
import com.skillw.randomitem.api.manager.ItemManager;
import com.skillw.randomitem.api.randomitem.RandomItem;
import com.skillw.randomitem.manager.ItemManagerImpl;
import com.skillw.randomitem.manager.RandomItemApiImpl;
import com.skillw.randomitem.section.type.*;
import com.skillw.randomitem.util.ConfigUtils;
import com.skillw.randomitem.util.NumberUtils;
import com.skillw.randomitem.util.StringUtils;
import io.izzel.taboolib.internal.apache.lang3.concurrent.BasicThreadFactory;
import io.izzel.taboolib.internal.gson.Gson;
import io.izzel.taboolib.internal.gson.GsonBuilder;
import io.izzel.taboolib.loader.Plugin;
import io.izzel.taboolib.metrics.BStats;
import io.izzel.taboolib.module.command.lite.CommandBuilder;
import io.izzel.taboolib.module.config.TConfig;
import io.izzel.taboolib.module.inject.TSchedule;
import io.izzel.taboolib.module.locale.TLocale;
import io.izzel.taboolib.module.tellraw.TellrawJson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
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
import java.util.stream.Stream;

import static com.skillw.randomitem.util.ConfigUtils.*;
import static com.skillw.randomitem.util.DebugUtils.sendDebug;
import static com.skillw.randomitem.util.FileUtils.saveResource;
import static com.skillw.randomitem.util.Utils.getCheckVersionMessage;
import static io.izzel.taboolib.module.locale.TLocaleLoader.getLocalPriorityFirst;
import static org.bukkit.Material.AIR;

/**
 * @author Glom_
 */
public final class Main extends Plugin {
    private static final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(10,
            new BasicThreadFactory.Builder().namingPattern("random-item-schedule-pool-%d").daemon(true).build());
    public static int gameVersion;
    private static Gson gson;
    private static Main instance;
    private static RandomItemApi randomItemAPI;
    private static ItemManager itemManager;
    public boolean papi;
    public boolean mm;
    private File configFile;
    private TConfig config;
    private File langFile;
    private File itemsFile;
    private File globalSectionsFile;

    private static void sendList(CommandSender sender, int page) {
        TellrawJson tellrawJson = TellrawJson.create();
        List<RandomItem> randomItems = new ArrayList<>(Main.getItemManager().getRandomItemHashMap().values());
        int total = randomItems.size();
        int number = ConfigUtils.getListNumber();
        int lastPage = total / number + (total % number != 0 ? 1 : 0);
        tellrawJson.append(ConfigUtils.getListUpMessage() + "\n");
        int lastI;
        if (lastPage == 1) {
            lastI = total;
        } else if (page != lastPage) {
            lastI = number * page;
        } else {
            lastI = total;
        }
        for (int i = (page - 1) * number + 1; i <= lastI; i++) {
            int index = i - 1;
            RandomItem randomItem = randomItems.get(index);
            tellrawJson.append(ConfigUtils.getListFormat(i, randomItem));
            tellrawJson.hoverItem(randomItem.getItemStack(), true);
            if (sender instanceof Player) {
                Player player = (Player) sender;
                tellrawJson.clickCommand("/ri give " + player.getDisplayName() + " " + randomItem.getId());
            }
            tellrawJson.append("\n");
        }
        int previousPage = page - 1;
        TellrawJson left = TellrawJson.create();
        left.append(ConfigUtils.getListLeftMessage());
        if (previousPage > 0) {
            left.clickCommand("/ri list " + previousPage);
        }
        int nextPage = page + 1;
        TellrawJson right = TellrawJson.create();
        right.append(ConfigUtils.getListRightMessage());
        if (nextPage <= lastPage) {
            right.clickCommand("/ri list " + nextPage);
        }
        tellrawJson.append(left);
        tellrawJson.append(ConfigUtils.getListPage(page, lastPage));
        tellrawJson.append(right);
        tellrawJson.append("\n");
        tellrawJson.append(ConfigUtils.getListDownMessage());
        tellrawJson.send(sender);
    }

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
        Bukkit.getConsoleSender().sendMessage(StringUtils.getMessage(text));
    }

    public static void sendWrong(String text) {
        Main.sendMessage(getPrefix() + "&c" + text);
    }

    public static boolean isDebug() {
        return ConfigUtils.isDebug();
    }

    public static ItemManager getItemManager() {
        return itemManager;
    }

    @TSchedule(delay = 15000, period = 15000)
    static void run() {
        Main.getInstance().checkVersion();
    }

    public static Gson getGson() {
        return gson;
    }

    private void createCommand() {
        CommandBuilder.create()
                .command(ConfigUtils.getCommandName())
                .aliases(ConfigUtils.getCommandAliases().toArray(new String[0]))
                .permission(ConfigUtils.getCommandPermission())
                .permissionMessage(getNoPermissionMessage())
                .execute(((sender, args) -> {
                    if (args.length == 0) {
                        for (String text : getCommandMessages()) {
                            sender.sendMessage(text);
                        }
                    }
                    if (args.length > 0) {
                        switch (args[0]) {
                            case "drop":
                                if (args.length == 10) {
                                    String name = args[1];
                                    Player player = Bukkit.getServer().getPlayer(name);
                                    String itemID = args[2];
                                    String amountFormula = args[3];
                                    String chanceFormula = args[4];
                                    boolean isSame;
                                    try {
                                        isSame = Boolean.parseBoolean(args[5]);
                                    } catch (Exception e) {
                                        isSame = false;
                                    }
                                    RandomItem randomItem = Main.getItemManager().getRandomItemHashMap().get(itemID);
                                    String worldName = args[6];
                                    if (player != null) {
                                        if (randomItem != null) {
                                            World world = Bukkit.getWorld(worldName);
                                            if (world != null) {
                                                double x = 0, y = 0, z = 0;
                                                try {
                                                    x = Double.parseDouble(args[7]);
                                                    y = Double.parseDouble(args[8]);
                                                    z = Double.parseDouble(args[9]);
                                                } catch (Exception e) {
                                                    sendValidXyzMessage(sender);
                                                }
                                                if (x != 0 && y != 0 && z != 0) {
                                                    Location location = new Location(world, x, y, z);
                                                    getItemManager().dropRandomItemRandomly(itemID, location, amountFormula, chanceFormula, isSame, player, sender, true);
                                                }
                                            } else {
                                                sendValidWorldMessage(sender, worldName);
                                            }
                                        } else {
                                            sendValidItemMessage(sender, itemID);
                                        }
                                    } else {
                                        System.out.println(name);
                                        sendValidPlayerMessage(sender, name);
                                    }
                                }
                                break;
                            case "save":
                                if (args.length == 3) {
                                    if (sender instanceof Player) {
                                        String itemID = args[1];
                                        String path = args[2];
                                        Player p = (Player) sender;
                                        ItemStack itemStack = p.getInventory().getItemInMainHand();
                                        if (itemStack.getType() == AIR) {
                                            sendValidSaveMessage(sender, "UNKNOWN");
                                            break;
                                        }
                                        ItemMeta itemMeta = itemStack.getItemMeta();
                                        String name = (itemMeta.hasDisplayName()) ? itemMeta.getDisplayName() : itemStack.getType().name();
                                        if (Main.getItemManager().createItemStackConfig(itemStack, itemID, path, true)) {
                                            sendSaveItemMessage(sender, name);
                                        } else {
                                            sendValidSaveMessage(sender, name);
                                        }
                                        break;
                                    }
                                    sendOnlyPlayerMessage(sender);
                                }
                                break;
                            case "give":
                                if (args.length >= 3) {
                                    String name = args[1];
                                    String itemID = args[2];
                                    String pointData = null;
                                    if (args.length == 4) {
                                        pointData = args[3];
                                    }
                                    Player player = Bukkit.getServer().getPlayer(name);
                                    if (player != null) {
                                        itemManager.giveRandomItem(player, itemID, sender, pointData, true);
                                        break;
                                    } else {
                                        sendValidPlayerMessage(sender, player.getDisplayName());
                                    }
                                    break;
                                }
                                break;
                            case "get":
                                if (args.length >= 2) {
                                    if (sender instanceof Player) {
                                        String itemID = args[1];
                                        Player player = (Player) sender;
                                        String pointData = null;
                                        if (args.length == 3) {
                                            pointData = args[2];
                                        }
                                        itemManager.giveRandomItem(player, itemID, sender, pointData, true);
                                    } else {
                                        sendOnlyPlayerMessage(sender);
                                    }
                                }
                                break;
                            case "list": {
                                int page = 1;
                                if (args.length == 1) {
                                    page = 1;
                                } else {
                                    try {
                                        page = Integer.parseInt(args[1]);
                                    } catch (Exception exception) {
                                        sendValidNumberMessage(sender);
                                    }
                                }
                                sendList(sender, page);
                            }
                            break;
                            case "reload":
                                Main.getInstance().loadConfig();
                                sendReloadMessage(sender);
                                break;
                            default:
                                break;
                        }
                    }
                }))
                .tab((sender, args) -> {
                    final String[] itemSub = {"get", "give", "list", "save", "drop", "reload"};
                    if (args.length == 1) {
                        return Arrays.stream(itemSub).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
                    }
                    if (args.length > 1) {
                        if ("get".equalsIgnoreCase(args[0]) && args.length == 2) {
                            return this.getRandomItemIDByCommand(args);
                        } else if ("give".equalsIgnoreCase(args[0]) && args.length == 3) {
                            return this.getRandomItemIDByCommand(args);
                        } else if ("drop".equalsIgnoreCase(args[0])) {
                            switch (args.length) {
                                case 3:
                                    return this.getRandomItemIDByCommand(args);
                                case 6:
                                    return Stream.of("true", "false").filter(s -> s.startsWith(args[5])).collect(Collectors.toList());
                                case 7:
                                    List<String> strings = new ArrayList<>();
                                    Bukkit.getWorlds().forEach(world -> strings.add(world.getName()));
                                    return strings.stream().filter(s -> s.startsWith(args[6])).collect(Collectors.toList());
                                default:
                                    if (args.length > 7) {
                                        Player player;
                                        if (sender instanceof Player) {
                                            player = (Player) sender;
                                        } else {
                                            player = Bukkit.getPlayer(args[1]);
                                        }
                                        if (player != null) {
                                            Location location = player.getLocation();
                                            switch (args.length) {
                                                case 8:
                                                    return Stream.of(String.valueOf(NumberUtils.format(location.getX(), -1, 0, 2, 0))).filter(s -> s.startsWith(args[7])).collect(Collectors.toList());
                                                case 9:
                                                    return Stream.of(String.valueOf(NumberUtils.format(location.getY(), -1, 0, 2, 0))).filter(s -> s.startsWith(args[8])).collect(Collectors.toList());
                                                case 10:
                                                    return Stream.of(String.valueOf(NumberUtils.format(location.getZ(), -1, 0, 2, 0))).filter(s -> s.startsWith(args[9])).collect(Collectors.toList());
                                                default:
                                                    break;
                                            }
                                        } else {
                                            return Stream.of("0").filter(s -> s.startsWith(args[args.length - 1])).collect(Collectors.toList());
                                        }
                                    }
                            }

                        }
                    }
                    return null;
                }).build();
    }

    private void initialize() {
        instance = this;
        gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        randomItemAPI = new RandomItemApiImpl();
        itemManager = new ItemManagerImpl();
        this.firstLoad();
        this.createCommand();
        new StringType().register();
        new NumberType().register();
        new CalculationType().register();
        new ScriptType().register();
        new LoreType().register();
    }

    private List<String> getRandomItemIDByCommand(String... args) {
        int index = args.length - 1;
        if (index == 1 || index == 2) {
            return Main.getItemManager().getRandomItemHashMap().keySet().stream().filter(s -> s.startsWith(args[index])).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public File getItemsFile() {
        return this.itemsFile;
    }

    private void firstLoad() {
        gameVersion = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].replace("v", "").replace("_", "").replace("R", ""));
        this.configFile = new File(this.getPlugin().getDataFolder(), "config.yml");
        this.langFile = new File(this.getPlugin().getDataFolder() + "/lang");
        this.itemsFile = new File(this.getPlugin().getDataFolder() + "/Items");
        this.globalSectionsFile = new File(this.getPlugin().getDataFolder() + "/GlobalSections");
        if (!this.configFile.exists()) {
            saveResource("config.yml", true);
        }
        if (!this.langFile.exists()) {
            saveResource("lang/en_US.yml", true);
            saveResource("lang/zh_CN.yml", true);
        }
        this.config = TConfig.create(this.configFile, this.getPlugin());
        if (!this.itemsFile.exists()) {
            saveResource(ConfigUtils.getLanguage() + "Items/ExampleItem.yml", true);
        }
        if (!this.globalSectionsFile.exists()) {
            saveResource(ConfigUtils.getLanguage() + "GlobalSections/Basic.yml", true);
            saveResource(ConfigUtils.getLanguage() + "GlobalSections/Script.yml", true);
        }
        {
            this.config.listener(() -> {
                this.loadConfig();
                if (Bukkit.getPluginManager().isPluginEnabled(this.getPlugin())) {
                    sendConfigReloadMessage(Bukkit.getConsoleSender());
                }
            });
        }
    }

    public File getConfigFile() {
        return this.configFile;
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public File getLangFile() {
        return this.langFile;
    }

    public void loadConfig() {
        if (!this.configFile.exists()) {
            saveResource("config.yml", true);
        }
        if (!this.langFile.exists()) {
            saveResource("lang/en_US.yml", true);
            saveResource("lang/zh_CN.yml", true);
        }
        if (!this.itemsFile.exists()) {
            saveResource(ConfigUtils.getLanguage() + "Items/ExampleItem.yml", true);
        }
        if (!this.globalSectionsFile.exists()) {
            saveResource(ConfigUtils.getLanguage() + "GlobalSections/Basic.yml", true);
            saveResource(ConfigUtils.getLanguage() + "GlobalSections/Script.yml", true);
        }
        sendDebug("&aReloading:", true);
        try {
            this.config.load(this.configFile);
        } catch (final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        TLocale.reload();
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
        sendMessage("&6Chosen language: &b" + getLocalPriorityFirst(Main.getInstance().getPlugin()));
        sendMessage("&5Depends Info: ");
        sendMessage("  &b- &6PlaceholderAPI " + ((this.papi) ? "&2&l√" : "&4&l×"));
        sendMessage((this.papi) ? "   &aFound &6PlaceholderAPI&a, HOOK!" : "   &cNot found &6PlaceholderAPI&c, Skip!");
        sendMessage("  &b- &6MythicMobs " + ((this.mm) ? "&2&l√" : "&4&l×"));
        sendMessage((this.mm) ? "   &aFound &6MythicMobs&a, HOOK!" : "   &cNot found &6MythicMobs&c, Skip!");
        this.checkVersion();
        sendMessage("&2RandomItem is enable! &dAuthor: Glom_ &6QQ: 88595433");
    }

    private void checkVersion() {
        String string = getCheckVersionMessage();
        if (string != null) {
            sendMessage(string);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.config.release();
        sendMessage("&cRandomItem is disable! &5Author: Glom_ &6QQ: 88595433");
        scheduledExecutorService.shutdown();
        instance = null;
        randomItemAPI = null;
        itemManager = null;
    }

    public File getGlobalSectionsFile() {
        return this.globalSectionsFile;
    }
}
