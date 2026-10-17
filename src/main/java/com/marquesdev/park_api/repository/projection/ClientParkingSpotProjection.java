package com.marquesdev.park_api.repository.projection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ClientParkingSpotProjection {

     UUID getId();
     String getBrand();
     String getModel();
     String getColor();
     String getLicensePlate();
     String getClientCpf();
     String getReceipt();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     LocalDateTime getEntryDate();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

     LocalDateTime getExitDate();
     String getParkingCode();
     BigDecimal getPrice();
     BigDecimal getDiscount();
}
