package tk.rabidbeaver.mcucontroller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import tk.rabidbeaver.libraries.ToolkitDev;

public class MCUService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        ToolkitDev.setupDevMcu();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
