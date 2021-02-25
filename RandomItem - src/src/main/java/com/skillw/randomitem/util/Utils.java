package com.skillw.randomitem.util;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.data.BasicData;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.weight.Weighable;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    public static void handlePointData(String pointData,
                                       ComplexData data) {
        ConcurrentHashMap<String, BaseSection> sectionMap = data.getSectionMap();
        ConcurrentHashMap<String, String> alreadySectionMap = data.getAlreadySectionMap();
        Player player = data.getPlayer();
        pointData = pointData.replace("[", "").replace("]", "").replace(" ", "");
        for (String text : pointData.split(",")) {
            if (text != null && !text.isEmpty()) {
                if (text.contains(":")) {
                    String id = text.split(":")[0];
                    String value = text.split(":")[1];
                    Set<String> values = new HashSet<>();
                    if (value.contains(";")) {
                        values.addAll(Arrays.asList(value.split(";")));
                    } else {
                        values.add(value);
                    }
                    if (sectionMap.containsKey(id)) {
                        List<BaseSection> sections = new ArrayList<>(sectionMap.values());
                        sections.forEach(section -> {
                            if (section.getId().equals(id)) {
                                if (section instanceof Weighable) {
                                    //在这里硬性规定了
                                    // 实现Weighable的BaseSection子类
                                    // 其实例的DataMap中必须存在一个"value-map",HashMap<String,BasicData<?>>
                                    // 所以会直接强转
                                    ConcurrentHashMap<String, BasicData<?>> basicDataMap = new ConcurrentHashMap<>();
                                    {
                                        ConcurrentHashMap<String, BasicData<?>> originalMap = (ConcurrentHashMap<String, BasicData<?>>) section.get("value-map");
                                        for (String key : originalMap.keySet()) {
                                            basicDataMap.put(key, originalMap.get(key).clone());
                                        }
                                    }
                                    for (String key : basicDataMap.keySet()) {
                                        if (!values.contains(key)) {
                                            basicDataMap.remove(key);
                                        }
                                    }
                                    section.put("value-map", basicDataMap);
                                    section.load(id, data);
                                } else {
                                    //因为singletonList返回的列表容量始终为1
                                    //且需保证后面对此列表的操作可以正常进行
                                    // 故用asList
                                    alreadySectionMap.put(id, value);
                                }
                            }
                        });
                    } else {
                        player.sendMessage(ConfigUtils.getValidIdMessage(id));
                    }
                }
            }
        }
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
