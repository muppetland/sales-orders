package com.liverpool.orders.repository;

import com.liverpool.orders.entities.OrdersDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersDetailRepository extends JpaRepository<OrdersDetail, Long> {
    @Query(value = "Select * From liverpool.ordersdetail Where orderid =:orderID order by detailid", nativeQuery = true)
    List<OrdersDetail> getOrdersDetailByOderID(@Param("orderID") String orderID);
}
