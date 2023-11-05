package com.liverpool.orders.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "ordersdetail")
@Entity
@Builder
public class OrdersDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "detailid")
    @JsonProperty("detailID")
    private Long detailID;

    @Column(name = "productid")
    @JsonProperty("productID")
    private Long productID;

    @Column(name = "amount")
    @JsonProperty("amount")
    private Double amount;

    @Column(name = "purchaseditems")
    @JsonProperty("purchasedItems")
    private Integer purchasedItems;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orderid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Orders orders;
}