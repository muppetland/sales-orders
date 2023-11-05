package com.liverpool.products.service;

import com.liverpool.products.dto.ProductDTO;
import com.liverpool.products.entities.Products;
import com.liverpool.products.logs.LogsHandle;
import com.liverpool.products.response.ProductsByNameResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ProductsService {
    List<Products> getAllProducts(LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    Products getProductByID(Long productID, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    ProductsByNameResponse getProductByName(String productName, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    @Transactional
    Products newProduct(Products products, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    @Transactional
    Products updateProductByID(ProductDTO productDTO, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    @Transactional
    Products updateStrockPurchasedProduct(Long productID, Integer purchasedItems, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    @Transactional
    Products updateStrockCancelledPurchasedProduct(Long productID, Integer purchasedItems, LogsHandle logsHandle, HttpServletRequest httpServletRequest);
    @Transactional
    void deleteProductByID(Long productID, LogsHandle logsHandle, HttpServletRequest httpServletRequest);

}
