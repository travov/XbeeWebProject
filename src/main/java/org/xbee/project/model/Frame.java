package org.xbee.project.model;

public class Frame {

    private String type;

    private String frameId;

    private String longSourceAddress;

    private String shortSourceAddress;

    private String digitalMask;

    private String analogMask;

    private String ATcommand;

    private String response;

    private String status;

    public Frame(String type, String longSourceAddress, String shortSourceAddress, String digitalMask, String analogMask) {
        this.type = type;
        this.longSourceAddress = longSourceAddress;
        this.shortSourceAddress = shortSourceAddress;
        this.digitalMask = digitalMask;
        this.analogMask = analogMask;
    }

    public Frame(String type, String frameId, String longSourceAddress, String shortSourceAddress, String digitalMask, String analogMask) {
        this.type = type;
        this.frameId = frameId;
        this.longSourceAddress = longSourceAddress;
        this.shortSourceAddress = shortSourceAddress;
        this.digitalMask = digitalMask;
        this.analogMask = analogMask;
    }

    public Frame(String type, String longSourceAddress, String shortSourceAddress, String digitalMask, String analogMask, String ATcommand, String status, String response) {
        this.type = type;
        this.longSourceAddress = longSourceAddress;
        this.shortSourceAddress = shortSourceAddress;
        this.digitalMask = digitalMask;
        this.analogMask = analogMask;
        this.ATcommand = ATcommand;
        this.status = status;
        this.response = response;
    }

    public Frame(String type, String frameId,  String longSourceAddress, String shortSourceAddress, String digitalMask, String analogMask, String ATcommand, String status, String response) {
        this.type = type;
        this.frameId = frameId;
        this.longSourceAddress = longSourceAddress;
        this.shortSourceAddress = shortSourceAddress;
        this.digitalMask = digitalMask;
        this.analogMask = analogMask;
        this.ATcommand = ATcommand;
        this.status = status;
        this.response = response;
    }

    public String getType() {
        return type;
    }

    public String getLongSourceAddress() {
        return longSourceAddress;
    }

    public String getShortSourceAddress() {
        return shortSourceAddress;
    }

    public String getDigitalMask() {
        return digitalMask;
    }

    public String getAnalogMask() {
        return analogMask;
    }

    public String getATcommand() {
        return ATcommand;
    }

    public String getResponse() {
        return response;
    }

    public String getStatus() {
        return status;
    }

    public String getFrameId() {
        return frameId;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }
}
