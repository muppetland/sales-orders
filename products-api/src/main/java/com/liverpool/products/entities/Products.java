package com.liverpool.products.entities;

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
@Table(name = "products")
@Entity
@Builder
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "productid")
    @JsonProperty("productID")
    private Long productID;

    @Column(name = "description",length = 100)
    @JsonProperty("description")
    private String description;

    @Column(name = "amount")
    @JsonProperty("amount")
    private Double amount;

    @Column(name = "stock")
    @JsonProperty("stock")
    private Integer stock;

    @Column(name = "registrationdatetime")
    @JsonProperty("registrationDateTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime registrationDateTime;

    @Column(name = "lastpurchase")
    @JsonProperty("lastPurchase")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private java.time.LocalDateTime lastPurchase;

    @Column(name = "activate")
    private Boolean activate;
}
