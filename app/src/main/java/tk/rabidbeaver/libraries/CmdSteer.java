package tk.rabidbeaver.libraries;

import android.util.Log;

public class CmdSteer {

    public static void detect(int value) {
        Log.d("CMDSTEER", "detect: "+value);
        switch (value) {
            case 0:
                HandlerSteer.detect(0);
                ToolkitDev.writeMcu(1, 16, 0);
                return;
            case 1:
                HandlerSteer.detect(1);
                ToolkitDev.writeMcu(1, 16, 1);
                ToolkitDev.writeMcu(1, 16, 2);
                return;
            case 2:
                if (HandlerSteer.sDetect == 0) {
                    detect(1);
                } else {
                    detect(0);
                }
        }
    }

    public static void clear() {
        Log.d("CMDSTEER", "clear");
        ToolkitDev.writeMcu(1, 16, 32);
    }

    public static void save() {
        Log.d("CMDSTEER", "save");
        ToolkitDev.writeMcu(1, 16, 33);
    }

    private static boolean isAdcScanOk() {
        int i = 0;
        while (i < 6 && HandlerSteer.SCAN_ADC[i] > 240) {
            i++;
        }
        if (i == 6) {
            return false;
        }
        return true;
    }

    public static boolean keyAdc(int keyCode) {
        int adc = HandlerSteer.sAdc;
        Log.d("CMDSTEER", "keyAdc: "+keyCode);
        if (isAdcScanOk()) {
            Log.d("CMDSTEER", "keyAdc scan OK");
            if (keyCode >= 0 && keyCode < 50) {
                switch (keyCode) {
                    //case 0:
                    //    ToolkitDev.writeMcu(1, 17, adc); // radar? Don't use.
                    //    return true;
                    case 1:
                        ToolkitDev.writeMcu(1, 18, adc); // 0x01001020 I (0xe)
                        return true;
                    case 2:
                        ToolkitDev.writeMcu(1, 19, adc); // 0x01002109 I (0x11)
                        return true;
                    case 3:
                        ToolkitDev.writeMcu(1, 20, adc); // 0x01002107 I (0xf)
                        return true;
                    case 4:
                        ToolkitDev.writeMcu(1, 21, adc); // 0x01002108 I (0x10)
                        return true;
                    //case 5:
                    //    ToolkitDev.writeMcu(1, 23, adc); // no response
                    //    return;
                    //case 6:
                    //    ToolkitDev.writeMcu(1, 22, adc); // no response
                    //    return;
                    case 7:
                        ToolkitDev.writeMcu(1, 24, adc); // 0x01000701 I (0x12)
                        return true;
                    case 8:
                        ToolkitDev.writeMcu(1, 25, adc); // 0x01001007 I (0xd)
                        return true;
                    case 9:
                        ToolkitDev.writeMcu(1, 26, adc); // 0x01071000 I (0xc)
                        return true;
                    //case 10:
                    //    ToolkitDev.writeMcu(1, 27, adc); // no response
                    //    return;
                    //case 11:
                    //    ToolkitDev.writeMcu(1, 28, adc); // no response
                    //    return;
                    //case 12:
                    //    ToolkitDev.writeMcu(1, 29, adc); // no response
                    //    return;
                    case 16:
                        ToolkitDev.writeMcu(1, 128, adc); // 0x01107001 I (0x1)
                        return true;
                    case 17:
                        ToolkitDev.writeMcu(1, 129, adc); // 0x01107002 I (0x2)
                        return true;
                    case 18:
                        ToolkitDev.writeMcu(1, 130, adc); // 0x01107003 ...
                        return true;
                    case 19:
                        ToolkitDev.writeMcu(1, 131, adc); // 0x01107004
                        return true;
                    case 20:
                        ToolkitDev.writeMcu(1, 132, adc); // 0x01107005
                        return true;
                    case 21:
                        ToolkitDev.writeMcu(1, 133, adc); // 0x01107006
                        return true;
                    case 22:
                        ToolkitDev.writeMcu(1, 134, adc); // 0x01107007
                        return true;
                    case 23:
                        ToolkitDev.writeMcu(1, 135, adc); // 0x01107008
                        return true;
                    case 24:
                        ToolkitDev.writeMcu(1, 136, adc); // 0x01107009
                        return true;
                    case 25:
                        ToolkitDev.writeMcu(1, 137, adc); // 0x0110700a
                        return true;
                    case 26:
                        ToolkitDev.writeMcu(1, 138, adc); // 0x0110700b I (0xb)
                        return true;
                    default:
                        return false;
                }
            }
        } else Log.d("CMDSTEER", "scan: FAIL");

        return false;
    }

    public static void mcuKeyControl(int value) {
        switch (value) {
            case 1:
                ToolkitDev.writeMcu(193, 1);
                return;
            case 2:
                ToolkitDev.writeMcu(193, 2);
                HandlerSteer.sKey = 0;
                HandlerSteer.sKeyFunc = 0;
                return;
            case 3:
                ToolkitDev.writeMcu(193, 3);
                return;
            case 4:
                ToolkitDev.writeMcu(193, 4);
        }
    }

    public static void mcuKey(int keycode, int func) {
        HandlerSteer.sKey = keycode;
        HandlerSteer.sKeyFunc = func;
        if (keycode > 0) ToolkitDev.writeMcu(194, keycode);
    }

    public static void init() {
        for (int i = 0; i < 35; i++) {
            HandlerSteer.MCU_KEY_FUNC[i] = 1;
        }
    }
/*
    private void loadMcuKeyData() {
        DataSteer.MCU_KEY_FUNC[25] = this.mSph.getLastPut(25, 2);
        DataSteer.MCU_KEY_FUNC[26] = this.mSph.getLastPut(26, 2);
        DataSteer.MCU_KEY_FUNC[27] = this.mSph.getLastPut(27, 2);
        DataSteer.MCU_KEY_FUNC[28] = this.mSph.getLastPut(28, 2);
        DataSteer.MCU_KEY_FUNC[29] = this.mSph.getLastPut(29, 2);
        DataSteer.MCU_KEY_FUNC[30] = this.mSph.getLastPut(30, 2);
        DataSteer.MCU_KEY_FUNC[31] = this.mSph.getLastPut(31, 2);
        DataSteer.MCU_KEY_FUNC[32] = this.mSph.getLastPut(32, 2);
        DataSteer.MCU_KEY_FUNC[33] = this.mSph.getLastPut(33, 2);
        DataSteer.MCU_KEY_FUNC[34] = this.mSph.getLastPut(34, 2);
    }

    private void saveMcuKeyData() {
        if (DataSteer.MCU_KEY_FUNC[25] != this.mSph.getLastPut(25, 2)) {
            this.mSph.putDelay(25, DataSteer.MCU_KEY_FUNC[25]);
        }
        if (DataSteer.MCU_KEY_FUNC[26] != this.mSph.getLastPut(26, 2)) {
            this.mSph.putDelay(26, DataSteer.MCU_KEY_FUNC[26]);
        }
        if (DataSteer.MCU_KEY_FUNC[27] != this.mSph.getLastPut(27, 2)) {
            this.mSph.putDelay(27, DataSteer.MCU_KEY_FUNC[27]);
        }
        if (DataSteer.MCU_KEY_FUNC[28] != this.mSph.getLastPut(28, 2)) {
            this.mSph.putDelay(28, DataSteer.MCU_KEY_FUNC[28]);
        }
        if (DataSteer.MCU_KEY_FUNC[29] != this.mSph.getLastPut(29, 2)) {
            this.mSph.putDelay(29, DataSteer.MCU_KEY_FUNC[29]);
        }
        if (DataSteer.MCU_KEY_FUNC[30] != this.mSph.getLastPut(30, 2)) {
            this.mSph.putDelay(30, DataSteer.MCU_KEY_FUNC[30]);
        }
        if (DataSteer.MCU_KEY_FUNC[31] != this.mSph.getLastPut(31, 2)) {
            this.mSph.putDelay(31, DataSteer.MCU_KEY_FUNC[31]);
        }
        if (DataSteer.MCU_KEY_FUNC[32] != this.mSph.getLastPut(32, 2)) {
            this.mSph.putDelay(32, DataSteer.MCU_KEY_FUNC[32]);
        }
        if (DataSteer.MCU_KEY_FUNC[33] != this.mSph.getLastPut(33, 2)) {
            this.mSph.putDelay(33, DataSteer.MCU_KEY_FUNC[33]);
        }
        if (DataSteer.MCU_KEY_FUNC[34] != this.mSph.getLastPut(34, 2)) {
            this.mSph.putDelay(34, DataSteer.MCU_KEY_FUNC[34]);
        }
    }*/
}
