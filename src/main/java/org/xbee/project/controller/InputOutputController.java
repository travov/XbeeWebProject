package org.xbee.project.controller;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.utils.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.xbee.project.listener.MyDiscoveryListener;
import org.xbee.project.listener.MyIIOSampleReceiveListener;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.xbee.project.listener.MyDiscoveryListener.getDevice;

@RestController
@RequestMapping(InputOutputController.REST_URL)
public class InputOutputController {

    static final String REST_URL = "/io";

    public static int frameId = 1;

    public static int maxTime = 0;

    private XBeeNetwork network;

    public static XBeeDevice device;

    @Autowired
    public InputOutputController(XBeeDevice device, IDiscoveryListener discoveryListener) {
        InputOutputController.device = device;
        try {
            device.open();
            this.network = device.getNetwork();
            /*ApplicationContext context = new ClassPathXmlApplicationContext("spring/spring-db.xml");*/
            network.addDiscoveryListener(discoveryListener);
            network.setDiscoveryTimeout(15000);
            //device.addPacketListener(new MyDataReceiveListener());
            device.addIOSampleListener(new MyIIOSampleReceiveListener());

        } catch (XBeeException e) {
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/discover", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Integer> startDiscoveryProcess() {
        //clear list if not null
        if (!MyDiscoveryListener.devices.isEmpty())
            MyDiscoveryListener.devices.clear();

        network.startDiscoveryProcess();
        Map<String, Integer> map = new HashMap<>();
        map.put("discoveryTime", 8000);
        return map;
}

    /*@GetMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MyRemoteXbeeDevice> getDiscoveredDevices(){
        return MyDiscoveryListener.devices;
    }*/

    @PutMapping(value = "/sampling", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setSampling(@RequestParam("adr64bit") String XBee64BitAddress,
                              @RequestParam("adr16bit") String XBee16BitAddress,
                              @RequestParam("rate") String rate) throws XBeeException { //AT may be equal IC or IR
        RemoteXBeeDevice device = getDevice(XBee64BitAddress, XBee16BitAddress);
        device.setIOSamplingRate(Integer.parseInt(rate));
    }

    @PutMapping(value = "/changeDetection", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void setChangeDetection(@RequestParam("adr64bit") String XBee64BitAddress,
                                   @RequestParam("adr16bit") String XBee16BitAddress,
                                   @RequestBody Set<Integer> lines) throws XBeeException {
        Set<IOLine> set = new HashSet<>();
        lines.forEach(line -> set.add(IOLine.getDIO(line)));
        RemoteXBeeDevice device = getDevice(XBee64BitAddress, XBee16BitAddress);
        device.setDIOChangeDetection(set);
    }

    @PutMapping(value = "/wr", produces = MediaType.APPLICATION_JSON_VALUE)
    public void write(@RequestParam("adr64bit") String XBee64BitAddress,
                      @RequestParam("adr16bit") String XBee16BitAddress) throws XBeeException {
        RemoteXBeeDevice device = getDevice(XBee64BitAddress, XBee16BitAddress);
        device.writeChanges();
    }

    @GetMapping(value = "/dio")
    public String getDioValue(@RequestParam("adr64bit") String XBee64BitAddress,
                              @RequestParam("adr16bit") String XBee16BitAddress,
                              @RequestParam("index") int index) throws XBeeException {
        RemoteXBeeDevice device = getDevice(XBee64BitAddress, XBee16BitAddress);
        return device.getDIOValue(IOLine.getDIO(index)).getName();
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public void setNodeIdentifier(@RequestParam("adr64bit") String XBee64BitAddress,
                                    @RequestParam("adr16bit") String XBee16BitAddress,
                                    @RequestParam("newId") String id) throws XBeeException {
        RemoteXBeeDevice device = getDevice(XBee64BitAddress, XBee16BitAddress);
        device.setNodeID(id);
    }

    @GetMapping("/param")
    public String getParameter(@RequestParam("adr64bit") String XBee64BitAddress,
                               @RequestParam("adr16bit") String XBee16BitAddress,
                               @RequestParam("at") String param) throws XBeeException {

        byte[] parameter = getDevice(XBee64BitAddress, XBee16BitAddress).getParameter(param);
        String s = param.equals("NI") ? new String(parameter, StandardCharsets.UTF_8) : HexUtils.byteArrayToHexString(parameter);
        System.out.println(s);
        return s;
    }

    @PutMapping("/param")
    public String setParameter(@RequestParam("adr64bit") String XBee64BitAddress,
                               @RequestParam("adr16bit") String XBee16BitAddress,
                               @RequestParam("at") String param,
                               @RequestParam("value") String value) throws XBeeException {
        byte[] bytes = param.equals("NI") ? value.getBytes() : HexUtils.hexStringToByteArray(value);
        RemoteXBeeDevice device = getDevice(XBee64BitAddress, XBee16BitAddress);
        device.setParameter(param, bytes);
        return getParameter(XBee64BitAddress, XBee16BitAddress, param);
    }

    public static void checkFrameId(){
        frameId++;
        if (frameId == 255)
            frameId = 1;
    }

    /*public Frame getReceivedFrame(int frameId) throws InterruptedException {
        long time = System.currentTimeMillis();
        if (maxTime < 33) throw new IllegalArgumentException("Maximum time must not be less than 33!");
        long end = time + maxTime;
        Frame frame = null;
        while (System.currentTimeMillis() < end){
            frame = map.get(frameId);
            if (frame != null)
                break;
            //Thread.sleep(500);
        }
        return frame;
    }*/
}