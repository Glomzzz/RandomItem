package com.skillw.randomitem.listener;

import io.izzel.taboolib.module.inject.TListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.skillw.randomitem.util.ConfigUtils.isCheckVersion;
import static com.skillw.randomitem.util.StringUtils.getMessage;
import static com.skillw.randomitem.util.Utils.getCheckVersionMessage;

/**
 * @ClassName : com.skillw.randomitem.listener.CheckVersionListener
 * Created by Glom_ on 2021-02-24 22:39:27
 * Copyright  2020 user. All rights reserved.
 */
@TListener
public class CheckVersionListener implements Listener {
    @EventHandler
    public void onOPJoin(PlayerJoinEvent event) {
        if (!isCheckVersion()) {
            return;
        }
        Player player = event.getPlayer();
        if (!player.isOp()) {
            return;
        }
        String string = getCheckVersionMessage();
        if (string != null) {
            player.sendMessage(getMessage(string));
        }
    }
}
