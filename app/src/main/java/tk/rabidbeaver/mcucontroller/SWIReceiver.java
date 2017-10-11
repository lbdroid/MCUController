package tk.rabidbeaver.mcucontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tk.rabidbeaver.libraries.CmdSteer;

public class SWIReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int value, v2;
        switch (intent.getAction()) {
            case Constants.SWI.CLEAR:
                CmdSteer.clear();
                break;
            case Constants.SWI.SAVE:
                CmdSteer.save();
                break;
            case Constants.SWI.DETECT:
                value = intent.getIntExtra("VALUE", -1);
                CmdSteer.detect(value);
                break;
            case Constants.SWI.ADCKEY:
                value = intent.getIntExtra("KEYCODE", -1);
                CmdSteer.keyAdc(value);
                break;
            case Constants.SWI.MCUKEY:
                value = intent.getIntExtra("KEYCODE", -1);
                v2 = intent.getIntExtra("FUNC", -1);
                CmdSteer.mcuKey(value, v2);
                break;
            case Constants.SWI.MCUKEYCONTROL:
                value = intent.getIntExtra("VALUE", -1);
                CmdSteer.mcuKeyControl(value);
                break;
        }
    }
}
