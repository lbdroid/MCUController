package tk.rabidbeaver.libraries;

import android.os.SystemClock;
import android.util.Log;

public class ReceiverMcu {
    private final byte[] DATA_MISC = new byte[1024];
    private static int mSleepTick;
    private int RADIO_band = -1;
    private int RADIO_channel;
    private int RADIO_freq;
    private int TV_freq;
    private final char[] ch_d3 = new char[8];
    private int mCanbusType;
    private int mChecksumIndex = 0;
    private int mFrameStartIndex = 0;
    private int mIsTipMcuError = 0;
    //private C00631 mRecieverExtra = new C00631();
    private int mSize = 0;
    private int mStm32NeedReupgradeTip;
    private int mTvSignalCheckCnt;
    private int mTwSpiMcuUpdating;
    private int mTwSpiOsdUpdating;
    private TickLock mLockUiOk = new TickLock();

    /*USELESS: class C00631 {
        private final byte[] CMD;
        private final byte[] DATA = new byte[1024];
        private int cnt = 0;
        private int end;
        private int j;
        private boolean mCmdReceive = false;
        private int mSizeExtra = 0;

        C00631() {
            byte[] bArr = new byte[8];
            bArr[0] = (byte) -35;
            bArr[1] = (byte) 85;
            bArr[5] = (byte) 1;
            bArr[6] = (byte) 1;
            bArr[7] = (byte) -52;
            CMD = bArr;
        }

        public void onReceive(byte[] data) {
            if (!mCmdReceive) {
                cnt += data.length;
                if (cnt > 10240) {
                    mCmdReceive = true;
                    return;
                }
                System.arraycopy(data, 0, DATA, mSizeExtra, data.length);
                mSizeExtra += data.length;
                end = mSizeExtra - 8;
                if (end >= 0) {
                    for (int i = 0; i <= end; i++){
                        for (j = 0; j < 8 && DATA[i + j] == CMD[j];) j++;
                        if (j == 8) {
                            mCmdReceive = true;
                            //ObjApp.getInfoView().pushInfo(0, "mcu need update", App.getInstance().getResources().getString(C0060R.string.mcu_need_update), Align.CENTER, 50, -1, 60, 2147483392);
                            //SystemProperties.set("sys.fyt.mcu_need_update", "1");
                            HandlerMain.mcuNeedUpdate(1);
                            return;
                        }

                    }
                    System.arraycopy(DATA, end + 1, DATA, 0, 7);
                    mSizeExtra = 7;
                }
            }
        }
    }*/

    public class TickLock {
        private long cur;
        private long last;

        public boolean unlock(int ms) {
            this.cur = SystemClock.uptimeMillis();
            if (this.cur - this.last < ((long) ms)) {
                return false;
            }
            this.last = this.cur;
            return true;
        }

        public int pass() {
            this.cur = SystemClock.uptimeMillis();
            return (int) (this.cur - this.last);
        }

        public void reset() {
            this.last = SystemClock.uptimeMillis();
        }

        public void resetToZero() {
            this.last = 0;
        }
    }

    public static void threadSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * The serial reads do not always correspond precisely to full MCU data frames.
     * This function reassembles the data that is read from the MCU into command
     * frames.
     *
     * When it identifies a complete command frame, it is sent on to onHandle, not
     * including header or checksum.
     */
    public void onReceive(byte[] data) {
        if (data == null || data.length <= 0) {
            threadSleep(100);
            return;
        }
        byte checksum;
        int i;
        //this.mRecieverExtra.onReceive(data);
        if (mSize + data.length > 1024) {
            mSize = 0;
            mFrameStartIndex = 0;
            mChecksumIndex = 0;
        }
        System.arraycopy(data, 0, DATA_MISC, mSize, data.length);
        mSize += data.length;
        if (mChecksumIndex != 0) {
            if (mChecksumIndex < mSize) {
                checksum = DATA_MISC[2];
                for (i = 3; i < mChecksumIndex; i++) {
                    checksum = (byte) (DATA_MISC[i] ^ checksum);
                }
                if (checksum == DATA_MISC[mChecksumIndex]) {
                    onHandle(DATA_MISC, 4, mChecksumIndex - 4);
                    mFrameStartIndex = mChecksumIndex + 1;
                } else {
                    mFrameStartIndex = 2;
                }
                mChecksumIndex = 0;
            } else {
                return;
            }
        }
        int end = mSize - 3;
        while (mFrameStartIndex < end) {
            if (DATA_MISC[mFrameStartIndex] == (byte) -120 && DATA_MISC[mFrameStartIndex + 1] == (byte) 85) {
                mChecksumIndex = ((DATA_MISC[mFrameStartIndex + 2] << 8) & 65280) | (DATA_MISC[mFrameStartIndex + 3] & 255);
                if (mChecksumIndex > 512) {
                    mChecksumIndex = 0;
                } else {
                    mChecksumIndex += mFrameStartIndex + 4;
                    if (mChecksumIndex >= mSize) {
                        mChecksumIndex -= mFrameStartIndex;
                        break;
                    }
                    checksum = DATA_MISC[mFrameStartIndex + 2];
                    for (i = mFrameStartIndex + 3; i < mChecksumIndex; i++) {
                        checksum = (byte) (DATA_MISC[i] ^ checksum);
                    }
                    if (checksum == DATA_MISC[mChecksumIndex]) {
                        onHandle(DATA_MISC, mFrameStartIndex + 4, (mChecksumIndex - mFrameStartIndex) - 4);
                        mFrameStartIndex = mChecksumIndex;
                    } else {
                        mFrameStartIndex++;
                    }
                    mChecksumIndex = 0;
                }
            }
            mFrameStartIndex++;
        }
        if (mFrameStartIndex != 0) {
            mSize -= mFrameStartIndex;
            if (mSize != 0) {
                System.arraycopy(DATA_MISC, mFrameStartIndex, DATA_MISC, 0, mSize);
            }
            mFrameStartIndex = 0;
        }
    }

    public static int makeInt(byte high, byte mid, byte low) {
        return (((high << 16) & 16711680) | ((mid << 8) & 65280)) | (low & 255);
    }

    private void onHandle(byte[] data, int start, int length) {
        if (!DataMain.sMcuActived) {
            DataMain.sMcuActived = true;
        }
        String inCommand = "0x";
        String hexStr = "";
        for (int ii = start; ii<start+length; ii++){
            hexStr = Integer.toHexString(data[ii]);
            while (hexStr.length() < 2) hexStr = "0"+hexStr;
            inCommand += hexStr;
        }
        Log.d("MCUSERIAL", "COMMAND _IN: "+inCommand);
        int[] postData;
        int i;
        int j;
        int end;
        int value;
        Object mData;
        int index;
        StringBuilder stringBuilder;
        switch (data[start]) {
            case (byte) -112: // 144 / 0x90
                /* TODO canbus shit: if (DataCanbus.MCLS[FinalCanbus.U_CANBUS_FRAME_TO_MTU] != null) {
                    postData = new int[(length - 1)];
                    i = 0;
                    j = start + 1;
                    end = length - 1;
                    while (i < end) {
                        postData[i] = data[j];
                        i++;
                        j++;
                    }
                    ModuleCallbackList.update(DataCanbus.MCLS, (int) FinalCanbus.U_CANBUS_FRAME_TO_MTU, postData);
                    return;
                }*/
                return;
            case (byte) -76: // 180 / 0xB4
                Log.d("MCU", "GSENSOR: ignore");
                return;
            case (byte) -64: // 192 / 0xC0
                HandlerSteer.mcuKeyEnable(data[start + 1] & 1);
                return;
            case (byte) -61: // 195 / 0xC3
                int studyKeyLength = length - 1;
                mData = new byte[studyKeyLength];
                System.arraycopy(data, start + 1, mData, 0, studyKeyLength);
                HandlerSteer.onMcuKeyStudied((byte[])mData);
                return;
            case (byte) -60: // 196 / 0xC4
                HandlerSteer.onMcuKeyEvent(data[start + 1] & 255, (data[start + 2] & 1) == 1 ? 0 : 1);
                return;
            case (byte) -32: // 224 / 0xE0
                //HandlerMain.mcuVer(new String(data, start + 1, length - 1));
                return;
            case (byte) -29: // 227 / 0xE3
                /* TODO canbus shit: Print.screenHex(data, start, length);
                DataCanbus.sCanbus.onHandle(data, start + 1, length - 1);
                if (DataMain.sCanbusFrame2Ui && DataCanbus.MCLS[FinalCanbus.U_CANBUS_FRAME_TO_UI] != null) {
                    postData = new int[(length - 1)];
                    i = 0;
                    j = start + 1;
                    end = length - 1;
                    while (i < end) {
                        postData[i] = data[j];
                        i++;
                        j++;
                    }
                    ModuleCallbackList.update(DataCanbus.MCLS, (int) FinalCanbus.U_CANBUS_FRAME_TO_UI, postData);
                    return;
                }*/
                return;
            case (byte) -24: // 232 / 0xE8
                // useless sound junk: HandlerSound.balFadeSrc(data[start + 1] & 255, data[start + 2] & 255);
                return;
            case (byte) -23: // 233 / 0xE9
                /* TODO canbus shit: Print.screenHex(data, start, length);
                mData = new byte[(length - 1)];
                System.arraycopy(data, start + 1, mData, 0, length - 1);
                DataAnalysis.sCanbus.onReceive(mData); */
                return;
            case (byte) -22: // 234 / 0xEA
                /* TODO MCU update
                switch (ToolkitMisc.makeInt(data[start + 1], data[start + 2])) {
                    case 61443:
                        if (this.mStm32NeedReupgradeTip == 0) {
                            this.mStm32NeedReupgradeTip = 1;
                            ObjApp.getInfoView().pushInfo(0, "stm32_need_upgrade", "STM32", Align.CENTER, 30, -1, 40, 2147483392);
                            return;
                        }
                        return;
                    case 61459:
                        if (this.mTwSpiOsdUpdating == 0) {
                            this.mTwSpiOsdUpdating = 1;
                            HandlerMcuSpiUpgrade.upgradeCmd("/system/mcu/TwSpiOsd.bin", 0);
                            HandlerUI.getInstance().postDelayed(new C00653(), 5000);
                            return;
                        }
                        return;
                    case 61475:
                        if (this.mTwSpiMcuUpdating == 0) {
                            this.mTwSpiMcuUpdating = 1;
                            HandlerMcuSpiUpgrade.upgradeCmd("/system/mcu/TwSpiMcu.bin", 1);
                            HandlerUI.getInstance().postDelayed(new C00664(), 5000);
                            return;
                        }
                        return;
                    default:
                        return;
                } */
                return;
            case (byte) -19: // 237 / 0xED
                //HandlerMain.spiOsdVer(new String(data, start + 1, length - 1));
                return;
            case (byte) -18: // 238 / 0xEE
                //HandlerMain.spiMcuVer(new String(data, start + 1, length - 1));
                return;
            case (byte) -17: // 239 / 0xEF
                String str = new String(data, start + 1, length - 1);
                if ("_ivd".equals(str)) {
                    //HandlerRadio.moduleId(3);
                    return;
                }
                /*if (this.mIsTipMcuError == 0) {
                    this.mIsTipMcuError = 1;
                    ObjApp.getInfoView().pushInfo(0, "mcu need update", "MCU", Align.CENTER, 50, -1, 60, 2147483392);
                    SystemProperties.set("sys.fyt.mcu_notmatch_hardware", str);
                    HandlerRemove.getInstance().postDelayed(new C00675(), 6000);
                }*/
                HandlerMain.tip(7);
                return;
            case (byte) 1:
                switch (data[start + 1]) {
                    case (byte) -95:
                        //DataDvd.sCmd.cmdFromMcu(data, start + 2, length - 2);
                        return;
                    case (byte) -45:
                        Log.d("RADIO", "B-45");
                        byte B0 = data[start + 2];
                        byte B1 = data[start + 3];
                        index = (((B0 & 128) >> 6) | ((B1 & 128) >> 7)) << 1;
                        this.ch_d3[index] = (char) (B0 & 127);
                        this.ch_d3[index + 1] = (char) (B1 & 127);
                        if (index == 6) {
                            HandlerRadio.psText(new String(this.ch_d3, 0, 8));
                            return;
                        }
                        return;
                    case (byte) 0:
                        onHandleMain(data, start + 2, length - 2);
                        return;
                    case (byte) 1:
                        //onHandleDvd(data, start + 2, length - 2);
                        return;
                    case (byte) 2:
                        //onHandleTv(data, start + 2, length - 2);
                        return;
                    case (byte) 3:
                        onHandleRadio(data, start + 2, length - 2);
                        return;
                    case (byte) 7:
                        HandlerSteer.keyAct(0xc);
                        return;
                    case (byte) 8:
                        //onHandleIpod(data, start + 2, length - 2);
                        return;
                    case (byte) 10:
                        //onHandleSound(data, start + 2, length - 2);
                        return;
                    case (byte) 11:
                        switch (data[start + 2]) {
                            case (byte) 96:
                                switch (data[start + 3]) {
                                    case (byte) 0:
                                        HandlerMain.ledColor(0);
                                        return;
                                    case (byte) 1:
                                        HandlerMain.ledColor(1);
                                        return;
                                    case (byte) 2:
                                        HandlerMain.ledColor(2);
                                        return;
                                    case (byte) 3:
                                        HandlerMain.ledColor(3);
                                        return;
                                    case (byte) 4:
                                        HandlerMain.ledColor(4);
                                        return;
                                    case (byte) 5:
                                        HandlerMain.ledColor(5);
                                        return;
                                    case (byte) 6:
                                        HandlerMain.ledColor(6);
                                        return;
                                    default:
                                        return;
                                }
                            case (byte) 97:
                                HandlerMain.rollKeyType(DataMain.sRollKeyEnable, data[start + 3] & 1);
                                return;
                            case (byte) 98:
                                DataMain.sRollKeyEnable = data[start + 3] & 1;
                                return;
                            default:
                                return;
                        }
                    case (byte) 12:
                        switch (data[start + 2]) {
                            case (byte) 2:
                                if (DataMain.sBackcarRadarEnableType == 0) {
                                    //HandlerMain.backcarRadarEnable(data[start + 3] & 1);
                                    return;
                                }
                                return;
                            default:
                                return;
                        }
                    case (byte) 13:
                        return;
                    case (byte) 16:
                        onHandleSteer(data, start + 2, length - 2);
                        return;
                    case (byte) 56:
                        switch (data[start + 2]) {
                            case (byte) 32:
                                Log.d("MCU", "mediaPlayPause");
                                //TODO ToolkitApp.mediaPlayPause(DataMain.sAppId);
                                return;
                            case (byte) 48:
                                switch (data[start + 3]) {
                                    case (byte) 0:
                                        Log.d("MCU", "mediaPrev");
                                        //TODO ToolkitApp.mediaPrev(DataMain.sAppId);
                                        return;
                                    case (byte) 1:
                                        Log.d("MCU", "mediaNext");
                                        //TODO ToolkitApp.mediaNext(DataMain.sAppId);
                                        return;
                                    default:
                                        return;
                                }
                            case (byte) 64:
                                int num = data[start + 3] & 255;
                                if (num <= 9) {
                                    Log.d("MCU", "mediaKeyNum: "+num);
                                    //TODO ToolkitApp.mediaKeyNum(DataMain.sAppId, num);
                                }
                                switch (num) {
                                    case 10:
                                    case 11:
                                    case 12:
                                        return;
                                    default:
                                        return;
                                }
                            case (byte) 80:
                                switch (data[start + 3]) {
                                    case (byte) 0:
                                    case (byte) 1:
                                    case (byte) 2:
                                    case (byte) 3:
                                    case (byte) 4:
                                    case (byte) 5:
                                    case (byte) 6:
                                    case (byte) 7:
                                    case (byte) 8:
                                    case (byte) 9:
                                        Log.d("MCU", "mediaKeyNum: "+data[start+3]);
                                        //TODO ToolkitApp.mediaKeyNum(DataMain.sAppId, data[start + 3] & 255);
                                        return;
                                    case (byte) 10:
                                    case (byte) 11:
                                    case (byte) 12:
                                    case (byte) 13:
                                        return;
                                    case (byte) 16:
                                        Log.d("MCU", "mediaPlayPause");
                                        //TODO ToolkitApp.mediaPlayPause(DataMain.sAppId);
                                        return;
                                    case (byte) 17:
                                        Log.d("MCU", "mediaStop");
                                        //TODO ToolkitApp.mediaStop(DataMain.sAppId);
                                        return;
                                    case (byte) 18:
                                        Log.d("MCU", "mediaPrev");
                                        //TODO ToolkitApp.mediaPrev(DataMain.sAppId);
                                        return;
                                    case (byte) 19:
                                        Log.d("MCU", "mediaNext");
                                        //TODO ToolkitApp.mediaNext(DataMain.sAppId);
                                        return;
                                    case (byte) 20:
                                        Log.d("MCU", "mediaFB");
                                        //TODO ToolkitApp.mediaFB(DataMain.sAppId);
                                        return;
                                    case (byte) 21:
                                        Log.d("MCU", "mediaFF");
                                        //TODO ToolkitApp.mediaFF(DataMain.sAppId);
                                        return;
                                    default:
                                        return;
                                }
                            default:
                                return;
                        }
                    case (byte) 118:
                        switch (data[start + 2]) {
                            case (byte) 0:
                                HandlerMain.trunkState((data[start + 3] & 1) == 0 ? 1 : 0);
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }
            case (byte) 3:
                /* TODO amp stuff: receiver = DataDev.sReceiverMcu2Amp;
                if (receiver != null) {
                    receiver.onReceive(data, start + 1, length - 1);
                    return;
                }*/
                return;
            case (byte) 5:
                if (data[start + 1] == (byte) 33) {
                    switch (data[start + 2]) {
                        case (byte) 0:
                            HandlerMain.brightLevelCmd(-2);
                            return;
                        case (byte) 1:
                            HandlerMain.brightLevelCmd(-1);
                            return;
                        default:
                            return;
                    }
                }
                return;
            case (byte) 6:
                switch (data[start + 1]) {
                    case (byte) 2:
                        /*if (DataSound.sDefVolBootType == 0) {
                            HandlerSound.defVol(data[start + 2] & 255);
                            return;
                        }*/
                        return;
                    case (byte) 32:
                        HandlerMain.resetArmLaterCmd(data[start + 2] & 255);
                        return;
                    default:
                        return;
                }
            case (byte) 7:
                //HandlerMain.mcuSerial(new String(data, start + 3, length - 3));
                return;
            case (byte) 8:
                HandlerMain.panelKeyType(data[start + 1] & 15);
                HandlerMain.panelKeyTypeCnt((data[start + 1] >> 4) & 15);
                return;
            case (byte) 10:
                int B02 = data[start + 1];
                DataMain.sMcu0x0AFlag = B02;
                HandlerMain.mcuPowerOption((B02 >> 6) & 1);
                /*if (DataSound.sDefVolBootEnableType == 0) {
                    HandlerSound.defVolOnBoot((B02 >> 3) & 1);
                }*/
                if (DataMain.sMcuOn == 0) {
                    HandlerMain.accOn(1);
                    return;
                }
                return;
            case (byte) 11:
                switch (data[start + 1]) {
                    case (byte) 0:
                        if (data[start + 2] == (byte) 2) {
                            //checkTvSignal();
                            return;
                        }
                        return;
                    default:
                        return;
                }
            case (byte) 12:
                switch (data[start + 1]) {
                    case (byte) 0:
                        //HandlerMain.hostbackcarEnable((data[start + 2] & 1) == 0 ? 1 : 0);
                        return;
                    case (byte) 1:
                        if (DataMain.sBackcarTrackEnableType == 0) {
                            //HandlerMain.backcarTrackEnable((data[start + 2] & 1) == 0 ? 1 : 0);
                            return;
                        }
                        return;
                    case (byte) 2:
                        HandlerMain.lampletOnBoot(data[start + 2] & 1);
                        return;
                    case (byte) 3:
                        HandlerMain.lampOnAlawys(data[start + 2] & 1);
                        return;
                    case (byte) 4:
                        HandlerMain.radarParkEnable(data[start + 2] & 1);
                        return;
                    case (byte) 5:
                        HandlerMain.panoramaOn(data[start + 2] & 1);
                        return;
                    case (byte) 6:
                        HandlerMain.backcarType(data[start + 2] & 1);
                        return;
                    case (byte) 7:
                        //HandlerTpms.tpmsEnable(data[start + 2] & 1);
                        return;
                    case (byte) 8:
                        HandlerMain.cutAccDelayCloseScreen(data[start + 2] & 1);
                        return;
                    case (byte) 9:
                        HandlerMain.panelKeyEnable((data[start + 2] & 1) == 0 ? 1 : 0);
                        return;
                    case (byte) 10:
                        //TODO amp: HandlerAmp.ampStatus((data[start + 2] & 1) == 0 ? 0 : 1);
                        return;
                    case (byte) 11:
                        HandlerMain.startStopEnable((data[start + 2] & 1) == 0 ? 0 : 1);
                        return;
                    default:
                        return;
                }
            case (byte) 33:
                Log.d("RADIO", "B33");
                int band = data[start + 1] & 255;
                int step = data[start + 2] & 255;
                int freqMin = makeInt(data[start + 3], data[start + 4], data[start + 5]);
                int freqMax = makeInt(data[start + 6], data[start + 7], data[start + 8]);
                if (band <= 2) {
                    step /= 10;
                    freqMin /= 10;
                    freqMax /= 10;
                }
                int stepCnt = (freqMax - freqMin) / step;
                if (HandlerRadio.sExtraInfoByMcu != 0) {
                    int[] value2 = new int[4];
                    value2[0] = freqMin;
                    HandlerRadio.sFreqMin = freqMin;
                    value2[1] = freqMax;
                    HandlerRadio.sFreqMax = freqMax;
                    value2[2] = step;
                    HandlerRadio.sFreqStepLen = step;
                    value2[3] = stepCnt;
                    HandlerRadio.sFreqStepCnt = stepCnt;
                    //ModuleCallbackList.update(DataRadio.MCLS, 16, value2);
                    return;
                }
                return;
            case (byte) 65:
                /*index = data[start + 1] & 255;
                int channelCnt = data[start + 2] & 255;
                int freq = (((data[start + 3] << 16) & 16711680) | ((data[start + 4] << 8) & 65280)) | ((data[start + 5] << 0) & 255);
                if (index > 0) {
                    DataTv.CHANNEL_FREQ[index - 1] = freq;
                }
                if (channelCnt == 1) {
                    HandlerTv.channelCnt(0);
                }
                HandlerTv.channelCnt(channelCnt);*/
                return;
            case (byte) 66:
                //HandlerTv.channel(data[start + 1] & 255);
                return;
            case (byte) 67:
                //HandlerTv.area(data[start + 1] & 255);
                return;
            case (byte) 80:
                Log.d("RADIO", "RDS Channel Text -- skip");
                /*TODO int channel = data[start + 1] - 1;
                if (channel >= 0 && channel < 30) {
                    stringBuilder = new StringBuilder(32);
                    i = start + 2;
                    end = start + length;
                    while (i < end && data[i] != (byte) 0) {
                        stringBuilder.append((char) data[i]);
                        i++;
                    }
                    String value3 = stringBuilder.toString();
                    if (channel >= 18) {
                        channel -= 18;
                        if (HandlerRadio.RDS_CHANNEL_TEXT_AM[channel] == null || !value3.contentEquals(HandlerRadio.RDS_CHANNEL_TEXT_AM[channel])){
                            HandlerRadio.RDS_CHANNEL_TEXT_AM[channel] = value3;
                            //ModuleCallbackList.update(DataRadio.MCLS, 14, channel + 0, value3);
                            return;
                        }
                        return;
                    } else if (HandlerRadio.RDS_CHANNEL_TEXT_FM[channel] == null || !value3.contentEquals(HandlerRadio.RDS_CHANNEL_TEXT_FM[channel])) {
                        HandlerRadio.RDS_CHANNEL_TEXT_FM[channel] = value3;
                        //ModuleCallbackList.update(DataRadio.MCLS, 14, 65536 + channel, value3);
                        return;
                    } else {
                        return;
                    }
                }*/
                return;
            case (byte) 81:
                stringBuilder = new StringBuilder(length);
                i = start + 1;
                end = start + length;
                while (i < end && data[i] != (byte) 0) {
                    stringBuilder.append((char) data[i]);
                    i++;
                }
                HandlerRadio.rdsText(stringBuilder.toString());
                return;
            case (byte) 97:
                /*TODO canbus: value = ((data[start + 1] << 8) & 65280) | (data[start + 2] & 255);
                if (value == 65535) {
                    HandlerCanbus.tip(0);
                    return;
                } else if (value == 65534) {
                    HandlerCanbus.canbusId(0);
                    return;
                } else if (value == 57344) {
                    HandlerCanbus.canbusId(FinalCanbus.CAR_E000_ATS);
                    return;
                } else {
                    this.mCanbusType = value;
                    if (value == 0) {
                        HandlerCanbus.canbusId(0);
                        return;
                    }
                    return;
                }*/
                return;
            case (byte) 98:
                /*TODO canbux: int cnt = (length - 1) / 2;
                if (cnt > 1000) {
                    cnt = 1000;
                }
                for (i = 0; i < cnt; i++) {
                    DataCanbus.MCU_CANBUS_SUPPORT[i] = ((data[(start + 1) + (i << 1)] << 8) & 65280) | (data[(start + 2) + (i << 1)] & 255);
                }
                HandlerCanbus.mcuCanbusSupportCnt(0);
                HandlerCanbus.mcuCanbusSupportCnt(cnt);*/
                return;
            case (byte) 99:
                Log.d("STEER", "RADAR 99");
                //onHandleRadar(data, start + 1, length - 1);
                return;
            case (byte) 100:
                HandlerMain.steerAngle(data[start + 1] & 255);
                return;
            case (byte) 101:
                /*TODO int tempOut = ToolkitMisc.makeInt(data[start + 1], data[start + 2]);
                if (tempOut > 600) {
                    HandlerMain.tempOut(tempOut);
                    return;
                }*/
                return;
            case (byte) 103:
                /* TODO canbus: DataAnalysis.sMcuBand = ToolkitMisc.makeInt(data[start + 1], data[start + 2], data[start + 3]);
                HandlerAnalysis.matchCanbusBand();*/
                return;
            case (byte) 117:
                switch (data[start + 1]) {
                    case (byte) 4:
                        //HandlerMain.updateMcuErrorCode(null);
                        return;
                    case (byte) 16:
                        synchronized (DataMain.MCU_ERROR_CODE) {
                            end = start + length;
                            for (i = start + 2; i < end; i += 2) {
                                value = ((data[i] << 8) & 65280) | (data[i + 1] & 255);
                                DataMain.MCU_ERROR_CODE.put(value, value);
                            }
                        }
                        return;
                    default:
                        return;
                }
            case (byte) 122:
                switch (data[start + 1]) {
                    case (byte) 0:
                        HandlerMain.lampletColorCtrl(data[start + 2] & 1);
                        return;
                    case (byte) 1:
                    case (byte) 2:
                        return;
                    case (byte) 3:
                        HandlerMain.lampletCleanOn(data[start + 2] & 1);
                        return;
                    default:
                        return;
                }
            case (byte) 123:
                switch (data[start + 2]) {
                    case (byte) 0:
                        HandlerMain.ambientLightOn(data[start + 3] & 1);
                        return;
                    case (byte) 1:
                        return;
                    case (byte) 2:
                        HandlerMain.ambientLightColor(data[start + 3] & 255);
                        return;
                    default:
                        return;
                }
        }
        Log.d("MCUSERIAL", "COMMAND NOT HANDLED: "+inCommand);
    }

    // 0x0100xxxxxx input
    private void onHandleMain(byte[] data, int start, int length) {
        int i = 1;
        switch (data[start]) {
            case (byte) -120:
                mSleepTick = 0;
                HandlerMain.mcuOn(0);
                return;
            case (byte) -119:
                /* TODO switch (data[start + 1]) {
                    case (byte) 83:
                        SystemProperties.set("sys.fyt.sleeping", "1");
                        Log.d("sleep", "0x89 0x53 STEP1 + time: = " + SystemClock.uptimeMillis() + " isWifiClosed = " + ToolkitApp.isWifiClosed() + " mSleepTick = " + mSleepTick);
                        if (SystemProperties.getInt("sys.sleeptimes", 0) > DataMain.rebootTimes) {
                            SystemProperties.set("sys.fyt.sleeping", "0");
                            ToolkitApp.reboot();
                            return;
                        } else if (DataChip.getChipId() != 4 || ToolkitPlatform.canSleep()) {
                            int i2 = mSleepTick;
                            mSleepTick = i2 + 1;
                            if (i2 == 2) {
                                HandlerMain.armSleepWakeup(0);
                                if (DataChip.getChipId() == 4 && DataMain.sSleepWakeup == 0) {
                                    ChipSofia.setMode(1);
                                }
                            }
                            if (DataMain.sOnResetState == 0 || DataMain.sMcuPowerOption != 0) {
                                if (!ToolkitApp.isWifiClosed()) {
                                    if (!"open".equals(SystemProperties.get("persist.sys.wifi.states"))) {
                                        SystemProperties.set("persist.sys.wifi.states", "open");
                                    }
                                    ToolkitApp.closeWifiWhenSleep();
                                } else if (mSleepTick > 4) {
                                    ToolkitDev.writeMcu(1, 170, 95);
                                    Log.d("sleep", "0x89 0x53 REVEIVER MCU " + SystemClock.uptimeMillis());
                                }
                                ObjApp.getMsgView().msg("sleep 0x89 0x53");
                                Log.d("sleep", "0x89 0x53 STEP2 + time: = " + SystemClock.uptimeMillis());
                                return;
                            }
                            if (!(ToolkitApp.isWifiClosed() || "open".equals(SystemProperties.get("persist.sys.wifi.states")))) {
                                SystemProperties.set("persist.sys.wifi.states", "open");
                            }
                            if (mSleepTick > 4) {
                                ToolkitDev.writeMcu(1, 170, 95);
                                Log.d("sleep", "0x89 0x53 REVEIVER MCU " + SystemClock.uptimeMillis());
                                return;
                            }
                            return;
                        } else {
                            SystemProperties.set("sys.fyt.sleeping", "0");
                            return;
                        }
                    case (byte) 84:
                        mSleepTick = 0;
                        ToolkitDev.writeMcu(1, 170, 97);
                        ObjApp.getMsgView().msg("sleep 0x89 0x54");
                        Log.d("sleep", "0x89 0x54 STEP3 + time: = " + SystemClock.uptimeMillis());
                        return;
                    case (byte) 85:
                        ObjApp.getMsgView().msg("0x89 0x55");
                        Log.d("sleep", "0x89 0x55 canSleep = " + ToolkitPlatform.canSleep());
                        if (ToolkitPlatform.canSleep()) {
                            if (DataChip.getChipId() == 4) {
                                if (this.mLock8955.unlock(1000)) {
                                    ToolkitApp.threadSleep(2000);
                                    ToolkitDev.writeMcu(1, 170, 98);
                                    ToolkitApp.threadSleep(500);
                                    this.mLock8955.reset();
                                } else {
                                    return;
                                }
                            } else if (DataChip.getChipId() != 2) {
                                ToolkitDev.writeMcu(1, 170, 98);
                                ToolkitApp.threadSleep(500);
                            }
                            SystemProperties.set("sys.sleeptimes", (SystemProperties.getInt("sys.sleeptimes", 0) + 1));
                            ToolkitPlatform.muteAmp(0);
                            ToolkitApp.killAppWhenSleep();
                            ToolkitApp.go2Sleep();
                            DataMain.sMcuActived = false;
                            ObjApp.getMsgView().msg("0x89 0x55 gotoSleep");
                            Log.d("sleep", "0x89 0x55 gotoSleep");
                        }
                        SystemProperties.set("sys.fyt.sleeping", "0");
                        return;
                    default:
                        return;
                }*/return;
            case (byte) -112:
                return;
            case (byte) -110:
                /*if (DataSound.sBackCarMuteType == 0) {
                    if ((data[start + 1] & 1) != 0) {
                        i = 0;
                    }
                    HandlerSound.backcarMute(i);
                    return;
                }*/
                return;
            case (byte) -106:
                HandlerMain.lampletByTime(data[start + 1] & 1);
                return;
            case (byte) -102:
                HandlerRadio.sensityFm(data[start + 1] & 255);
                return;
            case (byte) -100:
                HandlerRadio.sensityAm(data[start + 1] & 255);
                return;
            case (byte) -98:
                HandlerRadio.autoSensity(data[start + 1] & 1);
                return;
            case (byte) -96:
                //SystemProperties.set("sys.fyt.mcu_cus_id", new StringBuilder(String.valueOf(data[start + 1] & 255)).toString());
                return;
            case (byte) -69:
                HandlerRadio.power(data[start + 1] & 1);
                return;
            case (byte) -65:
                HandlerMain.mcuKeyair(data[start + 1] & 255);
                return;
            case (byte) -16:
                /* TODO canbus: switch (data[start + 1]) {
                    case (byte) 2:
                        HandlerCanbus.update((int) FinalCanbus.U_CAR_BT_ON, 0);
                        return;
                    case (byte) 3:
                        HandlerCanbus.update((int) FinalCanbus.U_CAR_BT_ON, 1);
                        return;
                    default:
                        return;
                }*/
                return;
            case (byte) -4:
                HandlerMain.mcuKeyairshow(1);
                return;
            case (byte) 0:
                switch (data[start + 1]) {
                    case (byte) 0:
                        HandlerMain.mcuOn(0);
                        return;
                    case (byte) 1:
                        HandlerMain.mcuOn(1);
                        return;
                    case (byte) 2:
                        HandlerMain.blackScreen(1);
                        return;
                    case (byte) 3:
                        HandlerMain.blackScreen(0);
                        return;
                    case (byte) 4:
                        /*if (DataSound.sTickLockMute.unlock(500)) {
                            HandlerSound.muteSrc(1);
                            return;
                        }*/
                        return;
                    case (byte) 5:
                        /*if (DataSound.sTickLockMute.unlock(500)) {
                            HandlerSound.muteSrc(0);
                            return;
                        }*/
                        return;
                    case (byte) 16:
                        if (DataMain.sStandbyType == 0) {
                            HandlerMain.standby(1);
                            return;
                        }
                        return;
                    case (byte) 17:
                        return;
                    case (byte) 18:
                        HandlerMain.lamplet(0); // headlights off?
                        return;
                    case (byte) 19:
                        HandlerMain.lamplet(1); // headlights on?
                        return;
                    case (byte) 32:
                        return;
                    case (byte) 33:
                        // This unlock thing seems to just make sure that the writes don't happen
                        // more than once in 2 seconds.
                        if (this.mLockUiOk.unlock(2000)) {
                            ToolkitDev.writeMcu(1, 170, 96);
                            ToolkitDev.writeMcu(1, 0, 0);
                            return;
                        }
                        return;
                    case (byte) 34:
                        return;
                    case (byte) 49:
                        HandlerMain.accOn(1);
                        return;
                    case (byte) 50:
                        HandlerMain.accOn(0);
                        return;
                    case (byte) 112:
                        /* TODO canbus:
                        DataCanbus.mAnalysisCanbusType = 1;*/
                        return;
                    default:
                        return;
                }
            case (byte) 1:
                return;
            case (byte) 4:
                int state = -1;
                switch (data[start + 1]) {
                    case (byte) 0:
                        state = 16;
                        break;
                    case (byte) 1:
                        state = 18;
                        break;
                    case (byte) 2:
                        state = 17;
                        break;
                    case (byte) 5:
                        state = 21;
                        break;
                    case (byte) 7:
                        state = 23;
                        break;
                    case (byte) 8:
                        state = 24;
                        break;
                    case (byte) 9:
                        state = 25;
                        break;
                    case (byte) 10:
                    case (byte) 14:
                        state = 27;
                        break;
                    case (byte) 21:
                        state = 26;
                        break;
                    case (byte) 32:
                        state = 35;
                        break;
                    case (byte) 33:
                        state = 36;
                        break;
                    case (byte) 34:
                    case (byte) 35:
                        state = 37;
                        break;
                    case (byte) 48:
                        state = 39;
                        break;
                    case (byte) 65:
                        state = 40;
                        break;
                    case (byte) 66:
                        state = 41;
                        break;
                    case (byte) 67:
                        state = 42;
                        break;
                    case (byte) 68:
                        state = 43;
                        break;
                    case (byte) 69:
                        state = 44;
                        break;
                }
                if (state != -1 && DataMain.sMcuStateFrom == 0) {
                    HandlerMain.mcuState(state);
                    return;
                }
                return;
            case (byte) 5:
                return;
            case (byte) 6:
                switch (data[start + 1] & 255) {
                    case 0:
                        HandlerMain.handbrake(0);
                        return;
                    case 1:
                        HandlerMain.handbrake(1);
                        return;
                    case 2:
                        HandlerMain.reserveAction0(1);
                        return;
                    case 3:
                        HandlerMain.reserveAction0(0);
                        return;
                    case 4:
                        HandlerMain.reserveAction1(0);
                        return;
                    case 5:
                        HandlerMain.reserveAction1(1);
                        return;
                    default:
                        return;
                }
            case (byte) 7:
                HandlerSteer.keyAct(0x12);
                return;
            case (byte) 8:
                HandlerMain.mcuKeyBtPhone();
                return;
            case (byte) 9:
                switch (data[start + 1]) {
                    case (byte) 1:
                        HandlerMain.mcuKeyAudio();
                        return;
                    case (byte) 2:
                        HandlerMain.mcuKeyEq();
                        return;
                    default:
                        return;
                }
            case (byte) 13:
                switch (data[start + 1]) {
                    case (byte) 1:
                        CmdRadio.band(-3);
                        return;
                    case (byte) 2:
                        CmdRadio.band(-2);
                        return;
                    default:
                        return;
                }
            case (byte) 16:
                switch (data[start + 1]) {
                    case (byte) 7:
                        HandlerSteer.keyAct(0xd);
                    case (byte) 11:
                        HandlerMain.mcuKeyPlayer();
                        return;
                    case (byte) 12:
                        // TODO This going to respond by sending a new "mcu state" to the mcu.
                        // TODO the MCU state is one of the MCU_STATE values in CmdMain.class
                        // TODO mcuState of 27 is "NULL", probably a good default value.
                        // TODO I'm not entirely sure what the purpose of mcu state is, possibly
                        // TODO just to determine how key presses are handled.
                        /*if (DataMain.MCU_STATE_STACK.getTop(-1) == 16) {
                            DataMain.MCU_STATE_STACK.pop(-1);
                            HandlerMain.mcuState(DataMain.MCU_STATE_STACK.getTop(27));
                            ToolkitDev.writeMcu(1, 0, mcuState);
                            return;
                        }*/
                        ToolkitDev.writeMcu(1, 0, 27);
                        return;
                    case (byte) 16:
                        HandlerMain.mcuKeyBand();
                        return;
                    case (byte) 32:
                        HandlerSteer.keyAct(0xe);
                        return;
                    default:
                        return;
                }
            case (byte) 17:
                switch (data[start + 1]) {
                    case (byte) -35:
                        HandlerMain.mcuBootOn(1);
                        return;
                    case (byte) 1:
                        HandlerMain.mcuKeyHome();
                        return;
                    case (byte) 6:
                        HandlerMain.mcuKeyBack();
                        return;
                    case (byte) 16:
                        //ModuleCallbackList.update(DataCanbus.MCLS, 1008, null, null, null);
                        return;
                    case (byte) 33:
                        //TODO JumpPage.recentTask();
                        return;
                    case (byte) 34:
                        HandlerMain.mcuAllApps();
                        return;
                    default:
                        return;
                }
            case (byte) 18:
                HandlerMain.lamplet(0);
                return;
            case (byte) 19:
                HandlerMain.lamplet(1);
                return;
            case (byte) 20:
                HandlerMain.brightLevelCmd(-3);
                return;
            case (byte) 22:
                //HandlerMain.cncAuxState(data[start + 1] & 1, (data[start + 1] >> 1) & 1);
                return;
            case (byte) 33:
                switch (data[start + 1]) {
                    case (byte) 0:
                        HandlerMain.mcuKeyEnter();
                        return;
                    case (byte) 1:
                        HandlerMain.mcuKeyRollLeft();
                        return;
                    case (byte) 2:
                        HandlerMain.mcuKeyRollRight();
                        return;
                    case (byte) 3:
                        HandlerMain.mcuKeyLeft();
                        return;
                    case (byte) 4:
                        HandlerMain.mcuKeyRight();
                        return;
                    case (byte) 5:
                        HandlerMain.mcuKeyUp();
                        return;
                    case (byte) 6:
                        HandlerMain.mcuKeyDown();
                        return;
                    case (byte) 7:
                        HandlerSteer.keyAct(0xf);
                        return;
                    case (byte) 8:
                        HandlerSteer.keyAct(0x10);
                        return;
                    case (byte) 9:
                        HandlerSteer.keyAct(0x11);
                        return;
                    case (byte) 18:
                        return;
                    case (byte) 32:
                        HandlerMain.mcuKeyPlay();
                        return;
                    case (byte) 33:
                        HandlerMain.mcuKeyPause();
                        return;
                    case (byte) 48:
                        HandlerMain.mcuKeyEnter0x30();
                        return;
                    case (byte) 49:
                        HandlerMain.mcuKeyLeft0x31();
                        return;
                    case (byte) 50:
                        HandlerMain.mcuKeyRight0x32();
                        return;
                    case (byte) 64:
                        HandlerMain.resetFactory();
                        return;
                    default:
                        return;
                }
            case (byte) 34:
                if (DataMain.sAnyKeyBootType == 0) {
                    HandlerMain.anyKeyBoot(data[start + 1] & 1);
                    return;
                }
                return;
            case (byte) 35:
                switch (data[start + 1]) {
                    case (byte) 2:
                        HandlerMain.carBackcar(0); // reverse off?
                        return;
                    case (byte) 3:
                        HandlerMain.carBackcar(1); // reverse on?
                        return;
                    default:
                        return;
                }
            case (byte) 36:
                if (DataMain.sHandbrakeEnableType == 0) {
                    HandlerMain.handbrakeEnable(data[start + 1] & 1);
                    return;
                }
                return;
            case (byte) 48:
                return;
            case (byte) 66:
                // TODO SystemProperties.set("sys.fyt.mcu_reverse", new StringBuilder(String.valueOf((data[start + 1] >> 0) & 1)).toString());
                return;
            case (byte) 80:
                if (DataMain.sOsdTimeType == 0) {
                    //HandlerMain.osdTime((data[start + 1] >> 0) & 1);
                    return;
                }
                return;
            case (byte) 86:
                /*TODO amp: if (DataSound.sAmpEnableType == 0) {
                    HandlerSound.amp((data[start + 1] >> 0) & 1);
                    return;
                }*/
                return;
            case (byte) 96:
                HandlerRadio.rdsEnable(data[start + 1] & 1);
                return;
            case (byte) 98:
                HandlerRadio.airLine(data[start + 1] & 1);
                return;
            case (byte) 120:
                return;
            case (byte) 122:
                /*TODO canbus: int value = data[start + 1] & 255;
                if (value == 255) {
                    HandlerCanbus.tip(0);
                    return;
                }
                ObjApp.getMsgView().msg("STEP2 MCU回传的协议ID = " + this.mCanbusType + " 高低配 = " + value);
                HandlerCanbus.canbusId((value << 16) | this.mCanbusType);
                */
                return;
            default:
                return;
        }
    }

    private void onHandleRadio(byte[] data, int start, int length) {
        int i = 1;
        int value;
        switch (data[start]) {
            case Byte.MIN_VALUE:
                switch (data[start + 1]) {
                    case (byte) 0:
                        HandlerRadio.sortType(1);
                        return;
                    case (byte) 1:
                        HandlerRadio.sortType(0);
                        return;
                    default:
                        return;
                }
            case (byte) -126:
                Log.d("MCU", "unknown band/channel");
                switch (data[start + 1]) {
                    case (byte) -95:
                        CmdRadio.band(-2);
                        return;
                    case (byte) -79:
                        CmdRadio.band(-3);
                        return;
                    case (byte) -77:
                    case (byte) 122:
                        CmdRadio.band(-1);
                        return;
                    case (byte) 1:
                        return;
                    case (byte) 2:
                        CmdRadio.selectChannel(0);
                        return;
                    case (byte) 3:
                        CmdRadio.selectChannel(1);
                        return;
                    case (byte) 4:
                        CmdRadio.selectChannel(2);
                        return;
                    case (byte) 5:
                        CmdRadio.selectChannel(3);
                        return;
                    case (byte) 6:
                        CmdRadio.selectChannel(4);
                        return;
                    case (byte) 7:
                        CmdRadio.selectChannel(5);
                        return;
                    case (byte) 35:
                        CmdRadio.search(2);
                        return;
                    case (byte) 48:
                        CmdRadio.freqDown();
                        return;
                    case (byte) 49:
                        CmdRadio.freqUp();
                        return;
                    case (byte) 50:
                        CmdRadio.prevChannel();
                        return;
                    case (byte) 51:
                        CmdRadio.nextChannel();
                        return;
                    case (byte) 52:
                        CmdRadio.stereo();
                        return;
                    case (byte) 54:
                        CmdRadio.save();
                        return;
                    case (byte) 55:
                        CmdRadio.loc();
                        return;
                    case (byte) 65:
                        CmdRadio.seekDown();
                        return;
                    case (byte) 66:
                        CmdRadio.seekUp();
                        return;
                    default:
                        return;
                }
            case (byte) 0:
                Log.d("MCU", "freq");
                int channel = (data[start + 1] & 255) - 1;
                if (channel < 0) {
                    return;
                }
                if (HandlerRadio.sBand >= 0 && HandlerRadio.sBand < 65536) {
                    HandlerRadio.channel(channel + 0);
                    return;
                } else if (HandlerRadio.sBand >= 65536 && HandlerRadio.sBand < 131072) {
                    HandlerRadio.channel(channel + 65536);
                    return;
                } else {
                    return;
                }
            case (byte) 1:
                Log.d("MCU", "freq 1");
                this.RADIO_freq = (data[start + 1] & 255) * 10000;
                return;
            case (byte) 2:
                Log.d("MCU", "freq 2");
                this.RADIO_freq += (data[start + 1] & 255) * 100;
                return;
            case (byte) 3:
                Log.d("MCU", "freq 3");
                this.RADIO_freq += data[start + 1] & 255;
                if (this.RADIO_freq > 100000) {
                    this.RADIO_freq -= 100000;
                    if (this.RADIO_channel >= 1 && this.RADIO_channel <= 18) {
                        this.RADIO_channel--;
                        if (this.RADIO_channel < 18 && HandlerRadio.FREQ_FM[this.RADIO_channel] != this.RADIO_freq) {
                            HandlerRadio.FREQ_FM[this.RADIO_channel] = this.RADIO_freq;
                            //ModuleCallbackList.update(DataRadio.MCLS, 4, this.RADIO_channel + 65536, this.RADIO_freq);
                        }
                    } else if (this.RADIO_channel >= 101 && this.RADIO_channel <= 112) {
                        this.RADIO_channel -= 101;
                        if (this.RADIO_channel < 12 && HandlerRadio.FREQ_AM[this.RADIO_channel] != this.RADIO_freq) {
                            HandlerRadio.FREQ_AM[this.RADIO_channel] = this.RADIO_freq;
                            //ModuleCallbackList.update(DataRadio.MCLS, 4, this.RADIO_channel + 0, this.RADIO_freq);
                        }
                    }
                    //EventRadio.NE_RADIO_LIST.onNotify();
                    return;
                }
                HandlerRadio.freq(this.RADIO_freq);
                return;
            case (byte) 5:
                Log.d("MCU", "B5");
                switch (data[start + 1]) {
                    case (byte) 0:
                        HandlerRadio.searchState(0);
                        return;
                    case (byte) 1:
                        HandlerRadio.searchState(1);
                        return;
                    case (byte) 2:
                        HandlerRadio.searchState(2);
                        return;
                    case (byte) 3:
                        HandlerRadio.searchState(3);
                        return;
                    case (byte) 16:
                        HandlerRadio.scan(1);
                        HandlerRadio.stereo(0);
                        return;
                    case (byte) 17:
                        HandlerRadio.scan(1);
                        HandlerRadio.stereo(1);
                        return;
                    default:
                        return;
                }
            case (byte) 6:
                Log.d("MCU", "B6");
                int band = data[start + 1] & 255;
                if (band >= 10) {
                    band = (band - 10) + 0;
                } else {
                    band += 65536;
                }
                HandlerRadio.band(band);
                if (this.RADIO_band != band) {
                    this.RADIO_band = band;
                    int i2 = 0;
                    while (i2 < 12) {
                        if (HandlerRadio.FREQ_AM[i2] < 500 || HandlerRadio.FREQ_AM[i2] > 1800) {
                            ToolkitDev.writeMcu(1, 35, i2 + 101);
                        }
                        i2++;
                    }
                    i2 = 0;
                    while (i2 < 18) {
                        if (HandlerRadio.FREQ_FM[i2] < 6500 || HandlerRadio.FREQ_FM[i2] > 10800) {
                            ToolkitDev.writeMcu(1, 35, i2 + 1);
                        }
                        i2++;
                    }
                    ToolkitDev.writeMcu(1, 3, 19);
                    return;
                }
                return;
            case (byte) 7:
                Log.d("MCU", "B7");
                HandlerRadio.loc(data[start + 1] & 1);
                return;
            case (byte) 8:
            case (byte) 24:
                value = data[start + 1] & 255;
                if (value > 1) {
                    return;
                }
                /*TODO if (DataRadio.sScan != 0) {
                    HandlerRadio.scan(0);
                    HandlerRadio.stereo(0);
                    return;
                }*/
                HandlerRadio.scan(0);
                if (value != 0) {
                    i = 0;
                }
                HandlerRadio.stereo(i);
                return;
            case (byte) 16:
                this.RADIO_channel = data[start + 1] & 255;
                return;
            case (byte) 33:
                HandlerRadio.ptyId(data[start + 1] & 255);
                return;
            case (byte) 34:
                value = data[start + 1] & 255;
                HandlerRadio.rdsAfEnable(value & 1);
                HandlerRadio.rdsTa((value >> 1) & 1);
                HandlerRadio.rdsTp((value >> 2) & 1);
                HandlerRadio.rdsTaEnable((value >> 3) & 1);
                HandlerRadio.rdsPiSeek((value >> 4) & 1);
                HandlerRadio.rdsTaSeek((value >> 5) & 1);
                HandlerRadio.rdsPtySeek((value >> 6) & 1);
                return;
            case (byte) 48:
                HandlerRadio.area((data[start + 1] & 255) - 1);
        }
    }

    private void onHandleSteer(byte[] data, int start, int length) {
        switch (data[start]) {
            case Byte.MIN_VALUE:
                HandlerSteer.detect(data[start + 1]);
                return;
            case (byte) 0:
                HandlerSteer.adc(data[start + 1] & 255);
                return;
            case (byte) 1:
                HandlerSteer.keyAdc(0, data[start + 1] & 255);
                return;
            case (byte) 2:
                HandlerSteer.keyAdc(1, data[start + 1] & 255);
                return;
            case (byte) 3:
                HandlerSteer.keyAdc(2, data[start + 1] & 255);
                return;
            case (byte) 4:
                HandlerSteer.keyAdc(3, data[start + 1] & 255);
                return;
            case (byte) 5:
                HandlerSteer.keyAdc(4, data[start + 1] & 255);
                return;
            case (byte) 6:
                HandlerSteer.keyAdc(6, data[start + 1] & 255);
                return;
            case (byte) 7:
                HandlerSteer.keyAdc(5, data[start + 1] & 255);
                return;
            case (byte) 8:
                HandlerSteer.keyAdc(7, data[start + 1] & 255);
                return;
            case (byte) 9:
                HandlerSteer.keyAdc(8, data[start + 1] & 255);
                return;
            case (byte) 10:
                HandlerSteer.keyAdc(9, data[start + 1] & 255);
                return;
            case (byte) 11:
                HandlerSteer.keyAdc(10, data[start + 1] & 255);
                return;
            case (byte) 12:
                HandlerSteer.keyAdc(11, data[start + 1] & 255);
                return;
            case (byte) 13:
                HandlerSteer.keyAdc(12, data[start + 1] & 255);
                return;
            case (byte) 81:
            case (byte) 82:
            case (byte) 83:
            case (byte) 84:
            case (byte) 85:
            case (byte) 86:
                HandlerSteer.adcScan(data[start] - 81, data[start + 1] & 255);
                return;
            case (byte) 97:
            case (byte) 98:
            case (byte) 99:
            case (byte) 100:
            case (byte) 101:
            case (byte) 102:
            case (byte) 103:
            case (byte) 104:
            case (byte) 105:
            case (byte) 106:
            case (byte) 107:
                HandlerSteer.keyAdc((data[start] - 97) + 16, data[start + 1] & 255);
                return;
            case (byte) 112:
                HandlerSteer.keyAct(data[start + 1]);
        }
    }

    /*private void onHandleRadar(byte[] data, int start, int length) {
        switch (data[start]) {
            case (byte) 0:
                HandlerMain.radarFl(data[start + 1] & 255);
                return;
            case (byte) 1:
                HandlerMain.radarFml(data[start + 1] & 255);
                return;
            case (byte) 2:
                HandlerMain.radarFmr(data[start + 1] & 255);
                return;
            case (byte) 3:
                HandlerMain.radarFr(data[start + 1] & 255);
                return;
            case (byte) 4:
                HandlerMain.radarRl(data[start + 1] & 255);
                return;
            case (byte) 5:
                HandlerMain.radarRml(data[start + 1] & 255);
                return;
            case (byte) 6:
                HandlerMain.radarRmr(data[start + 1] & 255);
                return;
            case (byte) 7:
                HandlerMain.radarRr(data[start + 1] & 255);
                return;
            case (byte) 8:
                HandlerMain.radarRSF(data[start + 1] & 255);
                return;
            case (byte) 9:
                HandlerMain.radarRSMF(data[start + 1] & 255);
                return;
            case (byte) 10:
                HandlerMain.radarRSMB(data[start + 1] & 255);
                return;
            case (byte) 11:
                HandlerMain.radarRSB(data[start + 1] & 255);
                return;
            case (byte) 12:
                HandlerMain.radarLSF(data[start + 1] & 255);
                return;
            case (byte) 13:
                HandlerMain.radarLSMF(data[start + 1] & 255);
                return;
            case (byte) 14:
                HandlerMain.radarLSMB(data[start + 1] & 255);
                return;
            case (byte) 15:
                HandlerMain.radarLSB(data[start + 1] & 255);
                return;
            case (byte) 16:
                HandlerMain.radar(data[start + 1] == (byte) 0 ? 0 : 1);
                return;
            default:
                return;
        }
    }*/

    public static void resetTick() {
        mSleepTick = 0;
    }
}
