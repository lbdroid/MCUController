package tk.rabidbeaver.libraries;

import android.content.Intent;
import android.util.Log;

import tk.rabidbeaver.mcucontroller.Constants;

public class HandlerSteer {
    public static final int[] SCAN_ADC = new int[6];
    public static int sDetect;
    public static int sKey;
    public static int sAdc;
    public static int sKeyFunc;
    public static final int[] MCU_KEY_FUNC = new int[35];
    public static final int[] KEY_ADC = new int[50];

    public static void adc(int value) {
        Log.d("HANDLERSTEER", "adc: "+value);
        if (sAdc != value) sAdc = value;
    }

    public static void keyAdc(int keyCode, int adc) {
        Log.d("HANDLERSTEER", "keyAdc(keyCode, adc): "+keyCode+", "+adc);
        if (keyCode >= 0 && keyCode < 50 && KEY_ADC[keyCode] != adc) {
            KEY_ADC[keyCode] = adc;
        }
    }

    public static void adcScan(int index, int adc) {
        Log.d("HANDLERSTEER", "adcScan(index, adc): "+index+", "+adc);
        if (index >= 0 && index < 6 && SCAN_ADC[index] != adc) {
            SCAN_ADC[index] = adc;
        }
    }

    public static void detect(int value) {
        Log.d("HANDLERSTEER", "detect: "+value);
        if (sDetect != value) {
            sDetect = value;
        }
    }

    public static void keyAct(int value){
        Log.d("HANDLERSTEER", "keyAct: "+value);

        Intent i = new Intent();
        i.setAction(Constants.SWI.BROADCAST);
        i.putExtra("KEY", value);
        ToolkitDev.context.sendBroadcast(i);
    }


    public static void mcuKeyEnable(int enable) {
        Log.d("HANDLERSTEER", "mcuKeyEnable: "+enable);
    }

    public static void onMcuKeyStudied(byte[] data) {
        Log.d("HANDLERSTEER", "onMcuKeyStudied");
        if (data != null && data.length > 0) {
            String bstring = "0x";
            String chrstr = "";
            for (int j=0; j<data.length; j++){
                chrstr = Integer.toHexString(data[j]);
                while (chrstr.length() < 2) chrstr = "0"+chrstr;
                bstring+=chrstr;
            }
            Log.d("HANDLERSTEER", "onMcuKeyStudied: "+bstring);
            int i;
            if (data[0] == (byte) -1) {
                for (i = 0; i < 35; i++) {
                    MCU_KEY_FUNC[i] = 2;
                }
                return;
            }
            for (i = 0; i < data.length; i++) {
                int keyCode = data[i] & 255;
                if (keyCode >= 0 && keyCode <= 24) {
                    MCU_KEY_FUNC[i] = 1;
                } else if (keyCode > 24 && keyCode < 35) {
                    if (keyCode == sKey) {
                        MCU_KEY_FUNC[keyCode] = sKeyFunc;
                    }
                }
            }
        }
    }

    public static void onMcuKeyEvent(int keyCode, int action) {
        Log.d("HANDLERSTEER", "onMcuKeyEvent(keyCode, action): "+keyCode+", "+action);
        /*if (DataCanUp.sOccupiedMcu != 1) {
            if (DataMain.sHostBackcar != 1 || keyCode == 243 || action != 0) {
                if (!DataRadio.rdsWorkNotTouchKey || HandlerRadio.isRdsWork() != 1 || action != 0) {
                    if (!(DataChip.getChipId() == 0 || DataChip.getChipId() == 1 || DataSound.sBeep == 0 || action != 0)) {
                        ToolkitDev.writeMcu(1, 0, 82);
                    }
                    IEventHandler handler = DataBspKey.sKeyEventHandler;
                    if (handler != null) {
                        if (handler.onHandle(0, new int[]{keyCode, action}, null, null, null) && action != 1) {
                            return;
                        }
                    }
                    if (keyCode >= 0 && keyCode <= 24) {
                        switch (keyCode) {
                            case 0:
                                HandlerBspKey.bspKeyPower(action);
                                return;
                            case 1:
                                HandlerBspKey.bspKeyNavi(action);
                                return;
                            case 2:
                                HandlerBspKey.bspKeyMode(action);
                                return;
                            case 3:
                                HandlerBspKey.bspKeyPrev(action);
                                return;
                            case 4:
                                HandlerBspKey.bspKeyNext(action);
                                return;
                            case 5:
                                HandlerBspKey.bspKeyHome(action);
                                return;
                            case 6:
                                HandlerBspKey.bspKeyBack(action);
                                return;
                            case 7:
                                HandlerBspKey.bspKeyVolUp(action);
                                return;
                            case 8:
                                HandlerBspKey.bspKeyVolDown(action);
                                return;
                            case 9:
                                HandlerBspKey.bspKeyMenu(action);
                                return;
                            case 10:
                                HandlerBspKey.bspKeyAllApps(action);
                                return;
                            case 11:
                                HandlerBspKey.bspKeyEject(action);
                                return;
                            case 12:
                                HandlerBspKey.bspKeyMute(action);
                                return;
                            case 13:
                                HandlerBspKey.bspKeyVa(action);
                                return;
                            case 14:
                                HandlerBspKey.bspKeyDim(action);
                                return;
                            case 15:
                                HandlerBspKey.bspKeyRecentTask(action);
                                return;
                            case 16:
                                HandlerBspKey.bspKeyPlayPause(action);
                                return;
                            case 17:
                                HandlerBspKey.bspKeyCamera(action);
                                return;
                            case 18:
                                HandlerBspKey.bspKeyPhone(action);
                                return;
                            case 19:
                                HandlerBspKey.bspKeyTonePlus(action);
                                return;
                            case 20:
                                HandlerBspKey.bspKeyToneMinus(action);
                                return;
                            case 21:
                                HandlerBspKey.bspKeyBand(action);
                                return;
                            case 22:
                                HandlerBspKey.bspKeyDvd(action);
                                return;
                            case 23:
                                HandlerBspKey.bspKeyPanorama(action);
                                return;
                            case 24:
                                HandlerBspKey.bspKeyRadio(action);
                                return;
                            default:
                                return;
                        }
                    } else if (keyCode > 24 && keyCode < 35) {
                        int func = DataSteer.MCU_KEY_FUNC[keyCode];
                        if (action == 0) {
                            switch (func) {
                                case 3:
                                    HandlerBspKey.bspKeyBlackScreen(action);
                                    return;
                                case 4:
                                    JumpPage.audioPlayer();
                                    return;
                                case 5:
                                    JumpPage.videoPlayer();
                                    return;
                                case 6:
                                    JumpPage.aux();
                                    return;
                                case 7:
                                    JumpPage.btPageDialByKey();
                                    return;
                                case 8:
                                    JumpPage.btPageBtAvForce();
                                    return;
                                case 9:
                                    JumpPage.eq();
                                    return;
                                case 10:
                                    JumpPage.tv();
                                    return;
                                case 11:
                                    JumpPage.ipod();
                                    return;
                                case 12:
                                    JumpPage.carSettings();
                                    return;
                                case 13:
                                    JumpPage.settings();
                                    return;
                                case 14:
                                    HandlerMain.standbyCmd(2);
                                    return;
                                case 15:
                                    HandlerMain.keyN0();
                                    return;
                                case 16:
                                    HandlerMain.keyN1();
                                    return;
                                case 17:
                                    HandlerMain.keyN2();
                                    return;
                                case 18:
                                    HandlerMain.keyN3();
                                    return;
                                case 19:
                                    HandlerMain.keyN4();
                                    return;
                                case 20:
                                    HandlerMain.keyN5();
                                    return;
                                case 21:
                                    HandlerMain.keyN6();
                                    return;
                                case 22:
                                    HandlerMain.keyN7();
                                    return;
                                case 23:
                                    HandlerMain.keyN8();
                                    return;
                                case 24:
                                    HandlerMain.keyN9();
                                    return;
                                case 25:
                                    HandlerMain.keyNP();
                                    return;
                                case 26:
                                    HandlerMain.keyNX();
                                    return;
                                case 27:
                                    HandlerMain.keyNJ();
                                    return;
                                case 28:
                                    ModuleCallbackList.update(DataMain.MCLS, 60, 1);
                                    return;
                                case 29:
                                    ModuleCallbackList.update(DataMain.MCLS, 60, 2);
                                    return;
                                case 30:
                                    ModuleCallbackList.update(DataMain.MCLS, 60, 3);
                                    return;
                                case 31:
                                    ModuleCallbackList.update(DataMain.MCLS, 60, 4);
                                    return;
                                case 32:
                                    ModuleCallbackList.update(DataMain.MCLS, 60, 0);
                                    return;
                                case 33:
                                    HandlerMain.keyFF();
                                    return;
                                case 34:
                                    HandlerMain.keyFB();
                                    return;
                                default:
                                    return;
                            }
                        }
                    }
                }
            }
        }*/
    }
}
