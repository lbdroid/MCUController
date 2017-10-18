package tk.rabidbeaver.libraries;

import android.util.Log;

public class CmdRadio {

    public static void freqUp() {
        ToolkitDev.writeMcu(1, 3, 17);
    }

    public static void freqDown() {
        ToolkitDev.writeMcu(1, 3, 16);
    }

    public static void seekUp() {
        ToolkitDev.writeMcu(1, 3, 6);
    }

    public static void seekDown() {
        ToolkitDev.writeMcu(1, 3, 5);
    }

    public static void selectChannel(int value) {
        if (value >= 0 && value < 12) {
            ToolkitDev.writeMcu(1, 3, value + 128 + 101);
        } else if (value >= 65536 && value < 65554) {
            ToolkitDev.writeMcu(1, 3, ((value + 128) - 65536) + 1);
        }
    }

    /*public static void saveChannel(int value) {
        HandlerRadio.psText(null);
        if (value >= 0 && value < 12) {
            ToolkitDev.writeMcu(1, 3, value + 64 + 19);
        } else if (value >= 65536 && value < 65554) {
            ToolkitDev.writeMcu(1, 3, ((value + 64) - 65536) + 1);
        }
    }*/

    static void nextChannel() {
        ToolkitDev.writeMcu(1, 3, 10);
    }

    static void prevChannel() {
        ToolkitDev.writeMcu(1, 3, 9);
    }

    /*static void save() {
        ToolkitDev.writeMcu(1, 3, 8);
    }*/

    /*public static void scan() {
        ToolkitDev.writeMcu(1, 3, 18);
    }*/

    public static void band(int value) {
        // Band -1 doesn't seem to do anything.
        // Band -3 is FM, and rotates through 3 "sets".
        // Band -2 is AM, and rotates through 2 "sets".
        // Sending 0 for AM or 65536 for FM is preferable.
        switch (value) {
            case -3: // FM
                value = HandlerRadio.sBand + 1;
                if (value < 65536 || value >= 65539) {
                    band(65536);
                    return;
                } else {
                    band(value);
                    return;
                }
            case -2: // AM
                if (HandlerRadio.sBand != 0) {
                    band(0);
                    return;
                } else {
                    band(1);
                    return;
                }
            case -1: // ?
                ToolkitDev.writeMcu(1, 3, 24);
                return;
            default:
                if (value >= 0 && value < 2) {
                    ToolkitDev.writeMcu(1, 3, value + 29);
                } else if (value >= 65536 && value < 65539) {
                    ToolkitDev.writeMcu(1, 3, (value - 65536) + 26);
                }
        }
    }

    public static void area(int value) {
        if (value >= 0 && value <= 4) {
            ToolkitDev.writeMcu(1, 3, value + 33);
        }
    }

    public static void freq(int value1, int value2) {
        // value 1 should only be 0 or 3. The other two are bogus.
        // To tune to FM station, use value1=3, value3 = MHz*100, so for instance, 93.1 becomes 9310
        Log.d("RADIO", "Frequency step count: "+HandlerRadio.sFreqStepCnt+", Frequency step length: "+HandlerRadio.sFreqStepLen);
        Log.d("RADIO", "Min frequency: "+HandlerRadio.sFreqMin+", Max frequency: "+HandlerRadio.sFreqMax);
        switch (value1) {
            case 0:
                if (value2 >= 0 && value2 <= HandlerRadio.sFreqStepCnt) {
                    ToolkitDev.writeMcu(225, 5, value2 / 100, value2 % 100, 238, 238);
                    return;
                }
                return;
            case 1:
                value1 = HandlerRadio.sFreqMax - HandlerRadio.sFreqMin;
                if (value1 != 0) {
                    freq(0, ((value2 - HandlerRadio.sFreqMin) * HandlerRadio.sFreqStepCnt) / value1);
                    return;
                }
                return;
            case 2:
                freq(0, (HandlerRadio.sFreqStepCnt * value2) / 65535);
                return;
            case 3:
                if (value2 < 0) value2 = 0;
                else if (value2 > 10800) value2 = 10800;
                ToolkitDev.writeMcu(37, (value2 >> 8) & 255, value2 & 255);
        }
    }

    /*public static void sensitivity(int value1, int value2) {
        switch (value1) {
            case 0:
                switch (value2) {
                    case -2:
                        ToolkitDev.writeMcu(1, 0, 156);
                        return;
                    case -1:
                        ToolkitDev.writeMcu(1, 0, 157);
                        return;
                    default:
                        return;
                }
            case 1:
                switch (value2) {
                    case -2:
                        ToolkitDev.writeMcu(1, 0, 154);
                        return;
                    case -1:
                        ToolkitDev.writeMcu(1, 0, 155);
                }
        }
    }*/

    public static void autoSensitivity(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 0, 158);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 0, 159);
                return;
            case 2:
                if (HandlerRadio.sAutoSensity == 0) {
                    autoSensitivity(1);
                } else {
                    autoSensitivity(0);
                }
        }
    }

    public static void rdsEnable(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 0, 96);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 0, 97);
                return;
            case 2:
                if (HandlerRadio.sRdsEnable == 0) {
                    rdsEnable(1);
                } else {
                    rdsEnable(0);
                }
        }
    }

    public static void stereo() {
        ToolkitDev.writeMcu(1, 3, 11);
    }

    public static void loc() {
        ToolkitDev.writeMcu(1, 3, 13);
    }

    public static void rdsAfEnable(int value) {
        switch (value) {
            case 2:
                ToolkitDev.writeMcu(1, 3, 23);
        }
    }

    public static void rdsTaEnable(int value) {
        switch (value) {
            case 2:
                ToolkitDev.writeMcu(1, 3, 22);
        }
    }

    public static void rdsPtyEnable(int value) {
        switch (value) {
            case 2:
                ToolkitDev.writeMcu(1, 3, 21);
        }
    }

    static void search(int value) {
        switch (value) {
            case 2:
                ToolkitDev.writeMcu(1, 3, 4);
        }
    }

    /*public static void sortType(int value) {
        switch (value) {
            case 0:
                if (HandlerRadio.sSortType != 0) {
                    ToolkitDev.writeMcu(1, 3, 128);
                    return;
                }
                return;
            case 1:
                if (HandlerRadio.sSortType != 1) {
                    ToolkitDev.writeMcu(1, 3, 128);
                }
        }
    }*/

    /*public static void airLine(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 0, 98);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 0, 99);
                return;
            case 2:
                if (HandlerRadio.sAirLine == 0) {
                    airLine(1);
                } else {
                    airLine(0);
                }
        }
    }*/

    public static void powerOn(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 0, 187);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 0, 188);
                return;
            case 2:
                if (HandlerRadio.sPowerOn == 0) {
                    powerOn(1);
                } else {
                    powerOn(0);
                }
        }
    }
}
