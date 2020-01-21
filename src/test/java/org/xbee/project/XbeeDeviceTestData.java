package org.xbee.project;

import org.xbee.project.model.MyRemoteXbeeDevice;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class XbeeDeviceTestData {
    public static final MyRemoteXbeeDevice DEVICE1 = new MyRemoteXbeeDevice("RouterButton", "0013A20040EC3B01", "9F42", "23A7");
    public static final MyRemoteXbeeDevice DEVICE2 = new MyRemoteXbeeDevice("RouterLightSensor", "0013A20040EC3B22", "49DC", "23A7");

    public static final String DEVICE1_SAMPLING = "2000";
    public static final Set<Integer> DEVICE1_LINES = new HashSet<Integer>(){{add(0);
                                                                             add(1);}};
    public static final String DEVICE1_NEW_NI = "NewRouterButton";
    public static final String HIGH = "High";
    public static final String LOW = "Low";
    public static final String D0 = "D0";
    public static final String D1 = "D1";
    public static final String D2 = "D2";
    public static final String D3 = "D3";
    public static final String D4 = "D4";
    public static final String D5 = "D5";
    public static final String P0 = "P0";
    public static final String P1 = "P1";
    public static final String P2 = "P2";
    public static final String NI = "NI";
    public static final String AT_SAMPLING = "IR";
    public static final String AT_CHANGE_DETECTION = "IC";

    public static void assertMatch(Iterable<MyRemoteXbeeDevice> actual, Iterable<MyRemoteXbeeDevice> expected) {
        assertThat(actual).isEqualTo(expected);
    }
}
