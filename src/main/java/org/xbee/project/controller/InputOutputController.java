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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xbee.project.listener.MyDiscoveryListener;
import org.xbee.project.listener.MyIIOSampleReceiveListener;
import org.xbee.project.model.IOLineState;
import org.xbee.project.model.MyRemoteXbeeDevice;
import org.xbee.project.util.ResponseObject;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(InputOutputController.REST_URL)
public class InputOutputController {

    static final String REST_URL = "/io";

    private XBeeNetwork network;

    private MyDiscoveryListener discoveryListener;

    protected static XBeeDevice device;

    private MyIIOSampleReceiveListener receiveListener;

    @Autowired
    public InputOutputController(XBeeDevice device, IDiscoveryListener discoveryListener, IIOSampleReceiveListener receiveListener) {
        InputOutputController.device = device;
        try {
            device.open();
            this.network = device.getNetwork();
            this.discoveryListener = (MyDiscoveryListener) discoveryListener;
            this.receiveListener = (MyIIOSampleReceiveListener) receiveListener;
            network.addDiscoveryListener(discoveryListener);
            device.addIOSampleListener(receiveListener);

        } catch (XBeeException e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/discover", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> startDiscoveryProcess(@RequestParam("timeout") Integer timeout) throws XBeeException {
        //clear list if not null
        if (!discoveryListener.devices.isEmpty()) {
            discoveryListener.devices.clear();
        }
        network.setDiscoveryTimeout(timeout);
        network.startDiscoveryProcess();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("startDiscoveryProcess"));
    }

    @GetMapping(value = "/states", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<IOLineState>> getStates(@RequestParam("deviceId")  Integer deviceId,
                                       @RequestParam(value = "at",required = false) String atCommand,
                                       @RequestParam(value = "startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
                                       @RequestParam(value = "endDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime){
        List<IOLineState> states;
        if (deviceId != null && atCommand == null && startDateTime == null && endDateTime == null){
          states = receiveListener.getByDeviceId(deviceId);
        }
        else states = receiveListener .getByDeviceIdAndTime(deviceId, atCommand, startDateTime, endDateTime);
        return ResponseEntity.status(HttpStatus.OK).body(states);
    }

    @GetMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MyRemoteXbeeDevice>> getDiscoveredDevices(@RequestParam(value = "active", required = false) Boolean active){
        List<MyRemoteXbeeDevice> devices;
        if (active == null)
            devices = discoveryListener.getAllDevices();
        else
            devices = discoveryListener.getActiveDevices(active);
        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }

    @PutMapping(value = "/sampling", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> setSampling(@RequestParam("adr64bit") String XBee64BitAddress,
                              @RequestParam("rate") String rate) throws XBeeException {
        RemoteXBeeDevice device = discoveryListener.getDevice(XBee64BitAddress);
        device.setIOSamplingRate(Integer.parseInt(rate));
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("setSampling"));
    }

    @PutMapping(value = "/changeDetection", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> setChangeDetection(@RequestParam("adr64bit") String XBee64BitAddress,
                                   @RequestBody Set<Integer> lines) throws XBeeException {
        Set<IOLine> set = new HashSet<>();
        lines.forEach(line -> set.add(IOLine.getDIO(line)));
        RemoteXBeeDevice device = discoveryListener.getDevice(XBee64BitAddress);
        device.setDIOChangeDetection(set);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("setChangeDetection"));
    }

    @PutMapping(value = "/wr", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> write(@RequestParam("adr64bit") String XBee64BitAddress) throws XBeeException {
        RemoteXBeeDevice device = discoveryListener.getDevice(XBee64BitAddress);
        device.writeChanges();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("write"));
    }

    @GetMapping(value = "/dio", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> getDioValue(@RequestParam("adr64bit") String XBee64BitAddress,
                                           @RequestParam("index") int index) throws XBeeException {
        RemoteXBeeDevice device = discoveryListener.getDevice(XBee64BitAddress);
        IOLine line = IOLine.getDIO(index);
        String value = device.getDIOValue(line).getName();
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(XBee64BitAddress, "getDioValue", new HashMap<String, String>(){{
            put(line.getConfigurationATCommand(), value);
        }}));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> setNodeIdentifier(@RequestParam("adr64bit") String XBee64BitAddress,
                                    @RequestParam("newId") String id) throws XBeeException {
        RemoteXBeeDevice device = discoveryListener.getDevice(XBee64BitAddress);
        device.setNodeID(id);
        discoveryListener.updateDevice(device);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("setNodeIdentifier"));
    }

    @GetMapping(value = "/param", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> getParameter(@RequestParam("adr64bit") String XBee64BitAddress,
                                            @RequestParam("at") String param) throws XBeeException {

        byte[] parameter = discoveryListener.getDevice(XBee64BitAddress).getParameter(param);
        String value = param.equals("NI") ? new String(parameter, StandardCharsets.UTF_8) : HexUtils.byteArrayToHexString(parameter);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(XBee64BitAddress, "getParameter", new HashMap<String, String>(){{
            put(param, value);
        }}));
    }

    @PutMapping(value = "/param", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseObject> setParameter(@RequestParam("adr64bit") String XBee64BitAddress,
                                            @RequestParam("at") String param,
                                            @RequestParam("value") String value) throws XBeeException {
        byte[] bytes = param.equals("NI") ? value.getBytes() : HexUtils.hexStringToByteArray(value);
        RemoteXBeeDevice device = discoveryListener.getDevice(XBee64BitAddress);
        device.setParameter(param, bytes);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("setParameter"));
    }
}