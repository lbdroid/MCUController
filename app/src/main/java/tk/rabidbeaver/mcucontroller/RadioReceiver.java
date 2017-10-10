package tk.rabidbeaver.mcucontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tk.rabidbeaver.libraries.CmdRadio;

public class RadioReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        switch (intent.getAction()){
            case Constants.ACTION.BAND:
                if (intent.getStringExtra("BAND").contentEquals("AM")) CmdRadio.band(0);
                else CmdRadio.band(65536);
                break;
            case Constants.ACTION.TUNE:
                int freq = intent.getIntExtra("FREQ", 0);
                CmdRadio.freq(3, freq);
                break;
            case Constants.ACTION.TUNE_UP:
                CmdRadio.freqUp();
                break;
            case Constants.ACTION.TUNE_DOWN:
                CmdRadio.freqDown();
                break;
            case Constants.ACTION.SEEK_UP:
                CmdRadio.seekUp();
                break;
            case Constants.ACTION.SEEK_DOWN:
                CmdRadio.seekDown();
                break;
            case Constants.ACTION.POWER_ON:
                CmdRadio.powerOn(1);
                break;
            case Constants.ACTION.POWER_OFF:
                CmdRadio.powerOn(0);
                break;
        }
    }
}
