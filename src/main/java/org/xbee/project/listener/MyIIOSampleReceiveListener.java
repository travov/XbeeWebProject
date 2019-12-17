package org.xbee.project.listener;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbee.project.model.IOLineState;
import org.xbee.project.model.MyRemoteXbeeDevice;
import org.xbee.project.repository.DeviceRepository;
import org.xbee.project.repository.IOLineStateRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class MyIIOSampleReceiveListener implements IIOSampleReceiveListener {

    private static final Logger log = LoggerFactory.getLogger(MyIIOSampleReceiveListener.class);

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
            log.info("Digital Pin: " + key.getName() + " Value: " + value.getName());
        });

        if (analogValues != null) {
            analogValues.forEach((key, value) -> {
                Integer linesId = stateRepository.getLineId(key.getConfigurationATCommand());
                IOLineState state = new IOLineState(device.getId(), linesId, String.valueOf(value), LocalDateTime.now());
                stateRepository.save(state);
                log.info("Analog Pin: " + key.getName() + " Value: " + value);
            });
        }
    }

    public List<IOLineState> getByDeviceId(Integer deviceId) {
        return stateRepository.getByDeviceId(deviceId);
    }

    public List<IOLineState> getByDeviceIdAndTime(Integer deviceId, String atCommand, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return stateRepository.getByDeviceIdAndTime(deviceId, atCommand, startDateTime, endDateTime);
    }
}
