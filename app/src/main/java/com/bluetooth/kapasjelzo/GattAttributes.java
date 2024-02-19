package com.bluetooth.kapasjelzo;

import java.util.HashMap;

public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String Bite_Alarm_Service = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
    public static String Bite_Alarm_CHARACTERISTIC = "ca73b3ba-39f6-4ab3-91ae-186dc9577d99";
    public static String Bite_Alarm_Descriptor_UUID="00002902-0000-1000-8000-00805f9b34fb";

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
