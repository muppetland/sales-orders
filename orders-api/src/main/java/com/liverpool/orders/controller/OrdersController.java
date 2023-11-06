package com.liverpool.orders.controller;

import com.liverpool.orders.dto.OrdersRequestDTO;
import com.liverpool.orders.logs.LogsHandle;
import com.liverpool.orders.response.OrdersByDateResponse;
import com.liverpool.orders.response.OrdersResponse;
import com.liverpool.orders.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
public class OrdersController {
    private static final Logger logTracking = LoggerFactory.getLogger(OrdersController.class);
    private static final LogsHandle logsHandle = new LogsHandle();

    @Autowired
    private OrdersService ordersService;

    @PostMapping("/newOrder")
    public ResponseEntity<OrdersResponse> newOrder(@RequestBody OrdersRequestDTO ordersRequestDTO, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to create a new order.", httpServletRequest);
        return new ResponseEntity<OrdersResponse>(ordersService.newOrder(ordersRequestDTO, logsHandle, httpServletRequest), HttpStatus.CREATED);
    }

    @GetMapping("/getOrderByID")
    public ResponseEntity<OrdersResponse> getOrderByID(@RequestParam("orderID") String orderID, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to get information for an orderID.", httpServletRequest);
        return new ResponseEntity<OrdersResponse>(ordersService.getOrderByID(orderID, logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @DeleteMapping("/cancelOrder")
    public ResponseEntity<OrdersResponse> cancelOrder(@RequestParam("orderID") String orderID, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to cancel an order and retrieve products to stock.", httpServletRequest);
        ordersService.cancelOrder(orderID, logsHandle, httpServletRequest);
        return new ResponseEntity<OrdersResponse>(ordersService.getOrderByID(orderID, logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @GetMapping("/getOrderByPurchaseDate")
    ResponseEntity<OrdersByDateResponse> getOrderByPurchaseDate(@RequestParam(defaultValue = "${config-options.startDate}", required = false) String startDate,
                                                                @RequestParam(defaultValue = "${config-options.endDate}", required = false) String endDate,
                                                                @RequestParam(defaultValue = "${config-options.pageNo}", required = false) Integer pageNo,
                                                                @RequestParam(defaultValue = "${config-options.pageSize}", required = false) Integer pageSize,
                                                                HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to get all orders by purchase date.", httpServletRequest);
        return new ResponseEntity<OrdersByDateResponse>(ordersService.getAllOrdersByPurchaseDate(pageNo, pageSize, startDate, endDate, logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @GetMapping("/getOrderByCancellationDate")
    ResponseEntity<OrdersByDateResponse> getOrderByCancellationDate(@RequestParam(defaultValue = "${config-options.startDate}", required = false) String startDate,
                                                                    @RequestParam(defaultValue = "${config-options.endDate}", required = false) String endDate,
                                                                    @RequestParam(defaultValue = "${config-options.pageNo}", required = false) Integer pageNo,
                                                                    @RequestParam(defaultValue = "${config-options.pageSize}", required = false) Integer pageSize,
                                                                    HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to get all orders by purchase date.", httpServletRequest);
        return new ResponseEntity<OrdersByDateResponse>(ordersService.getAllOrdersByCancellationDate(pageNo, pageSize, startDate, endDate, logsHandle, httpServletRequest), HttpStatus.OK);
    }

}
