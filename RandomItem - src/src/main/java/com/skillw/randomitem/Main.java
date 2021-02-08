package com.skillw.randomitem;

import com.skillw.randomitem.api.RandomItemAPI;
import com.skillw.randomitem.api.manager.ItemManager;
import com.skillw.randomitem.api.object.ItemCompute;
import com.skillw.randomitem.api.object.RandomItem;
import com.skillw.randomitem.api.object.SubString;
import com.skillw.randomitem.compute.ItemComputeImpl;
import com.skillw.randomitem.manager.ItemManagerImpl;
import com.skillw.randomitem.manager.RandomItemAPIImpl;
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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

import static com.skillw.randomitem.utils.ConfigUtils.*;
import static com.skillw.randomitem.utils.RandomItemUtils.loadRandomsFromSection;
import static org.bukkit.Material.AIR;

/**
 * @author Glom_
 */
public final class Main extends Plugin {
    private static final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(20,
            new BasicThreadFactory.Builder().namingPattern("random-item-schedule-pool-%d").daemon(true).build());
    public static int version;
    private static Main instance;
    private static RandomItemAPI randomItemAPI;
    private static ItemManager itemManager;
    public boolean papi;
    public boolean mm;
    int pluginId = 10031;
    private File configFile;
    private TConfig config;
    private File messageFile;
    private TConfig message;
    private File itemsFile;

    public static RandomItemAPI getRandomItemAPI() {
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

    public static ItemManager getItemManager() {
        return itemManager;
    }

    private void initialize() {
        instance = this;
        randomItemAPI = new RandomItemAPIImpl();
        itemManager = new ItemManagerImpl();
        this.firstLoad();
        this.loadConfig();
        CommandBuilder.create()
                .command("randomitem")
                .aliases("ri")
                .execute(((sender, args) -> {
                    if (!sender.hasPermission("randomitem.admin")) {
                        sender.sendMessage(getNoPermissionMessage());
                        return;
                    }
                    if (args.length >= 2) {
                        if (args.length == 7 && "drop".equalsIgnoreCase(args[0])) {
                            String name = args[1];
                            Player p = Bukkit.getServer().getPlayer(name);
                            String itemID = args[2];
                            RandomItem randomItem = Main.getItemManager().getRPGItemHashMap().get(itemID);
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
                        if ("save".equalsIgnoreCase(args[0])) {
                            if (sender instanceof Player) {
                                String itemID = args[1];
                                Player p = (Player) sender;
                                ItemStack itemStack = p.getInventory().getItemInMainHand();
                                if (itemStack.getType() == AIR) {
                                    sender.sendMessage(getValidSaveMessage("空气"));
                                    return;
                                }
                                ItemMeta itemMeta = itemStack.getItemMeta();
                                String name = (itemMeta.hasDisplayName()) ? itemMeta.getDisplayName() : itemStack.getType().name();
                                if (Main.getItemManager().createItemStackConfig(itemStack, itemID)) {
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
                        if (!sender.hasPermission("randomitem.admin")) {
                            if (sender.hasPermission("randomitem.show")) {
                                sender.sendMessage(getNoPermissionMessage());
                            }
                            return;
                        }
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
                                return this.getStrings("[]");
                            }
                            return this.getStrings(args);
                        } else if ("give".equalsIgnoreCase(args[0])) {
                            if (args.length == 2) {
                                return null;
                            }
                            if (args.length == 4) {
                                return this.getStrings("[]");
                            }
                            return this.getStrings(args);
                        }
                    }
                    return null;
                }).build();
    }

    private List<String> getStrings(String... args) {
        if (args.length == 2) {
            ArrayList<String> stringArrayList = new ArrayList<>();
            for (RandomItem main : Main.getItemManager().getRPGItemHashMap().values()) {
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
        if (!this.configFile.exists()) {
            this.getPlugin().saveResource("config.yml", true);
        }
        if (!this.messageFile.exists()) {
            this.getPlugin().saveResource("message.yml", true);
        }
        if (!this.itemsFile.exists()) {
            this.getPlugin().saveResource("Items/ExampleItem.yml", true);
        }
        this.config = TConfig.create(this.configFile, this.getPlugin());
        this.message = TConfig.create(this.messageFile, this.getPlugin());
        TConfigWatcher.getInst().removeListener(this.configFile);
        TConfigWatcher.getInst().removeListener(this.messageFile);
        TConfigWatcher.getInst().removeListener(this.itemsFile);
        {
            TConfigWatcher.getInst().addSimpleListener(this.configFile, () -> {
                if (!this.configFile.exists()) {
                    this.getPlugin().saveResource("config.yml", true);
                }
                this.loadConfig();
                sendMessage("&b[&9RandomItem&b]" + " &e配置文件 &d&lconfig.yml &e更新了,已自动加载!");
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
                sendMessage("&b[&9RandomItem&b]" + " &e配置文件 &d&lmessage.yml &e更新了,已自动加载!");
            });
            TConfigWatcher.getInst().addSimpleListener(this.itemsFile, () -> {
                if (!this.itemsFile.exists()) {
                    this.getPlugin().saveResource("Items/ExampleItem.yml", true);
                }
                randomItemAPI.reloadRandomItems();
                sendMessage("&b[&9RandomItem&b]" + " &e配置文件夹 &d&lItems/ &e下的文件更新了,已自动加载! &7(这个只会在文件加入/删除时自动加载 其它情况下请自行/ri reload!)");
            });
        }
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
        try {
            this.config.load(this.configFile);
            this.message.load(this.messageFile);
        } catch (final IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        ConcurrentHashMap<String, List<SubString>> stringsMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, String> numbersMap = new ConcurrentHashMap<>();
        ItemCompute itemCompute = new ItemComputeImpl();
        {
            ConfigurationSection randomsSection = this.getPluginConfig().getConfigurationSection("options.global-randoms");
            loadRandomsFromSection(stringsMap, numbersMap, itemCompute, randomsSection);
        }
        ConfigUtils.setGlobalNumbersMap(numbersMap);
        ConfigUtils.setGlobalStringsMap(stringsMap);
        ConfigUtils.setGlobalItemCompute(itemCompute);
        randomItemAPI.reloadRandomItems();
    }

    @Override
    public void onLoad() {
        this.initialize();
        sendMessage("&bRandomItem加载成功! &9作者: Glom_ &6QQ: 88595433");
    }

    @Override
    public void onEnable() {
        this.papi = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        this.mm = Bukkit.getPluginManager().isPluginEnabled("MythicMobs");
        BStats stats = new BStats(this.getPlugin());
        stats.addCustomChart(new BStats.MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));
        sendMessage("&eRandomItem正在启用...");
        sendMessage("&5前置加载: ");
        sendMessage("  &b- &6PlaceholderAPI " + ((this.papi) ? "&2&l√" : "&4&l×"));
        sendMessage((this.papi) ? "   &a已发现&6PlaceholderAPI&a, 成功挂钩!" : "   &c未发现&6PlaceholderAPI&c, 跳过兼容!");
        sendMessage("  &b- &6MythicMobs " + ((this.mm) ? "&2&l√" : "&4&l×"));
        sendMessage((this.mm) ? "   &a已发现&6MythicMobs&a, 成功挂钩!" : "   &c未发现&6MythicMobs&c, 跳过兼容!");
        sendMessage("&2RandomItem启用成功! &d作者: Glom_ &6QQ: 88595433");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        TConfigWatcher.getInst().removeListener(this.configFile);
        TConfigWatcher.getInst().removeListener(this.messageFile);
        TConfigWatcher.getInst().removeListener(this.itemsFile);
        sendMessage("&cRandomItem卸载成功! &5作者: Glom_ &6QQ: 88595433");
        instance = null;
        randomItemAPI = null;
        itemManager = null;
    }
}
