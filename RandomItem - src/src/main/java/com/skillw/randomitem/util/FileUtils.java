package com.skillw.randomitem.util;

import com.skillw.randomitem.Main;
import io.izzel.taboolib.loader.PluginBase;
import io.izzel.taboolib.module.locale.TLocaleLoader;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @ClassName : com.skillw.randomitem.util.FileUtils
 * Created by Glom_ on 2021-02-26 03:13:03
 * Copyright  2020 user. All rights reserved.
 */
public final class FileUtils {
    private FileUtils() {

    }

    public static List<File> getSubFilesFromFile(File file) {
        List<File> files = new ArrayList<>();
        File[] allFiles = file.listFiles();
        if (allFiles == null) {
            return files;
        }
        for (File subFile : allFiles) {
            if (subFile.isFile() && subFile.getName().endsWith(".yml")) {
                files.add(subFile);
                continue;
            }
            files.addAll(getSubFilesFromFile(subFile));
        }
        return files;
    }
    
    //Form org.bukkit.plugin.java.JavaPlugin
    public static void saveResource(@NotNull String resourcePath, boolean replace) {
        PluginBase plugin = Main.getInstance().getPlugin();
        if (resourcePath != null && !resourcePath.isEmpty()) {
            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = plugin.getResource(resourcePath);
            if (in == null) {
                String lang = TLocaleLoader.getLocalPriorityFirst(Main.getInstance().getPlugin());
                Main.sendWrong("The language &b" + lang + " &c doesn't exist!!");
                in = plugin.getResource(resourcePath.replace(lang, "en_US"));
            }
            if (resourcePath.contains("languages")) {
                resourcePath = resourcePath.split(ConfigUtils.getLanguage())[1];
            }
            File outFile = new File(plugin.getDataFolder(), resourcePath);
            int lastIndex = resourcePath.lastIndexOf(47);
            File outDir = new File(plugin.getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            try {
                if (outFile.exists() && !replace) {
                    plugin.getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
                } else {
                    OutputStream out = new FileOutputStream(outFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    in.close();
                }
            } catch (IOException var10) {
                plugin.getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, var10);
            }
        } else {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }
    }
}
