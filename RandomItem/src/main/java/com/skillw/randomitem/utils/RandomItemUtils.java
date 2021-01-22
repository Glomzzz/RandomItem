package com.skillw.randomitem.utils;

import com.skillw.randomitem.Main;
import com.skillw.randomitem.WeightRandom;
import javafx.util.Pair;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.skillw.randomitem.Main.sendMessage;

/**
 * @author Glom_
 * @date 2020/10/25 19:53
 */
public class RandomItemUtils {
    private static boolean isRightFormat = true;

    /**
     * 使用正则表达式提取三角括号中的内容
     *
     * @param msg 字符串
     * @return 三角括号中的内容的字符串集合
     */
    public static List<String> extractMessageByTriangularBrackets(String msg) {

        List<String> list = new ArrayList<>();
        Pattern p = Pattern.compile("(<[^>]*>)");
        Matcher m = p.matcher(msg);
        while (m.find()) {
            list.add(m.group().substring(1, m.group().length() - 1));
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
        Pattern p = Pattern.compile("(\\{[^}]*})");
        Matcher m = p.matcher(msg);
        while (m.find()) {
            list.add(m.group().substring(1, m.group().length() - 1));
        }
        return list;
    }

    /**
     * 替换字符从&到§
     *
     * @param msg 文本
     * @return 替换后的文本
     */
    public static String getMessage(final String msg) {
        if (msg != null) {
            return msg.replace("&", "§");
        } else {
            return msg;
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

    public static List<File> getSubFilesFromFile(File file) {
        List<File> files = new ArrayList<>();
        if (file != null) {
            File[] tempList = file.listFiles();
            if (tempList != null) {
                for (File value : tempList) {
                    if (value != null) {
                        if (value.isFile()) {
                            files.add(value);
                        }
                        if (value.isDirectory()) {
                            files.addAll(getSubFilesFromFile(value));
                        }
                    }
                }
            }
        }
        return files;
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

    public static String doReplace(String text, UUID uuid) {
        {
            String newFormula = text;
            List<String> holders = extractMessageByBigBrackets(newFormula);
            if (holders.isEmpty()) {
                return newFormula;
            }
            for (String holder : holders) {
                if (holder.matches(".*papi.*")) {
                    String papiHolderKey = holder.split("papi.")[1];
                    holder = "%" + papiHolderKey + "%";
                    holder = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(uuid), holder);
                    newFormula = newFormula.replace("{" + holder + "}", holder);
                }
                if (holder.matches(".*papi.*")) {
                    String papiHolderKey = holder.split("papi.")[1];
                    holder = holder.replace("%" + papiHolderKey + "%", "" + 0);
                    newFormula = newFormula.replace("{" + holder + "}", holder);
                }
            }
            return newFormula;
        }
    }

    public static LivingEntity getLivingEntityByUuid(UUID id) {
        if (Bukkit.getServer().getPlayer(id) != null) {
            return Bukkit.getServer().getPlayer(id);
        }
        return (LivingEntity) Main.getInstance().getServer().getEntity(id);
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

    public static String getRandomString(List<String> strings) {
        List<String> newValue = new ArrayList<>();
        for (String text : strings) {
            if (text.matches(".*<next>.*")) {
                for (String line : text.split("<next>")) {
                    if (line != null && !line.isEmpty()) {
                        newValue.add(getMessage(line));
                    }
                }
            }
        }
        int maxIndex = newValue.size() - 1;
        if (maxIndex == 0) {
            return getMessage(newValue.get(maxIndex));
        } else {
            return getWeightRandomString(newValue);
        }
    }

    public static String getRandomStringWithNext(List<String> strings) {
        int maxIndex = strings.size() - 1;
        if (maxIndex == 0) {
            return getMessage(strings.get(maxIndex));
        } else {
            return getWeightRandomString(strings);
        }
    }


    public static List<String> getRandomStrings(List<String> strings) {
        int maxIndex = strings.size() - 1;
        List<String> newValue = new ArrayList<>();
        String text;
        if (maxIndex == 0) {
            text = getMessage(strings.get(maxIndex));
        } else {
            text = getWeightRandomString(strings);
        }
        if (text != null && text.matches(".*<next>.*")) {
            for (String line : text.split("<next>")) {
                if (line != null && !line.isEmpty()) {
                    newValue.add(getMessage(line));
                }
            }
        }
        return newValue;
    }

    public static String getWeightRandomString(List<String> strings) {
        if (strings.get(0).matches(".*->\\|.*") && strings.get(0).split("->\\|")[0].matches("^[1-9]\\d*$")) {
            List<Pair<String, Double>> pairs = new ArrayList<>();
            for (String string : strings) {
                pairs.add(new Pair<>(string.split("->\\|")[1], getDouble(string.split("->\\|")[0])));
            }
            WeightRandom<String, Double> stringDoubleWeightRandom = new WeightRandom<>(pairs);
            return getMessage(stringDoubleWeightRandom.random());
        } else {
            int random = RandomItemUtils.getRandom(0, strings.size() - 1);
            return getMessage(strings.get(random));
        }
    }

    public static String listToString(List<String> strings) {
        return strings.toString().replace(", ", "").replace("[", "").replace("]", "");
    }

    public static String listToStringWithNext(List<String> strings) {
        return strings.toString().replace(", ", "<next>").replace("[", "").replace("]", "");
    }

    public static List<String> getStrings(String replace, String withNext) {
        String[] splits = withNext.split("<next>");
        List<String> strings = new ArrayList<>();
        if (replace.matches(".*\\..*")) {
            String numbers = replace.split("\\.")[1];
            int maxIndex = splits.length - 1;
            for (int i : getNumbers(numbers, maxIndex)) {
                if (splits[i] != null && !splits[i].isEmpty()) {
                    strings.add(splits[i]);
                }
            }
        } else {
            if (withNext.matches(".*<next>.*")) {
                Collections.addAll(strings, withNext.split("<next>"));
            }
        }
        return strings;
    }

    public static List<Integer> getNumbers(String numbers, int maxIndex) {
        List<Integer> integers = new ArrayList<>();
        if (numbers != null && !numbers.isEmpty()) {
            if (numbers.matches(".*,.*")) {
                String[] numbers1 = numbers.split(",");
                for (String number : numbers1) {
                    if (number != null && !number.isEmpty()) {
                        getNumbers_(number, maxIndex, integers);
                    }
                }
            } else {
                getNumbers_(numbers, maxIndex, integers);
            }
        }
        return integers;
    }

    private static void getNumbers_(String numbers, int maxIndex, List<Integer> integers) {
        if (numbers.matches(".*-.*")) {
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

    public static void loadRandom(ConcurrentHashMap<String, List<String>> stringsMap, ConcurrentHashMap<String, String> numbersMap, ConfigurationSection randomsSection) {
        Main.getRandomsBySection(stringsMap, numbersMap, randomsSection);
        {
            ConcurrentHashMap<String, List<String>> map = ConfigUtils.getStringsMap();
            if (!map.isEmpty()) {
                for (String key : map.keySet()) {
                    List<String> value = map.get(key);
                    if (!stringsMap.containsKey(key)) {
                        stringsMap.put(key, value);
                    }
                }
            }
        }
        {
            ConcurrentHashMap<String, String> map = ConfigUtils.getNumbersMap();
            if (!map.isEmpty()) {
                for (String key : map.keySet()) {
                    String value = map.get(key);
                    if (!numbersMap.containsKey(key)) {
                        numbersMap.put(key, value);
                    }
                }
            }
        }
    }

}
