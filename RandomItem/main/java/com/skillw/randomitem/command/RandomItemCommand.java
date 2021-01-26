package com.skillw.randomitem.command;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.RandomItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.skillw.randomitem.utils.ConfigUtils.*;

/**
 * @author Glom_
 * @date 2020/10/25 22:34
 */
public class RandomItemCommand implements CommandExecutor, TabCompleter {
    private final String[] itemSub = {"get", "give", "save", "drop", "reload"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        {
            {
                if (!sender.hasPermission("randomitem.admin")) {
                    sender.sendMessage(getNoPermissionMessage());
                    return true;
                }
                if (args.length >= 2) {
                    if (args.length == 7 && "drop".equalsIgnoreCase(args[0])) {
                        String name = args[1];
                        Player p = Bukkit.getServer().getPlayer(name);
                        String itemKey = args[2];
                        RandomItem randomItem = RandomItem.getRPGItemHashMap().get(itemKey);
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
                                    return true;
                                }
                            } else {
                                sender.sendMessage(getValidItemMessage(itemKey));
                            }
                            return true;
                        } else {
                            sender.sendMessage(getValidPlayerMessage(name));
                        }
                        return true;
                    }
                    if ("save".equalsIgnoreCase(args[0])) {
                        if (sender instanceof Player) {
                            String itemKey = args[1];
                            Player p = (Player) sender;
                            ItemStack itemStack = p.getInventory().getItemInMainHand();
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            String name = (itemMeta.hasDisplayName()) ? itemMeta.getDisplayName() : itemStack.getType().toString();
                            RandomItem.createItemStackConfig(itemStack, itemKey);
                            sender.sendMessage(getSaveItemMessage(name));
                            return true;
                        }
                        sender.sendMessage(getOnlyPlayerMessage());
                    }

                    if ("get".equalsIgnoreCase(args[0])) {
                        if (sender instanceof Player) {
                            String itemKey = args[1];
                            Player p = (Player) sender;
                            RandomItem randomItem = RandomItem.getRPGItemHashMap().get(itemKey);
                            if (randomItem != null) {
                                ItemStack itemStack = randomItem.getItemStack(p);
                                p.getInventory().addItem(itemStack);
                                sender.sendMessage(getGetItemMessage(itemStack.getItemMeta().getDisplayName(), itemStack.getAmount()));
                                return true;
                            }
                            sender.sendMessage(getValidItemMessage(itemKey));
                        } else {
                            sender.sendMessage(getOnlyPlayerMessage());
                        }
                    }
                    if (args.length >= 3 &&
                            "give".equalsIgnoreCase(args[0])) {
                        String name = args[1];
                        String itemKey = args[2];
                        Player p = Bukkit.getServer().getPlayer(name);
                        RandomItem main = RandomItem.getRPGItemHashMap().get(itemKey);
                        if (p != null) {
                            if (main != null) {
                                p.getInventory().addItem(main.getItemStack(p));
                                return true;
                            }
                            sender.sendMessage(getValidItemMessage(itemKey));
                            return true;
                        } else {
                            sender.sendMessage(getValidPlayerMessage(name));
                        }
                        return true;
                    }
                    return true;
                }
                if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
                    if (!sender.hasPermission("randomitem.admin")) {
                        if (sender.hasPermission("randomitem.show")) {
                            sender.sendMessage(getNoPermissionMessage());
                        }
                        return true;
                    }
                    Main.getInstance().loadConfig();
                    sender.sendMessage(getReloadMessage());
                    return true;
                }
                for (String text : getCommandMessages()) {
                    sender.sendMessage(text);
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && "randomitem".equalsIgnoreCase(command.getName())) {
            return Arrays.stream(this.itemSub).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        if (args.length > 0 && "randomitem".equalsIgnoreCase(command.getName())) {
            if ("get".equalsIgnoreCase(args[0])) {
                return getStrings(args);
            } else if ("give".equalsIgnoreCase(args[0])) {
                if (args.length == 1) {
                    return null;
                }
                return getStrings(args);
            }
        }
        return null;
    }

    private List<String> getStrings(String[] args) {
        if (args.length == 2) {
            ArrayList<String> stringArrayList = new ArrayList<>();
            for (RandomItem main : RandomItem.getRPGItemHashMap().values()) {
                stringArrayList.add(main.getId());
            }
            String[] strings = stringArrayList.toArray(new String[0]);
            return Arrays.stream(strings).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
        }
        return null;
    }
}
