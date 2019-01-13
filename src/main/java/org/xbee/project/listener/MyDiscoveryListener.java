package org.xbee.project.listener;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.utils.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbee.project.model.MyRemoteXbeeDevice;
import org.xbee.project.repository.DeviceRepository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import static org.xbee.project.controller.InputOutputController.*;

@Component
public class MyDiscoveryListener implements IDiscoveryListener {

    @Autowired
    private DeviceRepository repository;

    public static List<RemoteXBeeDevice> devices = new ArrayList<>();

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
                devices.forEach(d -> repository.save(new MyRemoteXbeeDevice(d.getNodeID(), d.get64BitAddress().toString(), d.get16BitAddress().toString(), "role")));
                System.out.println(">> Discovery process finished successfully.");
        }
        else
            System.out.println(">> Discovery process finished due to the following error: " + error);
    }

    public static RemoteXBeeDevice getDevice(String XBee64BitAddress, String XBee16BitAddress){
        return devices.stream().filter(device -> XBee64BitAddress.equals(device.get64BitAddress().toString())
                & XBee16BitAddress.equals(device.get16BitAddress().toString())).findAny().orElse(null);
    }


    public void getMaxTime() throws XBeeException, InterruptedException {
        int max = 0;
        for (RemoteXBeeDevice d: MyDiscoveryListener.devices) {
            int sleepPeriod = Integer.parseInt(new BigInteger(HexUtils.byteArrayToHexString(d.getParameter("SP")), 16).toString(10)) * 10;
            System.out.println(d.getNodeID() + " sleep period: " + sleepPeriod);
            int timeBeforeSleep = Integer.parseInt(new BigInteger(HexUtils.byteArrayToHexString(d.getParameter("ST")), 16).toString(10));
            System.out.println(d.getNodeID() + " time before sleep: " + timeBeforeSleep);
            int sum = sleepPeriod + timeBeforeSleep;
            if (sum > max)
                max = sum;
        }

        System.out.println("Max: " + max);
        maxTime = max;
    }
}