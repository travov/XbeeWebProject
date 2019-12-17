package org.xbee.project.util;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

public class ResponseObject {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String adr64bit;

    private String method;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> parameters;

    public ResponseObject(String method) {
        this.method = method;
    }

    public ResponseObject(String adr64bit, String method, Map<String, String> parameters) {
        this.adr64bit = adr64bit;
        this.method = method;
        this.parameters = parameters;
    }

    public String getAdr64bit() {
        return adr64bit;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

}
