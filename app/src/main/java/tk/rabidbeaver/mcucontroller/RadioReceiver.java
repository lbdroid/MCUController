package tk.rabidbeaver.mcucontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tk.rabidbeaver.libraries.CmdRadio;

public class RadioReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        switch (intent.getAction()){
            case Constants.RADIO.BAND:
                if (intent.getStringExtra("BAND").contentEquals("AM")) CmdRadio.band(0);
                else CmdRadio.band(65536);
                break;
            case Constants.RADIO.TUNE:
                int freq = intent.getIntExtra("FREQ", 0);
                CmdRadio.freq(3, freq);
                break;
            case Constants.RADIO.TUNE_UP:
                CmdRadio.freqUp();
                break;
            case Constants.RADIO.TUNE_DOWN:
                CmdRadio.freqDown();
                break;
            case Constants.RADIO.SEEK_UP:
                CmdRadio.seekUp();
                break;
            case Constants.RADIO.SEEK_DOWN:
                CmdRadio.seekDown();
                break;
            case Constants.RADIO.POWER_ON:
                CmdRadio.powerOn(1);
                break;
            case Constants.RADIO.POWER_OFF:
                CmdRadio.powerOn(0);
                break;
            case Constants.RADIO.RDS_ON:
                CmdRadio.rdsEnable(1);
                CmdRadio.rdsAfEnable(2);
                CmdRadio.rdsPtyEnable(2);
                CmdRadio.rdsTaEnable(2);
                break;
            case Constants.RADIO.RDS_OFF:
                CmdRadio.rdsEnable(0);
                break;
            case Constants.RADIO.AREA:
                CmdRadio.area(intent.getIntExtra("AREA", 0));
                break;
            case Constants.RADIO.AUTOSENS_ON:
                CmdRadio.autoSensitivity(1);
                break;
            case Constants.RADIO.AUTOSENS_OFF:
                CmdRadio.autoSensitivity(0);
                break;
            case Constants.RADIO.STEREO:
                CmdRadio.stereo();
                break;
            case Constants.RADIO.LOC:
                CmdRadio.loc();
                break;
            case Constants.RADIO.CHANNEL:
                CmdRadio.selectChannel(intent.getIntExtra("CHANNEL", 0));
        }
    }
}
