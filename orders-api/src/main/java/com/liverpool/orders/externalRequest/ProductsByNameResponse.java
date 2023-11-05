package com.liverpool.orders.externalRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductsByNameResponse {
    private Long productID;
    private String description;
    private Double amount;
    private Integer stock;
    private Boolean activate;
}
