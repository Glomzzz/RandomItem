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
    public void giveRandomItem(Player player, String itemID, CommandSender sender, String pointData, boolean isDebug) {
        RandomItem randomItem = Main.getItemManager().getRandomItemHashMap().get(itemID);
        if (randomItem != null) {
            ItemStack itemStack = randomItem.getItemStack(player, pointData, isDebug);
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

    private ItemStack[] generateRandomItemRandomly(String itemID, String amountFormula, String chanceFormula, boolean isSame, Player player, String pointData, boolean isDebug) {
        RandomItem randomItem = Main.getItemManager().getRandomItemHashMap().get(itemID);
        if (randomItem == null) {
            ConfigUtils.sendValidItemMessage(player, itemID);
            return new ItemStack[0];
        }
        int amount = 1;
        if (amountFormula != null) {
            amount = (int) Math.round(getResult(replacePAPI(amountFormula, player)));
        }
        double chance = 1;
        if (chanceFormula != null) {
            chance = getResult(replacePAPI(chanceFormula, player));
        }
        List<ItemStack> itemStacks = new ArrayList<>();
        if (isSame) {
            ItemStack itemStack = randomItem.getItemStack(player, pointData, isDebug);
            for (int i = 0; i < amount; i++) {
                itemStacks.add(itemStack);
            }
        } else {
            for (int i = 0; i < amount; i++) {
                ItemStack itemStack = randomItem.getItemStack(player, pointData, isDebug);
                itemStacks.add(itemStack);
            }
        }
        double random = new Random().nextDouble();
        return chance > random ? itemStacks.toArray(new ItemStack[0]) : new ItemStack[0];
    }

    @Override
    public void giveRandomItemRandomly(String itemID, String amountFormula, String chanceFormula, boolean isSame, Player player, CommandSender sender, String pointData, boolean isDebug) {
        ItemStack[] itemStacks = this.generateRandomItemRandomly(itemID, amountFormula, chanceFormula, isSame, player, pointData, isDebug);
        if (itemStacks != null && itemStacks.length != 0) {
            player.getInventory().addItem(itemStacks);
        }
        if (sender != null) {
            sendGiveItemMessage(sender, player.getDisplayName(), itemID, itemStacks.length);
        }
        sendGetItemMessage(player, itemID, itemStacks.length);
    }

    @Override
    public void dropRandomItemRandomly(String itemID, Location location, String amountFormula, String chanceFormula, boolean isSame, Player player, CommandSender commandSender, boolean isDebug) {
        ItemStack[] itemStacks = this.generateRandomItemRandomly(itemID, amountFormula, chanceFormula, isSame, player, null, isDebug);
        if (itemStacks != null && itemStacks.length != 0) {
            World world = location.getWorld();
            for (ItemStack itemStack : itemStacks) {
                world.dropItem(location, itemStack);
            }
            if (commandSender != null) {
                sendDropItemMessage(commandSender, itemID, itemStacks.length, world.getName(), "" + location.getX(), "" + location.getY(), "" + location.getZ());
            }
        }
    }

    @Override
    public boolean createItemStackConfig(ItemStack itemStack, String itemKey, String path, boolean isDebug) {
        ItemSaveCallable itemSaveCallable = new ItemSaveCallable(itemStack, itemKey, path, isDebug);
        Future<Boolean> future = Main.getScheduledExecutorService().submit(itemSaveCallable);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

}
