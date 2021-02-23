package com.skillw.randomitem.utils;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.data.BasicData;
import com.skillw.randomitem.api.randomitem.RandomItem;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.section.ComplexData;
import com.skillw.randomitem.api.section.weight.Weighable;
import io.izzel.taboolib.module.tellraw.TellrawJson;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.utils.ConfigUtils.getPrefix;
import static com.skillw.randomitem.utils.NumberUtils.getNumbers;
import static com.skillw.randomitem.utils.StringUtils.*;

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
        ConcurrentHashMap<String, LinkedList<String>> alreadySectionMap = data.getAlreadySectionMap();
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
                                    //我在这里硬性规定了
                                    // 实现Weighable的BaseSection子类
                                    // 其实例的DataMap中必须存在一个"value",List<BasicData<?>>
                                    // 所以会直接强转
                                    CopyOnWriteArrayList<BasicData<?>> basicDataList = new CopyOnWriteArrayList<>((List<BasicData<?>>) section.get("values"));
                                    basicDataList.removeIf(basicData -> !values.contains(basicData.getId()));
                                    section.put("values", basicDataList);
                                    section.load(id, data);
                                } else {
                                    //因为singletonList返回的列表容量始终为1
                                    //且需保证后面对此列表的操作可以正常进行
                                    // 故用asList
                                    alreadySectionMap.put(id, new LinkedList<>(Arrays.asList(value)));
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

    public static List<String> handleStringsReplaced(List<String> values, ComplexData data) {
        for (int i = 0; i < values.size(); i++) {
            String value = handleStringReplaced(values.get(i), data);
            values.set(i, value);
        }
        return values;
    }

    public static String handleStringReplaced(String value, ComplexData data) {
        List<String> strings = intercept(value);
        for (String replaced : strings) {
            String string = handleReplaced(replaced, data);
            if (string != null) {
                sendDebug("&d  - &b" + "<" + replaced + ">" + " &5= &e" + string.replace("\n", "&f\\n"));
                value = value.replace("<" + replaced + ">", string);
            }
        }
        return value;
    }

    public static String handleReplaced(String replaced, ComplexData data) {
        ConcurrentHashMap<String, BaseSection> sectionMap = data.getSectionMap();
        ConcurrentHashMap<String, LinkedList<String>> alreadySectionMap = data.getAlreadySectionMap();
        String string = null;
        String otherKey;
        if (replaced.contains(".")) {
            if (replaced.split("\\.").length > 0) {
                otherKey = handleStringReplaced(replaced.split("\\.")[0], data);
                BaseSection section = sectionMap.get(otherKey);
                if (section != null) {
                    if (!alreadySectionMap.containsKey(otherKey)) {
                        section.load(replaced, data);
                    }
                    String value = replaced.split("\\.")[1];
                    String key = section.getId() + "." + value;
                    if (section.getDataMap().containsKey(value) || "id".equals(value) || alreadySectionMap.containsKey(key)) {
                        if (alreadySectionMap.containsKey(key)) {
                            StringBuilder str = new StringBuilder();
                            for (String text : alreadySectionMap.get(key)) {
                                str.append(text).append("\n");
                            }
                            return str.toString();
                        } else {
                            if ("id".equals(value)) {
                                return section.getId();
                            }
                            return replaceAll(String.valueOf(section.getDataMap().get(value)), data);
                        }
                    }
                }
                List<String> stringList = alreadySectionMap.get(otherKey);
                if (checkNull(stringList, "Wrong section ID in " + replaced + "!")) {
                    return replaced;
                }
                string = listToStringWithNext(getStrings(replaced, stringList, data));
            }
        } else {
            if (sectionMap.containsKey(replaced)) {
                otherKey = replaced;
                if (!alreadySectionMap.containsKey(otherKey)) {
                    BaseSection section = sectionMap.get(otherKey);
                    section.load(replaced, data);
                }
                string = listToStringWithNext(alreadySectionMap.get(otherKey));
            }
        }
        return string;
    }

    public static String replaceAll(String string, ComplexData data) {
        string = handleStringReplaced(string, data);
        return deleteSlashes(getMessage(replacePAPI(string, data.getPlayer())));
    }

    public static List<String> getStrings(String replace, List<String> stringList, ComplexData data) {
        List<String> strings = new ArrayList<>();
        if (replace.contains(".")) {
            String numbers;
            if (replace.contains(">.<")) {
                numbers = replace.split(">\\.")[1];
            } else if (replace.contains(">.") && !replace.contains(".<")) {
                numbers = replace.split(">\\.")[1];
            } else if (replace.contains(".<")) {
                numbers = "<" + replace.split("\\.<")[1];
            } else {
                numbers = replace.split("\\.")[1];
            }
            if (numbers.contains("<") && numbers.contains(">")) {
                numbers = handleStringReplaced(numbers, data);
            }
            int maxIndex = stringList.size() - 1;
            for (int i : getNumbers(numbers, maxIndex)) {
                if (stringList.get(i) != null && !stringList.get(i).isEmpty()) {
                    strings.add(stringList.get(i));
                }
            }
        } else {
            strings.addAll(stringList);
        }
        return strings;
    }

    public static boolean checkNull(Object object, String message) {
        if (object == null || object.toString().isEmpty()) {
            Main.sendMessage(getPrefix() + "&4" + message);
            return true;
        }
        return false;
    }

    public static void sendList(CommandSender sender, int page) {
        TellrawJson tellrawJson = TellrawJson.create();
        List<RandomItem> randomItems = new ArrayList<>(Main.getItemManager().getRandomItemHashMap().values());
        int total = randomItems.size();
        int number = ConfigUtils.getListNumber();
        int lastPage = total / number + (total % number != 0 ? 1 : 0);
        tellrawJson.append(ConfigUtils.getListUpMessage() + "\n");
        int lastI;
        if (lastPage == 1) {
            lastI = total;
        } else if (page != lastPage) {
            lastI = number * page;
        } else {
            lastI = total;
        }
        for (int i = (page - 1) * number + 1; i <= lastI; i++) {
            int index = i - 1;
            RandomItem randomItem = randomItems.get(index);
            tellrawJson.append(ConfigUtils.getListFormat(i, randomItem));
            tellrawJson.hoverItem(randomItem.getItemStack(), true);
            if (sender instanceof Player) {
                Player player = (Player) sender;
                tellrawJson.clickCommand("/ri give " + player.getDisplayName() + " " + randomItem.getId());
            }
            tellrawJson.append("\n");
        }
        int previousPage = page - 1;
        TellrawJson left = TellrawJson.create();
        left.append(ConfigUtils.getListLeftMessage());
        if (previousPage > 0) {
            left.clickCommand("/ri list " + previousPage);
        }
        int nextPage = page + 1;
        TellrawJson right = TellrawJson.create();
        right.append(ConfigUtils.getListRightMessage());
        if (nextPage <= lastPage) {
            right.clickCommand("/ri list " + nextPage);
        }
        tellrawJson.append(left);
        tellrawJson.append(ConfigUtils.getListPage(page, lastPage));
        tellrawJson.append(right);
        tellrawJson.append("\n");
        tellrawJson.append(ConfigUtils.getListDownMessage());
        tellrawJson.send(sender);
    }

    public static int getNewestVersion() {
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
            return -114514;
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
        return Integer.parseInt(result.toString().split("：")[1].split("<")[0]);
    }

}
