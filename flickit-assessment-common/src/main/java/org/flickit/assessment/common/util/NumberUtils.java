package org.flickit.assessment.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtils {

    private static final double DEFAULT_PRECISION = 0.001;

    public static boolean isLessThanWithPrecision(double a, double b) {
        return isLessThan(a, b, DEFAULT_PRECISION);
    }

    public static boolean isLessThan(double a, double b, double precision) {
        return a < b && Math.abs(a - b) >= precision;
    }
}
