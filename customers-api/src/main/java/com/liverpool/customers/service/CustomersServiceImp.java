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
        vlMsg = "Declaramos el listado que contrendrá la información de los clientes vigentes en sistema.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        List<Customers> customersList = new ArrayList<Customers>();

        //request all customers...
        vlMsg = "Realizamos la consulta para traer el listado de todos los clientes registrados en sistema.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        customersRepository.findAll().forEach(customersList::add);

        //if we don't have any record, we must notice...
        if (customersList.isEmpty()) {
            vlMsg = "No tenemos hasta el momento ningun cliente registrado en sistema.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new IllegalQueryOperationException(vlMsg);
        }

        //customers found...
        vlMsg = "A continuación presentamos un listado de los clientes encontrados en sistema.";
        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
        return customersList;
    }

    @Override
    public Customers getCustomerByID(Long customerID, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't sent the customerID we can't continues...
        if (customerID == null || customerID <= 0) {
            //we can't continues 'cause customerID is mandatory for this action...
            vlMsg = "Es necesario incluir en el pathvariable el ID del cliente, de lo contrario no podremos realizar la petición solicitada.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //get information from current customer...
            Customers customers = customersRepository.findById(customerID)
                    .orElseThrow(() ->
                    {
                        //no record found...
                        vlMsg = "No se ha encontrado información del clienteID [" + customerID + "]";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new IllegalQueryOperationException(vlMsg);
                    });

            //customer found...
            vlMsg = "El clienteID [" + customerID + "] se encuentra vigente en sistema.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            return customers;
        }
    }

    @Override
    public CustomerResponse getCustomerByName(String customerName, LogsHandle logsHandle, HttpServletRequest httpServletRequest) {
        //if request don't sent the customerName we can't continues...
        if (customerName == null || customerName.trim().equals("")) {
            //we can't continues 'cause customerName is mandatory for this action...
            vlMsg = "Es necesario incluir en el pathvariable el nombre del cliente, de lo contrario no podremos realizar la petición solicitada.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //get information from current customer...
            Customers customers = customersRepository.findByCustomerNameIgnoreCase(customerName);

            if (customers == null) {
                //no record found...
                vlMsg = "No se ha encontrado información del cliente [" + customerName + "]";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new IllegalQueryOperationException(vlMsg);
            }

            //customer found...
            vlMsg = "El cliente [" + customerName + "] se encuentra vigente en sistema.";
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
            vlMsg = "Es necesario enviar información en el cuerpo del API, de lo contrario no podremos proceder su petición.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //name must be included...
            if (customers.getCustomerName() == null || customers.getCustomerName().trim().equals("")) {
                vlMsg = "Es necesario incluir el nombre del cliente a registrar, de lo contrario no podremos realizar la petición solicitada.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new BadRequestException(vlMsg);
            } else {
                // date of birth must be included...
                if (customers.getDateBirth() == null) {
                    vlMsg = "Es necesario incluir la fecha de nacimiento del cliente a registrar, de lo contrario no podremos realizar la petición solicitada.";
                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                    throw new BadRequestException(vlMsg);
                } else {
                    //status must be included...
                    if (customers.getActivate() == null) {
                        vlMsg = "Es necesario indicar con false o true la actividad del cliente a registrar, 'true' para activo y 'false' para inactivo, de lo contrario no podremos realizar la petición solicitada.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new BadRequestException(vlMsg);
                    } else {
                        //validate if current employee is not present in system...
                        vlMsg = "Consultando información de los datos enviados.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        Customers customerFound = customersRepository.findByCustomerNameIgnoreCase(customers.getCustomerName().trim());

                        //validate if current record exists in system...
                        if (customerFound == null) {
                            //we can register current customer...
                            vlMsg = "El registro enviado no existe, por lo tanto podemo registrar el cliente enviado.";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);

                            //save record...
                            vlMsg = "Guardamos el registro del objeto enviado.";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            Customers customersSaved = customersRepository.save(customers);

                            //customer saved...
                            vlMsg = "El cliente se ha registrado con el siguiente ID [" + customersSaved.getCustomerID() + "]";
                            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                            return customersSaved;
                        } else {
                            // we can't continues 'cause this customer is already registered...
                            vlMsg = "Lo sentimos, el cliente que intenta registrar ya existe en sistema.";
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
            vlMsg = "Es necesario incluir el ID del cliente a modificar, de lo contrario no podremos realizar la petición solicitada.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //set main clais to get result...
            Customers customerFound = null;

            //name must be included...
            if (customerDTO.getCustomerName() == null || customerDTO.getCustomerName().trim().equals("")) {
                vlMsg = "Es necesario incluir el nombre del cliente a modificar, de lo contrario no podremos realizar la petición solicitada.";
                logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                throw new BadRequestException(vlMsg);
            } else {
                // date of birth must be included...
                if (customerDTO.getDateBirth() == null) {
                    vlMsg = "Es necesario incluir la fecha de nacimiento del cliente a modificar, de lo contrario no podremos realizar la petición solicitada.";
                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                    throw new BadRequestException(vlMsg);
                } else {
                    //status must be included...
                    if (customerDTO.getActivate() == null) {
                        vlMsg = "Es necesario indicar con false o true la actividad del cliente a modificar, 'true' para activo y 'false' para inactivo, de lo contrario no podremos realizar la petición solicitada.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new BadRequestException(vlMsg);
                    } else {
                        //we need to validate if customerID exists...
                        vlMsg = "Validamos que el ID del cliente a editar exista.";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        customerFound = customersRepository.findById(customerDTO.getCustomerID())
                                .orElseThrow(() -> {
                                    //we don't have information with those arguments...
                                    vlMsg = "El ID del cliente enviado no existe, por lo tanto, no podremos actualizar los datos del cliente enviado.";
                                    logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                                    throw new IllegalQueryOperationException(vlMsg);
                                });
                    }
                }
            }

            //passing values to entity from dto...
            vlMsg = "Pasando DTO a Entity para realizar la actualización de la información.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            //Customers customersEdit = DTOtoEntity(customerDTO);
            customerFound = DTOtoEntity(customerDTO);

            //if preview validations are ok, we can continues with the update...
            vlMsg = "Realizamos la actualización de la información del clienteID enviado.";
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
            vlMsg = "Es necesario incluir en el pathvariable el ID del cliente, de lo contrario no podremos realizar la petición solicitada.";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            throw new BadRequestException(vlMsg);
        } else {
            //get information from current customer...
            vlMsg = "Realizamos la consulta del clienteID [" + customerID + "]";
            logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
            Customers customers = customersRepository.findById(customerID)
                    .orElseThrow(() ->
                    {
                        //no record found...
                        vlMsg = "No se ha encontrado información del clienteID [" + customerID + "]";
                        logsHandle.addLogController(logTracking, vlMsg, httpServletRequest);
                        throw new IllegalQueryOperationException(vlMsg);
                    });

            //customer found...
            vlMsg = "El clienteID [" + customerID + "] se ha eliminado del sistema.";
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
