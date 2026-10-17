package com.marquesdev.park_api.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ParkingDiscountUtils {

    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("0.30");
    private static final long DISCOUNT_FREQUENCY = 10;

    private ParkingDiscountUtils() {}

    public static  BigDecimal calculateDiscount(BigDecimal fee, long parkingCount){
        if (parkingCount <= 0 || parkingCount % DISCOUNT_FREQUENCY != 0){
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }

        return fee.multiply(DISCOUNT_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
