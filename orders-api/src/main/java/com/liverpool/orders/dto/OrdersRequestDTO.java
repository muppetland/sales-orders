package com.liverpool.orders.dto;

import com.liverpool.orders.response.OrdersDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class OrdersRequestDTO {
    private String customerName;
    private List<OrdersDetailResponse> purchasedItems;

    public OrdersRequestDTO() {
        //add constructor with array to store all purchased items...
        purchasedItems = new ArrayList<>();
    }
}
