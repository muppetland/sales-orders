package com.liverpool.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductDetailDTO {
    private String orderID;
    private Long productID;
    private Double amount;
    private Integer items;
}
