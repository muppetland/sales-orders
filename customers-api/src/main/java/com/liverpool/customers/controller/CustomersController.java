package com.liverpool.customers.controller;

import com.liverpool.customers.dto.CustomerDTO;
import com.liverpool.customers.entities.Customers;
import com.liverpool.customers.logs.LogsHandle;
import com.liverpool.customers.response.CustomerResponse;
import com.liverpool.customers.service.CustomersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
public class CustomersController {
    private static final Logger logTracking = LoggerFactory.getLogger(CustomersController.class);
    private static final LogsHandle logsHandle = new LogsHandle();

    @Autowired
    private CustomersService customersService;

    @GetMapping("/{customerID}")
    public ResponseEntity<Customers> getCustomerByID(@PathVariable(value = "customerID", required = true) Long customerID, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to get information by customerID.", httpServletRequest);
        return new ResponseEntity<Customers>(customersService.getCustomerByID(customerID, logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @GetMapping("/getCustomerByName")
    public ResponseEntity<CustomerResponse> getCustomerByCustomerNameID(@RequestParam(name = "customerName", required = true) String customerName, Long customerID, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to get information by customerName.", httpServletRequest);
        return new ResponseEntity<CustomerResponse>(customersService.getCustomerByName(customerName, logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @GetMapping("/getAllCustomers")
    public ResponseEntity<List<Customers>> getAllCustomers(HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to get information for all customers registered.", httpServletRequest);
        return new ResponseEntity<List<Customers>>(customersService.getAllCustomers(logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @PostMapping("/newCustomer")
    public ResponseEntity<Customers> newCustomer(@RequestBody Customers customers, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to create a new customer.", httpServletRequest);
        return new ResponseEntity<Customers>(customersService.newCustomer(customers, logsHandle, httpServletRequest), HttpStatus.CREATED);
    }

    @PutMapping("/updateCustomer")
    public ResponseEntity<Customers> updateCustomerByID(@RequestBody CustomerDTO customerDTO, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to update data from customerID.", httpServletRequest);
        return new ResponseEntity<Customers>(customersService.updateCustomerByID(customerDTO, logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @DeleteMapping("/deleteCustomer/{customerID}")
    public ResponseEntity<?> deleteCustomerByID(@PathVariable(value = "customerID", required = true) Long customerID, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to delete a customer by customerID.", httpServletRequest);
        customersService.deleteCustomerByID(customerID, logsHandle, httpServletRequest);
        return new ResponseEntity<>("Customer has been removed.", HttpStatus.OK);
    }

}
