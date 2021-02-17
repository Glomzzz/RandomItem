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
     * @param start   the min value
     * @param end     the max value
     * @param fixed   how many places after "."
     * @param decimal how many decimal places
     * @return A random double
     */
    public static double getRandom(double start, double end, int fixed, int decimal) {
        if (start > end) {
            return Math.max(start, 0);
        } else {
            double decimalNumber = format(Math.random(), decimal);
            double x = (Math.random() * (end - start + 1)) + start + decimalNumber;
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
