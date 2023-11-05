package com.liverpool.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liverpool.orders.dto.OrdersDTO;
import com.liverpool.orders.dto.OrdersDetailDTO;
import com.liverpool.orders.dto.OrdersRequestDTO;
import com.liverpool.orders.entities.Orders;
import com.liverpool.orders.entities.OrdersDetail;
import com.liverpool.orders.externalRequest.CustomerResponse;
import com.liverpool.orders.externalRequest.ProductsByNameResponse;
import com.liverpool.orders.externalRequest.ProductsUpdateStock;
import com.liverpool.orders.logs.LogsHandle;
import com.liverpool.orders.repository.OrdersDetailRepository;
import com.liverpool.orders.repository.OrdersRepository;
import com.liverpool.orders.response.OrdersByDateResponse;
import com.liverpool.orders.response.OrdersDetailResponse;
import com.liverpool.orders.response.OrdersResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import org.hibernate.query.IllegalQueryOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImp implements OrdersService {
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrdersDetailRepository ordersDetailRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;
    public static final Logger logTracking = LoggerFactory.getLogger(OrdersServiceImp.class);
    private String vlMsg = "";
    private List<OrdersDetailResponse> ordersDetailResponsesList;
    private List<OrdersDetailDTO> ordersDetailToUpdateStock = new ArrayList<>();
    private static Double subTotal = (double) 0;
    private static Double tax = (double) 0;
    private static Double totalAmount = (double) 0;
    private static Integer purchasedItems = 0;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private Long customerID;
    private Double outFinalAmount = (double) 0;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Value("${config-options.taxvalue}")
    private Double taxValue;

    @Override
    public OrdersResponse newOrder(OrdersRequestDTO ordersRequestDTO, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //from current request body we need to validate the customer sent and the items added to array, if those information exists we can continues...
        vlMsg = "Connecting to rest service to get extra data from current customer name...";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        CustomerResponse customerResponse = null;
        Orders orderSaved = null;
        LocalDateTime now = LocalDateTime.now();
        LocalDate nowDate = LocalDate.now();
        try {
            customerResponse = webClientBuilder.build().get().uri("http://customers-service/customers-api/getCustomerByName?customerName=" + ordersRequestDTO.getCustomerName().trim()).retrieve().bodyToMono(CustomerResponse.class).block();

            //catch customerID to use it to compare previews purchases...
            customerID = customerResponse.getCustomerID();
        } catch (Exception ex) {
            //maybe we can't access to api or return 500 (record doesn't exits)
            logsHandle.addLogController(logTracking, ex.getMessage(), httpServletRequest);
            throw new BadRequestException("The name of customer doesn't exists in our database, check the name and retry.");
        }

        //if class is not null, we got a detail from current customer name...
        if (customerResponse != null) {
            //we need to read each item and validate if this product exists, otherwise we can't continues...
            vlMsg = "Loop each item in current list to validate if those products exists in our database..";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            ordersDetailResponsesList = Collections.unmodifiableList(ordersRequestDTO.getPurchasedItems().stream().filter(a -> {
                if (existsProduct(a.getDescription().trim(), a.getAmount(), a.getItems(), logsHandle, httpServletRequest) == true) {
                    //replace current value of this product...
                    a.setAmount(outFinalAmount);
                    return true;
                } else {
                    return false;
                }
            }).collect(Collectors.toList()));

            //now we have all products validated and we have to get final amounts and tax for this purchase...
            purchasedItems = ordersDetailResponsesList.stream().mapToInt(a -> a.getItems()).sum();
            subTotal = Double.parseDouble(df.format(ordersDetailResponsesList.stream().mapToDouble(a -> a.getAmount()).sum()));
            tax = Double.parseDouble(df.format((subTotal * taxValue) - subTotal));
            totalAmount = Double.parseDouble(df.format((subTotal * taxValue)));

            //to process with the order we at least one product in array...
            if (ordersDetailToUpdateStock.isEmpty()) {
                // we can't continues...
                vlMsg = "Sorry, we need at least one product in your order.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new BadRequestException(vlMsg);
            }

            //finally we save our sales order...
            OrdersDTO ordersDTO = OrdersDTO.builder().orderID(null).cancellationDateTime(null).purchasedate(now).subTotal(subTotal).tax(tax).totalAmount(totalAmount).status("v").purchasedItems(purchasedItems).customerID(customerID).build();

            //save order...
            vlMsg = "Save current order...";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            Orders orders = OrdersDTOtoEntity(ordersDTO);
            try {
                orderSaved = ordersRepository.save(orders);
            } catch (IllegalQueryOperationException ex) {
                //error ocurrs while we save the transaction...
                vlMsg = "¡Ups!, we have a problem, we're working to solve it, try later or perhaps you are sending invalid data into the request body.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new IllegalArgumentException(vlMsg);
            }

            //now we have an order, so, we need to save order detail and update stock...
            vlMsg = "Let's to save order detail.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            saveOrdersDetail(ordersDetailToUpdateStock, orders, logsHandle, httpServletRequest);
        }

        //create a response class...
        vlMsg = "Return a complete response from current order.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        OrdersResponse ordersResponse = OrdersResponse.builder().customerName(customerResponse.getCustomerName()).purchasedItems(ordersDetailResponsesList).totalAmount(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(totalAmount)).subtotal(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(subTotal)).tax(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(tax)).orderID(orderSaved.getOrderID()).datePurchase(nowDate).build();
        return ordersResponse;
    }

    @Override
    public OrdersResponse getOrderByID(String orderID, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //validate if current orderID exists...
        Orders orders = ordersRepository.findById(orderID).orElseThrow(() -> {
            vlMsg = "Sorry, the orderID sent was not found.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        });

        //In the event that we do not have a connection to the database directly, we will use an API call to bring the client name...
        String customerName = getCustomerNameByID(orders.getCustomerID(), logsHandle, httpServletRequest);

        //changing date formt to dd/MM/yyyy...
        LocalDate purchaseDate = orders.getPurchasedate().toLocalDate();
        LocalDate cancelledPurchaseDate = null;
        if (orders.getCancellationDateTime() != null) {
            cancelledPurchaseDate = orders.getCancellationDateTime().toLocalDate();
        }

        //if exist orderID, we build the response...
        vlMsg = "Set array to storage all records from current orderID.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        List<OrdersDetail> ordersDetailsMainList = new ArrayList<>();
        ordersDetailsMainList = ordersDetailRepository.getOrdersDetailByOderID(orderID);

        //adding order detail...
        List<OrdersDetailResponse> ordersDetailResponses = new ArrayList<>();
        ordersDetailResponses = ordersDetailsMainList.stream().map(a -> {
            //we need to creat an objet OrdersDetailResponse and fill it with current information...
            OrdersDetailResponse odersDetailResp = OrdersDetailResponse.builder().items(a.getPurchasedItems()).description(getProductNameByID(a.getProductID(), logsHandle, httpServletRequest)).amount(a.getAmount()).build();
            return odersDetailResp;
        }).collect(Collectors.toList());

        //setting up response body...
        try {
            vlMsg = "Build final response.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            OrdersResponse ordersResponse = new OrdersResponse();
            ordersResponse.setOrderID(orderID);
            ordersResponse.setTax(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(orders.getTax()));
            ordersResponse.setSubtotal(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(orders.getSubTotal()));
            ordersResponse.setCustomerName(customerName);
            ordersResponse.setCancellationDate(cancelledPurchaseDate);
            ordersResponse.setTotalAmount(NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(orders.getTotalAmount()));
            ordersResponse.setPurchasedItems(ordersDetailResponses);
            ordersResponse.setDatePurchase(purchaseDate);

            //send to log...
            //String mapperObject = new ObjectMapper().writeValueAsString(ordersResponse);
            vlMsg = "Response body for orderID: ";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            return ordersResponse;
        } catch (Exception ex) {
            vlMsg = "¡Ups!, we have a problem with our mapper class, we're to solve it, try later.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        }
    }

    @Override
    public void cancelOrder(String orderID, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //validate if current orderID exists...
        Orders orders = ordersRepository.findById(orderID).orElseThrow(() -> {
            vlMsg = "Sorry, the orderID sent was not found.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        });

        //we need to retrieve products to stock...
        vlMsg = "Retrieve products to stock.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        ordersDetailRepository.getOrdersDetailByOderID(orderID).forEach(a -> {
            //get connetion to api of products...
            retrieveProductToStock(a.getProductID(), a.getPurchasedItems(), logsHandle, httpServletRequest);
        });

        //set values to cancel the order...
        try {
            ordersRepository.canelOrderByID(orderID);
        } catch (IllegalQueryOperationException ex) {
            vlMsg = "¡Ups!, we have a problem with our process, we're working to solve it, try later.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new IllegalQueryOperationException(vlMsg);
        }
    }

    @Override
    public OrdersByDateResponse getAllOrdersByPurchaseDate(Integer pageNo, Integer pageSize, String startDate, String endDate, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //firt of all we need to validate date format to process with this query...
        Date vlStartDate = null;
        Date vlEndDate = null;
        String vlSD;
        String vlED;
        try {
            //declare date variables...
            final DateFormat vlDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            //setting date format...
            if (startDate.trim().equals("") && endDate.trim().equals("")) {
                //Filter by current day when we don't have a date configured...
                vlStartDate = new SimpleDateFormat("dd/MM/yyyy").parse("1900-01-01");
                vlEndDate = new Date();
            } else {
                //if sent only one date...
                if (!startDate.trim().equals("") && endDate.trim().equals("")) {
                    //firt date is with current params and end date will be current day...
                    vlStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
                    vlEndDate = new Date();
                } else {
                    //we have only endDate..
                    if (startDate.trim().equals("") && !endDate.trim().equals("")) {
                        //second date is with current params and start date will be same as endDate...
                        vlStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
                        vlEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
                    } else {
                        //all dates are presents...
                        vlStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
                        vlEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
                    }
                }
            }

            //in this case dates are request in format dd/MM/yyyy, but in the query we need to send yyyy-MM-dd, so we need to change this format...
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            vlSD = dateFormat.format(vlStartDate);
            vlED = dateFormat.format(vlEndDate);
        } catch (ParseException e) {
            //error in format of date...
            vlMsg = "There're problems with the format of dates, please check it, remember formar [dd/MM/yyyy].";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new RuntimeException(vlMsg);
        }

        //for this query we include pageable options...
        Sort sort = Sort.by("purchaseDateTime").descending();
        Pageable peageble = PageRequest.of(pageNo, pageSize, sort);
        Page<Orders> ordersMainList = null;

        //get orders with those params...
        try {
            ordersMainList = ordersRepository.getAllOrdersByPurchaseDate(vlSD, vlED, peageble);
        } catch (IllegalQueryOperationException ex) {
            //error...
            vlMsg = "There're problems with the format of dates, please check it, remember formar [dd/MM/yyyy].";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new IllegalQueryOperationException(vlMsg);
        }

        //if we have information we can continues...
        List<OrdersResponse> ordersListResponse = null;
        if (ordersMainList.isEmpty()) {
            vlMsg = "There's no information about orders.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //now with orders list we gonna generate an array to get information for each record...
            ordersListResponse = ordersMainList.stream().map(a -> {
                vlMsg = "Getting information for orderID: " + a.getOrderID();
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                OrdersResponse ordersResponse = getOrderByID(a.getOrderID(), logsHandle, httpServletRequest);
                return ordersResponse;
            }).collect(Collectors.toList());
        }

        //build response body...
        OrdersByDateResponse ordersResult = OrdersByDateResponse
                .builder()
                .endDate(endDate)
                .startDate(startDate)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .orders(ordersListResponse)
                .ordersDisplayed(ordersMainList.getNumberOfElements())
                .recodsTotal(ordersMainList.getTotalElements())
                .recodsByPages(ordersMainList.getTotalPages())
                .build();
        return ordersResult;
    }

    @Override
    public OrdersByDateResponse getAllOrdersByCancellationDate(Integer pageNo, Integer pageSize, String startDate, String endDate, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //firt of all we need to validate date format to process with this query...
        Date vlStartDate = null;
        Date vlEndDate = null;
        String vlSD;
        String vlED;
        try {
            //declare date variables...
            final DateFormat vlDateFormat = new SimpleDateFormat("dd/MM/yyyy");

            //setting date format...
            if (startDate.trim().equals("") && endDate.trim().equals("")) {
                //Filter by current day when we don't have a date configured...
                vlStartDate = new SimpleDateFormat("dd/MM/yyyy").parse("1900-01-01");
                vlEndDate = new Date();
            } else {
                //if sent only one date...
                if (!startDate.trim().equals("") && endDate.trim().equals("")) {
                    //firt date is with current params and end date will be current day...
                    vlStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
                    vlEndDate = new Date();
                } else {
                    //we have only endDate..
                    if (startDate.trim().equals("") && !endDate.trim().equals("")) {
                        //second date is with current params and start date will be same as endDate...
                        vlStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
                        vlEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
                    } else {
                        //all dates are presents...
                        vlStartDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
                        vlEndDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
                    }
                }
            }

            //in this case dates are request in format dd/MM/yyyy, but in the query we need to send yyyy-MM-dd, so we need to change this format...
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            vlSD = dateFormat.format(vlStartDate);
            vlED = dateFormat.format(vlEndDate);
        } catch (ParseException e) {
            //error in format of date...
            vlMsg = "There're problems with the format of dates, please check it, remember formar [dd/MM/yyyy].";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new RuntimeException(vlMsg);
        }

        //for this query we include pageable options...
        Sort sort = Sort.by("purchaseDateTime").descending();
        Pageable peageble = PageRequest.of(pageNo, pageSize, sort);
        Page<Orders> ordersMainList = null;

        //get orders with those params...
        try {
            ordersMainList = ordersRepository.getAllOrdersByCancellationDate(vlSD, vlED, peageble);
        } catch (IllegalQueryOperationException ex) {
            //error...
            vlMsg = "There're problems with the format of dates, please check it, remember formar [dd/MM/yyyy].";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new IllegalQueryOperationException(vlMsg);
        }

        //if we have information we can continues...
        List<OrdersResponse> ordersListResponse = null;
        if (ordersMainList.isEmpty()) {
            vlMsg = "There's no information about orders.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //now with orders list we gonna generate an array to get information for each record...
            ordersListResponse = ordersMainList.stream().map(a -> {
                vlMsg = "Getting information for orderID: " + a.getOrderID();
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                OrdersResponse ordersResponse = getOrderByID(a.getOrderID(), logsHandle, httpServletRequest);
                return ordersResponse;
            }).collect(Collectors.toList());
        }

        //build response body...
        OrdersByDateResponse ordersResult = OrdersByDateResponse
                .builder()
                .endDate(endDate)
                .startDate(startDate)
                .pageNo(pageNo)
                .pageSize(pageSize)
                .orders(ordersListResponse)
                .ordersDisplayed(ordersMainList.getNumberOfElements())
                .recodsTotal(ordersMainList.getTotalElements())
                .recodsByPages(ordersMainList.getTotalPages())
                .build();
        return ordersResult;
    }


    private void saveOrdersDetail(List<OrdersDetailDTO> finalList, Orders orders, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //for each record of the current list we need to save it and update stock...
        finalList.stream().forEach(a -> {
            //build orderDetail body...
            OrdersDetail ordersDetail = OrdersDetailDTOtoEntity(a);
            ordersDetail.setOrders(orders);

            try {
                //save data..
                String mapperObject = new ObjectMapper().writeValueAsString(ordersDetail);
                vlMsg = "Save order detail for: " + mapperObject;
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                ordersDetailRepository.save(ordersDetail);

                //now we need to update stock from current product...
                vlMsg = "Updating stock for: " + mapperObject;
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                try {
                    //request update stock...
                    ProductsUpdateStock productsUpdateStock = webClientBuilder.build().put().uri("http://products-service/products-api/updateProductPurchase?productID=" + a.getProductID() + "&purchasedItems=" + a.getPurchasedItems()).retrieve().bodyToMono(ProductsUpdateStock.class).block();
                } catch (Exception ex) {
                    //maybe we can't access to api or return 500 (record doesn't exits)
                    logsHandle.addLogController(logTracking, ex.getMessage(), httpServletRequest);
                }
            } catch (IllegalQueryOperationException ex) {
                //error ocurrs while we save the transaction...
                vlMsg = "¡Ups!, we have a problem, we're working to solve it, try later or perhaps you are sending invalid data into the request body.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new IllegalArgumentException(vlMsg);
            } catch (JsonProcessingException e) {
                vlMsg = "¡Ups!, we have problems with mapper class, we're working to solve it.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new RuntimeException(vlMsg);
            }
        });
    }

    private OrdersDetail OrdersDetailDTOtoEntity(OrdersDetailDTO ordersDetailDTO) {
        //parsing DTO to Entity to save data...
        return OrdersDetail.builder().purchasedItems(ordersDetailDTO.getPurchasedItems()).detailID(ordersDetailDTO.getDetailID()).orders(ordersDetailDTO.getOrders()).amount(ordersDetailDTO.getAmount()).productID(ordersDetailDTO.getProductID()).build();
    }

    private Orders OrdersDTOtoEntity(OrdersDTO ordersDTO) {
        //parsing DTO to Entity to save data...
        return Orders.builder().orderID(ordersDTO.getOrderID()).cancellationDateTime(ordersDTO.getCancellationDateTime()).purchasedate(ordersDTO.getPurchasedate()).status(ordersDTO.getStatus()).tax(ordersDTO.getTax()).subTotal(ordersDTO.getSubTotal()).totalAmount(ordersDTO.getTotalAmount()).customerID(ordersDTO.getCustomerID()).purchasedItems(ordersDTO.getPurchasedItems()).build();
    }

    private Boolean retrieveProductToStock(Long productID, Integer retrivedItems, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        try {
            //Update stock...
            ProductsByNameResponse customerResponse = webClientBuilder.build().put().uri("http://products-service/products-api/updateProductCancelledPurchase?productID=" + productID + "&purchasedItems=" + retrivedItems).retrieve().bodyToMono(ProductsByNameResponse.class).block();
            return true;
        } catch (Exception ex) {
            //maybe we can't access to api or return 500 (record doesn't exits)
            vlMsg = "The name of customer doesn't exists in our database, check the name and retry.";
            logsHandle.addLogController(logTracking, ex.getMessage(), httpServletRequest);
            throw new BadRequestException(vlMsg);
        }
    }

    private String getCustomerNameByID(Long customerID, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        try {
            //Getting name of customer from customerID...
            CustomerResponse customerResponse = webClientBuilder.build().get().uri("http://customers-service/customers-api/" + customerID).retrieve().bodyToMono(CustomerResponse.class).block();

            //get name...
            return customerResponse.getCustomerName();
        } catch (Exception ex) {
            //maybe we can't access to api or return 500 (record doesn't exits)
            vlMsg = "The name of customer doesn\'t exists in our database, check the name and retry.";
            logsHandle.addLogController(logTracking, ex.getMessage(), httpServletRequest);
            throw new BadRequestException(vlMsg);
        }
    }

    private String getProductNameByID(Long productID, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        try {
            //Getting name of product from produdctID...
            ProductsByNameResponse customerResponse = webClientBuilder.build().get().uri("http://products-service/products-api/" + productID).retrieve().bodyToMono(ProductsByNameResponse.class).block();

            //get name...
            return customerResponse.getDescription();
        } catch (Exception ex) {
            //maybe we can't access to api or return 500 (record doesn't exits)
            vlMsg = "The name of customer doesn't exists in our database, check the name and retry.";
            logsHandle.addLogController(logTracking, ex.getMessage(), httpServletRequest);
            throw new BadRequestException(vlMsg);
        }
    }

    private Boolean existsProduct(String productName, Double amount, Integer items, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //we need to lookup this product in our database...
        Boolean vlResult = true;
        Double finalAmount = (double) 0;
        vlMsg = "Connecting to rest service to get extra data from current product name...";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        ProductsByNameResponse productsByNameResponse = null;
        try {
            //request product info...
            productsByNameResponse = webClientBuilder.build().get().uri("http://products-service/products-api/getProductByName?productName=" + productName.trim()).retrieve().bodyToMono(ProductsByNameResponse.class).block();

            //our stock is enougth to buy this product?
            Integer currentStock = productsByNameResponse.getStock();
            Long productID = productsByNameResponse.getProductID();
            if (items > currentStock) {
                //we have not more items availables por this purchase...
                vlMsg = "Sorry, we have no more items in stock for this purchase.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                vlResult = false;
            } else {
                //if current product has been bougth in others sales orders, we need to get last amount in case current amount be higher...
                Double lastAmount = (double) 0;
                try {
                    vlMsg = "Getting amount for this productid in last sales order...";
                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                    lastAmount = ordersRepository.getAmountProductByCustomer(customerID, productID);

                    //if variable gets null, we change to 0...
                    if (lastAmount == null) {
                        lastAmount = amount;
                    }
                } catch (Exception ex) {
                    //we have no information for this product in priors sales order for this customer...
                    vlMsg = "This product has not been bought by this customers in priors sales orders...";
                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                }

                //if current amount is higher than last amount, we need to set prior amount for this purchase...
                if (amount > lastAmount) {
                    vlMsg = "Current amount is higher than last purchase, so we need to set prior amount...";
                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                    finalAmount = lastAmount;
                } else {
                    vlMsg = "This product has not been included in others sales orders, therefore, we repeat the amount...";
                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                    finalAmount = amount;
                }

                //finally we have a product that will be added to this purchase...
                outFinalAmount = finalAmount;

                //adding current product to list to update stock..
                OrdersDetailDTO ordersDetailDTO = OrdersDetailDTO.builder().detailID(null).productID(productID).orders(null).amount(outFinalAmount).purchasedItems(items).build();
                ordersDetailToUpdateStock.add(ordersDetailDTO);
            }
        } catch (Exception ex) {
            //maybe we can't access to api or return 500 (record doesn't exits)
            logsHandle.addLogController(logTracking, ex.getMessage(), httpServletRequest);
            vlResult = false;
        }

        //this product exist in our database, we can add it to final list...
        return vlResult;
    }
}
