package tk.rabidbeaver.mcucontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tk.rabidbeaver.libraries.HandlerMain;

public class MainReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Constants.MAIN.LED_COLOR:
                HandlerMain.ledColorCmd(intent.getIntExtra("COLOR", 0));
            default:
                break;
        }
    }
}
