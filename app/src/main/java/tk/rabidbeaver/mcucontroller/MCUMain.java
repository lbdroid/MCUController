package tk.rabidbeaver.mcucontroller;

import android.app.Activity;
import android.os.Bundle;

import tk.rabidbeaver.libraries.ToolkitDev;

public class MCUMain extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcumain);

        ToolkitDev.setupDevMcu(this);
    }
}
