package org.xbee.project.util;

import java.nio.charset.StandardCharsets;

public class HexUtils {
    private HexUtils(){
    }

    public static String byteArrayToHexString(byte[] value, String at) {
        if (at.equals("NI") || at.equals("CC") || at.equals("DN"))
            return new String(value, StandardCharsets.UTF_8);
        else return com.digi.xbee.api.utils.HexUtils.byteArrayToHexString(value);
    }

    public static byte[] hexStringToByteArray(String value, String at) {
        if (at.equals("NI") || at.equals("CC") || at.equals("DN"))
            return value.getBytes();
        else return com.digi.xbee.api.utils.HexUtils.hexStringToByteArray(value);
    }
}
