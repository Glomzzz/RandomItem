package com.skillw.randomitem.utils;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.api.object.ItemCompute;
import com.skillw.randomitem.api.object.Pair;
import com.skillw.randomitem.api.object.SubString;
import com.skillw.randomitem.api.object.WeightRandom;
import com.skillw.randomitem.string.SubStringImpl;
import com.skillw.randomitem.weight.PairImpl;
import com.skillw.randomitem.weight.WeightRandomImpl;
import io.izzel.taboolib.TabooLib;
import io.izzel.taboolib.module.nms.nbt.NBTBase;
import io.izzel.taboolib.module.nms.nbt.NBTCompound;
import io.izzel.taboolib.module.nms.nbt.NBTList;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.skillw.randomitem.Main.sendMessage;

/**
 * @author Glom_
 * @date 2020/10/25 19:53
 */
public class RandomItemUtils {
    protected static final Pattern PATTERN = Pattern.compile("\\d+s");
    private static final Pattern PATTERN1 = Pattern.compile("(\\{[^}]*})");
    private static final Pattern PATTERN2 = Pattern.compile("(<[^>]*>)");
    private static boolean isRightFormat = true;

    /**
     * 使用正则表达式提取三角括号中的内容
     *
     * @param msg 字符串
     * @return 三角括号中的内容的字符串集合
     */
    public static List<String> extractMessageByTriangularBrackets(String msg) {

        List<String> list = new ArrayList<>();
        Matcher matcher = PATTERN2.matcher(msg);
        while (matcher.find()) {
            list.add(matcher.group().substring(1, matcher.group().length() - 1));
        }
        return list;
    }

    /**
     * 使用正则表达式提取中括号中的内容
     *
     * @param msg 字符串
     * @return 括号中的内容的字符串集合
     */
    public static List<String> extractMessageByBigBrackets(String msg) {

        List<String> list = new ArrayList<>();
        Matcher matcher = PATTERN1.matcher(msg);
        while (matcher.find()) {
            list.add(matcher.group().substring(1, matcher.group().length() - 1));
        }
        return list;
    }

    /**
     * 替换字符从&到§
     *
     * @param msg 文本
     * @return 替换后的文本
     */
    public static String getMessage(String msg) {
        if (msg != null) {
            return msg.replace("&", "§");
        } else {
            return null;
        }
    }

    public static String messageToOriginalText(String msg) {
        if (msg != null) {
            return msg.replace("§", "&");
        } else {
            return null;
        }
    }

    /**
     * 移除字符串的颜色
     *
     * @param msg 字符串
     * @return 移除颜色后的
     */
    public static String removeColor(final String msg) {
        return ChatColor.stripColor(msg);
    }

    /**
     * 取数值
     *
     * @param lore 传入的物品Lore
     * @return 获取的数值
     */
    public static double getDouble(String lore) {
        return Double.parseDouble(removeColor(lore).replaceAll("[^0-9.+-]", ""));
    }

    public static double getResult(final String formula) {
        double returnValue = 0;
        try {
            returnValue = doAnalysis(formula);
        } catch (final NumberFormatException nfe) {
            nfe.printStackTrace();
            sendMessage("公式格式有误，请检查:" + formula);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        if (!isRightFormat) {
            sendMessage("公式格式有误，请检查:" + formula);
        }
        return returnValue;
    }

    private static double doAnalysis(String formula) {
        double returnValue = 0;
        final LinkedList<Integer> stack = new LinkedList<>();
        int curPos;
        String beforePart;
        String afterPart;
        String calculator;
        isRightFormat = true;
        while (isRightFormat && (formula.indexOf('(') >= 0 || formula.indexOf(')') >= 0)) {
            curPos = 0;
            for (final char s : formula.toCharArray()) {
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
                        sendMessage("有未关闭的右括号！");
                        isRightFormat = false;
                    }
                }
                curPos++;
            }
            if (stack.size() > 0) {
                sendMessage("有未关闭的左括号！");
                break;
            }
        }
        if (isRightFormat) {
            returnValue = doCalculation(formula);
        }
        return returnValue;
    }

    private static double doCalculation(final String formula) {
        final ArrayList<Double> values = new ArrayList<>();
        final ArrayList<String> operators = new ArrayList<>();
        int curPos = 0;
        int prePos = 0;
        int minus = 0;
        for (final char s : formula.toCharArray()) {
            final boolean is4 = (s == '+' || s == '-' || s == '*' || s == '/') && minus != 0 && minus != 2;
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

    public static double getRandom(double start, double end, int fixed) {
        if (fixed == 0) {
            return getRandom((int) start, (int) end);
        }
        if ((start > end)) {
            return Math.max(start, 0);
        } else {
            double x = (Math.random() * (end - start + 1)) + start;
            return Math.min(format(x, fixed), format(end, fixed));
        }
    }

    public static int getRandom(int start, int end) {
        if ((start > end)) {
            return Math.max(start, 0);
        } else {
            double x = (Math.random() * (end - start + 1)) + start;
            if (x < 0) {
                return 0;
            } else {
                return (int) Math.min(Math.round(x), end);
            }
        }
    }

    public static List<String> getRandomStrings(List<SubString> subString) {
        if (subString.size() == 1) {
            List<String> strings = subString.get(0).getStrings();
            strings.forEach(s -> s = getMessage(s));
            return strings;
        } else {
            return getWeightRandomString(subString);
        }
    }

    public static String doPAPIReplace(String text, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !Main.getInstance().papi) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    public static String doPAPIReplace(String text, Player player) {
        if (!Main.getInstance().papi) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }

    public static List<String> getWeightRandomString(List<SubString> subStrings) {
        List<Pair<List<String>, Double>> pairs = new ArrayList<>();
        for (SubString subString : subStrings) {
            pairs.add(new PairImpl<>(subString.getStrings(), subString.getWeight()));
        }
        WeightRandom<List<String>, Double> stringDoubleWeightRandom = new WeightRandomImpl<>(pairs);
        return stringDoubleWeightRandom.random();
    }

    public static String listToStringWithNext(List<String> strings) {
        return strings.toString().replace(", ", "\n").replace("[", "").replace("]", "");
    }

    public static List<String> getStrings(String replace, List<String> stringList) {
        List<String> strings = new ArrayList<>();
        if (replace.contains(".")) {
            String numbers = replace.split("\\.")[1];
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

    public static void addGlobalRandom(ConcurrentHashMap<String, List<SubString>> stringsMap, ConcurrentHashMap<String, String> numberMap, ItemCompute itemCompute) {
        {
            ConcurrentHashMap<String, List<SubString>> map = ConfigUtils.getGlobalStringsMap();
            if (!map.isEmpty()) {
                for (String key : map.keySet()) {
                    List<SubString> value = map.get(key);
                    if (stringsMap.containsKey(key)) {
                        continue;
                    }
                    stringsMap.put(key, value);
                }
            }
        }
        {
            ConcurrentHashMap<String, String> map = ConfigUtils.getGlobalNumbersMap();
            if (!map.isEmpty()) {
                for (String key : map.keySet()) {
                    String value = map.get(key);
                    if (numberMap.containsKey(key)) {
                        continue;
                    }
                    numberMap.put(key, value);
                }
            }
        }
        {
            ItemCompute globalItemCompute = ConfigUtils.getGlobalItemCompute();
            if (!globalItemCompute.getComputeMap().isEmpty()) {
                for (String key : globalItemCompute.getComputeMap().keySet()) {
                    String formula = globalItemCompute.getComputeMap().get(key);
                    String max = globalItemCompute.getMaxMap().get(key);
                    if (itemCompute.getComputeMap().containsKey(key)) {
                        continue;
                    }
                    itemCompute.getComputeMap().put(key, formula);
                    itemCompute.getMaxMap().put(key, max);  //注意这里
                    // 我是知道我写的MaxMap和ComputeMap的每个Key是永远一样的 且永远有值
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
        for (int i = 0; i < allFiles.length; i++) {
            File subFile = allFiles[i];
            if (subFile.isFile()) {
                files.add(subFile);
            } else {
                files.addAll(getSubFilesFromFile(subFile));
            }
        }
        return files;
    }

    public static void pointHandle(String pointData,
                                   ConcurrentHashMap<String, List<SubString>> subStringMap,
                                   ConcurrentHashMap<String, String> numberMap,
                                   ConcurrentHashMap<String, String> computeMap,
                                   ConcurrentHashMap<String, List<String>> alreadyStringsMap) {
        pointData = pointData.replace("[", "").replace("]", "").replace(" ", "");
        for (String text : pointData.split(",")) {
            if (text != null && !text.isEmpty()) {
                if (text.contains(":")) {
                    String id = text.split(":")[0];
                    String value = text.split(":")[1];
                    if (subStringMap.containsKey(id)) {
                        CopyOnWriteArrayList<SubString> subStrings = new CopyOnWriteArrayList<>(subStringMap.get(id));
                        subStrings.forEach(subString -> {
                            if (!subString.getID().equals(value)) {
                                subStrings.remove(subString);
                            }
                        });
                        subStringMap.put(id, subStrings);
                        alreadyStringsMap.put(id, subStrings.get(0).getStrings());
                    } else if (numberMap.containsKey(id)) {
                        numberMap.put(id, value);
                    } else if (computeMap.containsKey(id)) {
                        computeMap.put(id, value);
                    }
                }
            }
        }
    }

    public static void loadRandomsFromSection(ConcurrentHashMap<String, List<SubString>> stringsMap, ConcurrentHashMap<String, String> numberMap, ItemCompute itemCompute, ConfigurationSection randomsSection) {
        for (String randomKey : randomsSection.getKeys(false)) {
            ConfigurationSection randomSection = randomsSection.getConfigurationSection(randomKey);
            String type = randomSection.getString("type");
            switch (type) {
                case "number": {
                    String start = (randomSection.getString("start"));
                    String bound = (randomSection.getString("bound"));
                    String fixed = (randomSection.getString("fixed") != null) ? randomSection.getString("fixed") : "0";
                    numberMap.put(randomKey, start + "," + bound + "," + fixed);
                }
                break;
                case "strings": {
                    ConfigurationSection section = randomSection.getConfigurationSection("strings");
                    if (section != null) {
                        List<SubString> subStrings = new ArrayList<>();
                        for (String subStringKey : section.getKeys(false)) {
                            ConfigurationSection subStringSection = section.getConfigurationSection(subStringKey);
                            int weight = subStringSection.getInt("weight");
                            List<String> strings = subStringSection.getStringList("values");
                            if (!strings.isEmpty()) {
                                subStrings.add(new SubStringImpl(subStringKey, weight, strings));
                            }
                        }
                        stringsMap.put(randomKey, subStrings);
                    }
                }
                break;
                case "compute":
                    itemCompute.addComputeFromSection(randomSection);
                    break;
                default:
                    break;
            }
        }
    }

    public static NBTCompound translateSection(NBTCompound nbt,
                                               ConfigurationSection section,
                                               ConcurrentHashMap<String, List<SubString>> stringMap,
                                               ConcurrentHashMap<String, String> computeMap,
                                               ConcurrentHashMap<String, String> numberMap,
                                               ConcurrentHashMap<String, List<String>> alreadyStringsMap,
                                               Player player) {
        for (String key : section.getKeys(false)) {
            if (key.equals("Enchantments")) {
                continue;
            }
            Object object = section.get(key);
            NBTBase nbtBase;
            if (object instanceof ConfigurationSection) {
                nbtBase = translateSection(new NBTCompound(), section.getConfigurationSection(key), stringMap, computeMap, numberMap, alreadyStringsMap, player);
            } else if ((nbtBase = toNBT(object, stringMap, computeMap, numberMap, alreadyStringsMap, player)) == null) {
                TabooLib.getLogger().warn("无效类型: " + object + " [" + object.getClass().getSimpleName() + "]");
                continue;
            }
            nbt.put(key, nbtBase);
        }

        return nbt;
    }

    private static NBTList translateList(NBTList nbtListBase, List list, ConcurrentHashMap<String, List<SubString>> stringMap, ConcurrentHashMap<String, String> computeMap, ConcurrentHashMap<String, String> numberMap, ConcurrentHashMap<String, List<String>> alreadyStringsMap, Player player) {
        for (Object object : list) {
            NBTBase base = toNBT(object, stringMap, computeMap, numberMap, alreadyStringsMap, player);
            if (base == null) {
                TabooLib.getLogger().warn("Invalid Type: " + object + " [" + object.getClass().getSimpleName() + "]");
            } else {
                nbtListBase.add(base);
            }
        }
        return nbtListBase;
    }

    private static NBTBase toNBT(Object object, ConcurrentHashMap<String, List<SubString>> stringMap, ConcurrentHashMap<String, String> computeMap, ConcurrentHashMap<String, String> numberMap, ConcurrentHashMap<String, List<String>> alreadyStringsMap, Player player) {
        if (object instanceof NBTBase) {
            return (NBTBase) object;
        } else if (object instanceof String) {
            return PATTERN.matcher(object.toString()).matches() ? toNBT(Short.valueOf(object.toString().substring(0, object.toString().length() - 1)), stringMap, computeMap, numberMap, alreadyStringsMap, player) : new NBTBase(doReplace((String) object, stringMap, computeMap, numberMap, alreadyStringsMap, player));
        } else if (object instanceof Integer) {
            return new NBTBase(Integer.parseInt(doReplace(String.valueOf(object), stringMap, computeMap, numberMap, alreadyStringsMap, player)));
        } else if (object instanceof Double) {
            return new NBTBase(Double.parseDouble(doReplace(String.valueOf(object), stringMap, computeMap, numberMap, alreadyStringsMap, player)));
        } else if (object instanceof Float) {
            return new NBTBase(Float.parseFloat(doReplace(String.valueOf(object), stringMap, computeMap, numberMap, alreadyStringsMap, player)));
        } else if (object instanceof Short) {
            return new NBTBase(Short.parseShort(doReplace(String.valueOf(object), stringMap, computeMap, numberMap, alreadyStringsMap, player)));
        } else if (object instanceof Long) {
            return new NBTBase(Long.parseLong(doReplace(String.valueOf(object), stringMap, computeMap, numberMap, alreadyStringsMap, player)));
        } else if (object instanceof Byte) {
            return new NBTBase(Byte.parseByte(doReplace(String.valueOf(object), stringMap, computeMap, numberMap, alreadyStringsMap, player)));
        } else if (object instanceof byte[]) {
            return new NBTBase((byte[]) object);
        } else if (object instanceof int[]) {
            return new NBTBase((int[]) object);
        } else if (object instanceof List) {
            return translateList(new NBTList(), (List) object, stringMap, computeMap, numberMap, alreadyStringsMap, player);
        } else {
            NBTCompound nbtCompound;
            if (object instanceof Map) {
                nbtCompound = new NBTCompound();
                ((Map) object).forEach((key, value) -> nbtCompound.put(key.toString(), toNBT(value, stringMap, computeMap, numberMap, alreadyStringsMap, player)));
                return nbtCompound;
            } else if (object instanceof ConfigurationSection) {
                nbtCompound = new NBTCompound();
                ((ConfigurationSection) object).getValues(false).forEach((key, value) -> nbtCompound.put(key, toNBT(value, stringMap, computeMap, numberMap, alreadyStringsMap, player)));
                return nbtCompound;
            } else {
                return new NBTBase("error: " + object);
            }
        }
    }

    public static String doReplace(String string, ConcurrentHashMap<String, List<SubString>> subStringMap, ConcurrentHashMap<String, String> computeMap, ConcurrentHashMap<String, String> numberMap, ConcurrentHashMap<String, List<String>> alreadyStringMap, Player player) {
        for (String key : computeMap.keySet()) {
            String value = computeMap.get(key);
            string = string.replace("{" + key + "}", value);
        }
        {
            List<String> replaces = RandomItemUtils.extractMessageByTriangularBrackets(string);
            for (String replace : replaces) {
                for (String key : subStringMap.keySet()) {
                    if (replace.contains(key)) {
                        List<SubString> values = subStringMap.get(key);
                        List<String> strings;
                        if (!alreadyStringMap.containsKey(key)) {
                            strings = RandomItemUtils.getRandomStrings(values);
                        } else {
                            strings = alreadyStringMap.get(key);
                        }
                        string = string.replace("<" + replace + ">", RandomItemUtils.listToStringWithNext(RandomItemUtils.getStrings(replace, strings)));
                        alreadyStringMap.put(key, strings);
                    }
                }
            }

        }
        for (String key : numberMap.keySet()) {
            String value = numberMap.get(key);
            string = string.replace("<" + key + ">", value);
        }
        return getMessage(RandomItemUtils.doPAPIReplace(string, player));
    }

}
