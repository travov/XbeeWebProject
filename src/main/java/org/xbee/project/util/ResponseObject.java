package org.xbee.project.util;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

public class ResponseObject<K, V> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String adr64bit;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<K, V> parameters;

    public ResponseObject(String adr64bit, Map<K, V> parameters) {
        this.adr64bit = adr64bit;
        this.parameters = parameters;
    }

    public String getAdr64bit() {
        return adr64bit;
    }

    public Map<K, V> getParameters() {
        return parameters;
    }

}
