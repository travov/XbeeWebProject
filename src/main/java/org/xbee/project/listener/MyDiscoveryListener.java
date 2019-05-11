package org.xbee.project.listener;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IDiscoveryListener;
import com.digi.xbee.api.utils.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbee.project.model.MyRemoteXbeeDevice;
import org.xbee.project.repository.DeviceRepository;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class MyDiscoveryListener implements IDiscoveryListener {

    @Autowired
    private DeviceRepository repository;

    public List<RemoteXBeeDevice> devices = new ArrayList<>();

    public static Set<MyRemoteXbeeDevice> dp = new HashSet<>();

    public static Set<MyRemoteXbeeDevice> db = new HashSet<>();

    @Override
    public void deviceDiscovered(RemoteXBeeDevice discoveredDevice) {
        devices.add(discoveredDevice);
    }

    public static void main(String[] args) {
        dp.add(new MyRemoteXbeeDevice("1", "XBEE641234", "1234", true));
        dp.add(new MyRemoteXbeeDevice("8", "XBEE645678", "5678", true));
        dp.add(new MyRemoteXbeeDevice("9", "XBEE6491011", "9101", true));
        dp.add(new MyRemoteXbeeDevice("52", "XBEE525252", "5252", true));
        dp.add(new MyRemoteXbeeDevice("53", "XBEE535353", "5353", true));

        db.add(new MyRemoteXbeeDevice("1", "XBEE641234", "1234", false));
        db.add(new MyRemoteXbeeDevice("3", "XBEE643333", "3333", true));
        db.add(new MyRemoteXbeeDevice("8", "XBEE645678", "5678", false));
        db.add(new MyRemoteXbeeDevice("9", "XBEE6491011", "9101", true));
        db.add(new MyRemoteXbeeDevice("10", "XBEE6410100", "10100", false));
        db.add(new MyRemoteXbeeDevice("50", "XBEE645050", "5050", true));
        db.add(new MyRemoteXbeeDevice("51", "XBEE645151", "5151", false));

        AtomicInteger count = new AtomicInteger();

        Set<MyRemoteXbeeDevice> bothButDbNoActive = db.stream().filter(d -> !d.isActive()).collect(Collectors.toSet());
        count.getAndIncrement();
        bothButDbNoActive.retainAll(dp);
        //dp.forEach(System.out::println);
        System.out.println("Есть в обоих списках но в дб неактивны");
        bothButDbNoActive.forEach(d -> {
            System.out.println(d);
            db.remove(d);
            db.add(new MyRemoteXbeeDevice(d.getNodeId(), d.getxBee64BitAddress(),d.getxBee16BitAddress(), d.isActive()));
            count.getAndIncrement();
        });

        System.out.println("-----------------------------------------");

        Set<MyRemoteXbeeDevice> notContainsInDb = new HashSet<>(dp);
        notContainsInDb.removeAll(db);
        System.out.println("Нет в дб");
        notContainsInDb.forEach(d -> {
            System.out.println(d);
            db.remove(d);
            db.add(new MyRemoteXbeeDevice(d.getNodeId(), d.getxBee64BitAddress(), d.getxBee16BitAddress(), true));
            count.getAndIncrement();
        });
        System.out.println("-----------------------------------------");

        Set<MyRemoteXbeeDevice> justInDbAndActive = db.stream().filter(MyRemoteXbeeDevice::isActive).collect(Collectors.toSet());
        justInDbAndActive.removeAll(dp);
        //dp.forEach(System.out::println);
        System.out.println("Нету в списке discovery process но активны в дб");
        justInDbAndActive.forEach(d -> {
            System.out.println(d);
            db.remove(d);
            db.add(new MyRemoteXbeeDevice(d.getNodeId(), d.getxBee64BitAddress(), d.getxBee16BitAddress(), false));
            count.getAndIncrement();
        });

        List<MyRemoteXbeeDevice> collect = db.stream().sorted((d1, d2) -> Integer.valueOf(d1.getNodeId()).compareTo(Integer.valueOf(d2.getNodeId()))).collect(Collectors.toList());
        //collect.forEach(System.out::println);
        System.out.println("Number of queries in db is " + count);

    }

    @Override
    public void discoveryError(String error) { //add throwing exception
        System.out.println(">> There was an error discovering devices: " + error);
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
            System.out.println(">> Discovery process finished successfully.");
        }
        else
            System.out.println(">> Discovery process finished due to the following error: " + error);
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