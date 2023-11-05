package com.liverpool.orders.repository;

import com.liverpool.orders.dto.OrdersViewDTO;
import com.liverpool.orders.entities.Orders;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, String> {
    @Query(value = "Select amount From liverpool.ordersdetail Where orderid = (Select orderid From liverpool.orders Where customerid =:customerID and cancellationdatetime is null order by purchasedatetime desc limit 1) and productid =:productID", nativeQuery = true)
    public Double getAmountProductByCustomer(@Param("customerID") Long customerID, @Param("productID") Long productID);

    @Query(value = "Select * From liverpool.vw_ordersByID Where orderid =:orderID", nativeQuery = true)
    public Optional<OrdersViewDTO> ordersViewFindByOrderID(@Param("orderID") String orderID);

    @Modifying
    @Transactional
    @Query(value = "Update liverpool.orders Set cancellationdatetime =now(), status ='C' Where orderid =:orderID", nativeQuery = true)
    void canelOrderByID(@Param("orderID") String orderID);

    @Query(value = "SELECT * FROM liverpool.orders where date(purchasedatetime) between :startDate and :endDate and status = 'v'", nativeQuery = true)
    public Page<Orders> getAllOrdersByPurchaseDate(@Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);

    @Query(value = "SELECT * FROM liverpool.orders where date(cancellationdatetime) between :startDate and :endDate and status = 'C'", nativeQuery = true)
    public Page<Orders> getAllOrdersByCancellationDate(@Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);

}
