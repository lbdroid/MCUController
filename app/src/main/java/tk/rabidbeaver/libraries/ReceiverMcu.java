package tk.rabidbeaver.libraries;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;

import tk.rabidbeaver.mcucontroller.Constants;

class ReceiverMcu {
    private final byte[] DATA_MISC = new byte[1024];
    private static int mSleepTick;
    private int RADIO_band = -1;
    private int RADIO_channel;
    private int RADIO_freq;
    private final char[] ch_d3 = new char[8];
    private int mChecksumIndex = 0;
    private int mFrameStartIndex = 0;
    private int mSize = 0;
    private TickLock mLockUiOk = new TickLock();

    private boolean wifiOnResume = true;
    private boolean btOnResume = true;
    private boolean resumeWireless = false;

    private class TickLock {
        private long cur;
        private long last;

        boolean unlock(int ms) {
            this.cur = SystemClock.uptimeMillis();
            if (this.cur - this.last < ((long) ms)) {
                return false;
            }
            this.last = this.cur;
            return true;
        }
    }

    private static void threadSleep(long ms) {
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
    void onReceive(byte[] data) {
        if (data == null || data.length <= 0) {
            threadSleep(100);
            return;
        }
        byte checksum;
        int i;
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

    private static int makeInt(byte high, byte mid, byte low) {
        return (((high << 16) & 16711680) | ((mid << 8) & 65280)) | (low & 255);
    }

    private void onHandle(byte[] data, int start, int length) {
        if (!DataMain.sMcuActived) {
            DataMain.sMcuActived = true;
        }
        String inCommand = "0x";
        String hexStr;
        byte[] thisCmd = new byte[length];
        for (int ii = start; ii<start+length; ii++){
            hexStr = Integer.toHexString(data[ii]);
            while (hexStr.length() < 2) hexStr = "0"+hexStr;
            inCommand += hexStr.substring(hexStr.length() - 2);
            thisCmd[ii-start] = data[ii];
        }
        ToolkitDev.writeLog(false, thisCmd);
        Log.d("MCUSERIAL", "COMMAND _IN: "+inCommand);
        int i;
        int end;
        byte[] mData;
        int index;
        StringBuilder stringBuilder;
        switch (data[start]) {
            case (byte) -64: // 192 / 0xC0
                HandlerSteer.mcuKeyEnable(data[start + 1] & 1);
                return;
            case (byte) -61: // 195 / 0xC3
                int studyKeyLength = length - 1;
                mData = new byte[studyKeyLength];
                System.arraycopy(data, start + 1, mData, 0, studyKeyLength);
                HandlerSteer.onMcuKeyStudied(mData);
                return;
            case (byte) -60: // 196 / 0xC4
                HandlerSteer.onMcuKeyEvent(data[start + 1] & 255, (data[start + 2] & 1) == 1 ? 0 : 1);
                return;
            case (byte) 1:
                switch (data[start + 1]) {
                    case (byte) -95:
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
                        }
                        return;
                    case (byte) 0:
                        onHandleMain(data, start + 2);
                        return;
                    case (byte) 3:
                        onHandleRadio(data, start + 2);
                        return;
                    case (byte) 7:
                        HandlerSteer.keyAct(0xc);
                        return;
                    case (byte) 16:
                        onHandleSteer(data, start + 2);
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
                                }
                                return;
                            case (byte) 64:
                                int num = data[start + 3] & 255;
                                if (num <= 9) {
                                    Log.d("MCU", "mediaKeyNum: "+num);
                                    //TODO ToolkitApp.mediaKeyNum(DataMain.sAppId, num);
                                }
                                return;
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
                                }
                        }
                        return;
                }
                return;
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
                            return;
                        case (byte) 1:
                            return;
                    }
                }
                return;
            case (byte) 6:
                switch (data[start + 1]) {
                    case (byte) 32:
                        HandlerMain.resetArmLaterCmd(data[start + 2] & 255);
                }
                return;
            case (byte) 10:
                int B02 = data[start + 1];
                DataMain.sMcu0x0AFlag = B02;
                HandlerMain.mcuPowerOption((B02 >> 6) & 1);
                if (DataMain.sMcuOn == 0) HandlerMain.accOn(1);
                return;
            case (byte) 12:
                switch (data[start + 1]) {
                    case (byte) 10:
                        //TODO amp: HandlerAmp.ampStatus((data[start + 2] & 1) == 0 ? 0 : 1);
                }
                return;
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
                    HandlerRadio.sFreqMin = freqMin;
                    HandlerRadio.sFreqMax = freqMax;
                    HandlerRadio.sFreqStepLen = step;
                    HandlerRadio.sFreqStepCnt = stepCnt;
                    return;
                }
                return;
            case (byte) 80:
                Log.d("RADIO", "RDS Channel Text -- skip");
                int channel = data[start + 1] - 1;
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
                            HandlerRadio.updateRdsChannelText();
                            return;
                        }
                        return;
                    } else if (HandlerRadio.RDS_CHANNEL_TEXT_FM[channel] == null || !value3.contentEquals(HandlerRadio.RDS_CHANNEL_TEXT_FM[channel])) {
                        HandlerRadio.RDS_CHANNEL_TEXT_FM[channel] = value3;
                        HandlerRadio.updateRdsChannelText();
                        return;
                    } else {
                        return;
                    }
                }

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
        }
        Log.d("MCUSERIAL", "COMMAND NOT HANDLED: "+inCommand);
    }

    // 0x0100xxxxxx input
    private void onHandleMain(byte[] data, int start) {
        WifiManager wifi;
        BluetoothAdapter bta;
        switch (data[start]) {
            case (byte) -120: // 0x88
                Log.d("ONHANDLEMAIN", "-120");
                System.setProperty("sys.fyt.sleeping", "0");
                System.setProperty("sys.sleep", "0");
                mSleepTick = 0;
                //HandlerMain.mcuOn(1);

                if (resumeWireless) {
                    wifi = (WifiManager) ToolkitDev.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (wifiOnResume && wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLED && wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
                        wifi.setWifiEnabled(true);
                    bta = BluetoothAdapter.getDefaultAdapter();
                    if (btOnResume && !bta.isEnabled()) bta.enable();
                    resumeWireless = false;
                }

                return;
            case (byte) -119: // 0x89
                Log.d("ONHANDLEMAIN", "-119:"+Integer.toHexString(data[start+1]));
                System.setProperty("sys.fyt.sleeping", "1");
                System.setProperty("sys.sleep", "1");
                System.setProperty("sys.sleeptimes", "1");
                resumeWireless = true;
                switch (data[start + 1]) {
                    case (byte) 83:
                        Log.d("sleep", "0x89 0x53 STEP1 + time: = " + SystemClock.uptimeMillis());
                        ToolkitDev.stopHeartBeat();
                        int i2 = mSleepTick;
                        mSleepTick = i2 + 1;
                        if (i2 == 2) {
                            if (DataMain.sSleepWakeup == 0) {
                                HandlerMain.setUsbMode(1);
                            }
                        }

                        if (mSleepTick < 2) {
                            wifi = (WifiManager) ToolkitDev.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            wifiOnResume = false;
                            if (wifi.getWifiState() != WifiManager.WIFI_STATE_DISABLED) {
                                wifiOnResume = true;
                                wifi.setWifiEnabled(false);
                            }

                            bta = BluetoothAdapter.getDefaultAdapter();
                            btOnResume = false;
                            if (bta.isEnabled()) {
                                btOnResume = true;
                                bta.disable();
                            }
                        }

                        if (mSleepTick > 4) {
                            ToolkitDev.writeMcu(1, 170, 95); // 0x01aa5f
                            Log.d("sleep", "0x89 0x53 RECEIVER MCU " + SystemClock.uptimeMillis());
                            return;
                        }
                        return;

                    case (byte) 84:
                        mSleepTick = 0;
                        ToolkitDev.writeMcu(1, 170, 97); // 0x01aa61
                        Log.d("sleep", "0x89 0x54 STEP3 + time: = " + SystemClock.uptimeMillis());
                        return;
                    case (byte) 85:
                        Log.d("sleep", "0x89 0x55 canSleep = duh!");
                        //HandlerMain.mcuOn(0);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie){}
                        ToolkitDev.writeMcu(1, 170, 98); // 0x01aa62

                        //TODO: eventually, this package needs to become a system package and get rid of this little standby shim
                        Intent i = new Intent();
                        i.setAction(Constants.MAIN.STANDBY);
                        ToolkitDev.context.sendBroadcast(i);

                        DataMain.sMcuActived = false;
                        Log.d("sleep", "0x89 0x55 gotoSleep");

                        return;
                    default:
                        return;
                }
            case (byte) -69:
                HandlerRadio.power(data[start + 1] & 1);
                return;
            case (byte) 0:
                switch (data[start + 1]) {
                    case (byte) 0:
                        Log.d("ONHANDLEMAIN", "mcuOn 0");
                        HandlerMain.mcuOn(0);
                        return;
                    case (byte) 1:
                        Log.d("ONHANDLEMAIN", "mcuOn 1");
                        HandlerMain.mcuOn(1);
                        ToolkitDev.startHeartBeat();
                        return;
                    case (byte) 33:
                        Log.d("ONHANDLEMAIN", "33");
                        // This unlock thing seems to just make sure that the writes don't happen
                        // more than once in 2 seconds.
                        if (this.mLockUiOk.unlock(2000)) {
                            ToolkitDev.writeMcu(1, 170, 96);
                            ToolkitDev.writeMcu(1, 0, 0);
                            return;
                        }
                        return;
                    case (byte) 49: // 0x31
                        Log.d("ONHANDLEMAIN", "accOn 1");
                        HandlerMain.accOn(1);
                        return;
                    case (byte) 50: // 0x32
                        Log.d("ONHANDLEMAIN", "accOn 0");
                        HandlerMain.accOn(0);
                        return;
                }
                return;
            case (byte) 7:
                HandlerSteer.keyAct(0x12);
                return;
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
                        //TODO HandlerMain.mcuKeyPlayer();
                        return;
                    case (byte) 12:
                        ToolkitDev.writeMcu(1, 0, 27);
                        return;
                    case (byte) 16:
                        //TODO HandlerMain.mcuKeyBand();
                        return;
                    case (byte) 32:
                        HandlerSteer.keyAct(0xe);
                        return;
                    default:
                        return;
                }
            case (byte) 17:
                switch (data[start + 1]) {
                    case (byte) 1:
                        //TODO HandlerMain.mcuKeyHome();
                        return;
                    case (byte) 6:
                        //TODO HandlerMain.mcuKeyBack();
                }
                return;
            case (byte) 18:
                //TODO HEADLIGHTS OFF HERE
                Log.d("ONHANDLEMAIN", "headlights 0 B");
                HandlerMain.headlights(0);
                return;
            case (byte) 19:
                //TODO HEADLIGHTS ON HERE
                Log.d("ONHANDLEMAIN", "headlights 1 B");
                HandlerMain.headlights(1);
                return;
            case (byte) 33:
                /*TODO switch (data[start + 1]) {
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
                }*/
                return;
            case (byte) 35:
                switch (data[start + 1]) {
                    case (byte) 2:
                        Log.d("ONHANDLEMAIN", "reverse 0");
                        HandlerMain.reverse(0); // reverse off?
                        return;
                    case (byte) 3:
                        Log.d("ONHANDLEMAIN", "reverse 1");
                        HandlerMain.reverse(1); // reverse on?
                        return;
                    default:
                        return;
                }
            case (byte) 36:
                Log.d("ONHANDLEMAIN", "handbrakeEnable: "+Integer.toHexString(data[start+1]));
                    HandlerMain.eBrakeSet(data[start + 1] & 1);
                return;
            case (byte) 86:
                /*TODO amp: if (DataSound.sAmpEnableType == 0) {
                    HandlerSound.amp((data[start + 1] >> 0) & 1);
                    return;
                }*/
                return;
            case (byte) 96:
                HandlerRadio.rdsEnable(data[start + 1] & 1);
        }
    }

    private void onHandleRadio(byte[] data, int start) {
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
                    HandlerRadio.channel(channel);
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
                        }
                    } else if (this.RADIO_channel >= 101 && this.RADIO_channel <= 112) {
                        this.RADIO_channel -= 101;
                        if (this.RADIO_channel < 12 && HandlerRadio.FREQ_AM[this.RADIO_channel] != this.RADIO_freq) {
                            HandlerRadio.FREQ_AM[this.RADIO_channel] = this.RADIO_freq;
                        }
                    }
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
                if (band >= 10) band -= 10;
                else band += 65536;
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

    private void onHandleSteer(byte[] data, int start) {
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

    static void resetTick() {
        mSleepTick = 0;
    }
}
