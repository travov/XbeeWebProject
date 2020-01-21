package org.xbee.project.controller;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xbee.project.listener.MyDiscoveryListener;
import org.xbee.project.listener.MyIIOSampleReceiveListener;
import org.xbee.project.model.IOLineState;
import org.xbee.project.model.MyRemoteXbeeDevice;
import org.xbee.project.util.HexUtils;
import org.xbee.project.util.ResponseObject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(InputOutputController.REST_URL)
public class InputOutputController {

    static final String REST_URL = "/io";

    private final Logger log = LoggerFactory.getLogger(getClass());

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

    @PostMapping(value = "/discovery", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity startDiscoveryProcess(@RequestParam("timeout") Integer timeout) throws XBeeException {
        log.info("discovery process with timeout={}", timeout);
        if (!discoveryListener.devices.isEmpty()) {
            discoveryListener.devices.clear();
        }
        network.setDiscoveryTimeout(timeout);
        network.startDiscoveryProcess();
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/states", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<IOLineState> getStates(@RequestParam("deviceId")  Integer deviceId,
                                       @RequestParam(value = "line", required = false) String line,
                                       @RequestParam(value = "startDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
                                       @RequestParam(value = "endDateTime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime){
        log.info("states from id={} from line {}", deviceId, line);
        List<IOLineState> states;
        if (deviceId != null && line == null && startDateTime == null && endDateTime == null){
          states = receiveListener.getByDeviceId(deviceId);
        }
        else states = receiveListener.getByDeviceIdAndTime(deviceId, line, startDateTime, endDateTime);
        return states;
    }

    @GetMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MyRemoteXbeeDevice> getDiscoveredDevices(@RequestParam(value = "active", required = false) Boolean active){
        log.info("devices");
        List<MyRemoteXbeeDevice> devices;
        if (active == null)
            devices = discoveryListener.getAllDevices();
        else
            devices = discoveryListener.getActiveDevices(active);
        return devices;
    }

    @PutMapping(value = "/sampling", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setSampling(@RequestParam("mac") String mac,
                                      @RequestParam("rate") String rate) throws XBeeException {
        log.info("sampling rate {} with mac={}", rate, mac);
        RemoteXBeeDevice device = discoveryListener.getDevice(mac);
        device.setIOSamplingRate(Integer.parseInt(rate));
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/changeDetection", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setChangeDetection(@RequestParam("mac") String mac,
                                             @RequestBody Set<Integer> lines) throws XBeeException {
        log.info("set change detection to lines {} with mac={}", lines, mac);
        Set<IOLine> set = new HashSet<>();
        lines.forEach(line -> set.add(IOLine.getDIO(line)));
        RemoteXBeeDevice device = discoveryListener.getDevice(mac);
        device.setDIOChangeDetection(set);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/changeDetection", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseObject<String, Set> getChangeDetection(@RequestParam("mac") String mac) throws XBeeException {
        log.info("get change detection from mac={}", mac);
        RemoteXBeeDevice device = discoveryListener.getDevice(mac);
        Set<IOLine> cd = device.getDIOChangeDetection();
        Set<Integer> changeDetectionLines;
        if (cd != null)
            changeDetectionLines = cd.stream().map(IOLine::getIndex).collect(Collectors.toSet());
        else
            changeDetectionLines = new HashSet<>();
        return new ResponseObject<>(mac,  new HashMap<String, Set>(){{
            put("IC", changeDetectionLines);
        }});
    }

    @PutMapping(value = "/wr", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity write(@RequestParam("mac") String mac) throws XBeeException {
        log.info("write to mac={}", mac);
        RemoteXBeeDevice device = discoveryListener.getDevice(mac);
        device.writeChanges();
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/dio", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseObject<String, String> getDioValue(@RequestParam("mac") String mac,
                                                      @RequestParam("index") int index) throws XBeeException {
        log.info("get dio from mac={} with index={}", mac, index);
        RemoteXBeeDevice device = discoveryListener.getDevice(mac);
        IOLine line = IOLine.getDIO(index);
        String value = device.getDIOValue(line).getName();
        return new ResponseObject<>(mac, new HashMap<String, String>(){{
            put(line.getConfigurationATCommand(), value);
        }});
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setNodeIdentifier(@RequestParam("mac") String mac,
                                            @RequestParam("newId") String nodeId) throws XBeeException {
        log.info("set node identifier {} to mac={}", nodeId, mac);
        RemoteXBeeDevice device = discoveryListener.getDevice(mac);
        device.setNodeID(nodeId);
        discoveryListener.updateDevice(device);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/param", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseObject<String, String> getParameter(@RequestParam("mac") String mac,
                                                       @RequestParam("at") String param) throws XBeeException {
        log.info("get parameter {} from mac={}", param, mac);
        byte[] parameter = discoveryListener.getDevice(mac).getParameter(param);
        String value = HexUtils.byteArrayToHexString(parameter, param);
        return new ResponseObject<>(mac, new HashMap<String, String>(){{
            put(param, value);
        }});
    }

    @PutMapping(value = "/param", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setParameter(@RequestParam("mac") String mac,
                                       @RequestParam("at") String param,
                                       @RequestParam("value") String value) throws XBeeException {
        log.info("set parameter {} with value={} to mac {}", param, value, mac);
        byte[] bytes = HexUtils.hexStringToByteArray(value, param);
        RemoteXBeeDevice device = discoveryListener.getDevice(mac);
        device.setParameter(param, bytes);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/reach", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity checkIfReachable() {
        log.info("check if server is reachable");
        return ResponseEntity.ok().build();
    }
}