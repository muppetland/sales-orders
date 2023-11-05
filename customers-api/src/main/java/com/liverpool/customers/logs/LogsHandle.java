package com.liverpool.customers.logs;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;

@Log4j2
public class LogsHandle {
    public void addLogController(Logger logger, String msg, HttpServletRequest httpServletRequest){
        //adding log...
        logger.info("Consumiendo servicio desde: " + httpServletRequest.getRemoteAddr());
        logger.info("Log de la clase: " + logger.getName());
        logger.info(msg);
    }
}
