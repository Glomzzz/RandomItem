package com.skillw.randomitem.manager;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.manager.ItemManager;
import com.skillw.randomitem.api.randomitem.RandomItem;
import com.skillw.randomitem.callable.ItemSaveCallable;
import com.skillw.randomitem.util.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.skillw.randomitem.util.CalculationUtils.getResult;
import static com.skillw.randomitem.util.ConfigUtils.*;
import static com.skillw.randomitem.util.StringUtils.replacePAPI;

/**
 * @ClassName : com.skillw.randomitem.manager.ItemManagerImpl
 * Created by Glom_ on 2021-02-04 16:31:07
 * Copyright  2020 user. All rights reserved.
 */
public final class ItemManagerImpl implements ItemManager {
    private final HashMap<String, RandomItem> randomItemHashMap = new HashMap<>();

    @Override
    public HashMap<String, RandomItem> getRandomItemHashMap() {
        return this.randomItemHashMap;
    }

    @Override
    public void giveRandomItem(Player player, String itemID, CommandSender sender, String pointData) {
        RandomItem randomItem = Main.getItemManager().getRandomItemHashMap().get(itemID);
        if (randomItem != null) {
            ItemStack itemStack = randomItem.getItemStack(player, pointData);
            player.getInventory().addItem(itemStack);
            ItemMeta itemMeta = itemStack.getItemMeta();
            String name = (itemMeta.hasDisplayName()) ? itemMeta.getDisplayName() : itemStack.getType().name();
            if (sender != null) {
                sendGiveItemMessage(sender, player.getDisplayName(), name, itemStack.getAmount());
            }
            sendGetItemMessage(player, name, itemStack.getAmount());
        } else {
            sendValidItemMessage(sender, itemID);
        }
    }

    @Override
    public void dropRandomItemRandomly(String itemID, Location location, String amountFormula, String chanceFormula, boolean isSame, Player player, CommandSender commandSender) {
        RandomItem randomItem = Main.getItemManager().getRandomItemHashMap().get(itemID);
        if (randomItem == null) {
            ConfigUtils.sendValidItemMessage(player, itemID);
            return;
        }
        int amount = (int) Math.round(getResult(replacePAPI(amountFormula, player)));
        double chance = getResult(replacePAPI(chanceFormula, player));
        List<ItemStack> itemStacks = new ArrayList<>();
        if (isSame) {
            ItemStack itemStack = randomItem.getItemStack(player);
            itemStack.setAmount(amount);
            itemStacks.add(itemStack);
        } else {
            for (int i = 0; i < amount; i++) {
                ItemStack itemStack = randomItem.getItemStack(player);
                itemStacks.add(itemStack);
            }
        }
        double random = new Random().nextDouble();
        if (chance > random) {
            World world = location.getWorld();
            for (ItemStack itemStack : itemStacks) {
                world.dropItem(location, itemStack);
            }
            if (commandSender != null) {
                sendDropItemMessage(commandSender, itemID, amount, world.getName(), "" + location.getX(), "" + location.getY(), "" + location.getZ());
            }
        }
    }

    @Override
    public boolean createItemStackConfig(ItemStack itemStack, String itemKey, String path) {
        ItemSaveCallable itemSaveCallable = new ItemSaveCallable(itemStack, itemKey, path);
        Future<Boolean> future = Main.getScheduledExecutorService().submit(itemSaveCallable);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

}
