package org.xbee.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xbee.project.util.ErrorInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(annotations = RestController.class)
public class GlobalControllerExceptionHandler {

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Map<String, String>> conflict(HttpServletRequest req, Exception e) {
        Throwable rootCause = getRootCause(e);
        /*ErrorInfo errorInfo = new ErrorInfo(req.getRequestURL(), Exception.class.getSimpleName(), new String[]{rootCause.toString()});*/
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HashMap<String, String>(){{
            put("url", req.getRequestURL().toString());
            put("type", e.getClass().getSimpleName());
            put("details", rootCause.toString());
        }});
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
