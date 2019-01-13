package org.xbee.project.controller;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.utils.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.xbee.project.listener.MyDiscoveryListener;
import org.xbee.project.model.MyRemoteXbeeDevice;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping(InputOutputController.REST_URL)
public class InputOutputController {

    static final String REST_URL = "/io";

    private XBeeNetwork network;

    private MyDiscoveryListener discoveryListener;

    public static XBeeDevice device;

    @Autowired
    public InputOutputController(XBeeDevice device, IDiscoveryListener discoveryListener, IIOSampleReceiveListener receiveListener) {
        InputOutputController.device = device;
        try {
            device.open();
            this.network = device.getNetwork();
            this.discoveryListener = (MyDiscoveryListener) discoveryListener;
            network.addDiscoveryListener(discoveryListener);
            network.setDiscoveryTimeout(15000);
            //device.addPacketListener(new MyDataReceiveListener());
            device.addIOSampleListener(receiveListener);

        } catch (XBeeException e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/discover", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Integer> startDiscoveryProcess() {
        //clear list if not null
        if (!discoveryListener.devices.isEmpty()) {
            discoveryListener.devices.clear();
            discoveryListener.getDevices().clear();
        }

        network.startDiscoveryProcess();
        Map<String, Integer> map = new HashMap<>();
        map.put("discoveryTime", 8000);
        return map;
}

    @GetMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MyRemoteXbeeDevice> getDiscoveredDevices(){
        return discoveryListener.getDevices();
    }

    @PutMapping(value = "/sampling")
    public void setSampling(@RequestParam("adr64bit") String XBee64BitAddress,
                              @RequestParam("adr16bit") String XBee16BitAddress,
                              @RequestParam("rate") String rate) throws XBeeException { //AT may be equal IC or IR
        RemoteXBeeDevice device = discoveryListener.getDevice(XBee64BitAddress, XBee16BitAddress);
        device.setIOSamplingRate(Integer.parseInt(rate));
    }

    @PutMapping(value = "/changeDetection", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setChangeDetection(@RequestParam("adr64bit") String XBee64BitAddress,
                                   @RequestParam("adr16bit") String XBee16BitAddress,
                                   @RequestBody Set<Integer> lines) throws XBeeException {
        Set<IOLine> set = new HashSet<>();
        lines.forEach(line -> set.add(IOLine.getDIO(line)));
        RemoteXBeeDevice device = discoveryListener.getDevice(XBee64BitAddress, XBee16BitAddress);
        device.setDIOChangeDetection(set);
    }

    @PutMapping(value = "/wr")
    public void write(@RequestParam("adr64bit") String XBee64BitAddress,
                      @RequestParam("adr16bit") String XBee16BitAddress) throws XBeeException {
        RemoteXBeeDevice device = discoveryListener.getDevice(XBee64BitAddress, XBee16BitAddress);
        device.writeChanges();
    }

    @GetMapping(value = "/dio", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getDioValue(@RequestParam("adr64bit") String XBee64BitAddress,
                                           @RequestParam("adr16bit") String XBee16BitAddress,
                                           @RequestParam("index") int index) throws XBeeException {
        RemoteXBeeDevice device = discoveryListener.getDevice(XBee64BitAddress, XBee16BitAddress);
        IOLine line = IOLine.getDIO(index);
        String value = device.getDIOValue(line).getName();
        Map<String, String> map = new HashMap<>();
        map.put(line.getConfigurationATCommand(), value);
        return map;

    }

    @PutMapping
    public void setNodeIdentifier(@RequestParam("adr64bit") String XBee64BitAddress,
                                    @RequestParam("adr16bit") String XBee16BitAddress,
                                    @RequestParam("newId") String id) throws XBeeException {
        RemoteXBeeDevice device = discoveryListener.getDevice(XBee64BitAddress, XBee16BitAddress);
        device.setNodeID(id);
    }

    @GetMapping(value = "/param", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getParameter(@RequestParam("adr64bit") String XBee64BitAddress,
                                            @RequestParam("adr16bit") String XBee16BitAddress,
                                            @RequestParam("at") String param) throws XBeeException {

        byte[] parameter = discoveryListener.getDevice(XBee64BitAddress, XBee16BitAddress).getParameter(param);
        String value = param.equals("NI") ? new String(parameter, StandardCharsets.UTF_8) : HexUtils.byteArrayToHexString(parameter);
        Map<String, String> map = new HashMap<>();
        map.put(param, value);
        return map;
    }

    @PutMapping(value = "/param", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> setParameter(@RequestParam("adr64bit") String XBee64BitAddress,
                                            @RequestParam("adr16bit") String XBee16BitAddress,
                                            @RequestParam("at") String param,
                                            @RequestParam("value") String value) throws XBeeException {
        byte[] bytes = param.equals("NI") ? value.getBytes() : HexUtils.hexStringToByteArray(value);
        RemoteXBeeDevice device = discoveryListener.getDevice(XBee64BitAddress, XBee16BitAddress);
        device.setParameter(param, bytes);
        return getParameter(XBee64BitAddress, XBee16BitAddress, param);
    }
}