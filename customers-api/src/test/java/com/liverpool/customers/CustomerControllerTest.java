package com.liverpool.customers;


import com.liverpool.customers.entities.Customers;
import com.liverpool.customers.repository.CustomersRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Collection of methods to test our service customers.")
public class CustomerControllerTest {
    @Autowired
    private CustomersRepository customersRepository;

    private Customers generalCustomers;

    @BeforeEach
    void setupData() {
        generalCustomers = Customers
                .builder()
                .customerName("bart simpson")
                .customerID(1L)
                .activate(true)
                .dateBirth(LocalDate.parse("1986-10-07"))
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Test to validate customer saved.")
    //@Disabled
    public void newCustomerTest() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.

        // when - action or behaviour that we are going test...
        Customers customersSaved = customersRepository.save(generalCustomers);

        // then - verify the result or output using assert statements...
        assertThat(customersSaved).isNotNull(); //result must have information...
        assertThat(customersSaved.getCustomerID()).isGreaterThan(0); //customerID must have a value greater than 0...
    }


    @Test
    @Order(2)
    @DisplayName("Test to validate get value by customerID.")
    //@Disabled
    public void getCustomerByIDTest() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.
        customersRepository.save(generalCustomers);

        // when - action or behaviour that we are going test...
        List<Customers> customersList = customersRepository.findAll();
        Optional<Customers> customerFound = customersRepository.findById(Long.valueOf(generalCustomers.getCustomerID()));

        // then - verify the result or output using assert statements...
        if (!customerFound.isEmpty()) {
            assertThat(customerFound).isNotNull(); //result must have information...
            assertThat(customerFound.get().getActivate()).isEqualTo(true); // user is active in system.
            assertThat(customerFound.get().getCustomerName()).isEqualToIgnoringCase("Bart Simpson");
        } else {
            System.out.print("Customer doesn't exits.");
        }
    }


    @Test
    @Order(3)
    @DisplayName("Test to validate get value for all customers.")
    public void getAllCustomersTest() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.
        Customers customers = Customers
                .builder()
                .customerName("Barney Gomez")
                .customerID(2L)
                .activate(true)
                .dateBirth(LocalDate.parse("1986-10-08"))
                .build();
        customersRepository.save(generalCustomers);
        customersRepository.save(customers);

        // when - action or behaviour that we are going test...
        List<Customers> customersList = customersRepository.findAll();

        // then - verify the result or output using assert statements...
        if (!customersList.isEmpty()) {
            assertThat(customersList).isNotNull(); //result must have information...
            assertThat(customersList).size().isEqualTo(2); // we have just two records.
            assertThat(customersList.stream().filter(a -> a.getCustomerName().equalsIgnoreCase("Barney Gomez")));
        } else {
            System.out.print("Customer doesn't exits.");
        }
    }


    @Test
    @Order(4)
    @DisplayName("Test to validate update data by customerID.")
    public void updateCustomerByIDTest() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.
        customersRepository.save(generalCustomers);

        // when - action or behaviour that we are going test...
        Optional<Customers> customersFound = customersRepository.findById(generalCustomers.getCustomerID());
        customersFound.get().setCustomerName("El nene consentido");
        customersFound.get().setActivate(true);
        customersFound.get().setDateBirth(LocalDate.parse("1990-01-01"));
        Customers customersSaved = customersRepository.save(customersFound.get());

        // then - verify the result or output using assert statements...
        assertThat(customersSaved).isNotNull(); //result must have information...
        assertThat(customersSaved.getCustomerName()).isEqualToIgnoringCase("el nene consentido"); // name has been changed.
        assertThat(customersSaved.getDateBirth()).isEqualTo(LocalDate.parse("1990-01-01")); // birthdate has been changed.
    }


    @Test
    @Order(5)
    @DisplayName("Test to validate delete customer by customerID.")
    public void deleteCustomerByIDTest() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.
        customersRepository.save(generalCustomers);

        // when - action or behaviour that we are going test...
        customersRepository.deleteById(generalCustomers.getCustomerID());
        Optional<Customers> customersFound = customersRepository.findById(generalCustomers.getCustomerID());

        // then - verify the result or output using assert statements...
        assertThat(customersFound).isEmpty(); //result must be empty...
    }
}
