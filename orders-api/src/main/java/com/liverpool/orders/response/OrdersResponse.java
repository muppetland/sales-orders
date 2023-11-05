package com.liverpool.orders.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class OrdersResponse {
    private String orderID;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private java.time.LocalDate datePurchase;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private java.time.LocalDate cancellationDate;
    private String customerName;
    private List<OrdersDetailResponse> purchasedItems;
    private String subtotal;
    private String tax;
    private String totalAmount;

    public OrdersResponse() {
        //add constructor with array to store all purchased items...
        purchasedItems = new ArrayList<>();
    }
}
