package com.liverpool.orders.service;

import com.liverpool.orders.dto.OrdersRequestDTO;
import com.liverpool.orders.logs.LogsHandle;
import com.liverpool.orders.response.OrdersByDateResponse;
import com.liverpool.orders.response.OrdersResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

public interface OrdersService {
    OrdersResponse newOrder(OrdersRequestDTO ordersRequestDTO, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    OrdersResponse getOrderByID(String orderID, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    @Transactional
    void cancelOrder(String orderID, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    OrdersByDateResponse getAllOrdersByPurchaseDate(Integer pageNo, Integer pageSize, String startDate, String endDate, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    OrdersByDateResponse getAllOrdersByCancellationDate(Integer pageNo, Integer pageSize, String startDate, String endDate, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
}
