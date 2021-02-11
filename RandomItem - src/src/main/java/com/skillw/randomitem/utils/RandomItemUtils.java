package com.skillw.randomitem.utils;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.ComplexData;
import com.skillw.randomitem.api.data.BasicData;
import com.skillw.randomitem.api.debuggable.Debuggable;
import com.skillw.randomitem.api.section.BaseSection;
import com.skillw.randomitem.api.type.BaseSectionType;
import com.skillw.randomitem.api.weighable.Weighable;
import io.izzel.taboolib.TabooLib;
import io.izzel.taboolib.module.nms.nbt.NBTBase;
import io.izzel.taboolib.module.nms.nbt.NBTCompound;
import io.izzel.taboolib.module.nms.nbt.NBTList;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.*;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.skillw.randomitem.Main.sendDebug;
import static com.skillw.randomitem.Main.sendMessage;
import static com.skillw.randomitem.utils.ConfigUtils.getPrefix;

/**
 * @author Glom_
 */
public class RandomItemUtils {
    private static final Pattern PATTERN2 = Pattern.compile("(<[^>]*?>)");
    protected static Pattern PATTERN = Pattern.compile("\\d+s");
    private static boolean isRightFormat = true;

    /**
     * To replace the & to §.
     *
     * @param msg text
     * @return the txt after replacing
     */
    public static String getMessage(String msg) {
        if (msg != null) {
            return msg.replace("&", "§");
        } else {
            return null;
        }
    }

    /**
     * To replace the § to &.
     *
     * @param msg text
     * @return the txt after replacing
     */
    public static String messageToOriginalText(String msg) {
        if (msg != null) {
            return msg.replace("§", "&");
        } else {
            return null;
        }
    }

    /**
     * To get the result of the formula
     *
     * @param formula the formula
     * @return the result of the formula
     */
    public static double getResult(String formula) {
        double returnValue = 0;
        try {
            returnValue = doAnalysis(formula);
        } catch (NumberFormatException nfe) {
            sendMessage(getPrefix() + "&cFormula format error, please check: &e" + formula);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isRightFormat) {
            sendMessage(getPrefix() + "&cFormula format error, please check: &e" + formula);
            return 0;
        }
        return returnValue;
    }

    private static double doAnalysis(String formula) {
        double returnValue = 0;
        LinkedList<Integer> stack = new LinkedList<>();
        int curPos;
        String beforePart;
        String afterPart;
        String calculator;
        isRightFormat = true;
        while (isRightFormat && (formula.indexOf('(') >= 0 || formula.indexOf(')') >= 0)) {
            curPos = 0;
            for (char s : formula.toCharArray()) {
                if (s == '(') {
                    stack.add(curPos);
                } else if (s == ')') {
                    if (stack.size() > 0) {
                        beforePart = formula.substring(0, stack.getLast());
                        afterPart = formula.substring(curPos + 1);
                        calculator = formula.substring(stack.getLast() + 1, curPos);
                        formula = beforePart + doCalculation(calculator) + afterPart;
                        stack.clear();
                        break;
                    } else {
                        sendMessage(getPrefix() + "&cthere is an unclosed right bracket! &e" + formula);
                        isRightFormat = false;
                    }
                }
                curPos++;
            }
            if (stack.size() > 0) {
                sendMessage(getPrefix() + "&cthere is an unclosed left bracket! &e" + formula);
                break;
            }
        }
        if (isRightFormat) {
            returnValue = doCalculation(formula);
        }
        return returnValue;
    }

    private static double doCalculation(String formula) {
        ArrayList<Double> values = new ArrayList<>();
        ArrayList<String> operators = new ArrayList<>();
        int curPos = 0;
        int prePos = 0;
        int minus = 0;
        for (char s : formula.toCharArray()) {
            boolean is4 = (s == '+' || s == '-' || s == '*' || s == '/') && minus != 0 && minus != 2;
            if (is4) {
                values.add(Double.parseDouble(formula.substring(prePos, curPos).trim()));
                operators.add("" + s);
                prePos = curPos + 1;
                minus = minus + 1;
            } else {
                minus = 1;
            }
            curPos++;
        }
        values.add(Double.parseDouble(formula.substring(prePos).trim()));
        char op;
        for (curPos = 0; curPos <= operators.size() - 1; curPos++) {
            op = operators.get(curPos).charAt(0);
            switch (op) {
                case '*':
                    values.add(curPos, values.get(curPos) * values.get(curPos + 1));
                    values.remove(curPos + 1);
                    values.remove(curPos + 1);
                    operators.remove(curPos);
                    curPos = -1;
                    break;
                case '/':
                    values.add(curPos, values.get(curPos) / values.get(curPos + 1));
                    values.remove(curPos + 1);
                    values.remove(curPos + 1);
                    operators.remove(curPos);
                    curPos = -1;
                    break;
                default:
                    break;
            }
        }
        for (curPos = 0; curPos <= operators.size() - 1; curPos++) {
            op = operators.get(curPos).charAt(0);
            switch (op) {
                case '+':
                    values.add(curPos, values.get(curPos) + values.get(curPos + 1));
                    values.remove(curPos + 1);
                    values.remove(curPos + 1);
                    operators.remove(curPos);
                    curPos = -1;
                    break;
                case '-':
                    values.add(curPos, values.get(curPos) - values.get(curPos + 1));
                    values.remove(curPos + 1);
                    values.remove(curPos + 1);
                    operators.remove(curPos);
                    curPos = -1;
                    break;
                default:
                    break;
            }
        }
        return values.get(0);
    }

    /**
     * To format a number with fixed
     *
     * @param number A number
     * @param fixed  how many decimal places
     * @return the number after format
     */
    public static double format(double number, int fixed) {
        if (fixed == 0) {
            return Math.round(number);
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(fixed);
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
        return Double.parseDouble(numberFormat.format(number));
    }

    /**
     * To get a random double
     *
     * @param start the min value
     * @param end   the max value
     * @param fixed how many decimal places
     * @return A random double
     */
    public static double getRandom(double start, double end, int fixed) {
        if ((start > end)) {
            return Math.max(start, 0);
        } else {
            double x = (Math.random() * (end - start + 1)) + start;
            return Math.min(format(x, fixed), format(end, fixed));
        }
    }

    /**
     * To get a random integer
     *
     * @param start the min value
     * @param end   the max value
     * @return A random integer
     */
    public static int getRandom(int start, int end) {
        if ((start > end)) {
            return Math.max(start, 0);
        } else {
            int x = (int) Math.round((Math.random() * (end - start + 1)) + start);
            if (x < 0) {
                return 0;
            } else {
                return Math.min(x, end);
            }
        }
    }

    public static String replacePAPI(String text, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !Main.getInstance().papi) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    public static String replacePAPI(String text, Player player) {
        if (!Main.getInstance().papi) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    public static String listToStringWithNext(List<String> strings) {
        return strings.toString().replace(", ", "\n").replace("[", "").replace("]", "");
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
                numbers = replace.split("\\.")[1];
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

    public static List<Integer> getNumbers(String numbers, int maxIndex) {
        List<Integer> integers = new ArrayList<>();
        if (numbers != null && !numbers.isEmpty()) {
            if (numbers.contains(",")) {
                String[] numbers1 = numbers.split(",");
                for (String number : numbers1) {
                    if (number != null && !number.isEmpty()) {
                        handleNumbers(number, maxIndex, integers);
                    }
                }
            } else {
                handleNumbers(numbers, maxIndex, integers);
            }
        }
        return integers;
    }

    private static void handleNumbers(String numbers, int maxIndex, List<Integer> integers) {
        if (numbers.contains("-")) {
            for (int j = Integer.parseInt(numbers.split("-")[0]); j <= Integer.parseInt(numbers.split("-")[1]); j++) {
                if (j <= maxIndex) {
                    integers.add(j);
                }
            }
        } else {
            int i = Integer.parseInt(numbers);
            if (i <= maxIndex) {
                integers.add(i);
            }
        }
    }

    public static void debugSection(BaseSection baseSection) {
        if (baseSection instanceof Debuggable) {
            sendDebug("&d -> &b" + baseSection.getId() + " &5: ");
            for (String string : ((Debuggable) baseSection).getDebugMessages()) {
                sendDebug("&d     " + string);
            }
        } else {
            sendDebug("&d -> &b" + baseSection.getId());
        }
    }

    public static void addGlobalRandom(ConcurrentHashMap<String, BaseSection> sectionMap, List<String> usedGlobalSection) {
        sendDebug("&d - &aAdding Global Sections:");
        ConcurrentHashMap<String, BaseSection> globalSectionMap = ConfigUtils.getGlobalSectionMap();
        for (String key : globalSectionMap.keySet()) {
            if (sectionMap.containsKey(key)) {
                continue;
            }
            for (String neededKey : usedGlobalSection) {
                if (key.equals(neededKey)) {
                    BaseSection baseSection = globalSectionMap.get(key);
                    debugSection(baseSection);
                    sectionMap.put(key, baseSection);
                }
            }
        }
    }

    public static List<File> getSubFilesFromFile(File file) {
        List<File> files = new ArrayList<>();
        File[] allFiles = file.listFiles();
        if (allFiles == null) {
            return files;
        }
        for (File subFile : allFiles) {
            if (subFile.isFile()) {
                files.add(subFile);
            } else {
                files.addAll(getSubFilesFromFile(subFile));
            }
        }
        return files;
    }

    public static ConcurrentHashMap<String, BaseSection> cloneBaseSectionMap(ConcurrentHashMap<String, BaseSection> map) {
        ConcurrentHashMap<String, BaseSection> newMap = new ConcurrentHashMap<>();
        for (String key : map.keySet()) {
            BaseSection section = map.get(key);
            newMap.put(key, section.clone());
        }
        return newMap;
    }

    public static void pointHandle(String pointData,
                                   ComplexData data) {
        ConcurrentHashMap<String, BaseSection> sectionMap = data.getSectionMap();
        ConcurrentHashMap<String, List<String>> alreadySectionMap = data.getAlreadySectionMap();
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
                                    // 其Map必须有一个"value",List<BasicData<?>>
                                    // 所以会直接强转
                                    CopyOnWriteArrayList<BasicData<?>> basicDataList = new CopyOnWriteArrayList<>((List<BasicData<?>>) section.get("values"));
                                    basicDataList.removeIf(basicData -> !values.contains(basicData.getId()));
                                    section.put("values", basicDataList);
                                    section.handleSection(id, data);
                                } else {
                                    //因为singletonList返回的列表容量始终为1
                                    //且需保证后面对此列表的操作可以正常进行
                                    // 故用asList
                                    alreadySectionMap.put(id, Arrays.asList(value));
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

    public static void addRandomsFromSection(ConcurrentHashMap<String, BaseSection> baseSectionMap, ConfigurationSection randomsSection) {
        baseSectionMap.putAll(loadRandomsFromSection(randomsSection));
    }

    public static ConcurrentHashMap<String, BaseSection> loadRandomsFromSection(ConfigurationSection randomsSection) {
        ConcurrentHashMap<String, BaseSection> baseSectionMap = new ConcurrentHashMap<>();
        for (String randomKey : randomsSection.getKeys(false)) {
            ConfigurationSection randomSection = randomsSection.getConfigurationSection(randomKey);
            String type = null;
            if (randomSection != null) {
                type = randomSection.getString("type");
            }
            if (type == null) {
                continue;
            }
            for (BaseSectionType sectionType : BaseSectionType.getSectionTypes()) {
                sectionType.loadIfSameType(type, randomSection, baseSectionMap);
            }
        }
        return baseSectionMap;
    }

    //From https://github.com/TabooLib/TabooLib/
    public static NBTCompound translateSection(NBTCompound nbt,
                                               ConfigurationSection section,
                                               ComplexData data) {
        for (String key : section.getKeys(false)) {
            Object obj = section.get(key);
            NBTBase base;
            if (obj instanceof ConfigurationSection) {
                base = translateSection(new NBTCompound(), section.getConfigurationSection(key), data);
            } else if ((base = toNBT(obj, data)) == null) {
                TabooLib.getLogger().warn("Invalid Type: " + obj + " [" + obj.getClass().getSimpleName() + "]");
                continue;
            }
            sendDebug("&d  -> &b" + key + " &5= &e" + base.toString());
            nbt.put(key, base);
        }
        return nbt;
    }

    //From https://github.com/TabooLib/TabooLib/
    private static NBTList translateList(NBTList nbtListBase,
                                         List<?> list,
                                         ComplexData data) {
        for (Object object : list) {
            NBTBase base = toNBT(object, data);
            if (base == null) {
                TabooLib.getLogger().warn("Invalid Type: " + object + " [" + object.getClass().getSimpleName() + "]");
            } else {
                nbtListBase.add(base);
            }
        }
        return nbtListBase;
    }

    //From https://github.com/TabooLib/TabooLib/
    private static NBTBase toNBT(Object object, ComplexData data) {
        String string = String.valueOf(object);
        if (object instanceof NBTBase) {
            return (NBTBase) object;
        } else if (object instanceof String
                && !string.contains("double:")
                && !string.contains("integer:")
                && !string.contains("float:")
                && !string.contains("short:")
                && !string.contains("long:")
                && !string.contains("byte:")
                && !string.contains("compound:")) {
            return PATTERN.matcher(object.toString()).matches() ? toNBT(Short.valueOf(object.toString().substring(0, object.toString().length() - 1)), data) : new NBTBase(replaceAll((String) object, data));
        } else if (object instanceof Integer || string.contains("integer:")) {
            if (string.contains(":")) {
                string = string.split(":")[1];
                //这里先用parseDouble取处double值 再强转为int的原因是防止parseInteger读小数点报错
                return new NBTBase((int) (Double.parseDouble(replaceAll(string, data))));
            } else {
                return new NBTBase((int) object);
            }
        } else if (object instanceof Double || string.contains("double:")) {
            if (string.contains(":")) {
                string = string.split(":")[1];
                return new NBTBase(Double.parseDouble(replaceAll(string, data)));
            } else {
                return new NBTBase((double) object);
            }
        } else if (object instanceof Float || string.contains("float:")) {
            if (string.contains(":")) {
                string = string.split(":")[1];
                return new NBTBase(Float.parseFloat(replaceAll(string, data)));
            } else {
                return new NBTBase((float) object);
            }
        } else if (object instanceof Short || string.contains("short:")) {
            if (string.contains(":")) {
                string = string.split(":")[1];
                //这里先用parseDouble取处double值 再强转为short的原因是防止parseShort读小数点报错
                return new NBTBase((short) Double.parseDouble(replaceAll(string, data)));
            } else {
                return new NBTBase((short) object);
            }
        } else if (object instanceof Long || string.contains("long:")) {
            if (string.contains(":")) {
                string = string.split(":")[1];
                //这里先用parseDouble取处double值 再强转为long的原因是防止parseLong读小数点报错
                return new NBTBase((long) Double.parseDouble(replaceAll(string, data)));
            } else {
                return new NBTBase((long) object);
            }
        } else if (object instanceof Byte || string.contains("byte:")) {
            if (string.contains(":")) {
                string = string.split(":")[1];
                //这里先用parseDouble取处double值 再强转为byte的原因是防止parseByte读小数点报错
                return new NBTBase((byte) Double.parseDouble(replaceAll(string, data)));
            } else {
                return new NBTBase((byte) object);
            }
        } else if (object instanceof byte[]) {
            return new NBTBase((byte[]) object);
        } else if (object instanceof int[]) {
            return new NBTBase((int[]) object);
        } else if (object instanceof List) {
            return translateList(new NBTList(), (List<?>) object, data);
        } else {
            NBTCompound nbtCompound;
            if (object instanceof Map) {
                nbtCompound = new NBTCompound();
                ((Map<?, ?>) object).forEach((key, value) -> nbtCompound.put(key.toString(), toNBT(value, data)));
                return nbtCompound;
            } else if (object instanceof ConfigurationSection) {
                nbtCompound = new NBTCompound();
                ((ConfigurationSection) object).getValues(false).forEach((key, value) -> nbtCompound.put(key, toNBT(value, data)));
                return nbtCompound;
            } else {
                return new NBTBase("error: " + object);
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
            String string = RandomItemUtils.handleReplaced(replaced, data);
            if (string != null) {
                sendDebug("&d  - &b" + "<" + replaced + ">" + " &5= &e" + string.replace("\n", "&f\\n"));
                value = value.replace("<" + replaced + ">", string);
            }
        }
        return value;
    }

    public static List<String> extractMessageByTriangularBrackets(String msg) {

        List<String> list = new ArrayList<>();
        Matcher m = PATTERN2.matcher(msg);
        while (m.find()) {
            list.add(m.group().substring(1, m.group().length() - 1));
        }
        return list;
    }

    public static List<String> interceptRedundant(String text) {
        ArrayList<String> strings = new ArrayList<>();
        int start = 0, end = 0;
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '/') {
                if (count == 0) {
                    start = i;
                }
                count++;
            }
            if (text.charAt(i) == '\\') {
                count--;
                if (count == 0) {
                    end = i;
                    strings.add(text.substring(start, end + 1));
                }
            }
        }
        return strings;
    }

    /**
     * To get the List of the strings between "<" and ">"
     *
     * @param text the text
     * @return the List of the strings between "<" and ">"
     */
    public static List<String> intercept(String text) {
        ArrayList<String> strings = new ArrayList<>();
        for (String redundant : interceptRedundant(text)) {
            text = text.replace(redundant, "");
        }
        int start = 0, end = 0;
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '<') {
                if (count == 0) {
                    start = i;
                }
                count++;
            }
            if (text.charAt(i) == '>') {
                count--;
                if (count == 0) {
                    end = i;
                    strings.add(text.substring(start + 1, end));
                }
            }
        }
        return strings;
    }

    public static String handleReplaced(String replaced, ComplexData data) {
        ConcurrentHashMap<String, BaseSection> sectionMap = data.getSectionMap();
        ConcurrentHashMap<String, List<String>> alreadySectionMap = data.getAlreadySectionMap();
        String string = null;
        String otherKey;
        if (replaced.contains(".")) {
            if (replaced.split("\\.").length > 0) {
                otherKey = replaced.split("\\.")[0];
                if (otherKey.contains("<") && !otherKey.contains(">")) {
                    otherKey = otherKey.split("<")[1];
                    otherKey = handleStringReplaced(otherKey, data);
                }
                {
                    if (!alreadySectionMap.containsKey(otherKey)) {
                        BaseSection section = sectionMap.get(otherKey);
                        if (section != null) {
                            section.handleSection(replaced, data);
                        }
                    }
                    List<String> stringList = alreadySectionMap.get(otherKey);
                    if (checkNull(stringList, "Wrong section ID in " + replaced + "!")) {
                        return replaced;
                    }
                    string = RandomItemUtils.listToStringWithNext(RandomItemUtils.getStrings(replaced, stringList, data));
                }
            }
        } else {
            if (sectionMap.containsKey(replaced)) {
                otherKey = replaced;
                if (!alreadySectionMap.containsKey(otherKey)) {
                    BaseSection section = sectionMap.get(otherKey);
                    section.handleSection(replaced, data);
                }
                string = RandomItemUtils.listToStringWithNext(alreadySectionMap.get(otherKey));
            }
        }
        return string;
    }

    public static String replaceAll(String string, ComplexData data) {
        string = handleStringReplaced(string, data);
        return getMessage(RandomItemUtils.replacePAPI(string, data.getPlayer()));
    }

    public static boolean checkNull(Object object, String message) {
        if (object == null) {
            Main.sendMessage(getPrefix() + "&4" + message);
            return true;
        }
        return false;
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
