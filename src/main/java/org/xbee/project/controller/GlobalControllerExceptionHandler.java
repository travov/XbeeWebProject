package org.xbee.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.xbee.project.util.ErrorInfo;
import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice(annotations = RestController.class)
public class GlobalControllerExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ErrorInfo conflict(HttpServletRequest req, Exception e) {
        Throwable rootCause = getRootCause(e);
        log.error("Exception at request " + req.getRequestURL(), rootCause);
        /*return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HashMap<String, String>(){{
            put("url", req.getRequestURL().toString());
            put("type", e.getClass().getSimpleName());
            put("details", rootCause.toString());
        }});*/
        return new ErrorInfo(req.getRequestURL(), e.getClass().getSimpleName(), rootCause.toString());
    }

    public static Throwable getRootCause(Throwable t) {
        Throwable result = t;
        Throwable cause;

        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }

}
