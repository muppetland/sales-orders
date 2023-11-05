package com.liverpool.orders.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrdersDTO {
    private String orderID;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private java.time.LocalDateTime purchasedate;
    private Long customerID;
    private Double subTotal;
    private Double tax;
    private Double totalAmount;
    private Integer purchasedItems;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private java.time.LocalDateTime cancellationDateTime;
    private String status;
}
