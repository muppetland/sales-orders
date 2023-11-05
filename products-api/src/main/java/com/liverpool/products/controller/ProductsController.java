package com.liverpool.products.controller;

import com.liverpool.products.dto.ProductDTO;
import com.liverpool.products.entities.Products;
import com.liverpool.products.logs.LogsHandle;
import com.liverpool.products.response.ProductsByNameResponse;
import com.liverpool.products.service.ProductsService;
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
public class ProductsController {
    private static final Logger logTracking = LoggerFactory.getLogger(ProductsController.class);
    private static final LogsHandle logsHandle = new LogsHandle();

    @Autowired
    private ProductsService productsService;

    @GetMapping("/{productID}")
    public ResponseEntity<Products> getProductByID(@PathVariable(value = "productID", required = true) Long productID, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to get information from productID.", httpServletRequest);
        return new ResponseEntity<Products>(productsService.getProductByID(productID, logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @GetMapping("/getProductByName")
    public ResponseEntity<ProductsByNameResponse> getProductByID(@RequestParam(value = "productName", required = true) String productName, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to get detail from product filtered by product name.", httpServletRequest);
        return new ResponseEntity<ProductsByNameResponse>(productsService.getProductByName(productName, logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<Products>> getAllProducts(HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API utilizada para consultar información de todos los productos registrados en sistema.", httpServletRequest);
        return new ResponseEntity<List<Products>>(productsService.getAllProducts(logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @PostMapping("/newProduct")
    public ResponseEntity<Products> newProduct(@RequestBody Products products, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API utilizada para registrar un nuevo producto en sistema.", httpServletRequest);
        return new ResponseEntity<Products>(productsService.newProduct(products, logsHandle, httpServletRequest), HttpStatus.CREATED);
    }

    @PutMapping("/updateProduct")
    public ResponseEntity<Products> updateProductByID(@RequestBody ProductDTO productDTO, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API utilizada para la edición de los datos de un producto en sistema.", httpServletRequest);
        return new ResponseEntity<Products>(productsService.updateProductByID(productDTO, logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @PutMapping("/updateProductPurchase")
    public ResponseEntity<Products> updateProductByIDPurchase(@RequestParam("productID") Long productID, @RequestParam("purchasedItems") Integer purchasedItems, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to update stock from current purchased item.", httpServletRequest);
        return new ResponseEntity<Products>(productsService.updateStrockPurchasedProduct(productID, purchasedItems, logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @PutMapping("/updateProductCancelledPurchase")
    public ResponseEntity<Products> updateProductCancelledPurchase(@RequestParam("productID") Long productID, @RequestParam("purchasedItems") Integer purchasedItems, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API used to update stock from current purchased item.", httpServletRequest);
        return new ResponseEntity<Products>(productsService.updateStrockCancelledPurchasedProduct(productID, purchasedItems, logsHandle, httpServletRequest), HttpStatus.OK);
    }

    @DeleteMapping("/deleteProduct/{productID}")
    public ResponseEntity<HttpStatus> deleteProductByID(@PathVariable(value = "productID", required = true) Long productID, HttpServletRequest httpServletRequest) {
        logsHandle.addLogController(logTracking, "API utilizada para eliminar un producto por su ID e en sistema.", httpServletRequest);
        productsService.deleteProductByID(productID, logsHandle, httpServletRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
