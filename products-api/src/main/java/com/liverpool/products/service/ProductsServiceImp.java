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
        vlMsg = "Declaramos el listado que contrendrá la información de los productos vigentes en sistema.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        List<Products> productsList = new ArrayList<Products>();

        //request all products...
        vlMsg = "Realizamos la consulta para traer el listado de todos los productos registrados en sistema.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        productsRepository.findAll().forEach(productsList::add);

        //if we don't have any record, we must notice...
        if (productsList.isEmpty()) {
            vlMsg = "No tenemos hasta el momento ningun producto registrado en sistema.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new IllegalQueryOperationException(vlMsg);
        }

        //products found...
        vlMsg = "A continuación presentamos un listado de los productos encontrados en sistema.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        return productsList;
    }

    @Override
    public Products getProductByID(Long productID, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't send the productID we can't continues...
        if (productID == null || productID == 0) {
            //we can't continues 'cause productID is mandatory for this action...
            vlMsg = "Es necesario incluir en el pathvariable el ID del producto, de lo contrario no podremos realizar la petición solicitada.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //get information from current product...
            Products products = productsRepository.findByProuctID(productID);
            if (products == null) {
                //no record found...
                vlMsg = "No se ha encontrado información del productoID [" + productID + "]";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new IllegalQueryOperationException(vlMsg);
            }

            //product found...
            vlMsg = "El productoID [" + productID + "] se encuentra vigente en sistema.";
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
            vlMsg = "Es necesario enviar información en el cuerpo del API, de lo contrario no podremos proceder su petición.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //descript must be included...
            if (products.getDescription() == null || products.getDescription().trim().equalsIgnoreCase("")) {
                vlMsg = "Es necesario incluir el nombre del producto a registrar, de lo contrario no podremos realizar la petición solicitada.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new BadRequestException(vlMsg);
            } else {
                //amount must be included...
                if (products.getAmount() == null || products.getAmount() <= 0) {
                    //amount must be higher than 0 or amount must be present...
                    vlMsg = "Es necesario ingresr un monto mayor a $0 o bien, el monto deberá estar presente, de lo contrario no podremos realizar la petición solicitada.";
                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                    throw new BadRequestException(vlMsg);
                } else {
                    //stock must be included...
                    if (products.getStock() == null || products.getStock() <= 0) {
                        //stock must be higher than 0 or stock must be present...
                        vlMsg = "Es necesario ingresr un valor mayor a 0 o bien, el valor de stock deberá estar presente, de lo contrario no podremos realizar la petición solicitada.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new BadRequestException(vlMsg);
                    } else {
                        //status must be included...
                        if (products.getActivate() == null) {
                            vlMsg = "Es necesario indicar con false o true la actividad del producto a registrar, 'true' para activo y 'false' para inactivo, de lo contrario no podremos realizar la petición solicitada.";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            throw new BadRequestException(vlMsg);
                        } else {
                            //validate if current product is not present in system...
                            vlMsg = "Consultando información de los datos enviados.";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            Products productFound = productsRepository.findByDescriptionIgnoreCase(products.getDescription().trim());

                            //validate if current record exists in system...
                            if (productFound == null) {
                                //we can register current product...
                                vlMsg = "El registro enviado no existe, por lo tanto podemo reistrgar el producto enviado.";
                                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);

                                // we need to setup current datetime to get register dateTime value...
                                vlMsg = "Damos la hora y fecha del registro del producto.";
                                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                LocalDateTime now = LocalDateTime.now();
                                products.setRegistrationDateTime(now);

                                //save record...
                                vlMsg = "Guardamos el registro del objeto enviado.";
                                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                                Products productSaved = productsRepository.save(products);

                                //product saved...
                                vlMsg = "El producto se ha registrado con el siguiente ID [" + productSaved.getProductID() + "]";
                                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                                return productSaved;
                            } else {
                                // we can't continues 'cause this product is already registered...
                                vlMsg = "Lo sentimos, el producto que intenta registrar ya existe en sistema.";
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
            vlMsg = "Es necesario incluir el ID del producto a modificar, de lo contrario no podremos realizar la petición solicitada.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //set main class to get result...
            Products productsFound = null;

            //descript must be included...
            if (productDTO.getDescription() == null || productDTO.getDescription().trim().equalsIgnoreCase("")) {
                vlMsg = "Es necesario incluir el nombre del producto a editar, de lo contrario no podremos realizar la petición solicitada.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new BadRequestException(vlMsg);
            } else {
                //amount must be included...
                if (productDTO.getAmount() == null || productDTO.getAmount() <= 0) {
                    //amount must be higher than 0 or amount must be present...
                    vlMsg = "Es necesario ingresr un monto mayor a $0 o bien, el monto deberá estar presente, de lo contrario no podremos realizar la petición solicitada.";
                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                    throw new BadRequestException(vlMsg);
                } else {
                    //stock must be included...
                    if (productDTO.getStock() == null || productDTO.getStock() <= 0) {
                        //stock must be higher than 0 or stock must be present...
                        vlMsg = "Es necesario ingresr un valor mayor a 0 o bien, el valor de stock deberá estar presente, de lo contrario no podremos realizar la petición solicitada.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new BadRequestException(vlMsg);
                    } else {
                        //status must be included...
                        if (productDTO.getActivate() == null) {
                            vlMsg = "Es necesario indicar con false o true la actividad del producto a editar, 'true' para activo y 'false' para inactivo, de lo contrario no podremos realizar la petición solicitada.";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            throw new BadRequestException(vlMsg);
                        } else {
                            //we need to validate if productID exists...
                            vlMsg = "Validamos que el ID del producto a editar exista.";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            productsFound = productsRepository.findById(productDTO.getProductID())
                                    .orElseThrow(() -> {
                                        //we don't have information with those arguments...
                                        vlMsg = "El ID del producto enviado no existe, por lo tanto, no podremos actualizar los datos del producto enviado.";
                                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                                        throw new IllegalQueryOperationException(vlMsg);
                                    });
                        }
                    }
                }
            }

            //passing values to entity from dto...
            vlMsg = "Pasando DTO a Entity para realizar la actualización de la información.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            Products productsToUpdate = DTOtoEntity(productDTO);
            productsToUpdate.setRegistrationDateTime(productsFound.getRegistrationDateTime());

            //if preview validations are ok, we can continues with the update...
            vlMsg = "Realizamos la actualización de la información del productoID enviado.";
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
            vlMsg = "Es necesario incluir en el pathvariable el ID del product, de lo contrario no podremos realizar la petición solicitada.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //get information from current product...
            vlMsg = "Realizamos la consulta del productID [" + productID + "]";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            Products products = productsRepository.findById(productID)
                    .orElseThrow(() ->
                    {
                        //no record found...
                        vlMsg = "No se ha encontrado información del productID [" + productID + "]";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new IllegalQueryOperationException(vlMsg);
                    });

            //product found...
            vlMsg = "El productID [" + productID + "] se ha eliminado del sistema.";
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
