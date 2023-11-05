package com.liverpool.orders.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrdersDetailResponse {
    private String description;
    private Double amount;
    private Integer items;
}
