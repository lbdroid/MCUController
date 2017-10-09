package tk.rabidbeaver.libraries;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint.Align;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HandlerMain {
    private static final String LANG_EN = "US";
    private static final String LANG_ZH = "CN";
    private static final String LANG_ZH_DE = "DE";
    private static final String LANG_ZH_ES = "ES";
    private static final String LANG_ZH_FR = "FR";
    private static final String LANG_ZH_IT = "IT";
    private static final String LANG_ZH_JP = "JP";
    private static final String LANG_ZH_KR = "KR";
    private static final String LANG_ZH_TR = "TR";
    private static final String LANG_ZH_TW = "TW";

    private static int mJumpAppId = 0;
    public static int sBackcarBackSleep = -1;
    private static int sBackcarBak = -1;
    public static boolean sMcuAppCheckEnable = true;
    public static boolean sMcuPlatfCheckEnable = true;
    public static int sRequestMcuDataFlag;
    static int sUpdateAppIdTick = 0;
    private static int sleep_wakeup_timeout = 60;

    class C07481 implements Runnable {
        C07481() {
        }

        public void run() {
            ToolkitDev.writeMcuNon(1, 0, 189);
            Log.d("sleep", "enterSleepWakeup work + time: = " + SystemClock.uptimeMillis());
            HandlerMain.sleep_wakeup_timeout = HandlerMain.sleep_wakeup_timeout - 1;
            if (HandlerMain.sleep_wakeup_timeout == 0) {
                HandlerMain.enterSleepWakeup(0);
            }
        }
    }

    class C07492 implements Runnable {
        C07492() {
        }

        public synchronized void run() {
            if (DataMain.sMuteTickByChangeAppId > 0) {
                DataMain.sMuteTickByChangeAppId--;
                if (DataMain.sMuteTickByChangeAppId == 0) {
                    //HandlerMain.calcMuteMain();
                }
            }
            if (DataMain.sVaAudioOccupiedTick > 0) {
                DataMain.sVaAudioOccupiedTick--;
                if (DataMain.sVaAudioOccupiedTick == 2) {
                    //ModuleCallbackList.update(DataMain.MCLS, 57, 0);
                } else if (DataMain.sVaAudioOccupiedTick == 0) {
                    //HandlerMain.vaAudioOccupied(0);
                }
            }
            if (HandlerMain.sUpdateAppIdTick > 0) {
                HandlerMain.sUpdateAppIdTick--;
                if (HandlerMain.sUpdateAppIdTick <= 0) {
                    //ModuleCallbackList.update(DataMain.MCLS, 0, DataMain.sAppId);
                }
            }
        }
    }



    class C07514 implements Runnable {
        C07514() {
        }

        public void run() {
            ToolkitDev.writeMcu(1, 0, 0);
        }
    }

    class C07525 implements Runnable {
        C07525() {
        }

        public void run() {
            ToolkitDev.writeMcu(1, 0, 0);
        }
    }

    class C07536 implements Runnable {
        C07536() {
        }

        public void run() {
            ToolkitDev.writeMcu(1, 170, 144);
        }
    }


    class C07569 implements Runnable {
        C07569() {
        }

        public void run() {
            //ToolkitApp.keyHome();
        }
    }

    public static void requestMcuDataCmd() {
        ToolkitDev.writeMcu(1, 170, 96);
        ToolkitDev.writeMcu(1, 0, 0);
        if (sRequestMcuDataFlag == 0) {
            sRequestMcuDataFlag = 1;
            //HandlerNotRemove.getInstance().postDelayed(new C07514(), 5000);
        }
        //HandlerRemove.getInstance().postDelayed(new C07525(), 1000);
    }

    public static void lampletByTimeCmd(int value) {
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
                    return;
                } else {
                    lampletByTimeCmd(0);
                    return;
                }
            default:
                return;
        }
    }

    public static void anyKeyBootCmd(int value) {
        int i = 1;
        if (DataMain.sAnyKeyBootType == 0) {
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
        } else if (DataMain.sAnyKeyBootType == 1) {
            switch (value) {
                case 0:
                    anyKeyBoot(0);
                    return;
                case 1:
                    anyKeyBoot(1);
                    return;
                case 2:
                    if (DataMain.sAnyKeyBoot != 0) {
                        i = 0;
                    }
                    anyKeyBootCmd(i);
            }
        }
    }

    public static void naviOnBootCmd(int value) {
        switch (value) {
            case 0:
                if (DataMain.sNaviOnBoot != 0) {
                    //naviOnBoot(0);
                    return;
                }
                return;
            case 1:
                if (DataMain.sNaviOnBoot == 0) {
                    //naviOnBoot(1);
                    return;
                }
                return;
            case 2:
                //naviOnBoot((DataMain.sNaviOnBoot + 1) & 1);
        }
    }

    public static void handbrakeEnableCmd(int value) {
        int i = 1;
        if (DataMain.sHandbrakeEnableType == 0) {
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
        } else if (DataMain.sHandbrakeEnableType == 1) {
            switch (value) {
                case 0:
                    handbrakeEnable(0);
                    return;
                case 1:
                    handbrakeEnable(1);
                    return;
                case 2:
                    if (DataMain.sHandbrakeEnable != 0) {
                        i = 0;
                    }
                    handbrakeEnableCmd(i);
            }
        }
    }

    public static void backcarRadarEnableCmd(int value) {
        if (DataMain.sBackcarRadarEnableType == 0) {
            switch (value) {
                case 0:
                    ToolkitDev.writeMcu(1, 12, 4);
                    return;
                case 1:
                    ToolkitDev.writeMcu(1, 12, 5);
                    return;
                case 2:
                    if (DataMain.sBackcarRadarEnable == 0) {
                        backcarRadarEnableCmd(1);
                    } else {
                        backcarRadarEnableCmd(0);
                    }
            }
        } else if (DataMain.sBackcarRadarEnableType == 1) {
            switch (value) {
                case 0:
                    //backcarRadarEnable(0);
                    return;
                case 1:
                    //backcarRadarEnable(1);
                    return;
                case 2:
                    if (DataMain.sBackcarRadarEnable == 0) {
                        backcarRadarEnableCmd(1);
                    } else {
                        backcarRadarEnableCmd(0);
                    }
            }
        }
    }

    public static void backcarTrackEnableCmd(int value) {
        if (DataMain.sBackcarTrackEnableType == 0) {
            switch (value) {
                case 0:
                    ToolkitDev.writeMcu(12, 1, 1);
                    return;
                case 1:
                    ToolkitDev.writeMcu(12, 1, 0);
                    return;
                case 2:
                    if (DataMain.sBackcarTrackEnable == 0) {
                        backcarTrackEnableCmd(1);
                    } else {
                        backcarTrackEnableCmd(0);
                    }
            }
        } else if (DataMain.sBackcarTrackEnableType == 1) {
            switch (value) {
                case 0:
                    //backcarTrackEnable(0);
                    return;
                case 1:
                    //backcarTrackEnable(1);
                    return;
                case 2:
                    if (DataMain.sBackcarTrackEnable == 0) {
                        backcarTrackEnableCmd(1);
                    } else {
                        backcarTrackEnableCmd(0);
                    }
            }
        }
    }

    public static void backcarMirrorCmd(int value) {
        switch (value) {
            case 0:
                //backcarMirror(0);
                return;
            case 1:
                //backcarMirror(1);
                return;
            case 2:
                //backcarMirror((DataMain.sBackcarMirror + 1) & 1);
        }
    }

    public static void hostbackcarEnableCmd(int value) {
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

    public static void osdTimeCmd(int value) {
        int i = 1;
        if (DataMain.sOsdTimeType == 0) {
            switch (value) {
                case 0:
                    ToolkitDev.writeMcu(1, 0, 80);
                    return;
                case 1:
                    ToolkitDev.writeMcu(1, 0, 81);
                    return;
                case 2:
                    if (DataMain.sOsdTime == 0) {
                        osdTimeCmd(1);
                    } else {
                        osdTimeCmd(0);
                    }
            }
        } else if (DataMain.sOsdTimeType == 1) {
            switch (value) {
                case 0:
                    //osdTime(0);
                    return;
                case 1:
                    //osdTime(1);
                    return;
                case 2:
                    if (DataMain.sOsdTime != 0) {
                        i = 0;
                    }
                    osdTimeCmd(i);
            }
        }
    }

    public static void brightLevelCmd(int value) {
        /*TODO IEventHandler handler;
        switch (value) {
            case -5:
                brightLevelValueCmd(DataMain.sBrightLevel - 1);
                return;
            case -4:
                brightLevelValueCmd(DataMain.sBrightLevel + 1);
                return;
            case -3:
                handler = DataMain.sEHBrightLevel;
                if (handler == null || !handler.onHandle(value, null, null, null, null)) {
                    int[] brightLevelTable;
                    if (DataMain.sLamplet == 0) {
                        brightLevelTable = DataMain.sBrightLevelDayTable;
                    } else {
                        brightLevelTable = DataMain.sBrightLevelNightTable;
                    }
                    if (brightLevelTable != null) {
                        int length = brightLevelTable.length;
                        if (length != 0) {
                            brightLevelValueCmd(ToolkitMath.clamp(brightLevelTable[((length + ToolkitBacklight.index4Down(brightLevelTable, DataMain.sBrightLevel)) - 1) % length], 0, DataMain.sBrightLevelMax));
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            case -2:
                handler = DataMain.sEHBrightLevel;
                if (handler == null || !handler.onHandle(value, null, null, null, null)) {
                    brightLevelValueCmd(ToolkitMath.clamp(DataMain.sBrightLevel - DataMain.sBrightLevelStep, 0, DataMain.sBrightLevelMax));
                    return;
                }
                return;
            case -1:
                handler = DataMain.sEHBrightLevel;
                if (handler == null || !handler.onHandle(value, null, null, null, null)) {
                    brightLevelValueCmd(DataMain.sBrightLevel + DataMain.sBrightLevelStep);
                    return;
                }
                return;
            default:
                brightLevelValueCmd(value);
        }*/
    }

    public static void brightLevelValueCmd(int value) {
        if (DataMain.sLamplet == 0) {
            brightLevelDayCmd(value);
        } else {
            brightLevelNightCmd(value);
        }
    }

    public static void brightLevelDayCmd(int value) {
        //brightLevelDay(value);
    }

    public static void brightLevelNightCmd(int value) {
        //brightLevelNight(value);
    }

    public static void autoBlackScreenCmd(int value) {
        autoBlackScreen(value);
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

    public static void mcuPowerOptionCmd(int value) {
        int[] iArr;
        int i;
        switch (value) {
            case 0:
                iArr = new int[4];
                i = DataMain.sMcu0x0AFlag & -65;
                DataMain.sMcu0x0AFlag = i;
                iArr[1] = i;
                iArr[2] = 0;
                iArr[3] = 0;
                ToolkitDev.writeMcu(iArr);
                return;
            case 1:
                iArr = new int[4];
                i = DataMain.sMcu0x0AFlag | 64;
                DataMain.sMcu0x0AFlag = i;
                iArr[1] = i;
                iArr[2] = 0;
                iArr[3] = 0;
                ToolkitDev.writeMcu(iArr);
                return;
            case 2:
                if (DataMain.sMcuPowerOption == 0) {
                    mcuPowerOptionCmd(1);
                    return;
                } else {
                    mcuPowerOptionCmd(0);
                    return;
                }
            default:
                return;
        }
    }

    public static void blackScreenCmd(int value) {
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

    public static void standbyCmd(int value) {
        if (DataMain.sStandbyType == 1) {
            switch (value) {
                case 0:
                    if (DataMain.sStandby != 0) {
                        standby(0);
                        return;
                    }
                    return;
                case 1:
                    if (DataMain.sStandby == 0) {
                        standby(1);
                        return;
                    }
                    return;
                case 2:
                    if (DataMain.sStandby != 0) {
                        standby(0);
                        return;
                    } else {
                        standby(1);
                        return;
                    }
                default:
                    return;
            }
        }
        switch (value) {
            case 0:
                if (DataMain.sStandby != 0) {
                    standbyCmd(2);
                    return;
                }
                return;
            case 1:
                if (DataMain.sStandby == 0) {
                    standbyCmd(2);
                    return;
                }
                return;
            case 2:
                ToolkitDev.writeMcu(1, 0, 28);
        }
    }

    public static void resetArmLaterCmd(int value) {
        if (value < 0) {
            value = 0;
        }
        //ModuleCallbackList.update(DataMain.MCLS, 61, value);
        DataMain.sOnResetState = 1;
        //calcMuteMain();
        //if (DataChip.getChipId() != 3) {
        //    DataMain.sCmd.appId(DataMain.ARM_RESET_MODE);
        //}
        ToolkitDev.writeMcu(6, 32, value);
        //ToolkitApp.threadSleep(10);
        //ToolkitDev.writeMcu(6, 32, value);
    }

    public static void mcuErrorCodeCmd(int value) {
        switch (value) {
            case 0:
                synchronized (DataMain.MCU_ERROR_CODE) {
                    DataMain.MCU_ERROR_CODE.clear();
                }
                //ModuleCallbackList.update(DataMain.MCLS, 46, null, null, null);
                ToolkitDev.writeMcu(117, 2);
                return;
            case 1:
                ToolkitDev.writeMcu(117, 1);
        }
    }

    public static void enterSleepWakeup(int on) {
        if (DataMain.sAccOn != 0 || on == 0) {
            //ToolkitPlatform.lockSystem(0);
            ToolkitDev.writeMcuNon(1, 0, 190);
            Log.d("sleep", "enterSleepWakeup over + time: = " + SystemClock.uptimeMillis());
            return;
        }
        sleep_wakeup_timeout = 60;
        //ToolkitPlatform.lockSystem(1);
    }

    public static void keyCmd(int value) {
        //TODO ToolkitApp.onKey(value);
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

    public static void rebootMcuCmd() {
        //TODO DataDev.SERIAL_MCU.write(new byte[]{(byte) -86, (byte) 90, (byte) -1, (byte) -86, (byte) 91, (byte) -1, (byte) -86, (byte) 92, (byte) -1});
    }

    public static void rebootMcuCmdSpi() {
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

    public static void lampletOnBootCmd(int value) {
        int i = 1;
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(12, 2, 0);
                return;
            case 1:
                ToolkitDev.writeMcu(12, 2, 1);
                return;
            case 2:
                int[] iArr = new int[3];
                iArr[0] = 12;
                iArr[1] = 2;
                if (DataMain.sLampletOnBoot != 0) {
                    i = 0;
                }
                iArr[2] = i;
                ToolkitDev.writeMcu(iArr);
                return;
            default:
                return;
        }
    }

    public static void lampletOnAlwaysCmd(int value) {
        int i = 1;
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(12, 3, 0);
                return;
            case 1:
                ToolkitDev.writeMcu(12, 3, 1);
                return;
            case 2:
                int[] iArr = new int[3];
                iArr[0] = 12;
                iArr[1] = 3;
                if (DataMain.sLampOnAlawys != 0) {
                    i = 0;
                }
                iArr[2] = i;
                ToolkitDev.writeMcu(iArr);
                return;
            default:
                return;
        }
    }

    public static void radarParkEnableCmd(int value) {
        int i = 1;
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(12, 4, 0);
                return;
            case 1:
                ToolkitDev.writeMcu(12, 4, 1);
                return;
            case 2:
                int[] iArr = new int[3];
                iArr[0] = 12;
                iArr[1] = 4;
                if (DataMain.sRadarParkEnable != 0) {
                    i = 0;
                }
                iArr[2] = i;
                ToolkitDev.writeMcu(iArr);
                return;
            default:
                return;
        }
    }

    public static void raghtCameraState(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 0, 120);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 0, 121);
                return;
            default:
                return;
        }
    }

    public static void lampletColorControlCmd(int value) {
        int i = 1;
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(122, 0, 0);
                return;
            case 1:
                ToolkitDev.writeMcu(122, 0, 1);
                return;
            case 2:
                int[] iArr = new int[3];
                iArr[0] = 122;
                iArr[1] = 0;
                if (DataMain.sLampletColorCtrl != 0) {
                    i = 0;
                }
                iArr[2] = i;
                ToolkitDev.writeMcu(iArr);
                return;
            default:
                return;
        }
    }

    public static void panoramaOnCmd(int value) {
        int i = 1;
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(12, 5, 0);
                return;
            case 1:
                ToolkitDev.writeMcu(12, 5, 1);
                return;
            case 2:
                int[] iArr = new int[3];
                iArr[0] = 12;
                iArr[1] = 5;
                if (DataMain.sPanoramaOn != 0) {
                    i = 0;
                }
                iArr[2] = i;
                ToolkitDev.writeMcu(iArr);
                return;
            default:
                return;
        }
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
                return;
            default:
                return;
        }
    }

    public static void motorDownUp(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 0, 10);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 0, 11);
                return;
            default:
                return;
        }
    }

    public static void rollKeyTypeCmd(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 92, 0);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 92, 1);
                return;
            default:
                return;
        }
    }

    public static void ledColorCmd(int value) {
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(1, 91, 160);
                return;
            case 1:
                ToolkitDev.writeMcu(1, 91, 128);
                return;
            case 2:
                ToolkitDev.writeMcu(1, 91, 192);
                return;
            case 3:
                ToolkitDev.writeMcu(1, 91, 16);
                return;
            case 4:
                ToolkitDev.writeMcu(1, 91, 48);
                return;
            case 5:
                ToolkitDev.writeMcu(1, 91, 80);
                return;
            case 6:
                ToolkitDev.writeMcu(1, 91, 112);
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

    public static void factoryCmd() {
        /*if (DataChip.getChipId() == 4 || DataChip.getChipId() == 5) {
            ToolsJni.cmd_6_mute_amp(1);
            ToolkitDev.writeMcu(118, 250);
        }
        DataMain.sOnResetState = 1;
        calcMuteMain();
        HandlerSoundCmd.eqModeCmd(1);
        HandlerRemove.getInstance().postDelayed(new C07536(), 1000);*/
    }

    public static void cutAccTurnOffLcdcCmd(int value) {
        switch (value) {
            case 0:
                if (DataMain.sCutAccTurnOffLcdc != 0) {
                    cutAccTurnOffLcdc(0);
                    return;
                }
                return;
            case 1:
                if (DataMain.sCutAccTurnOffLcdc == 0) {
                    cutAccTurnOffLcdc(1);
                    return;
                }
                return;
            case 2:
                cutAccTurnOffLcdc((DataMain.sCutAccTurnOffLcdc + 1) & 1);
        }
    }

    public static void auxEnableCmd(int value) {
        switch (value) {
            case 0:
                if (DataMain.sAuxEnable != 0) {
                    //auxEnable(0);
                    return;
                }
                return;
            case 1:
                if (DataMain.sAuxEnable == 0) {
                    //auxEnable(1);
                    return;
                }
                return;
            case 2:
                //auxEnable((DataMain.sAuxEnable + 1) & 1);
        }
    }

    public static void sleepAirplaneCmd(int value) {
        switch (value) {
            case 0:
                //sleepAirplane(0);
                return;
            case 1:
                //sleepAirplane(1);
                return;
            case 2:
                //sleepAirplane((DataMain.sSleepAirplane + 1) & 1);
        }
    }

    public static void lampletCleanOnCmd(int value) {
        int i = 1;
        switch (value) {
            case 0:
                ToolkitDev.writeMcu(122, 3, 0);
                return;
            case 1:
                ToolkitDev.writeMcu(122, 3, 1);
                return;
            case 2:
                int[] iArr = new int[3];
                iArr[0] = 122;
                iArr[1] = 3;
                if (DataMain.sLampletCleanOn != 0) {
                    i = 0;
                }
                iArr[2] = i;
                ToolkitDev.writeMcu(iArr);
        }
    }

    public static void ambientLightCmd(int cmd, int value) {
        int i = 1;
        switch (cmd) {
            case 0:
                switch (value) {
                    case 0:
                        ToolkitDev.writeMcu(123, 0, 0, 0);
                        return;
                    case 1:
                        ToolkitDev.writeMcu(123, 0, 0, 1);
                        return;
                    case 2:
                        int[] iArr = new int[4];
                        iArr[0] = 123;
                        iArr[1] = 0;
                        iArr[2] = 0;
                        if (DataMain.sAmbientLightOn != 0) {
                            i = 0;
                        }
                        iArr[3] = i;
                        ToolkitDev.writeMcu(iArr);
                        return;
                    default:
                        return;
                }
            case 1:
                if (value != 0) {
                    ToolkitDev.writeMcu(123, 0, 1, 1);
                    return;
                } else {
                    ToolkitDev.writeMcu(123, 0, 1, 0);
                    return;
                }
            case 2:
                ToolkitDev.writeMcu(123, 0, 2, value);
                return;
            default:
                return;
        }
    }

    public static void videoOutCmd(int on) {
        int[] iArr = new int[3];
        iArr[0] = 1;
        iArr[1] = 0;
        iArr[2] = on == 0 ? 192 : 193;
        ToolkitDev.writeMcu(iArr);
    }

    public static void showFlashWriteCmd(int cmd) {
        int i = 1;
        switch (cmd) {
            case 0:
                //showFlashWrite(0);
                return;
            case 1:
                //showFlashWrite(1);
                return;
            case 2:
                if (DataMain.sFlashWrite != 0) {
                    i = 0;
                }
                //showFlashWrite(i);
        }
    }

    public static void armResetSelf() {
        ToolkitDev.writeMcu(1, 170, 113);
    }

    public static void panelKeyEnableCmd(int value) {
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

    public static void touch(int action, int x, int y) {
        if (action == 0 || action == 2) {
            ToolkitDev.writeMcu(230, (x >> 8) & 255, x & 255, (y >> 8) & 255, y & 255);
        } else if (action == 1) {
            ToolkitDev.writeMcu(230, 170, 170, 170, 170);
        }
    }

    public static void trunkCtrol() {
        ToolkitDev.writeMcu(1, 118, 0);
    }

    public static void mcuState(int value) {
        if (DataMain.sMcuState != value) {
            DataMain.sMcuState = value;
            /*DataMain.MCU_STATE_STACK.push(value);
            boolean needSync = false;
            int appId = DataMain.sAppId;
            if (appId <= 0 || appId >= 20) {
                needSync = true;
            } else if (CmdMain.APP_ID_MAP_MCU_STATE[appId] != value) {
                needSync = true;
            }
            if (needSync) {
                int size = DataMain.APP_ID_STACK.size();
                int[] array = DataMain.APP_ID_STACK.array();
                int i = size - 1;
                while (i >= 0) {
                    appId = array[i];
                    if (appId < 0 || appId >= 20 || CmdMain.APP_ID_MAP_MCU_STATE[appId] != value) {
                        i--;
                    } else {
                        DataMain.sAppIdUIRequest = appId;
                        if (!ToolkitPlatform.isScreenPort()) {
                            appId(appId);
                            DataSound.sCmd.appId(appId);
                        }
                        needSync = false;
                    }
                }
            }
            if (needSync) {
                appId = 0;
                while (appId < 20) {
                    if (CmdMain.APP_ID_MAP_MCU_STATE[appId] == value) {
                        DataMain.sAppIdUIRequest = appId;
                        if (!ToolkitPlatform.isScreenPort()) {
                            appId(appId);
                            DataSound.sCmd.appId(appId);
                        }
                    } else {
                        appId++;
                    }
                }
            }
            mJumpAppId = appId;*/
        }
        //go2LastTop();
    }

    public static void mcuOn(int value) {
        if (DataMain.sMcuOnForUi != value) {
            DataMain.sMcuOnForUi = value;
            //ModuleCallbackList.update(DataMain.MCLS, 1, value);
        }
        //if (DataStore.sCustomerCfgOk) {
            if (DataMain.sMcuOn != value) {
                DataMain.sMcuOn = value;
                /*calcMuteMain();
                if (value != 0) {
                    requestMcuDataCmd();
                    HandlerSound.initIcCmd();
                    DataMain.sJumpAppByMcuOn = true;
                    App.getInstance().mcuUpdateResetCanbusId(true);
                } else {
                    JumpPage.broadcastByIntentName("com.syu.video.hidepip");
                    DataMain.sTopAppWhenMcuOff = JumpPage.topApp();
                    ToolkitApp.keyHome();
                    Print.jumpPage("MCU关机,记录最上面的应用" + DataMain.sTopAppWhenMcuOff + " 并跳到主页");
                }
                EventMain.NE_MCU_ON.onNotify();*/
            }
            /*if ((DataChip.getPlatformId() == 1 || DataChip.getPlatformId() == 2 || DataChip.getPlatformId() == 3 || DataChip.getPlatformId() == 4 || DataChip.getPlatformId() == 5 || DataChip.getPlatformId() == 6 || DataChip.getPlatformId() == 0) && DataMain.sMcuOn != 0) {
                ToolkitPlatform.monitorAccOn();
            }*/
        //}
    }

    public static void standby(int value) {
        if (DataMain.sStandby != value) {
            DataMain.sStandby = value;
            //ModuleCallbackList.update(DataMain.MCLS, 2, value);
            //EventMain.NE_MCU_STANDBY.onNotify();
            //calcMuteMain();
        }
    }

    public static void blackScreen(int value) {
        if (DataMain.sBlackScreen != value) {
            DataMain.sBlackScreen = value;
            //ModuleCallbackList.update(DataMain.MCLS, 3, value);
            //EventMain.NE_BLACK_SCREEN.onNotify();
        }
    }

    public static void lamplet(int value) {
        if (DataMain.sLamplet != value) {
            DataMain.sLamplet = value;
            /*ModuleCallbackList.update(DataMain.MCLS, 4, value);
            ToolkitBacklight.fixBrightness();
            EventMain.NE_LAMPLET_ON.onNotify();
            Intent intent = new Intent(FinalApp.ACTION_GD_MODE_CHANGE);
            intent.putExtra(FinalApp.ACTION_GD_MODE_CHANGE_VALUE, value);
            App.getInstance().sendBroadcast(intent);
            try {
                if (System.getInt(App.getInstance().getContentResolver(), FinalApp.ACTION_GD_MODE_CHANGE_VALUE, 0) != value) {
                    System.putInt(App.getInstance().getContentResolver(), FinalApp.ACTION_GD_MODE_CHANGE_VALUE, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
        /*if (DataMain.ledColorEnable) {
            ToolkitPlatform.setLedColor(value);
        }*/
    }

    public static void anyKeyBoot(int value) {
        if (DataMain.sAnyKeyBoot != value) {
            DataMain.sAnyKeyBoot = value;
            //ModuleCallbackList.update(DataMain.MCLS, 5, value);
            if (DataMain.sAnyKeyBootType == 1) {
                //HandlerStore.armStoreDelay(158, value);
            }
        }
    }

    public static void handbrake(int value) {
        if (DataMain.sHandbrake != value) {
            DataMain.sHandbrake = value;
            /*ModuleCallbackList.update(DataMain.MCLS, 7, value);
            EventMain.NE_HANDBRAKE_ON.onNotify();
            Intent intent = new Intent("com.android.handbrake");
            intent.putExtra("status", value);
            App.getInstance().sendBroadcast(intent);*/
        }
        /*try {
            if (System.getInt(App.getInstance().getContentResolver(), "handbrake_status", 1) != value) {
                System.putInt(App.getInstance().getContentResolver(), "handbrake_status", value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static void handbrakeEnable(int value) {
        if (DataMain.sHandbrakeEnable != value) {
            DataMain.sHandbrakeEnable = value;
            /*ModuleCallbackList.update(DataMain.MCLS, 8, value);
            if (DataMain.sHandbrakeEnableType == 1) {
                HandlerStore.armStoreDelay(159, value);
            }
            EventMain.NE_HANDBRAKE_ENABLE.onNotify();
            Intent intent = new Intent("com.android.handbrake_enable");
            intent.putExtra("status", value);
            App.getInstance().sendBroadcast(intent);*/
        }
       /* try {
            if (System.getInt(App.getInstance().getContentResolver(), "handbrake_enable_status", 0) != value) {
                System.putInt(App.getInstance().getContentResolver(), "handbrake_enable_status", value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static void reserveAction0(int value) {
        if (DataMain.sReserveAction0 != value) {
            DataMain.sReserveAction0 = value;
            //EventMain.NE_RESERVE_ACTION0.onNotify();
        }
    }

    public static void reserveAction1(int value) {
        if (DataMain.sReserveAction1 != value) {
            DataMain.sReserveAction1 = value;
            //EventMain.NE_RESERVE_ACTION1.onNotify();
        }
    }

    public static void cutAccTurnOffLcdc(int value) {
        if (DataMain.sCutAccTurnOffLcdc != value) {
            DataMain.sCutAccTurnOffLcdc = value;
            //ModuleCallbackList.update(DataMain.MCLS, 66, value);
            //HandlerStore.armStoreDelay(148, value);
        }
    }

    public static void mcuNeedUpdate(int value) {
        if (DataMain.sMcuNeedUpdate != value) {
            DataMain.sMcuNeedUpdate = value;
            //ModuleCallbackList.update(DataMain.MCLS, 45, 5, value);
        }
    }

    public static void carBackcar(int value) {
        if (DataMain.sCarBackcar != value) {
            DataMain.sCarBackcar = value;
            //HandlerSound.fixVol();
            //EventMain.NE_CAR_BACKCAR.onNotify();
        }
    }

    public static void backcar(int value) {
        if (sBackcarBak != value) {
            DataMain.sHostBackcar = value;
            sBackcarBak = value;
            //ModuleCallbackList.update(DataMain.MCLS, 12, value);
            //HandlerSound.fixVol();
            //EventMain.NE_BACKCAR.onNotify();
        }
    }

    public static void lampletByTime(int value) {
        if (DataMain.sLampletByTime != value) {
            DataMain.sLampletByTime = value;
            //ModuleCallbackList.update(DataMain.MCLS, 29, value);
        }
    }

    public static void autoBlackScreen(int value) {
        if (DataMain.sAutoBlackScreen != value) {
            DataMain.sAutoBlackScreenRemain = value;
            DataMain.sAutoBlackScreen = value;
            //ModuleCallbackList.update(DataMain.MCLS, 36, value);
            //HandlerStore.armStoreDelay(140, value);
        }
    }

    public static void mcuPowerOption(int value) {
        if (DataMain.sMcuPowerOption != value) {
            DataMain.sMcuPowerOption = value;
            //ModuleCallbackList.update(DataMain.MCLS, 38, value);
        }
    }

    public static void tempOut(int value) {
        DataMain.sTempOut = value;
        //ModuleCallbackList.update(DataMain.MCLS, 40, (((value >> 15) & 1) << 28) | ((value & 32767) & 65535));
    }

    public static void steerAngle(int value) {
        if (DataMain.sSteerAngle != value) {
            DataMain.sSteerAngle = value;
            //ModuleCallbackList.update(DataMain.MCLS, 41, value);
            //EventMain.NE_STEER_ANGLE.onNotify();
        }
    }

    public static void armSleepWakeup(int value) {
        if (DataMain.sSleepWakeup != value) {
            DataMain.sSleepWakeup = value;
            /*TODO if (value != 0) {
                if (DataChip.getChipId() == 5 && DataVideo.sModuleId == 5 && SystemProperties.get("sys.car.reverse", "0").equals("2") && ToolsJni.cmd_32_get_boot_reverse_status() == 0) {
                    ToolsJni.cmd_33_reset_videoIc(1);
                    VideoTW9992.isTw9992Reseting = true;
                    VideoTW9992.isTw9992Init = false;
                }
                HandlerSound.clearSoundFlag();
                JumpPage.broadcastByIntentName("android.intent.action.ACTION_MT_COMMAND_SLEEP_OUT");
            } else {
                HandlerStore.armStore(1, DataMain.sTopAppWhenMcuOff);
                HandlerStore.armStore(2, DataMain.sAppId);
                JumpPage.broadcastByIntentName("android.intent.action.ACTION_MT_COMMAND_SLEEP_IN");
            }
            ModuleCallbackList.update(DataMain.MCLS, 43, value);
            EventMain.NE_SLEEP_WAKEUP.onNotify();*/
        }
        //ToolkitApp.setSystemProperty("sys.sleep", value == 0 ? "1" : "0");
    }

    public static void mcuKeyairshow(int action) {
        DataMain.sMcuAirKey = 1;
        //EventReceiver.NE_MCUAIRKEY.onNotify();
    }

    public static void mcuKeyair(int value) {
        DataMain.sMcuAirKeyfunction = value;
        //EventReceiver.NE_MCUAIR.onNotify();
    }

    public static void tip(int value) {
        //ModuleCallbackList.update(DataMain.MCLS, 45, value);
    }

    public static void backcarType(int value) {
        if (DataMain.sBackcarType != value) {
            DataMain.sBackcarType = value;
            //ModuleCallbackList.update(DataMain.MCLS, 47, value);
        }
    }

    public static void cutAccDelayCloseScreen(int value) {
        if (DataMain.sCutAccDelayCloseScreen != value) {
            DataMain.sCutAccDelayCloseScreen = value;
            //ModuleCallbackList.update(DataMain.MCLS, 71, value);
        }
    }

    public static void panelKeyEnable(int value) {
        if (DataMain.sPanelKeyEnable != value) {
            DataMain.sPanelKeyEnable = value;
            //ModuleCallbackList.update(DataMain.MCLS, 82, value);
        }
    }

    public static void startStopEnable(int value) {
        if (DataMain.sStartStopEnable != value) {
            DataMain.sStartStopEnable = value;
            //ModuleCallbackList.update(DataMain.MCLS, 88, value);
        }
    }

    public static void deviceType(int value) {
        if (DataMain.sDeviceType != value) {
            DataMain.sDeviceType = value;
            //ModuleCallbackList.update(DataMain.MCLS, 74, new int[]{DataMain.sAppId, 9, value});
        }
        if (DataMain.sAppId == 8 || DataMain.sAppId == 9) {
            ToolkitDev.writeMcu(116, 7, value, 0);
        }
    }

    public static void mediaType(int value) {
        if (DataMain.sMdiaType != value) {
            DataMain.sMdiaType = value;
            //ModuleCallbackList.update(DataMain.MCLS, 74, new int[]{DataMain.sAppId, 10, value});
        }
        if (DataMain.sAppId == 8 || DataMain.sAppId == 9) {
            ToolkitDev.writeMcu(116, 8, value, 0);
        }
    }

    public static void playTracks(int track, int trackCnt) {
        track++;
        if (track <= trackCnt) {
            if (DataMain.sTrack != track || DataMain.sTrackCnt != trackCnt) {
                DataMain.sTrack = track;
                DataMain.sTrackCnt = trackCnt;
                //EventMain.NE_PLAY_TRACK.onNotify();
                //ModuleCallbackList.update(DataMain.MCLS, 74, new int[]{DataMain.sAppId, 4, track, trackCnt});
                if (DataMain.sAppId == 8 || DataMain.sAppId == 9) {
                    ToolkitDev.writeMcu(116, 3, (65280 & track) >> 8, track & 255);
                    ToolkitDev.writeMcu(116, 4, (65280 & trackCnt) >> 8, trackCnt & 255);
                }
            }
        }
    }

    public static void playTimes(int playTime, int totalTime) {
        if (playTime <= totalTime) {
            if (DataMain.sPlayTime != playTime || DataMain.sPlayTotalTime != totalTime) {
                DataMain.sPlayTime = playTime;
                DataMain.sPlayTotalTime = totalTime;
                //EventMain.NE_PLAY_TIME.onNotify();
                //ModuleCallbackList.update(DataMain.MCLS, 74, new int[]{DataMain.sAppId, 5, playTime, totalTime});
                if (DataMain.sAppId == 8 || DataMain.sAppId == 9) {
                    ToolkitDev.writeMcu(116, 1, (65280 & playTime) >> 8, playTime & 255);
                    ToolkitDev.writeMcu(116, 2, (65280 & totalTime) >> 8, totalTime & 255);
                }
            }
        }
    }

    public static void gpsSpeed(int speed) {
        if (DataMain.sGpsSpeed != speed) {
            DataMain.sGpsSpeed = speed;
            //EventUtil.NE_GPS_SPEED.onNotify();
            //ModuleCallbackList.update(DataMain.MCLS, 101, speed);
        }
    }

    public static void accOn(int value) {
        if (DataMain.sAccOnForUi != value) {
            DataMain.sAccOnForUi = value;
            //ModuleCallbackList.update(DataMain.MCLS, 50, value);
        }
        if (DataMain.sAccOn != value) {
            DataMain.sAccOn = value;
            /*TODO EventMain.NE_ACC_ON.onNotify();
            ToolkitPlatform.accStateToBsp(value == 0 ? 0 : 1);
            if (DataMain.sSleepWakeup == 0 && DataChip.getChipId() != 2) {
                HandlerApp.wakeup();
                ReceiverMcu.resetTick();
            }
            if (value != 0) {
                HandlerSound.initIcCmd();
                if (DataMain.sCutAccTurnOffLcdc != 0) {
                    ToolkitPlatform.closeDoor(0);
                    ToolkitPlatform.muteAmp(0);
                }
                JumpPage.broadcastByIntentName("com.glsx.boot.ACCON");
                return;
            }
            if (DataMain.sCutAccTurnOffLcdc != 0) {
                ToolkitPlatform.muteAmp(1);
                ToolkitPlatform.closeDoor(1);
            }
            JumpPage.broadcastByIntentName("com.glsx.boot.ACCOFF");*/
        }
    }

    public static void panelKeyType(int value) {
        if (DataMain.sPanelKeyType != value) {
            DataMain.sPanelKeyType = value;
            //ModuleCallbackList.update(DataMain.MCLS, 51, value);
        }
    }

    public static void panelKeyTypeCnt(int value) {
        if (DataMain.sPanelKeyTypeCnt != value) {
            DataMain.sPanelKeyTypeCnt = value;
            //ModuleCallbackList.update(DataMain.MCLS, 63, value);
        }
    }

    public static void lampletOnBoot(int value) {
        if (DataMain.sLampletOnBoot != value) {
            DataMain.sLampletOnBoot = value;
            //ModuleCallbackList.update(DataMain.MCLS, 52, value);
        }
    }

    public static void lampOnAlawys(int value) {
        if (DataMain.sLampOnAlawys != value) {
            DataMain.sLampOnAlawys = value;
            //ModuleCallbackList.update(DataMain.MCLS, 53, value);
        }
    }

    public static void radarParkEnable(int value) {
        if (DataMain.sRadarParkEnable != value) {
            DataMain.sRadarParkEnable = value;
            //ModuleCallbackList.update(DataMain.MCLS, 86, value);
        }
    }

    public static void lampletColorCtrl(int value) {
        if (DataMain.sLampletColorCtrl != value) {
            DataMain.sLampletColorCtrl = value;
            //ModuleCallbackList.update(DataMain.MCLS, 54, value);
        }
    }

    public static void panoramaOn(int value) {
        if (DataMain.sPanoramaOn != value) {
            DataMain.sPanoramaOn = value;
            //ModuleCallbackList.update(DataMain.MCLS, 55, value);
        }
    }

    public static void fanCycle(int value) {
        if (DataMain.sFanCycle != value) {
            DataMain.sFanCycle = value;
            //ModuleCallbackList.update(DataMain.MCLS, 56, value);
        }
    }

    public static void lampletCleanOn(int value) {
        if (DataMain.sLampletCleanOn != value) {
            DataMain.sLampletCleanOn = value;
            //ModuleCallbackList.update(DataMain.MCLS, 78, value);
        }
    }

    public static void ambientLightOn(int value) {
        if (DataMain.sAmbientLightOn != value) {
            DataMain.sAmbientLightOn = value;
            //ModuleCallbackList.update(DataMain.MCLS, 79, 0, value);
        }
    }

    public static void ambientLightColor(int value) {
        if (DataMain.sAmbientLightColor != value) {
            DataMain.sAmbientLightColor = value;
            //ModuleCallbackList.update(DataMain.MCLS, 79, 2, value);
        }
    }

    public static void mirrorUpDown(int av1, int av2, int av3) {
        if (DataMain.sMirrorUpDownAv1 != av1 || DataMain.sMirrorUpDownAv2 != av2 || DataMain.sMirrorUpDownAv3 != av3) {
            DataMain.sMirrorUpDownAv1 = av1;
            DataMain.sMirrorUpDownAv2 = av2;
            DataMain.sMirrorUpDownAv3 = av3;
            //ModuleCallbackList.update(DataMain.MCLS, 58, new int[]{av1, av2, av3});
        }
    }

    public static void mcuRequestVideo(int av1, int av2, int av3) {
        if (DataMain.sMcuRequestVideoAv1 != av1 || DataMain.sMcuRequestVideoAv2 != av2 || DataMain.sMcuRequestVideoAv3 != av3) {
            DataMain.sMcuRequestVideoAv1 = av1;
            DataMain.sMcuRequestVideoAv2 = av2;
            DataMain.sMcuRequestVideoAv3 = av3;
            //ModuleCallbackList.update(DataMain.MCLS, 59, new int[]{av1, av2, av3});
        }
    }

    public static void mcuRequestVideoCmd(int av1, int av2, int av3) {
        if (DataMain.sMcuRequestVideoAv1 != av1 || DataMain.sMcuRequestVideoAv2 != av2 || DataMain.sMcuRequestVideoAv3 != av3) {
            DataMain.sMcuRequestVideoAv1 = av1;
            DataMain.sMcuRequestVideoAv2 = av2;
            DataMain.sMcuRequestVideoAv3 = av3;
            //ModuleCallbackList.update(DataMain.MCLS, 59, new int[]{av1, av2, av3});
            ToolkitDev.writeMcu(122, 2, av1, av2, av3);
        }
    }

    public static void mcuBootOn(int value) {
        if (DataMain.sMcuBootOn != value) {
            DataMain.sMcuBootOn = value;
            //ModuleCallbackList.update(DataMain.MCLS, 62, value);
        }
    }

    public static void signalOn(int on) {
        if (DataMain.sSignalOn != on) {
            DataMain.sSignalOn = on;
            Log.d("video", "signalOn = " + on);
            //ModuleCallbackList.update(DataMain.MCLS, 69, on);
        }
    }

    public static void trunkState(int value) {
        if (DataMain.sTrunkState != value) {
            DataMain.sTrunkState = value;
            //ModuleCallbackList.update(DataMain.MCLS, 87, value);
        }
    }

    public static void rollKeyType(int enable, int value) {
        if (DataMain.sRollKeyType != value) {
            DataMain.sRollKeyType = value;
            //ModuleCallbackList.update(DataMain.MCLS, 89, enable, value);
        }
    }

    public static void ledColor(int value) {
        if (DataMain.sLedColor != value) {
            DataMain.sLedColor = value;
            //ModuleCallbackList.update(DataMain.MCLS, 99, value);
        }
    }

    public static synchronized void mcuKeyMode() {
        // TODO broadcast
    }

    public static void mcuKeyUp() {
        // TODO broadcast
    }

    public static void mcuKeyDown() {
        // TODO broadcast
    }

    public static void mcuKeyLeft() {
        // TODO broadcast
    }

    public static void mcuKeyRight() {
        // TODO broadcast
    }

    public static void mcuKeyEnter() {
        // TODO broadcast
    }

    public static void mcuKeyRollLeft() {
        // TODO broadcast
    }

    public static void mcuKeyRollRight() {
        // TODO broadcast
    }

    public static void mcuKeyNavi() {
        // TODO broadcast
    }

    public static void mcuKeyBtPhone() {
        // TODO broadcast
    }

    public static void mcuKeyAudio() {
        // TODO broadcast
    }

    public static void mcuKeyEq() {
        // TODO broadcast
    }

    public static void mcuKeyVolUp() {
        // TODO broadcast
    }

    public static void mcuKeyVolDown() {
        // TODO broadcast
    }

    public static void mcuKeyVolMute() {
        // TODO broadcast
    }

    public static void mcuKeyHome() {
        // TODO broadcast
    }

    public static void mcuKeyMenu() {
        // TODO broadcast
    }

    public static void mcuKeyBack() {
        // TODO broadcast
    }

    public static void mcuAllApps() {
        // TODO broadcast
    }

    public static void mcuKeyPlayer() {
        // TODO broadcast
    }

    public static void mcuKeyBand() {
        // TODO broadcast
    }

    public static void mcuKeyPlay() {
        // TODO broadcast
    }

    public static void mcuKeyPause() {
        // TODO broadcast
    }

    public static void mcuKeyEnter0x30() {
        // TODO broadcast
    }

    public static void mcuKeyLeft0x31() {
        // TODO broadcast
    }

    public static void mcuKeyRight0x32() {
        // TODO broadcast
    }

    public static void keyN0() {
        // TODO broadcast
    }

    public static void keyN1() {
        // TODO broadcast
    }

    public static void keyN2() {
        // TODO broadcast
    }

    public static void keyN3() {
        // TODO broadcast
    }

    public static void keyN4() {
        // TODO broadcast
    }

    public static void keyN5() {
        // TODO broadcast
    }

    public static void keyN6() {
        // TODO broadcast
    }

    public static void keyN7() {
        // TODO broadcast
    }

    public static void keyN8() {
        // TODO broadcast
    }

    public static void keyN9() {
        // TODO broadcast
    }

    public static void keyNP() {
        // TODO broadcast
    }

    public static void keyNX() {
        // TODO broadcast
    }

    public static void keyNJ() {
        // TODO broadcast
    }

    public static void keyFB() {
        // TODO broadcast
    }

    public static void keyFF() {
        // TODO broadcast
    }

    public static void keySearch() {
        // TODO broadcast
    }

    public static void resetFactory() {
    }
}
