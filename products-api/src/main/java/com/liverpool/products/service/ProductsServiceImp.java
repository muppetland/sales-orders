package com.liverpool.products.service;

import com.liverpool.products.dto.ProductDTO;
import com.liverpool.products.entities.Products;
import com.liverpool.products.logs.LogsHandle;
import com.liverpool.products.repository.ProductsRepository;
import com.liverpool.products.response.ProductsByNameResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import org.hibernate.query.IllegalQueryOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductsServiceImp implements ProductsService {
    @Autowired
    private ProductsRepository productsRepository;

    public static final Logger logTracking = LoggerFactory.getLogger(ProductsServiceImp.class);
    private String vlMsg = "";
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<Products> getAllProducts(LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //declare array to storage all records found...
        vlMsg = "Create a list that will be storage all products.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        List<Products> productsList = new ArrayList<Products>();

        //request all products...
        vlMsg = "Invoke query to get all products in database.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        productsRepository.findAll().forEach(productsList::add);

        //if we don't have any record, we must notice...
        if (productsList.isEmpty()) {
            vlMsg = "No products registered yet.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new IllegalQueryOperationException(vlMsg);
        }

        //products found...
        vlMsg = "Show all products registered.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        return productsList;
    }

    @Override
    public Products getProductByID(Long productID, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't send the productID we can't continues...
        if (productID == null || productID == 0) {
            //we can't continues 'cause productID is mandatory for this action...
            vlMsg = "productID is mandatory.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //get information from current product...
            Products products = productsRepository.findByProuctID(productID);
            if (products == null) {
                //no record found...
                vlMsg = "productoID [" + productID + "] was not found.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new IllegalQueryOperationException(vlMsg);
            }

            //product found...
            vlMsg = "productoID [" + productID + "] is available.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            return products;
        }
    }

    @Override
    public ProductsByNameResponse getProductByName(String productName, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't send the productName we can't continues...
        if (productName == null || productName.trim().equals("")) {
            //we can't continues 'cause productName is mandatory for this action...
            vlMsg = "You must send the productName to continues with the process.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //get information from current product...
            Products products = productsRepository.findByDescriptionIgnoreCase(productName);
            if (products == null) {
                //no record found...
                vlMsg = "This product [" + productName + "] was not found.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new IllegalQueryOperationException(vlMsg);
            }

            //product found...
            vlMsg = "Product [" + productName + "] is available.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            ProductsByNameResponse productsResponse = ProductsByNameResponse
                    .builder()
                    .productID(products.getProductID())
                    .activate(products.getActivate())
                    .amount(products.getAmount())
                    .description(products.getDescription())
                    .stock(products.getStock())
                    .build();
            return productsResponse;
        }
    }

    @Override
    public Products newProduct(Products products, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //we need to know if the body sent have is not null...
        if (products == null) {
            vlMsg = "requestBody is mandatory.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //descript must be included...
            if (products.getDescription() == null || products.getDescription().trim().equalsIgnoreCase("")) {
                vlMsg = "description is mandatory.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new BadRequestException(vlMsg);
            } else {
                //amount must be included...
                if (products.getAmount() == null || products.getAmount() <= 0) {
                    //amount must be higher than 0 or amount must be present...
                    vlMsg = "amount is mandatory.";
                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                    throw new BadRequestException(vlMsg);
                } else {
                    //stock must be included...
                    if (products.getStock() == null || products.getStock() <= 0) {
                        //stock must be higher than 0 or stock must be present...
                        vlMsg = "stock value is mandatory.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new BadRequestException(vlMsg);
                    } else {
                        //status must be included...
                        if (products.getActivate() == null) {
                            vlMsg = "activate status is mandatory";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            throw new BadRequestException(vlMsg);
                        } else {
                            //validate if current product is not present in system...
                            vlMsg = "Getting information from requestBody.";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            Products productFound = productsRepository.findByDescriptionIgnoreCase(products.getDescription().trim());

                            //validate if current record exists in system...
                            if (productFound == null) {
                                //we can register current product...
                                vlMsg = "product was not found, we can't continues with the process.";
                                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);

                                // we need to setup current datetime to get register dateTime value...
                                vlMsg = "Setting date values.";
                                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                LocalDateTime now = LocalDateTime.now();
                                products.setRegistrationDateTime(now);

                                //save record...
                                vlMsg = "Save object.";
                                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                                Products productSaved = productsRepository.save(products);

                                //product saved...
                                vlMsg = "productID[" + productSaved.getProductID() + "] was registered.";
                                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                                return productSaved;
                            } else {
                                // we can't continues 'cause this product is already registered...
                                vlMsg = "This product is already registered.";
                                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                                throw new IllegalQueryOperationException(vlMsg);
                            }
                        }

                    }
                }
            }
        }
    }

    @Override
    public Products updateProductByID(ProductDTO productDTO, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't sent the productID we can't continues...
        if (productDTO.getProductID() == null || productDTO.getProductID() == 0) {
            //we can't continues 'cause productID is mandatory for this action...
            vlMsg = "productID is mandatory.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //set main class to get result...
            Products productsFound = null;

            //descript must be included...
            if (productDTO.getDescription() == null || productDTO.getDescription().trim().equalsIgnoreCase("")) {
                vlMsg = "description is mandatory.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new BadRequestException(vlMsg);
            } else {
                //amount must be included...
                if (productDTO.getAmount() == null || productDTO.getAmount() <= 0) {
                    //amount must be higher than 0 or amount must be present...
                    vlMsg = "amount is mandatory.";
                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                    throw new BadRequestException(vlMsg);
                } else {
                    //stock must be included...
                    if (productDTO.getStock() == null || productDTO.getStock() <= 0) {
                        //stock must be higher than 0 or stock must be present...
                        vlMsg = "stock value is mandatory.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new BadRequestException(vlMsg);
                    } else {
                        //status must be included...
                        if (productDTO.getActivate() == null) {
                            vlMsg = "activate status is mandatory.";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            throw new BadRequestException(vlMsg);
                        } else {
                            //we need to validate if productID exists...
                            vlMsg = "productID should be exists.";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            productsFound = productsRepository.findById(productDTO.getProductID())
                                    .orElseThrow(() -> {
                                        //we don't have information with those arguments...
                                        vlMsg = "productID was not found, we can't continues with the process.";
                                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                                        throw new IllegalQueryOperationException(vlMsg);
                                    });
                        }
                    }
                }
            }

            //passing values to entity from dto...
            vlMsg = "Changing DTO to Entity.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            Products productsToUpdate = DTOtoEntity(productDTO);
            productsToUpdate.setRegistrationDateTime(productsFound.getRegistrationDateTime());

            //if preview validations are ok, we can continues with the update...
            vlMsg = "Update data.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            Products productsEdited = productsRepository.save(productsToUpdate);
            return productsEdited;
        }
    }

    @Override
    public Products updateStrockPurchasedProduct(Long productID, Integer purchasedItems, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't sent the productID and purchasedItems we can't continues...
        Products productsFound = null;
        if (productID == null || productID == 0 || purchasedItems == null || purchasedItems == 0) {
            //we can't continue 'because product ID and purchased Items are mandatory for this action...
            vlMsg = "It's necessary add the values for product ID and purchasedItems to process this transaction.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //set main class to get result...
            //we need to validate if productID exists...
            vlMsg = "Validating if product is available...";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            productsFound = productsRepository.findById(productID)
                    .orElseThrow(() -> {
                        //we don't have information with those arguments...
                        vlMsg = "ProductID sent is no longer available.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new IllegalQueryOperationException(vlMsg);
                    });
        }

        //if product is available, so we need to update stock...
        LocalDateTime now = LocalDateTime.now();
        vlMsg = "setting up values to variables to update...";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        Products productsToUpdate = productsFound;
        productsToUpdate.setStock(productsFound.getStock() - purchasedItems);
        productsToUpdate.setLastPurchase(now);

        //update...
        vlMsg = "Updating stock...";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        Products productsEdited = null;
        try {
            productsEdited = productsRepository.save(productsToUpdate);
        } catch (IllegalQueryOperationException ex) {
            //error ocurrs while we save the transaction...
            vlMsg = "¡Ups!, we have a problem, we're working to solve it, try later or perhaps you are sending invalid data into the request body.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new IllegalArgumentException(vlMsg);
        }
        return productsEdited;
    }

    @Override
    public Products updateStrockCancelledPurchasedProduct(Long productID, Integer purchasedItems, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't sent the productID and purchasedItems we can't continues...
        Products productsFound = null;
        if (productID == null || productID == 0 || purchasedItems == null || purchasedItems == 0) {
            //we can't continue 'because product ID and purchased Items are mandatory for this action...
            vlMsg = "It's necessary add the values for product ID and purchasedItems to process this transaction.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //set main class to get result...
            //we need to validate if productID exists...
            vlMsg = "Validating if product is available...";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            productsFound = productsRepository.findById(productID)
                    .orElseThrow(() -> {
                        //we don't have information with those arguments...
                        vlMsg = "ProductID sent is no longer available.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new IllegalQueryOperationException(vlMsg);
                    });
        }

        //if product is available, so we need to update stock...
        LocalDateTime now = LocalDateTime.now();
        vlMsg = "setting up values to variables to update...";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        Products productsToUpdate = productsFound;
        productsToUpdate.setStock(productsFound.getStock() + purchasedItems);
        productsToUpdate.setLastPurchase(now);

        //update...
        vlMsg = "Updating stock...";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        Products productsEdited = null;
        try {
            productsEdited = productsRepository.save(productsToUpdate);
        } catch (IllegalQueryOperationException ex) {
            //error ocurrs while we save the transaction...
            vlMsg = "¡Ups!, we have a problem, we're working to solve it, try later or perhaps you are sending invalid data into the request body.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new IllegalArgumentException(vlMsg);
        }
        return productsEdited;
    }

    @Override
    public void deleteProductByID(Long productID, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't sent the productID we can't continues...
        if (productID == null || productID == 0) {
            //we can't continues 'cause productID is mandatory for this action...
            vlMsg = "productID is mandatory.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //get information from current product...
            vlMsg = "Invoke query to get productID [" + productID + "] data.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            Products products = productsRepository.findById(productID)
                    .orElseThrow(() ->
                    {
                        //no record found...
                        vlMsg = "productID [" + productID + "] was not found.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new IllegalQueryOperationException(vlMsg);
                    });

            //product found...
            vlMsg = "productID [" + productID + "] has been deleted.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            productsRepository.delete(products);
        }
    }

    private Products DTOtoEntity(ProductDTO productDTO) {
        return Products
                .builder()
                .productID(productDTO.getProductID())
                .stock(productDTO.getStock())
                .activate(productDTO.getActivate())
                .amount(productDTO.getAmount())
                .description(productDTO.getDescription().trim())
                .lastPurchase(productDTO.getLastPurchase())
                .registrationDateTime(productDTO.getRegistrationDateTime())
                .build();
    }
}
