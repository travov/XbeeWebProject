package org.xbee.project.listener;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.utils.HexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbee.project.model.MyRemoteXbeeDevice;
import org.xbee.project.repository.DeviceRepository;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MyDiscoveryListener implements IDiscoveryListener {

    @Autowired
    private DeviceRepository repository;
    private static final Logger log = LoggerFactory.getLogger(MyDiscoveryListener.class);

    public List<RemoteXBeeDevice> devices = new ArrayList<>();

    @Override
    public void deviceDiscovered(RemoteXBeeDevice discoveredDevice) {
        devices.add(discoveredDevice);
    }

    @Override
    public void discoveryError(String error) { //add throwing exception
        log.info(">> There was an error discovering devices: " + error);
        log.error(error);
    }

    @Override
    public void discoveryFinished(String error) {
        if (error == null) {
            //devices from discovery process
            Set<MyRemoteXbeeDevice> dp = new HashSet<>();
            devices.forEach(d ->{
                try {
                    String firmwareVersion = HexUtils.byteArrayToHexString(d.getParameter("VR"));
                    dp.add(new MyRemoteXbeeDevice(d.getNodeID(), d.get64BitAddress().toString(), d.get16BitAddress().toString(), firmwareVersion));
                } catch (XBeeException e) {
                    e.printStackTrace();
                    log.error("Exception " + e.getClass().getName(), e);
                }
            });

            //devices from database
            Set<MyRemoteXbeeDevice> database = new HashSet<>(repository.getAll());

            //devices are contained in both lists but in database list they are not active
            Set<MyRemoteXbeeDevice> bothButDbNoActive = database.stream().filter(d -> !d.isActive()).collect(Collectors.toSet());
            bothButDbNoActive.retainAll(dp);
            if (!bothButDbNoActive.isEmpty())
                bothButDbNoActive.forEach(d -> {
                    d.setActive(true);
                    repository.save(d);
                });

            //devices that are not contained in db
            Set<MyRemoteXbeeDevice> notContainedInDb = new HashSet<>(dp);
            notContainedInDb.removeAll(database);
            if (!notContainedInDb.isEmpty())
                notContainedInDb.forEach(d -> repository.save(d));

            //devices that are contained only in db and active
            Set<MyRemoteXbeeDevice> onlyInDbAndActive = database.stream().filter(MyRemoteXbeeDevice::isActive).collect(Collectors.toSet());
            onlyInDbAndActive.removeAll(dp);
            if (!onlyInDbAndActive.isEmpty()) {
                onlyInDbAndActive.forEach(d -> {
                    d.setActive(false);
                    repository.save(d);
                });
            }
            log.info(">> Discovery process finished successfully.");
        }
        else {
            log.info(">> Discovery process finished due to the following error: " + error);
            log.error(error);
        }
    }

    public RemoteXBeeDevice getDevice(String XBee64BitAddress){
        return devices.stream().filter(device -> XBee64BitAddress.equals(device.get64BitAddress().toString())).findAny().orElse(null);
    }

    public List<MyRemoteXbeeDevice> getAllDevices(){
       return repository.getAll();
    }

    public List<MyRemoteXbeeDevice> getActiveDevices(Boolean active){
        return repository.getAllWithActive(active);
    }

    public void updateDevice(RemoteXBeeDevice remote) throws XBeeException {
        MyRemoteXbeeDevice device = repository.get(remote.get64BitAddress().toString());
        String nodeId = new String(remote.getParameter("NI"), StandardCharsets.UTF_8);
        String firmwareVersion = HexUtils.byteArrayToHexString(remote.getParameter("VR"));
        device.setNodeId(nodeId);
        device.setRole(firmwareVersion);
        repository.save(device);

    }
}