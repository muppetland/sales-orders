package com.liverpool.orders.entities;

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
@Table(name = "orders")
@Entity
@Builder
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "orderid")
    @JsonProperty("orderID")
    private String orderID;

    @Column(name = "purchasedatetime")
    @JsonProperty("purchasedatetime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss")
    private java.time.LocalDateTime purchasedate;

    @Column(name = "customerid")
    @JsonProperty("customerID")
    private Long customerID;

    @Column(name = "subtotal")
    @JsonProperty("subTotal")
    private Double subTotal;

    @Column(name = "tax")
    @JsonProperty("tax")
    private Double tax;

    @Column(name = "totalamount")
    @JsonProperty("totalAmount")
    private Double totalAmount;

    @Column(name = "purchaseditems")
    @JsonProperty("purchasedItems")
    private Integer purchasedItems;

    @Column(name = "cancellationdatetime")
    @JsonProperty("cancellationDateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss")
    private java.time.LocalDateTime cancellationDateTime;

    @Column(name = "status")
    private String status;
}
