package org.xbee.project.listener;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbee.project.model.IOLineState;
import org.xbee.project.model.MyRemoteXbeeDevice;
import org.xbee.project.repository.DeviceRepository;
import org.xbee.project.repository.IOLineStateRepository;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class MyIIOSampleReceiveListener implements IIOSampleReceiveListener {

    @Autowired
    private IOLineStateRepository stateRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public void ioSampleReceived(RemoteXBeeDevice remoteDevice, IOSample ioSample) {

        Map<IOLine, IOValue> digitalValues = ioSample.getDigitalValues();
        Map<IOLine, Integer> analogValues = ioSample.getAnalogValues();

        MyRemoteXbeeDevice device = deviceRepository.get(remoteDevice.get64BitAddress().toString());

        digitalValues.forEach((key, value) -> {
            Integer linesId = stateRepository.getLineId(key.getConfigurationATCommand());
            IOLineState state = new IOLineState(device.getId(), linesId, value.getName(), LocalDateTime.now());
            stateRepository.save(state);
            System.out.println("Digital Pin: " + key.getName() + " Value: " + value.getName());
        });

        if (analogValues != null) {
            analogValues.forEach((key, value) -> {
                Integer linesId = stateRepository.getLineId(key.getConfigurationATCommand());
                IOLineState state = new IOLineState(device.getId(), linesId, String.valueOf(value), LocalDateTime.now());
                stateRepository.save(state);
                System.out.println("Digital Pin: " + key.getName() + " Value: " + value);
            });
        }
    }
}
