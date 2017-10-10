package tk.rabidbeaver.mcucontroller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Intent service = new Intent(context, MCUService.class);
        service.setAction("start");
        context.startService(service);
    }
}
