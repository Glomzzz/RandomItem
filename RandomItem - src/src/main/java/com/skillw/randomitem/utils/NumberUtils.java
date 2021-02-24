package com.skillw.randomitem.utils;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName : com.skillw.randomitem.utils.NumberHandler
 * Created by Glom_ on 2021-02-15 17:37:53
 * Copyright  2020 user. All rights reserved.
 */
public final class NumberUtils {
    private NumberUtils() {
    }

    /**
     * To format a number with fractionDigits and integerDigits
     *
     * @param number          A number
     * @param fixedDecimalMax the maximum number of digits allowed in the integer portion of a number
     * @param fixedDecimalMin the minimum number of digits allowed in the integer portion of a number
     * @param fixedIntegerMax the maximum number of digits allowed in the fraction portion of a number
     * @param fixedIntegerMin the minimum number of digits allowed in the fraction portion of a number
     * @return the number after format
     */
    public static double format(double number, int fixedIntegerMax, int fixedIntegerMin, int fixedDecimalMax, int fixedDecimalMin) {
        fixedDecimalMax = Math.max(fixedDecimalMax, fixedDecimalMin);
        if (fixedDecimalMax == 0) {
            return Math.round(number);
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(false);
        if (fixedIntegerMin != 0) {
            numberFormat.setMinimumIntegerDigits(fixedIntegerMin);
        }
        if (fixedDecimalMin != 0) {
            numberFormat.setMinimumFractionDigits(fixedDecimalMin);
        }
        if (fixedIntegerMax != -1) {
            numberFormat.setMaximumIntegerDigits(fixedIntegerMax);
        }
        if (fixedDecimalMax != -1) {
            numberFormat.setMaximumFractionDigits(fixedDecimalMax);
        }
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
        return Double.parseDouble(numberFormat.format(number));
    }

    /**
     * To get a random double
     *
     * @param start           the min value
     * @param end             the max value
     * @param fixedDecimalMax the maximum number of digits allowed in the integer portion of a number
     * @param fixedDecimalMin the minimum number of digits allowed in the integer portion of a number
     * @param fixedIntegerMax the maximum number of digits allowed in the fraction portion of a number
     * @param fixedIntegerMin the minimum number of digits allowed in the fraction portion of a number
     * @param decimal         how many decimal places
     * @return A random double
     */
    public static double getRandom(double start, double end, int fixedIntegerMax, int fixedIntegerMin, int fixedDecimalMax, int fixedDecimalMin, int decimal) {
        if (start > end) {
            return Math.max(start, 0);
        } else {
            double decimalNumber = decimal != 0 ? format(Math.random(), -1, 0, decimal, 0) : 0;
            double x = Math.round((Math.random() * (end - start + 1)) + start) + decimalNumber;
            return format(Math.min(x, end), fixedIntegerMax, fixedIntegerMin, fixedDecimalMax, fixedDecimalMin);
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
}
