package com.liverpool.orders.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class OrdersByDateResponse {
    private String startDate;
    private String endDate;
    private Integer pageNo;
    private Integer pageSize;
    private Integer ordersDisplayed;
    private Long recodsTotal;
    private Integer recodsByPages;
    private List<OrdersResponse> orders;

    public OrdersByDateResponse() {
        orders = new ArrayList<>();
    }
}
