package com.marquesdev.park_api.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ParkingFeeUtils {

    private static final BigDecimal FEE_UP_TO_15_MINUTES = new BigDecimal("5.00");
    private static final BigDecimal FEE_UP_TO_60_MINUTES = new BigDecimal("9.25");
    private static final BigDecimal FEE_PER_ADDITIONAL_15_MINUTES = new BigDecimal("1.75");

    private ParkingFeeUtils(){}

    public static BigDecimal calculateFee(LocalDateTime entryDate, LocalDateTime exitDate){
      long minutes = ChronoUnit.MINUTES.between(entryDate, exitDate);

      if (minutes < 0){
          throw new IllegalArgumentException("Exit date cannot be before entry date");
      }
      if (minutes <= 15){
          return FEE_UP_TO_15_MINUTES.setScale(2, RoundingMode.HALF_EVEN);
      }
      if (minutes <= 60){
          return FEE_UP_TO_60_MINUTES.setScale(2, RoundingMode.HALF_EVEN);
      }

      long additionalMinutes = minutes -60;
      long additionalBlocks = (additionalMinutes + 14) / 15;
      return FEE_UP_TO_60_MINUTES
              .add(FEE_PER_ADDITIONAL_15_MINUTES.multiply(BigDecimal.valueOf(additionalBlocks)))
              .setScale(2, RoundingMode.HALF_EVEN);
    }
}
