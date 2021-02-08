package com.skillw.randomitem.manager;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.manager.ItemManager;
import com.skillw.randomitem.api.object.RandomItem;
import com.skillw.randomitem.callable.ItemSaveCallable;
import com.skillw.randomitem.utils.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.skillw.randomitem.utils.ConfigUtils.getGetItemMessage;
import static com.skillw.randomitem.utils.ConfigUtils.getValidItemMessage;
import static com.skillw.randomitem.utils.RandomItemUtils.doPAPIReplace;
import static com.skillw.randomitem.utils.RandomItemUtils.getResult;

/**
 * @ClassName : com.skillw.randomitem.item.Serialization
 * Created by Glom_ on 2021-02-04 16:31:07
 * Copyright  2020 user. All rights reserved.
 */
public class ItemManagerImpl implements ItemManager {
    private final HashMap<String, RandomItem> RPG_ITEM_HASH_MAP = new HashMap<>();

    @Override
    public HashMap<String, RandomItem> getRPGItemHashMap() {
        return this.RPG_ITEM_HASH_MAP;
    }

    @Override
    public ItemStack getItemStack(String itemID, Player player) {
        RandomItem randomItem = this.getRPGItemHashMap().get(itemID);
        return randomItem.getItemStack(player);
    }

    @Override
    public void giveRandomItem(Player player, String itemID) {
        this.giveRandomItem(player, itemID, player);
    }

    @Override
    public void giveRandomItem(Player player, String itemID, CommandSender sender) {
        this.giveRandomItem(player, itemID, sender, null);
    }

    @Override
    public void giveRandomItem(Player player, String itemID, CommandSender sender, String pointData) {
        RandomItem randomItem = Main.getItemManager().getRPGItemHashMap().get(itemID);
        if (randomItem != null) {
            ItemStack itemStack = randomItem.getItemStack(player, pointData);
            player.getInventory().addItem(itemStack);
            player.sendMessage(getGetItemMessage(itemStack.getItemMeta().getDisplayName(), itemStack.getAmount()));
        } else {
            sender.sendMessage(getValidItemMessage(itemID));
        }
    }

    @Override
    public void dropRandomItemRandomly(String itemID, Location location, String amountString, String chanceString, boolean isSame, Player player) {
        RandomItem randomItem = Main.getItemManager().getRPGItemHashMap().get(itemID);
        if (randomItem == null) {
            Main.sendMessage(ConfigUtils.getValidItemMessage(itemID));
            return;
        }
        int amount = (int) Math.round(getResult(doPAPIReplace(amountString, player)));
        double chance = getResult(doPAPIReplace(chanceString, player));
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
        }
    }

    @Override
    public boolean createItemStackConfig(ItemStack itemStack, String itemKey) {
        ItemSaveCallable itemSaveCallable = new ItemSaveCallable(itemStack, itemKey);
        Future future = Main.getScheduledExecutorService().submit(itemSaveCallable);
        try {
            return (boolean) future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

}
