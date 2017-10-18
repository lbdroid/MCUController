package tk.rabidbeaver.libraries;

import android.content.Intent;

import com.syu.jni.ToolsJni;

import java.io.FileWriter;
import java.io.IOException;

import tk.rabidbeaver.mcucontroller.Constants;

public class HandlerMain {
    private static int sRequestMcuDataFlag;

    private static final String USB_MODE_DEVICE = "none";
    private static final String USB_MODE_HOST = "host";
    private static final String USB_MODE_PATH = "/sys/kernel/debug/intel_otg/mode";

    private static void requestMcuDataCmd() {
        ToolkitDev.writeMcu(1, 170, 96);
        ToolkitDev.writeMcu(1, 0, 0);
        if (sRequestMcuDataFlag == 0)  sRequestMcuDataFlag = 1;
    }

    static void headlights(int on){
        DataMain.headlightsOn = (on != 0);
    }

    static void reverse(int on){
        DataMain.reverseOn = (on != 0);
    }

    static void eBrakeSet(int on){
        DataMain.eBrakeSet = (on != 0);
    }

    private static void lampletByTimeCmd(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 0, 150);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 0, 151);
                return;
            case 2:
                if (DataMain.sLampletByTime == 0) {
                    lampletByTimeCmd(1);
                } else {
                    lampletByTimeCmd(0);
                }
        }
    }

    public static void anyKeyBootCmd(int value) {
        switch (value) {
            case 0:
                if (DataMain.sAnyKeyBoot != 0) {
                    ToolkitDev.writeMcu(1, 0, 50);
                    return;
                }
                return;
            case 1:
                if (DataMain.sAnyKeyBoot == 0) {
                    ToolkitDev.writeMcu(1, 0, 50);
                    return;
                }
                return;
            case 2:
                ToolkitDev.writeMcu(1, 0, 50);
        }
    }

    private static void handbrakeEnableCmd(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 0, 66);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 0, 67);
                return;
            case 2:
                if (DataMain.sHandbrakeEnable == 0) {
                    handbrakeEnableCmd(1);
                } else {
                    handbrakeEnableCmd(0);
                }
        }
    }

    private static void hostbackcarEnableCmd(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(12, 0, 1);
                return;
            case 1:
                ToolkitDev.writeMcu(12, 0, 0);
                return;
            case 2:
                if (DataMain.sHostbackcarEnable == 0) {
                    hostbackcarEnableCmd(1);
                } else {
                    hostbackcarEnableCmd(0);
                }
        }
    }

    public static void mcuSerialCmd(String value) {
        if (value != null && value.length() > 0) {
            int size = value.length();
            int[] data = new int[(size + 3)];
            data[0] = 7;
            data[1] = 1;
            data[2] = size;
            for (int i = 0; i < size; i++) {
                data[i + 3] = value.charAt(i);
            }
            ToolkitDev.writeMcu(data);
            ToolkitDev.writeMcu(7, 0);
        }
    }

    // I think this has something to do with standby vs shutdown
    public static void mcuPowerOptionCmd(int value) {
        int[] iArr;
        int i;
        switch (value) {
            case 0: // Set OFF
                iArr = new int[4];
                i = DataMain.sMcu0x0AFlag & -65; //0xbf = 1011 1111 --- unsetting the 2nd bit
                DataMain.sMcu0x0AFlag = i;
                iArr[1] = i;
                iArr[2] = 0;
                iArr[3] = 0;
                ToolkitDev.writeMcu(iArr);
                return;
            case 1: // Set ON
                iArr = new int[4];
                i = DataMain.sMcu0x0AFlag | 64; //0x40 = 0100 0000   --- setting the 2nd bit
                DataMain.sMcu0x0AFlag = i;
                iArr[1] = i;
                iArr[2] = 0;
                iArr[3] = 0;
                ToolkitDev.writeMcu(iArr);
                return;
            case 2: // INVERT
                if (DataMain.sMcuPowerOption == 0) mcuPowerOptionCmd(1);
                else mcuPowerOptionCmd(0);
        }
    }

    public static void mcuPowerOption(int value) {
        if (DataMain.sMcuPowerOption != value) DataMain.sMcuPowerOption = value;
    }

    private static void blackScreenCmd(int value) {
        switch (value) {
            case 0:
                if (DataMain.sBlackScreen == 1) {
                    ToolkitDev.writeMcu(1, 0, 6);
                    return;
                }
                return;
            case 1:
                if (DataMain.sBlackScreen == 0) {
                    ToolkitDev.writeMcu(1, 0, 5);
                    return;
                }
                return;
            case 2:
                if (DataMain.sBlackScreen == 0) {
                    blackScreenCmd(1);
                } else {
                    blackScreenCmd(0);
                }
        }
    }

    public static void mcuOnCmd(int value) {
        switch (value) {
            case 0:
                if (DataMain.sMcuOn != 0) {
                    mcuOnCmd(2);
                    return;
                }
                return;
            case 1:
                if (DataMain.sMcuOn == 0) {
                    mcuOnCmd(2);
                    return;
                }
                return;
            case 2:
                ToolkitDev.writeMcu(1, 0, 1);
        }
    }

    static void resetArmLaterCmd(int value) {
        if (value < 0) {
            value = 0;
        }

        DataMain.sOnResetState = 1;

        ToolkitDev.writeMcu(6, 32, value);
        //ToolkitApp.threadSleep(10);
        //ToolkitDev.writeMcu(6, 32, value);
    }

    public static void mcuErrorCodeCmd(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(117, 2);
                return;
            case 1:
                ToolkitDev.writeMcu(117, 1);
        }
    }

    public static void mcuUpgradeCmd(int[] ints, float[] flts, String[] strs) {
        if (ints != null && ints.length >= 1) {
            switch (ints[0]) {
                case 0:
                    if (strs != null && strs.length >= 1) {
                        //HandlerMcuUpgrade.mcuUpgradeCmd(strs[0]);
                        return;
                    }
                    return;
                case 1:
                    rebootMcuCmd();
                    return;
                case 2:
                    if (strs != null && strs.length >= 1 && ints.length >= 3) {
                        //HandlerMcuUpgrade.mcuUpgradeCmd(strs[0], ints[1], ints[2]);
                        return;
                    }
                    return;
                case 3:
                    if (strs != null && strs.length >= 1 && ints.length >= 3) {
                        //HandlerMcuUpgrade.mcuStm32UpgradeCmd(strs[0], ints[1], ints[2]);
                        return;
                    }
                    return;
                case 4:
                    if (strs != null && strs.length >= 1) {
                        //HandlerMcuSpiUpgrade.upgradeCmd(strs[0], 0);
                        return;
                    }
                    return;
                case 5:
                    if (strs != null && strs.length >= 1) {
                        //HandlerMcuSpiUpgrade.upgradeCmd(strs[0], 1);
                        return;
                    }
                    return;
                case 6:
                    rebootMcuCmdSpi();
            }
        }
    }

    private static void rebootMcuCmd() {
        //TODO DataDev.SERIAL_MCU.write(new byte[]{(byte) -86, (byte) 90, (byte) -1, (byte) -86, (byte) 91, (byte) -1, (byte) -86, (byte) 92, (byte) -1});
    }

    private static void rebootMcuCmdSpi() {
        //TODO DataDev.SERIAL_MCU.write(ToolkitDev.packFrameMcu(1, -86, 112));
    }

    public static void backcarTypeCmd(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(12, 6, 0);
                return;
            case 1:
                ToolkitDev.writeMcu(12, 6, 1);
        }
    }

    public static void cutAccDelayCloseScreenCmd(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(12, 8, 0);
                return;
            case 1:
                ToolkitDev.writeMcu(12, 8, 1);
        }
    }

    public static void panelKeyTypeCmd(int value) {
        ToolkitDev.writeMcu(8, value);
    }

    public static void fanCycleCmd(int value) {
        if (value < 0) {
            value = 0;
        } else if (value > 255) {
            value = 255;
        }
        ToolkitDev.writeMcu(124, value);
    }

    public static void startStopEnableCmd(int value) {
        int i = 1;
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(12, 11, 0);
                return;
            case 1:
                ToolkitDev.writeMcu(12, 11, 1);
                return;
            case 2:
                int[] iArr = new int[3];
                iArr[0] = 12;
                iArr[1] = 11;
                if (DataMain.sStartStopEnable != 0) {
                    i = 0;
                }
                iArr[2] = i;
                ToolkitDev.writeMcu(iArr);
        }
    }

    public static void motorDownUp(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 0, 10);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 0, 11);
        }
    }

    public static void rollKeyTypeCmd(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 92, 0);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 92, 1);
        }
    }

    public static void mirrorUpDownCmd(int index, int value) {
        int i = 0;
        int[] iArr;
        switch (index) {
            case 0:
                iArr = new int[5];
                iArr[0] = 122;
                iArr[1] = 3;
                if (value != 0) {
                    i = 1;
                }
                iArr[2] = i;
                iArr[3] = DataMain.sMirrorUpDownAv2;
                iArr[4] = DataMain.sMirrorUpDownAv3;
                ToolkitDev.writeMcu(iArr);
                return;
            case 1:
                iArr = new int[5];
                iArr[0] = 122;
                iArr[1] = 3;
                iArr[2] = DataMain.sMirrorUpDownAv1;
                if (value != 0) {
                    i = 1;
                }
                iArr[3] = i;
                iArr[4] = DataMain.sMirrorUpDownAv3;
                ToolkitDev.writeMcu(iArr);
                return;
            case 2:
                iArr = new int[5];
                iArr[0] = 122;
                iArr[1] = 3;
                iArr[2] = DataMain.sMirrorUpDownAv1;
                iArr[3] = DataMain.sMirrorUpDownAv2;
                if (value != 0) {
                    i = 1;
                }
                iArr[4] = i;
                ToolkitDev.writeMcu(iArr);
        }
    }

    public static void outBackcarCmd(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 0, 126);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 0, 125);
        }
    }

    public static void videoOutCmd(int on) {
        int[] iArr = new int[3];
        iArr[0] = 1;
        iArr[1] = 0;
        iArr[2] = on == 0 ? 192 : 193;
        ToolkitDev.writeMcu(iArr);
    }

    public static void armResetSelf() {
        ToolkitDev.writeMcu(1, 170, 113);
    }

    private static void panelKeyEnableCmd(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(12, 9, 1);
                return;
            case 1:
                ToolkitDev.writeMcu(12, 9, 0);
                return;
            case 2:
                panelKeyEnableCmd(DataMain.sPanelKeyEnable == 0 ? 1 : 0);
        }
    }

    public static void cpuTempCmd(int value) {
        ToolkitDev.writeMcu(1, 125, value);
    }

    static void mcuOn(int value) {
        if (value == 1){
            setUsbMode(1);
            //ToolkitDev.startHeartBeat();
        } else {
            setUsbMode(0);
            //ToolkitDev.stopHeartBeat();
        }

        if (DataMain.sMcuOnForUi != value) DataMain.sMcuOnForUi = value;
        if (DataMain.sMcuOn != value) {
            DataMain.sMcuOn = value;
            if (value != 0) requestMcuDataCmd();
        }
        /*if ((DataChip.getPlatformId() == 1 || DataChip.getPlatformId() == 2 || DataChip.getPlatformId() == 3 || DataChip.getPlatformId() == 4 || DataChip.getPlatformId() == 5 || DataChip.getPlatformId() == 6 || DataChip.getPlatformId() == 0) && DataMain.sMcuOn != 0) {
            ToolkitPlatform.monitorAccOn();
        }*/
    }

    static void setUsbMode(int mode) {
        try {
            FileWriter modeWriter = new FileWriter(USB_MODE_PATH, false);
            if (mode == 0) {
                modeWriter.write(USB_MODE_DEVICE);
            } else if (mode == 1) {
                modeWriter.write(USB_MODE_HOST);
            }
            modeWriter.flush();
            modeWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void accOn(int value) {
        if (DataMain.sAccOn != value) {
            DataMain.sAccOn = value;

            //ToolsJni.cmd_29_acc_state_to_bsp(value == 0 ? 0 : 1);
            ToolsJni.cmd_29_acc_state_to_bsp(0);
            mcuOnCmd(value);

            Intent i = new Intent();

            if (value == 1){
                ToolkitDev.writeMcu(1, 170, 96);
                ReceiverMcu.resetTick();
                i.setAction(Constants.MAIN.ACC_ON);
            } else i.setAction(Constants.MAIN.ACC_OFF);

            ToolkitDev.context.sendBroadcast(i);

            /*if (value == 0){
                i = new Intent();
                i.setAction(Constants.MAIN.STANDBY);
                ToolkitDev.context.sendBroadcast(i);
            }*/
        }
    }

    public static void mcuRequestVideoCmd(int av1, int av2, int av3) {
        if (DataMain.sMcuRequestVideoAv1 != av1 || DataMain.sMcuRequestVideoAv2 != av2 || DataMain.sMcuRequestVideoAv3 != av3) {
            DataMain.sMcuRequestVideoAv1 = av1;
            DataMain.sMcuRequestVideoAv2 = av2;
            DataMain.sMcuRequestVideoAv3 = av3;
            ToolkitDev.writeMcu(122, 2, av1, av2, av3);
        }
    }
}
