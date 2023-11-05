package com.liverpool.customers.repository;

import com.liverpool.customers.entities.Customers;
import com.liverpool.customers.response.CustomerResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomersRepository extends JpaRepository<Customers,Long> {
    Customers findByCustomerNameIgnoreCase(String fullName);
}
