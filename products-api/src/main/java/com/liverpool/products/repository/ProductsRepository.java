package com.liverpool.products.repository;

import com.liverpool.products.entities.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<Products,Long> {
    public Products findByDescriptionIgnoreCase(String description);
    @Query(value = "Select * From liverpool.products Where productID =:productID and activate=1",nativeQuery = true)
    public Products findByProuctID(@Param("productID") Long prouctID);

}
