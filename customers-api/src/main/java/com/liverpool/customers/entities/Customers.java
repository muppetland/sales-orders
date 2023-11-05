package com.liverpool.customers.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "customers")
@Entity
@Builder
public class Customers {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customerid")
    @JsonProperty("customerID")
    private Long customerID;

    @Column(name = "customername")
    @JsonProperty("customerName")
    private String customerName;

    @Column(name = "datebirth")
    @JsonProperty("dateBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private java.time.LocalDate dateBirth;

    @Column(name = "activate")
    private Boolean activate;
}
