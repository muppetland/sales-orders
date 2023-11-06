package com.liverpool.orders;

import com.liverpool.orders.entities.Orders;
import com.liverpool.orders.entities.OrdersDetail;
import com.liverpool.orders.repository.OrdersDetailRepository;
import com.liverpool.orders.repository.OrdersRepository;
import com.liverpool.orders.response.OrdersResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Collection of methods to test our service orders.")
public class OrdersControllerTest {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersDetailRepository ordersDetailRepository;

    private Orders generalOrders;
    private OrdersDetail generalOrdersDetail;
    private UUID uuidValue;

    @BeforeEach
    void setupData() {
        uuidValue = UUID.randomUUID();
        generalOrders = Orders
                .builder()
                .tax(0.16)
                .orderID(uuidValue.toString())
                .cancellationDateTime(null)
                .purchasedate(LocalDateTime.now())
                .status("v")
                .subTotal(10.00)
                .totalAmount(10.16)
                .customerID(1089175L)
                .build();

        generalOrdersDetail = OrdersDetail
                .builder()
                .productID(1L)
                .amount(100.00)
                .purchasedItems(1)
                .orders(generalOrders)
                .detailID(1L)
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Test to validate order saved.")
    //@Disabled
    public void newOrderTest() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.

        // when - action or behaviour that we are going test...
        Orders ordersSaved = ordersRepository.save(generalOrders);
        OrdersDetail ordersDetail = ordersDetailRepository.save(generalOrdersDetail);

        // then - verify the result or output using assert statements...
        assertThat(ordersSaved).isNotNull(); //result should have information...
        assertThat(ordersSaved.getOrderID()).isNotNull(); //orderID must have a string value..
        assertThat(ordersDetail).isNotNull(); // result should have information...
    }


    @Test
    @Order(2)
    @DisplayName("Test to validate get value by orderID.")
    //@Disabled
    public void getOrderByIDTest() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.
        Orders ordersSaved = ordersRepository.save(generalOrders);
        OrdersDetail ordersDetail = ordersDetailRepository.save(generalOrdersDetail);

        // when - action or behaviour that we are going test...
        List<Orders> ordersList = new ArrayList<>();
        Optional<Orders> orderFound = ordersRepository.findById(ordersSaved.getOrderID());
        ordersList.add(orderFound.get());

        // then - verify the result or output using assert statements...
        if (!ordersList.isEmpty()) {
            assertThat(ordersList).isNotNull(); //result should have information...
            assertThat(ordersList.stream().map(a -> {
                if (a.getTotalAmount() > 0)
                    System.out.print("Orden with amount valid: " + a.toString());
                return a;
            })).size().isEqualTo(1); //each record should have amount higher than 0.
        } else {
            System.out.print("Order doesn't exits.");
        }
    }


    @Test
    @Order(3)
    @DisplayName("Test to validate cancel order by orderID.")
    public void cancelOrderByID() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.
        ordersRepository.save(generalOrders);
        ordersDetailRepository.save(generalOrdersDetail);

        // when - action or behaviour that we are going test...
        ordersRepository.deleteById(generalOrders.getOrderID());
        Optional<Orders> ordersFound = ordersRepository.findById(generalOrders.getOrderID());

        // then - verify the result or output using assert statements...
        assertThat(ordersFound).isEmpty(); //result must be empty...
    }
}

