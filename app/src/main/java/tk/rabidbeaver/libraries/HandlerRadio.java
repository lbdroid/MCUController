package tk.rabidbeaver.libraries;

import android.util.Log;

public class HandlerRadio {
    public static int sBand = 65536;
    public static int sFreqMax;
    public static int sFreqMin;
    public static int sFreqStepCnt=150;
    public static int sAutoSensity;
    public static int sRdsEnable;
    public static int sSortType;
    public static int sAirLine;
    public static int sPowerOn;

    public static int sArea;
    public static int sExtraInfoByMcu;
    public static int sFreqStepLen;

    /*public int getRadioType() {
        return SystemProperties.getInt("sys.fyt.radio_type", 1);
    }*/

    public static void band(int value) {
        Log.d("RADIO", "band: "+value);
        if (sBand != value) {
            sBand = value;
            /*ModuleCallbackList.update(DataRadio.MCLS, 0, value);
            EventRadio.NE_RADIO_BAND.onNotify();
            if (DataRadio.sExtraInfoByMcu == 0) {
                extraFreqInfo();
            }*/
        }
    }

    public static void channel(int value) {
        /*if (DataRadio.sChannel != value) {
            DataRadio.sChannel = value;
            ModuleCallbackList.update(DataRadio.MCLS, 3, value);
        }*/
    }

    public static void area(int value) {
        Log.d("RADIO", "area: "+value);
        /*if (DataRadio.sArea != value) {
            DataRadio.sArea = value;
            ModuleCallbackList.update(DataRadio.MCLS, 2, value);
            if (DataRadio.sExtraInfoByMcu == 0) {
                extraFreqInfo();
            }
        }*/
    }

    public static void freq(int value) {
        Log.d("RADIO", "freq: "+value);
        /*if (DataRadio.sFreq != value) {
            psText(null);
            DataRadio.sFreq = value;
            ModuleCallbackList.update(DataRadio.MCLS, 1, value);
            EventRadio.NE_RADIO_FREQ.onNotify();
        }*/
    }

    public static void ptyId(int value) {
        Log.d("RADIO", "ptyId: "+value);
        /*if (DataRadio.sPtyId != value) {
            DataRadio.sPtyId = value;
            ModuleCallbackList.update(DataRadio.MCLS, 5, value);
        }*/
    }

    public static void rdsAfEnable(int value) {
        Log.d("RADIO", "rdsAfEnable: "+value);
        /*if (DataRadio.sRdsAfEnable != value) {
            DataRadio.sRdsAfEnable = value;
            ModuleCallbackList.update(DataRadio.MCLS, 6, value);
        }*/
    }

    public static void rdsTa(int value) {
        Log.d("RADIO", "rdsTa: "+value);
        /*if (DataRadio.sRdsTa != value) {
            DataRadio.sRdsTa = value;
            ModuleCallbackList.update(DataRadio.MCLS, 7, value);
        }*/
    }

    public static void rdsTp(int value) {
        Log.d("RADIO", "rdsTp: "+value);
        /*if (DataRadio.sRdsTp != value) {
            DataRadio.sRdsTp = value;
            ModuleCallbackList.update(DataRadio.MCLS, 8, value);
        }*/
    }

    public static void rdsTaEnable(int value) {
        Log.d("RADIO", "rdsTaEnable: "+value);
        /*if (DataRadio.sRdsTaEnable != value) {
            DataRadio.sRdsTaEnable = value;
            ModuleCallbackList.update(DataRadio.MCLS, 9, value);
        }*/
    }

    public static int isRdsWork() {
        /*if (DataRadio.sRdsTa == 1 && DataRadio.sRdsTp == 1 && DataRadio.sRdsTaEnable == 1) {
            return 1;
        }*/
        return 0;
    }

    public static void rdsPiSeek(int value) {
        Log.d("RADIO", "rdsPiSeek: "+value);
        /*if (DataRadio.sRdsPiSeek != value) {
            DataRadio.sRdsPiSeek = value;
            ModuleCallbackList.update(DataRadio.MCLS, 10, value);
        }*/
    }

    public static void rdsTaSeek(int value) {
        Log.d("RADIO", "rdsTaSeek: "+value);
        /*if (DataRadio.sRdsTaSeek != value) {
            DataRadio.sRdsTaSeek = value;
            ModuleCallbackList.update(DataRadio.MCLS, 11, value);
        }*/
    }

    public static void rdsPtySeek(int value) {
        Log.d("RADIO", "rdsPtySeek: "+value);
        /*if (DataRadio.sRdsPtySeek != value) {
            DataRadio.sRdsPtySeek = value;
            ModuleCallbackList.update(DataRadio.MCLS, 12, value);
        }*/
    }

    public static void rdsText(String value) {
        Log.d("RADIO", "rdsText: "+value);
        /*if (!ToolkitMisc.strEqual(DataRadio.sRdsText, value)) {
            DataRadio.sRdsText = value;
            ModuleCallbackList.update(DataRadio.MCLS, 13, value);
        }*/
    }

    public static void psText(String value) {
        Log.d("RADIO", "psText: "+value);
        /*if (!ToolkitMisc.strEqual(DataRadio.sPsText, value)) {
            DataRadio.sPsText = value;
            ModuleCallbackList.update(DataRadio.MCLS, 26, value);
        }*/
    }

    public static void rdsEnable(int value) {
        Log.d("RADIO", "rdsEnable: "+value);
        /*if (DataRadio.sRdsEnable != value) {
            DataRadio.sRdsEnable = value;
            ModuleCallbackList.update(DataRadio.MCLS, 15, value);
        }*/
    }

    public static void sensityAm(int value) {
        Log.d("RADIO", "sensitivityAm: "+value);
        /*if (DataRadio.sSensityAm != value) {
            DataRadio.sSensityAm = value;
            ModuleCallbackList.update(DataRadio.MCLS, 17, value);
        }*/
    }

    public static void sensityFm(int value) {
        Log.d("RADIO", "sensitivityFm: "+value);
        /*if (DataRadio.sSensityFm != value) {
            DataRadio.sSensityFm = value;
            ModuleCallbackList.update(DataRadio.MCLS, 18, value);
        }*/
    }

    public static void autoSensity(int value) {
        Log.d("RADIO", "autoSensitivity: "+value);
        /*if (DataRadio.sAutoSensity != value) {
            DataRadio.sAutoSensity = value;
            ModuleCallbackList.update(DataRadio.MCLS, 19, value);
        }*/
    }

    public static void scan(int value) {
        Log.d("RADIO", "scan: "+value);
        /*if (DataRadio.sScan != value) {
            DataRadio.sScan = value;
            ModuleCallbackList.update(DataRadio.MCLS, 20, value);
        }*/
    }

    public static void loc(int value) {
        Log.d("RADIO", "loc: "+value);
        /*if (DataRadio.sLoc != value) {
            DataRadio.sLoc = value;
            ModuleCallbackList.update(DataRadio.MCLS, 23, value);
        }*/
    }

    public static void stereo(int value) {
        Log.d("RADIO", "stereo: "+value);
        /*if (DataRadio.sStereo != value) {
            DataRadio.sStereo = value;
            ModuleCallbackList.update(DataRadio.MCLS, 21, value);
        }*/
    }

    public static void searchState(int value) {
        Log.d("RADIO", "searchState: "+value);
        /*if (DataRadio.sSearchState != value) {
            DataRadio.sSearchState = value;
            ModuleCallbackList.update(DataRadio.MCLS, 22, value);
        }*/
    }

    public static void sortType(int value) {
        Log.d("RADIO", "sortType: "+value);
        /*if (DataRadio.sSortType != value) {
            DataRadio.sSortType = value;
            ModuleCallbackList.update(DataRadio.MCLS, 24, value);
        }*/
    }

    public static void power(int value) {
        Log.d("RADIO", "power: "+value);
        /*if (DataRadio.sPowerOn != value) {
            DataRadio.sPowerOn = value;
            ModuleCallbackList.update(DataRadio.MCLS, 27, value);
        }*/
    }

    public static void extraFreqInfo() {
        if (sExtraInfoByMcu == 0) {
            int[] value = new int[4];
            switch ((sArea << 20) | sBand) {
                case 0:
                case 1:
                    value[0] = 530;
                    value[1] = 1720;
                    value[2] = 10;
                    break;
                case 65536:
                case 65537:
                case 65538:
                    value[0] = 8750;
                    value[1] = 10790;
                    value[2] = 20;
                    break;
                case 1048576:
                case 1048577:
                    value[0] = 520;
                    value[1] = 1620;
                    value[2] = 10;
                    break;
                case 1114112:
                case 1114113:
                case 1114114:
                case 2162688:
                case 2162689:
                case 2162690:
                    value[0] = 8750;
                    value[1] = 10800;
                    value[2] = 5;
                    break;
                case 2097152:
                case 2097153:
                case 3145728:
                case 3145729:
                    value[0] = 522;
                    value[1] = 1620;
                    value[2] = 9;
                    break;
                case 3211264:
                    value[0] = 6500;
                    value[1] = 7400;
                    value[2] = 3;
                    break;
                case 3211265:
                case 3211266:
                    value[0] = 8750;
                    value[1] = 10800;
                    value[2] = 10;
                    break;
                case 4194304:
                case 4194305:
                    value[0] = 520;
                    value[1] = 1629;
                    value[2] = 9;
                    break;
                case 4259840:
                case 4259841:
                case 4259842:
                    value[0] = 7600;
                    value[1] = 9000;
                    value[2] = 10;
                    break;
            }
            if (value[2] != 0) {
                value[3] = (value[1] - value[0]) / value[2];
            }
            //ModuleCallbackList.update(DataRadio.MCLS, 16, value);
        }// else if (DataRadio.sFreqMin != 0) {
         //   ModuleCallbackList.update(DataRadio.MCLS, 16, new int[]{DataRadio.sFreqMin, DataRadio.sFreqMax, DataRadio.sFreqStepLen, DataRadio.sFreqStepCnt});
        //}
    }

    public static void airLine(int value) {
        Log.d("RADIO", "airLine: "+value);
        /*if (DataRadio.sAirLine != value) {
            DataRadio.sAirLine = value;
            ModuleCallbackList.update(DataRadio.MCLS, 25, value);
        }*/
    }
}
