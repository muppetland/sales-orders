package com.liverpool.orders.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liverpool.orders.entities.Orders;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrdersDetailDTO {
    private Long detailID;
    private Long productID;
    private Double amount;
    private Integer purchasedItems;
    @JsonIgnore
    private Orders orders;
}
