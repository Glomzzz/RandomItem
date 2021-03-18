package com.skillw.randomitem.util

import com.skillw.randomitem.Main
import com.skillw.randomitem.api.section.ComplexData
import com.skillw.randomitem.util.ProcessUtils.replaceAll
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

/**
 * ClassName : com.skillw.randomitem.util.CommandUtils
 * Created by Glom_ on 2021-03-15 23:32:19
 * Copyright  2021 user. All rights reserved.
 */
object CommandUtils {
    @JvmStatic
    fun perform(command: String, data: ComplexData) {
        val player = data.player;
        val args = command.split(":")
        if (args.isEmpty()) return
        val finalCommand = replaceAll(command.replace(args[0] + ":", ""), data)
        when {
            args[0] == "console" -> {
                object : BukkitRunnable() {
                    override fun run() {
                        Bukkit.getConsoleSender().server.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            finalCommand
                        )
                    }
                }.runTask(Main.getInstance().plugin)
            }
            args[0] == "op" -> {
                object : BukkitRunnable() {
                    override fun run() {
                        val op = player.isOp
                        player.isOp = true
                        player.performCommand(finalCommand)
                        if (!op) player.isOp = false
                    }
                }.runTask(Main.getInstance().plugin)
            }
            args[0] == "player" -> {
                object : BukkitRunnable() {
                    override fun run() {
                        player.performCommand(finalCommand)
                    }
                }.runTask(Main.getInstance().plugin)
            }
        }
    }
}