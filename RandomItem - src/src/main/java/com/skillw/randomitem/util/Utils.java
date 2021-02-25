package com.skillw.randomitem.util;

import com.skillw.randomitem.Main;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.skillw.randomitem.util.ConfigUtils.getPrefix;
import static com.skillw.randomitem.util.ConfigUtils.isCheckVersion;

/**
 * @author Glom_
 */
public final class Utils {

    private Utils() {
    }

    public static List<File> getSubFilesFromFile(File file) {
        List<File> files = new ArrayList<>();
        File[] allFiles = file.listFiles();
        if (allFiles == null) {
            return files;
        }
        for (File subFile : allFiles) {
            if (!subFile.getName().endsWith(".yml")) {
                continue;
            }
            if (subFile.isFile()) {
                files.add(subFile);
            } else {
                files.addAll(getSubFilesFromFile(subFile));
            }
        }
        return files;
    }

    public static boolean checkNull(Object object, String message) {
        if (object == null || object.toString().isEmpty()) {
            Main.sendMessage(getPrefix() + "&4" + message);
            return true;
        }
        return false;
    }


    public static String getInfo() {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL("http://version.skillw.com/soft4.php");
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print("u=1919810114514");
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            Main.sendMessage("&cFailed to link to server! Please check your network!");
            return "-114514";
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    public static int getNewestVersion() {
        return Integer.parseInt(getInfo().split("插件版本：")[1].split("</version>")[0]);
    }

    public static String getCheckVersionMessage() {
        if (!isCheckVersion()) {
            return null;
        }
        int newestVersion = Utils.getNewestVersion();
        String newestVersionString = String.valueOf(newestVersion);
        if (ConfigUtils.getVersion() < newestVersion) {
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < newestVersionString.length(); i++) {
                stringBuffer.append(newestVersionString.charAt(i));
                if (i < newestVersionString.length() - 1) {
                    stringBuffer.append(".");
                }
            }
            return getPrefix() + "&cRandomItem has a new version &6" + stringBuffer.toString() + "&c, please go to&e https://www.spigotmc.org/resources/88226/ &cto download the latest version!";
        } else if (newestVersion != -114514) {
            return getPrefix() + "&aYour RandomItem is the latest version!";
        }
        return null;
    }

}
