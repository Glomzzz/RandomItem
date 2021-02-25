package com.skillw.randomitem.listener;

import com.skillw.randomitem.Main;
import io.izzel.taboolib.module.inject.TListener;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

/**
 * @ClassName : com.skillw.randomitem.listener.MythicMobsListener
 * Created by Glom_ on 2021-02-04 00:37:25
 * Copyright  2020 user. All rights reserved.
 */
@TListener(depend = "MythicMobs")
public final class MythicMobsListener implements Listener {
    @EventHandler
    public void death(MythicMobDeathEvent event) {
        MythicMob mobType = event.getMobType();
        if (!(event.getKiller() instanceof Player)) {
            return;
        }
        List<String> dropList = mobType.getConfig().getStringList("RandomItemDrops");
        for (String drop : dropList) {
            if (drop != null && !drop.isEmpty()) {
                String[] args = drop.split(" ");
                if (args.length < 3) {
                    return;
                }
                String itemID = args[0];
                String amountString = args[1];
                String chanceString = args[2];
                boolean isSame = true;
                if (args.length >= 4) {
                    isSame = Boolean.parseBoolean(args[3]);
                }
                Main.getItemManager().dropRandomItemRandomly(itemID, event.getEntity().getLocation(), amountString, chanceString, isSame, ((Player) event.getKiller()));
            }
        }
    }
}
