package tk.rabidbeaver.libraries;

public class CmdRadio {

    public void freqUp() {
        ToolkitDev.writeMcu(1, 3, 17);
    }

    public void freqDown() {
        ToolkitDev.writeMcu(1, 3, 16);
    }

    public void seekUp() {
        ToolkitDev.writeMcu(1, 3, 6);
    }

    public void seekDown() {
        ToolkitDev.writeMcu(1, 3, 5);
    }

    public void selectChannel(int value) {
        if (value >= 0 && value < 12) {
            ToolkitDev.writeMcu(1, 3, value + 128 + 101);
        } else if (value >= 65536 && value < 65554) {
            ToolkitDev.writeMcu(1, 3, ((value + 128) - 65536) + 1);
        }
    }

    public void saveChannel(int value) {
        HandlerRadio.psText(null);
        if (value >= 0 && value < 12) {
            ToolkitDev.writeMcu(1, 3, value + 64 + 19);
        } else if (value >= 65536 && value < 65554) {
            ToolkitDev.writeMcu(1, 3, ((value + 64) - 65536) + 1);
        }
    }

    public void nextChannel() {
        ToolkitDev.writeMcu(1, 3, 10);
    }

    public void prevChannel() {
        ToolkitDev.writeMcu(1, 3, 9);
    }

    public void save() {
        ToolkitDev.writeMcu(1, 3, 8);
    }

    public void scan() {
        ToolkitDev.writeMcu(1, 3, 18);
    }

    public void band(int value) {
        switch (value) {
            case -3:
                value = HandlerRadio.sBand + 1;
                if (value < 65536 || value >= 65539) {
                    band(65536);
                    return;
                } else {
                    band(value);
                    return;
                }
            case -2:
                if (HandlerRadio.sBand != 0) {
                    band(0);
                    return;
                } else {
                    band(1);
                    return;
                }
            case -1:
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

    public void area(int value) {
        if (value >= 0 && value <= 4) {
            ToolkitDev.writeMcu(1, 3, value + 33);
        }
    }

    public void freq(int value1, int value2) {
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

    public void sensity(int value1, int value2) {
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
    }

    public void autoSensity(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 0, 158);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 0, 159);
                return;
            case 2:
                if (HandlerRadio.sAutoSensity == 0) {
                    autoSensity(1);
                } else {
                    autoSensity(0);
                }
        }
    }

    public void rdsEnable(int value) {
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

    public void stereo(int value) {
        switch (value) {
            case 2:
                ToolkitDev.writeMcu(1, 3, 11);
        }
    }

    public void loc(int value) {
        switch (value) {
            case 2:
                ToolkitDev.writeMcu(1, 3, 13);
        }
    }

    public void rdsAfEnable(int value) {
        switch (value) {
            case 2:
                ToolkitDev.writeMcu(1, 3, 23);
        }
    }

    public void rdsTaEnable(int value) {
        switch (value) {
            case 2:
                ToolkitDev.writeMcu(1, 3, 22);
        }
    }

    public void rdsPtyEnable(int value) {
        switch (value) {
            case 2:
                ToolkitDev.writeMcu(1, 3, 21);
        }
    }

    public void search(int value) {
        switch (value) {
            case 2:
                ToolkitDev.writeMcu(1, 3, 4);
        }
    }

    public void sortType(int value) {
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
    }

    public void airLine(int value) {
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
    }

    public void powerOn(int value) {
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