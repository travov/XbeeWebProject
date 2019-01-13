package org.xbee.project.listener;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;

public class MyIIOSampleReceiveListener implements IIOSampleReceiveListener {

    @Override
    public void ioSampleReceived(RemoteXBeeDevice remoteDevice, IOSample ioSample) {
        ioSample.getDigitalValues().forEach((key, value) -> System.out.println("Digital Pin: " + key.getName() + " Value: " + value.getName()));
        System.out.println();

        ioSample.getAnalogValues().forEach((key, value) -> System.out.println(" Analog Pin: " + key.getName() + " Value: " + value));
        System.out.println();
    }
}
