package com.liverpool.customers.service;

import com.liverpool.customers.dto.CustomerDTO;
import com.liverpool.customers.entities.Customers;
import com.liverpool.customers.logs.LogsHandle;
import com.liverpool.customers.repository.CustomersRepository;
import com.liverpool.customers.response.CustomerResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import org.hibernate.query.IllegalQueryOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomersServiceImp implements CustomersService {
    @Autowired
    private CustomersRepository customersRepository;

    public static final Logger logTracking = LoggerFactory.getLogger(CustomersServiceImp.class);
    private String vlMsg = "";

    @Override
    public List<Customers> getAllCustomers(LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //declare array to storage all records found...
        vlMsg = "Setup list to storage all customers availables in system.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        List<Customers> customersList = new ArrayList<Customers>();

        //request all customers...
        vlMsg = "Invoke query to get all customers availables in system.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        customersRepository.findAll().forEach(customersList::add);

        //if we don't have any record, we must notice...
        if (customersList.isEmpty()) {
            vlMsg = "We have not customers registered yet.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new IllegalQueryOperationException(vlMsg);
        }

        //customers found...
        vlMsg = "Show a list of customers registered.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        return customersList;
    }

    @Override
    public Customers getCustomerByID(Long customerID, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't sent the customerID we can't continues...
        if (customerID == null || customerID <= 0) {
            //we can't continues 'cause customerID is mandatory for this action...
            vlMsg = "CustomerID is mandatory to continues with the process.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //get information from current customer...
            Customers customers = customersRepository.findById(customerID)
                    .orElseThrow(() ->
                    {
                        //no record found...
                        vlMsg = "CustomerID [" + customerID + "] was not found.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new IllegalQueryOperationException(vlMsg);
                    });

            //customer found...
            vlMsg = "CustomerID [" + customerID + "] is available.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            return customers;
        }
    }

    @Override
    public CustomerResponse getCustomerByName(String customerName, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't sent the customerName we can't continues...
        if (customerName == null || customerName.trim().equals("")) {
            //we can't continues 'cause customerName is mandatory for this action...
            vlMsg = "customerName is mandatory to continues with the process.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //get information from current customer...
            Customers customers = customersRepository.findByCustomerNameIgnoreCase(customerName);

            if (customers == null) {
                //no record found...
                vlMsg = "CustomerID [" + customerName + "] was not found.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new IllegalQueryOperationException(vlMsg);
            }

            //customer found...
            vlMsg = "customerName [" + customerName + "] is already registered.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            CustomerResponse customerResponse = CustomerResponse
                    .builder()
                    .customerID(customers.getCustomerID())
                    .customerName(customers.getCustomerName())
                    .build();
            return customerResponse;
        }
    }

    @Override
    public Customers newCustomer(Customers customers, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //we need to know if the body sent have is not null...
        if (customers == null) {
            vlMsg = "CustomerID is mandatory to process this request.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //name must be included...
            if (customers.getCustomerName() == null || customers.getCustomerName().trim().equals("")) {
                vlMsg = "CustomerID and CustomerName must be sent to process this request.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new BadRequestException(vlMsg);
            } else {
                // date of birth must be included...
                if (customers.getDateBirth() == null) {
                    vlMsg = "Birthdate is mandatory to process this request.";
                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                    throw new BadRequestException(vlMsg);
                } else {
                    //status must be included...
                    if (customers.getActivate() == null) {
                        vlMsg = "Status is mandatory to process this request.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new BadRequestException(vlMsg);
                    } else {
                        //validate if current employee is not present in system...
                        vlMsg = "Getting information from parameters sent.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        Customers customerFound = customersRepository.findByCustomerNameIgnoreCase(customers.getCustomerName().trim());

                        //validate if current record exists in system...
                        if (customerFound == null) {
                            //we can register current customer...
                            vlMsg = "Data sent doesn't exist, we can continues with the process.";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);

                            //save record...
                            vlMsg = "Save record.";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            Customers customersSaved = customersRepository.save(customers);

                            //customer saved...
                            vlMsg = "Customer was registered with ID [" + customersSaved.getCustomerID() + "]";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            return customersSaved;
                        } else {
                            // we can't continues 'cause this customer is already registered...
                            vlMsg = "¡Ups!, data sent is present in our database, we can't continues.";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            throw new IllegalQueryOperationException(vlMsg);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Customers updateCustomerByID(CustomerDTO customerDTO, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't sent the customerID we can't continues...
        if (customerDTO.getCustomerID() == null || customerDTO.getCustomerID() == 0) {
            //we can't continues 'cause customerID is mandatory for this action...
            vlMsg = "customerID is mandatory to process the request.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //set main clais to get result...
            Customers customerFound = null;

            //name must be included...
            if (customerDTO.getCustomerName() == null || customerDTO.getCustomerName().trim().equals("")) {
                vlMsg = "customerName is mandatory.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new BadRequestException(vlMsg);
            } else {
                // date of birth must be included...
                if (customerDTO.getDateBirth() == null) {
                    vlMsg = "Birthdate is mandatory.";
                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                    throw new BadRequestException(vlMsg);
                } else {
                    //status must be included...
                    if (customerDTO.getActivate() == null) {
                        vlMsg = "Active is mandatory.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new BadRequestException(vlMsg);
                    } else {
                        //we need to validate if customerID exists...
                        vlMsg = "customerID should be exists.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        customerFound = customersRepository.findById(customerDTO.getCustomerID())
                                .orElseThrow(() -> {
                                    //we don't have information with those arguments...
                                    vlMsg = "customerID was not found, therefore we can't continues with the process.";
                                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                                    throw new IllegalQueryOperationException(vlMsg);
                                });
                    }
                }
            }

            //passing values to entity from dto...
            vlMsg = "Convert DTO to Entity.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            //Customers customersEdit = DTOtoEntity(customerDTO);
            customerFound = DTOtoEntity(customerDTO);

            //if preview validations are ok, we can continues with the update...
            vlMsg = "Updating.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            Customers customersEdited = customersRepository.save(customerFound);
            return customersEdited;
        }
    }

    @Override
    public void deleteCustomerByID(Long customerID, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't sent the customerID we can't continues...
        if (customerID == null || customerID == 0) {
            //we can't continues 'cause customerID is mandatory for this action...
            vlMsg = "customerID is mandatory.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //get information from current customer...
            vlMsg = "Search customerID [" + customerID + "]";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            Customers customers = customersRepository.findById(customerID)
                    .orElseThrow(() ->
                    {
                        //no record found...
                        vlMsg = "customerID [" + customerID + "] is not found.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new IllegalQueryOperationException(vlMsg);
                    });

            //customer found...
            vlMsg = "customerID [" + customerID + "] has been deleted.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            customersRepository.delete(customers);
        }
    }

    private Customers DTOtoEntity(CustomerDTO customerDTO) {
        return Customers
                .builder()
                .customerID(customerDTO.getCustomerID())
                .activate(customerDTO.getActivate())
                .dateBirth(customerDTO.getDateBirth())
                .customerName(customerDTO.getCustomerName().trim())
                .build();
    }
}
