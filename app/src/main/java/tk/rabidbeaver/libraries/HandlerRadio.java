package tk.rabidbeaver.libraries;

import android.content.Intent;
import android.util.Log;

import tk.rabidbeaver.mcucontroller.Constants;

class HandlerRadio {
    static int sBand = 65536;
    static int sFreqMax;
    static int sFreqMin;
    static int sFreqStepCnt = 150;
    static int sAutoSensity;
    static int sRdsEnable;
    static int sPowerOn;

    static int sExtraInfoByMcu;
    static int sFreqStepLen;

    static final int[] FREQ_AM = new int[12];
    static final int[] FREQ_FM = new int[18];

    static final String[] RDS_CHANNEL_TEXT_AM = new String[12];
    static final String[] RDS_CHANNEL_TEXT_FM = new String[18];

    private static void broadcast(String eName, int eInt) {
        Intent i = new Intent();
        i.setAction(Constants.RADIO.BROADCAST);
        i.putExtra(eName, eInt);
        ToolkitDev.context.sendBroadcast(i);
    }

    private static void broadcast(String eName, String eStr) {
        Intent i = new Intent();
        i.setAction(Constants.RADIO.BROADCAST);
        i.putExtra(eName, eStr);
        ToolkitDev.context.sendBroadcast(i);
    }

    static void band(int value) {
        Log.d("RADIO", "band: "+value);
        broadcast("BAND", value);
        if (sBand != value) {
            sBand = value;
        }
    }

    static void channel(int value) {
        broadcast("CHANNEL", value);
    }

    static void area(int value) {
        broadcast("AREA", value);
        Log.d("RADIO", "area: "+value);
    }

    static void freq(int value) {
        broadcast("FREQ", value);
        Log.d("RADIO", "freq: "+value);
    }

    static void ptyId(int value) {
        broadcast("PTYID", value);
        Log.d("RADIO", "ptyId: "+value);
    }

    static void rdsAfEnable(int value) {
        Log.d("RADIO", "rdsAfEnable: "+value);
    }

    static void rdsTa(int value) {
        Log.d("RADIO", "rdsTa: "+value);
    }

    static void rdsTp(int value) {
        Log.d("RADIO", "rdsTp: "+value);
    }

    static void rdsTaEnable(int value) {
        Log.d("RADIO", "rdsTaEnable: "+value);
    }

    static void rdsPiSeek(int value) {
        Log.d("RADIO", "rdsPiSeek: "+value);
    }

    static void rdsTaSeek(int value) {
        Log.d("RADIO", "rdsTaSeek: "+value);
    }

    static void rdsPtySeek(int value) {
        Log.d("RADIO", "rdsPtySeek: "+value);
    }

    static void updateRdsChannelText(){
        Log.d("RADIO", "updateRdsChannelText");
    }

    static void rdsText(String value) {
        broadcast("RDSTEXT", value);
        Log.d("RADIO", "rdsText: "+value);
    }

    static void psText(String value) {
        broadcast("PSTEXT", value);
        Log.d("RADIO", "psText: "+value);
    }

    static void rdsEnable(int value) {
        Log.d("RADIO", "rdsEnable: "+value);
    }

    static void sensitivityAm(int value) {
        Log.d("RADIO", "sensitivityAm: "+value);
    }

    static void sensitivityFm(int value) {
        Log.d("RADIO", "sensitivityFm: "+value);
    }

    static void autoSensitivity(int value) {
        Log.d("RADIO", "autoSensitivity: "+value);
    }

    static void scan(int value) {
        Log.d("RADIO", "scan: "+value);
    }

    static void loc(int value) {
        broadcast("LOC", value);
        Log.d("RADIO", "loc: "+value);
    }

    static void stereo(int value) {
        broadcast("STEREO", value);
        Log.d("RADIO", "stereo: "+value);
    }

    static void searchState(int value) {
        Log.d("RADIO", "searchState: "+value);
    }

    static void sortType(int value) {
        Log.d("RADIO", "sortType: "+value);
    }

    static void power(int value) {
        Log.d("RADIO", "power: "+value);
    }

    /*public static void extraFreqInfo() {
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
        }
    }*/
}
