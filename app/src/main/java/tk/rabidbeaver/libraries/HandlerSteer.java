package tk.rabidbeaver.libraries;

public class HandlerSteer {
    public static final int[] SCAN_ADC = new int[6];
    public static int sDetect;
    public static int sKey;
    public static int sKeyFunc;
    public static final int[] MCU_KEY_FUNC = new int[35];

    /*public static final Runnable ON_KEY_AUDIO = new C08198();
    public static final Runnable ON_KEY_AUX = new Runnable() {
        public void run() {
            if (JumpPage.isAuxTop()) {
                JumpPage.bringFrontToBack();
            } else {
                JumpPage.activityByIntentName(DataApp.sAppAuxPrefix);
            }
        }
    };
    public static final Runnable ON_KEY_BACK = new C08165();
    public static final Runnable ON_KEY_DVD = new Runnable() {
        public void run() {
            JumpPage.dvd();
        }
    };
    public static final Runnable ON_KEY_DVR = new C08132();
    public static final Runnable ON_KEY_HOME = new C08143();
    public static final Runnable ON_KEY_MENU = new C08154();
    public static final Runnable ON_KEY_NEXT_HANG = new C08187();
    public static final Runnable ON_KEY_PANORAMA = new Runnable() {
        public void run() {
            HandlerBspKey.bspKeyPanorama(1);
        }
    };
    public static final Runnable ON_KEY_PREV_PHONE = new C08176();
    public static final Runnable ON_KEY_RADIO = new C08209();
    public static final Runnable ON_KEY_RIGHT_CAMERA = new Runnable() {
        public void run() {
            JumpPage.rightCamera();
        }
    };
    public static final Runnable ON_KEY_VA = new C08121();
    public static final Runnable ON_KEY_VIDEO = new Runnable() {
        public void run() {
            JumpPage.videoPlayer();
        }
    };

    class C08121 implements Runnable {
        C08121() {
        }

        public void run() {
            JumpPage.va();
        }
    }

    class C08132 implements Runnable {
        C08132() {
        }

        public void run() {
            JumpPage.dvr();
        }
    }

    class C08143 implements Runnable {
        C08143() {
        }

        public void run() {
            ToolkitApp.keyHome();
        }
    }

    class C08154 implements Runnable {
        C08154() {
        }

        public void run() {
            HandlerMain.mcuKeyMenu();
        }
    }

    class C08165 implements Runnable {
        C08165() {
        }

        public void run() {
            HandlerMain.mcuKeyBack();
        }
    }

    class C08176 implements Runnable {
        C08176() {
        }

        public void run() {
            if (DataMain.sAppId == 2) {
                DataBt.sCmd.key(0);
            } else {
                HandlerMain.mcuKeyLeft();
            }
        }
    }

    class C08187 implements Runnable {
        C08187() {
        }

        public void run() {
            if (DataBt.sPhoneState == 4) {
                DataBt.sCmd.rejectRing();
            } else if (DataBt.sPhoneState == 5) {
                DataBt.sCmd.hang();
            } else {
                HandlerMain.mcuKeyRight();
            }
        }
    }

    class C08198 implements Runnable {
        C08198() {
        }

        public void run() {
            JumpPage.audioPlayer();
        }
    }

    class C08209 implements Runnable {
        C08209() {
        }

        public void run() {
            JumpPage.radio();
        }
    }

    public static synchronized void moduleId(int value) {
        synchronized (HandlerSteer.class) {
            if (DataSteer.sModuleId != value) {
                DataSteer.sModuleId = value;
                DataSteer.sCmd.cmdOut();
                switch (value) {
                    case 1:
                        DataSteer.sCmd = new CmdSteer();
                        break;
                    default:
                        DataSteer.sCmd = new Stub();
                        break;
                }
                DataSteer.sCmd.cmdIn();
            }
        }
    }*/

    public static void keyPre(int value) {
        /*if (DataSteer.sKeyPre != value) {
            DataSteer.sKeyPre = value;
            ModuleCallbackList.update(DataSteer.MCLS, 0, value);
            if (value != -1) {
                DataSteer.sCmd.keyAdc(value, DataSteer.sAdc);
            }
        } else if (DataSteer.sKeyPre != -1) {
            keyPre(-1);
        }*/
    }

    public static void adc(int value) {
        /*if (DataSteer.sAdc != value) {
            DataSteer.sAdc = value;
            ModuleCallbackList.update(DataSteer.MCLS, 2, value);
            if (DataSteer.sKeyPre != -1) {
                DataSteer.sCmd.keyAdc(DataSteer.sKeyPre, value);
            }
        }*/
    }

    public static void keyAdc(int keyCode, int adc) {
        /*if (keyCode >= 0 && keyCode < 50 && DataSteer.KEY_ADC[keyCode] != adc) {
            DataSteer.KEY_ADC[keyCode] = adc;
            ModuleCallbackList.update(DataSteer.MCLS, 1, keyCode, adc);
        }*/
    }

    public static void adcScan(int index, int adc) {
        if (index >= 0 && index < 6 && SCAN_ADC[index] != adc) {
            SCAN_ADC[index] = adc;
            //ModuleCallbackList.update(DataSteer.MCLS, 3, index, adc);
        }
    }

    public static void detect(int value) {
        if (sDetect != value) {
            sDetect = value;
            //ModuleCallbackList.update(DataSteer.MCLS, 4, value);
        }
    }

    /*static {
        defActionMap();
    }

    public static void defActionMap() {
        DataSteer.ON_KEY_RESERVE[0] = ON_KEY_VA;
        DataSteer.ON_KEY_RESERVE[1] = ON_KEY_DVR;
        DataSteer.ON_KEY_RESERVE[2] = ON_KEY_HOME;
        DataSteer.ON_KEY_RESERVE[3] = ON_KEY_MENU;
        DataSteer.ON_KEY_RESERVE[4] = ON_KEY_BACK;
        DataSteer.ON_KEY_RESERVE[5] = ON_KEY_PREV_PHONE;
        DataSteer.ON_KEY_RESERVE[6] = ON_KEY_NEXT_HANG;
        DataSteer.ON_KEY_RESERVE[7] = ON_KEY_AUDIO;
        DataSteer.ON_KEY_RESERVE[8] = ON_KEY_RADIO;
        DataSteer.ON_KEY_RESERVE[9] = ON_KEY_DVD;
        DataSteer.ON_KEY_RESERVE[10] = ON_KEY_VIDEO;
    }*/

    public static void mcuKeyEnable(int enable) {
        /*if (DataSteer.sMcuKeyEnable != enable) {
            DataSteer.sMcuKeyEnable = enable;
            ModuleCallbackList.update(DataSteer.MCLS, 5, enable);
        }*/
    }

    public static void onMcuKeyStudied(byte[] data) {
        if (data != null && data.length > 0) {
            //Print.screenHex(data, 0, data.length);
            int i;
            if (data[0] == (byte) -1) {
                for (i = 0; i < 35; i++) {
                    MCU_KEY_FUNC[i] = 2;
                    //ModuleCallbackList.update(DataSteer.MCLS, 6, i, 2);
                }
                return;
            }
            for (i = 0; i < data.length; i++) {
                int keyCode = data[i] & 255;
                if (keyCode >= 0 && keyCode <= 24) {
                    MCU_KEY_FUNC[i] = 1;
                    //ModuleCallbackList.update(DataSteer.MCLS, 6, keyCode, 1);
                } else if (keyCode > 24 && keyCode < 35) {
                    if (keyCode == sKey) {
                        MCU_KEY_FUNC[keyCode] = sKeyFunc;
                    }
                   // ModuleCallbackList.update(DataSteer.MCLS, 6, keyCode, DataSteer.MCU_KEY_FUNC[keyCode]);
                }
            }
        }
    }

    public static void onMcuKeyEvent(int keyCode, int action) {
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
