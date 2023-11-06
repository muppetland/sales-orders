package com.liverpool.products;

import com.liverpool.products.entities.Products;
import com.liverpool.products.repository.ProductsRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Collection of methods to test our service products.")
public class ProductsControllerTest {
    @Autowired
    private ProductsRepository productsRepository;

    private Products generalProducts;

    @BeforeEach
    void setupData() {
        generalProducts = Products
                .builder()
                .productID(1L)
                .registrationDateTime(LocalDateTime.now())
                .stock(100)
                .description("Cartera de Piel Color Negro")
                .amount(1500.0)
                .activate(true)
                .lastPurchase(null)
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Test to validate product saved.")
    //@Disabled
    public void newProductTest() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.

        // when - action or behaviour that we are going test...
        Products productsSaved = productsRepository.save(generalProducts);

        // then - verify the result or output using assert statements...
        assertThat(productsSaved).isNotNull(); //result must have information...
        assertThat(productsSaved.getProductID()).isGreaterThan(0); //productID must have a value greater than 0...
    }


    @Test
    @Order(2)
    @DisplayName("Test to validate get value by productID.")
    //@Disabled
    public void getProductByIDTest() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.
        productsRepository.save(generalProducts);

        // when - action or behaviour that we are going test...
        List<Products> productsList = productsRepository.findAll();
        Optional<Products> productsFound = productsRepository.findById(Long.valueOf(generalProducts.getProductID()));

        // then - verify the result or output using assert statements...
        if (!productsFound.isEmpty()) {
            assertThat(productsFound).isNotNull(); //result must have information...
            assertThat(productsFound.get().getActivate()).isEqualTo(true); // user is active in system.
            assertThat(productsFound.get().getDescription()).isEqualToIgnoringCase("Cartera de Piel Color Negro");
            assertThat(productsFound.get().getAmount()).isGreaterThanOrEqualTo(1500);
            assertThat(productsFound.get().getStock()).isEqualTo(100);
        } else {
            System.out.print("Product doesn't exits.");
        }
    }


    @Test
    @Order(3)
    @DisplayName("Test to validate get value for all products.")
    public void getAllProductsTest() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.
        Products products = Products
                .builder()
                .productID(null)
                .registrationDateTime(LocalDateTime.now())
                .stock(50)
                .description("Gansito marinela")
                .amount(15.00)
                .activate(true)
                .lastPurchase(null)
                .build();
        productsRepository.save(generalProducts);
        productsRepository.save(products);

        // when - action or behaviour that we are going test...
        List<Products> productsList = productsRepository.findAll();

        // then - verify the result or output using assert statements...
        if (!productsList.isEmpty()) {
            assertThat(productsList).isNotNull(); //result must have information...
            assertThat(productsList).size().isEqualTo(2); // we have just two records.
            assertThat(productsList.stream().filter(a -> a.getDescription().equalsIgnoreCase("GANSITO MARINELA")));
        } else {
            System.out.print("Products don't exits.");
        }
    }


    @Test
    @Order(4)
    @DisplayName("Test to validate update data by productID.")
    public void updateProductByIDTest() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.
        productsRepository.save(generalProducts);

        // when - action or behaviour that we are going test...
        Optional<Products> productFound = productsRepository.findById(generalProducts.getProductID());
        productFound.get().setDescription("Cuchara para sopa");
        productFound.get().setActivate(false);
        productFound.get().setStock(18);
        productFound.get().setAmount(25.00);
        Products productsSaved = productsRepository.save(productFound.get());

        // then - verify the result or output using assert statements...
        assertThat(productsSaved).isNotNull(); //result must have information...
        assertThat(productsSaved.getDescription()).isEqualToIgnoringCase("Cuchara para sopa"); // name has been changed.
        assertThat(productsSaved.getActivate()).isEqualTo(false); // activate status has been changed.
        assertThat(productsSaved.getAmount()).isEqualTo(25.00); // amount has been changed.
        assertThat(productsSaved.getStock()).isEqualTo(18); // stock has been changed.
    }


    @Test
    @Order(5)
    @DisplayName("Test to validate delete product by productID.")
    public void deleteProductByIDTest() {
        // given - precondition or setup...
        // before invoke this method we have created an object with information to validate.
        productsRepository.save(generalProducts);

        // when - action or behaviour that we are going test...
        productsRepository.deleteById(generalProducts.getProductID());
        Optional<Products> productFound = productsRepository.findById(generalProducts.getProductID());

        // then - verify the result or output using assert statements...
        assertThat(productFound).isEmpty(); //result must be empty...
    }
}
