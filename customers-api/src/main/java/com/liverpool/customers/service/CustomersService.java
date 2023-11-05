package com.liverpool.customers.service;

import com.liverpool.customers.dto.CustomerDTO;
import com.liverpool.customers.entities.Customers;
import com.liverpool.customers.logs.LogsHandle;
import com.liverpool.customers.response.CustomerResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CustomersService {
    List<Customers> getAllCustomers(LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    Customers getCustomerByID(Long customerID, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    CustomerResponse getCustomerByName(String customerName, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    @Transactional
    Customers newCustomer(Customers customers, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    @Transactional
    Customers updateCustomerByID(CustomerDTO customerDTO, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    @Transactional
    void deleteCustomerByID(Long customerID, LogsHandle logsHandle, HttpServletRequest httpServletRequest);

}
