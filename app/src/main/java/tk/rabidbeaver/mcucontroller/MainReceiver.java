package tk.rabidbeaver.mcucontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tk.rabidbeaver.libraries.HandlerMain;

public class MainReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Constants.MAIN.MCU_SWITCH:
                HandlerMain.mcuOnCmd(2);
                break;
            case Constants.MAIN.MCU_STANDBY_SWITCH:
                HandlerMain.mcuPowerOptionCmd(2);
        }
    }
}