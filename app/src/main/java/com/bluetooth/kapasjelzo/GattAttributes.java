/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
