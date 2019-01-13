package org.xbee.project.listener;

import com.digi.xbee.api.listeners.IPacketReceiveListener;
import com.digi.xbee.api.packet.XBeePacket;
import com.digi.xbee.api.utils.HexUtils;

import java.math.BigInteger;

public class MyDataReceiveListener implements IPacketReceiveListener {

    /*public static Map<Integer, Frame> map = new HashMap<>();*/

    public void packetReceived(XBeePacket xBeePacket) {
        byte[] packetData = xBeePacket.getPacketData();
        String data = HexUtils.prettyHexString(HexUtils.byteArrayToHexString(packetData));
        System.out.println(data);
        String type = data.substring(0, 2);
        System.out.println("Frame type: " + type);
        if (type.equals("92")) {
            String longSourceAddress = data.substring(3, 26);
            String shortSourceAddress = data.substring(27, 32);
            String digitalMask = data.substring(39, 44);
            String analogMask = data.substring(45, 48);
            System.out.println("64-bit source address: " + longSourceAddress + "\n" +
                    "16-bit source address: " + shortSourceAddress + "\n" +
                    "Digital mask: " + digitalMask + "\n" +
                    "Analog mask: " + analogMask);
        }
        if (type.equals("97")){
            String longSourceAddress = data.substring(6, 29);
            String frameId = new BigInteger(data.substring(3, 5), 16).toString(10);
            System.out.println(frameId);
            String shortSourceAddress = data.substring(30, 35);
            String ATcommand = data.substring(36, 41).equals("49 53") ? "IS": data.substring(39, 44); //check
            String status = data.substring(42, 44);
            switch (status){
                case "00": status = "OK"; break;
                case "01": status = "ERROR"; break;
                case "02": status = "Invalid command"; break;
                case "03": status = "Invalid parameter"; break;
                case "04": status = "Remote command transmission failed"; break;
            }
            String response = "";
            String binaryAnalogMask = "";
            String binaryDigitalMask = "";
            if (data.length() > 44) {
                response = data.substring(45);
                response = response.replace(" ", "");
                if (response.length() > 4) {
                    binaryDigitalMask = new BigInteger(response.substring(2, 6), 16).toString(2);
                    binaryAnalogMask = new BigInteger(response.substring(6, 8), 16).toString(2);
                }

            }
            //"Sound in mV: " + (Integer.parseInt(response.substring(12), 16) * 1200) / 1023);
            System.out.println("listener " + frameId);
            /*map.put(Integer.parseInt(frameId), new Frame(type, frameId, longSourceAddress, shortSourceAddress, binaryDigitalMask, binaryAnalogMask, ATcommand, status, response));*/
            /*for (Map.Entry<Integer, Frame> pair: map.entrySet()){
                System.out.println(pair.getKey() + ": " + pair.getValue());
            }*/

        }
    }

    public static String getPins(String mask){
        StringBuilder b = new StringBuilder();
        String reversed = new StringBuilder(mask).reverse().toString();
        for (int i = 0;i < mask.length();i++){
            if (reversed.charAt(i) == '1')
                b.append(i + 1).append(" ");
        }
        return b.toString();
    }
}
