package tk.rabidbeaver.mcucontroller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import tk.rabidbeaver.libraries.CmdSteer;
import tk.rabidbeaver.libraries.HandlerSteer;
import tk.rabidbeaver.libraries.ToolkitDev;

public class MCUMain extends Activity {
    private int stored;
    private int[] slots = new int[]{16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 9, 8, 1, 3, 4, 2, 7};

    private Thread updateUi;
    private boolean stopUpdateUi;

    private String adcStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcumain);

        ToolkitDev.setupDevMcu(this);

        final Button swistart = findViewById(R.id.swi_start);
        final Button swirecord = findViewById(R.id.swi_record);
        final Button swisave = findViewById(R.id.swi_save);
        final Button swicancel = findViewById(R.id.swi_cancel);
        final LinearLayout swicontent = findViewById(R.id.swi_content);
        final TextView swiadc = findViewById(R.id.swi_adc);
        final TextView swistored = findViewById(R.id.swi_stored);

        swistart.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                stored = 0;
                swistored.setText("KEYS STORED: "+stored);
                swicontent.setVisibility(View.VISIBLE);
                swistart.setEnabled(false);
                CmdSteer.detect(1);
                CmdSteer.clear();
                swiadc.setText("");
                stopUpdateUi = false;
                updateUi = new Thread() {
                    @Override
                    public void run() {
                        try {
                            while(!stopUpdateUi) {
                                sleep(100);
                                adcStatus = "KEY 1: ["+HandlerSteer.SCAN_ADC[0]+", "+HandlerSteer.SCAN_ADC[2]+", "+HandlerSteer.SCAN_ADC[4]+"], "
                                        +"KEY2: ["+HandlerSteer.SCAN_ADC[1]+", "+HandlerSteer.SCAN_ADC[3]+", "+HandlerSteer.SCAN_ADC[5]+"]";
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        swiadc.setText(adcStatus);
                                    }
                                });

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                updateUi.start();
            }
        });

        swirecord.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (CmdSteer.keyAdc(slots[stored])){
                    stored++;
                    swistored.setText("KEYS STORED: "+stored);
                    Toast.makeText(getApplicationContext(), "SWI key recorded.", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(), "SWI recording failed, TRY AGAIN.", Toast.LENGTH_SHORT).show();
            }
        });

        swisave.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                stopUpdateUi = true;
                CmdSteer.save();
                CmdSteer.detect(0);
                swistart.setEnabled(true);
                swicontent.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "SWI programming SAVED.", Toast.LENGTH_SHORT).show();
            }
        });

        swicancel.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                stopUpdateUi = true;
                CmdSteer.detect(0);
                swistart.setEnabled(true);
                swicontent.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "SWI programming CANCELLED.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
