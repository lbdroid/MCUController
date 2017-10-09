package tk.rabidbeaver.libraries;

public class CmdSteer {

    public void detect(int value) {
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

    public void clear() {
        ToolkitDev.writeMcu(1, 16, 32);
    }

    public void save() {
        ToolkitDev.writeMcu(1, 16, 33);
    }

    private boolean isAdcScanOk() {
        int i = 0;
        while (i < 6 && HandlerSteer.SCAN_ADC[i] > 240) {
            i++;
        }
        if (i == 6) {
            return false;
        }
        return true;
    }

    public void keyAdc(int keyCode, int adc) {
        if (isAdcScanOk()) {
            HandlerSteer.keyPre(-1);
            if (keyCode >= 0 && keyCode < 50) {
                switch (keyCode) {
                    case 0:
                        ToolkitDev.writeMcu(1, 17, adc);
                        return;
                    case 1:
                        ToolkitDev.writeMcu(1, 18, adc);
                        return;
                    case 2:
                        ToolkitDev.writeMcu(1, 19, adc);
                        return;
                    case 3:
                        ToolkitDev.writeMcu(1, 20, adc);
                        return;
                    case 4:
                        ToolkitDev.writeMcu(1, 21, adc);
                        return;
                    case 5:
                        ToolkitDev.writeMcu(1, 23, adc);
                        return;
                    case 6:
                        ToolkitDev.writeMcu(1, 22, adc);
                        return;
                    case 7:
                        ToolkitDev.writeMcu(1, 24, adc);
                        return;
                    case 8:
                        ToolkitDev.writeMcu(1, 25, adc);
                        return;
                    case 9:
                        ToolkitDev.writeMcu(1, 26, adc);
                        return;
                    case 10:
                        ToolkitDev.writeMcu(1, 27, adc);
                        return;
                    case 11:
                        ToolkitDev.writeMcu(1, 28, adc);
                        return;
                    case 12:
                        ToolkitDev.writeMcu(1, 29, adc);
                        return;
                    case 16:
                        ToolkitDev.writeMcu(1, 128, adc);
                        return;
                    case 17:
                        ToolkitDev.writeMcu(1, 129, adc);
                        return;
                    case 18:
                        ToolkitDev.writeMcu(1, 130, adc);
                        return;
                    case 19:
                        ToolkitDev.writeMcu(1, 131, adc);
                        return;
                    case 20:
                        ToolkitDev.writeMcu(1, 132, adc);
                        return;
                    case 21:
                        ToolkitDev.writeMcu(1, 133, adc);
                        return;
                    case 22:
                        ToolkitDev.writeMcu(1, 134, adc);
                        return;
                    case 23:
                        ToolkitDev.writeMcu(1, 135, adc);
                        return;
                    case 24:
                        ToolkitDev.writeMcu(1, 136, adc);
                        return;
                    case 25:
                        ToolkitDev.writeMcu(1, 137, adc);
                        return;
                    case 26:
                        ToolkitDev.writeMcu(1, 138, adc);
                }
            }
        }
    }

    public void mcuKeyControl(int value) {
        switch (value) {
            case 1:
                ToolkitDev.writeMcu(193, 1);
                return;
            case 2:
                ToolkitDev.writeMcu(193, 2);
                //saveMcuKeyData();
                HandlerSteer.sKey = 0;
                HandlerSteer.sKeyFunc = 0;
                return;
            case 3:
                ToolkitDev.writeMcu(193, 3);
                return;
            case 4:
                //loadMcuKeyData();
                ToolkitDev.writeMcu(193, 4);
        }
    }

    public void mcuKey(int keycode, int func) {
        HandlerSteer.sKey = keycode;
        HandlerSteer.sKeyFunc = func;
        ToolkitDev.writeMcu(194, keycode);
    }

    private void init() {
        for (int i = 0; i < 25; i++) {
            HandlerSteer.MCU_KEY_FUNC[i] = 1;
        }
        //loadMcuKeyData();
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
