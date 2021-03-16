package com.skillw.randomitem.util;

import com.skillw.randomitem.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import static com.skillw.randomitem.Main.sendWrong;
import static com.skillw.randomitem.util.ConfigUtils.getValidNetWorkMessage;
import static com.skillw.randomitem.util.ConfigUtils.isCheckVersion;

/**
 * @author Glom_
 */
public final class Utils {

    private Utils() {
    }

    public static boolean checkNull(Object object, String message) {
        if (object == null || object.toString().isEmpty()) {
            sendWrong(message);
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
            Main.sendMessage(getValidNetWorkMessage());
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
                return "-114514";
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
            return ConfigUtils.getVersionLegacyMessage(stringBuffer.toString());
        } else if (newestVersion != -114514) {
            return ConfigUtils.getVersionLatestMessage();
        }
        return null;
    }

}
