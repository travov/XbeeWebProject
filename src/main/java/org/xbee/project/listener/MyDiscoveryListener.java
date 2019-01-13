package org.xbee.project.listener;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.utils.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbee.project.model.MyRemoteXbeeDevice;
import org.xbee.project.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyDiscoveryListener implements IDiscoveryListener {

    @Autowired
    private DeviceRepository repository;

    public List<RemoteXBeeDevice> devices = new ArrayList<>();

    private List<MyRemoteXbeeDevice> myDevices = new ArrayList<>();

    @Override
    public void deviceDiscovered(RemoteXBeeDevice discoveredDevice) {
        devices.add(discoveredDevice);
    }

    @Override
    public void discoveryError(String error) {
        System.out.println(">> There was an error discovering devices: " + error);
    }

    @Override
    public void discoveryFinished(String error) {
        if (error == null) {
                devices.forEach(d -> {
                    try {
                        String firmwareVersion = HexUtils.byteArrayToHexString(d.getParameter("VR"));
                        MyRemoteXbeeDevice device = repository.save(new MyRemoteXbeeDevice(d.getNodeID(), d.get64BitAddress().toString(), d.get16BitAddress().toString(), firmwareVersion));
                        myDevices.add(device);
                    } catch (XBeeException e) {
                        e.printStackTrace();
                    }
                });
                System.out.println(">> Discovery process finished successfully.");
        }
        else
            System.out.println(">> Discovery process finished due to the following error: " + error);
    }

    public RemoteXBeeDevice getDevice(String XBee64BitAddress, String XBee16BitAddress){
        return devices.stream().filter(device -> XBee64BitAddress.equals(device.get64BitAddress().toString())
                & XBee16BitAddress.equals(device.get16BitAddress().toString())).findAny().orElse(null);
    }

    public List<MyRemoteXbeeDevice> getDevices(){
        return myDevices;
    }

}