package tk.rabidbeaver.libraries;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tk.rabidbeaver.mcucontroller.R;

public class ToolkitDev {

    private static final Serial SERIAL_MCU = new Serial();
    private static final SerialThread SERIAL_THREAD_MCU = new SerialThread();
    private static boolean sMcuActived = false;
    public static Context context = null;
    private static boolean beatingHeart = false;

    private static final Handler mHandler = new Handler();
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

    public static synchronized void writeLog(boolean write, byte[] data){
        String timestamp = simpleDateFormat.format(Calendar.getInstance().getTime());

        final StringBuilder builder = new StringBuilder();
        for (byte b : data) {
            builder.append(String.format("%02x", b));
        }
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileOutputStream("/sdcard/mcucontroller.log", true));
            writer.println(timestamp + (write?" WRITE>>> ":" READ<<< ") + "0x" + builder.toString());
            writer.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] packFrameMcu(int... args) {
        if (args == null) {
            return null;
        }
        int length = args.length;
        byte[] data = new byte[(length + 5)];
        data[0] = (byte) -120;
        data[1] = (byte) 85;
        data[2] = (byte) (length >> 8);
        data[3] = (byte) (length & 255);
        int checksum = data[2] ^ data[3];
        for (int i = 0; i < length; i++) {
            data[i + 4] = (byte) args[i];
            checksum ^= args[i];
        }
        data[length + 4] = (byte) checksum;
        return data;
    }

    static void writeMcu(int... args) {
        if (sMcuActived) {
            String inCommand = "0x";
            String hexStr;
            for (int ii = 0; ii<args.length; ii++) {
                hexStr = Integer.toHexString(args[ii]);
                while (hexStr.length() < 2) hexStr = "0"+hexStr;
                inCommand += hexStr;
            }
            Log.d("MCUSERIAL", "COMMAND OUT: "+inCommand);
            byte[] data = packFrameMcu(args);
            if (data == null) {
                return;
            }
            writeLog(true, data);
            SERIAL_MCU.write(data);
        }
    }

    public static void writeMcuNon(int... args) {
        byte[] data = packFrameMcu(args);
        if (data != null) {
            SERIAL_MCU.write(data);
        }
    }

    private static final Runnable mcuHeartbeat = new Runnable() {
        @Override
        public void run() {
            ToolkitDev.writeMcu(1, 170, 85);
            if (beatingHeart) mHandler.postDelayed(this, 1000);
        }
    };

    static void startHeartBeat(){
        if (!beatingHeart) {
            beatingHeart = true;
            mcuHeartbeat.run();
        }
    }

    static void stopHeartBeat(){
        beatingHeart = false;
    }

    public static void setupDevMcu(Context c) {
        context = c;

        String path = "/dev/ttyS0";
        int baud = 38400;
        SERIAL_MCU.open(path);
        SERIAL_MCU.setup(baud);
        SERIAL_THREAD_MCU.setName(String.format("MCU DEV PATH = %s FD = %d BAUD = %d", new Object[]{path, Integer.valueOf(SERIAL_MCU.getFd()), Integer.valueOf(baud)}));
        SERIAL_THREAD_MCU.set(SERIAL_MCU, new ReceiverMcu());

        sMcuActived = true;

        // write "NULL" MCU state
        ToolkitDev.writeMcu(1, 0, 27);

        startHeartBeat();

        // TODO: In the crappy oem implementation, they enable and disable this during the
        // TODO: go-to-sleep and wake-up processes. I think they're disabling power management
        // TODO: on the DSP in order to reduce the power on/off clicks and pops. The system
        // TODO: property is read by the audio HAL when audio is played, which means that
        // TODO: to power it off during go-to-sleep, it is also necessary to play a sound.
        //
        // TODO: also, this doesn't actually work like this. Properties set in this manner are
        // TODO: only available to the process that calls it, so this is only an example. It has
        // TODO: to be added to the build.prop or set as root through a terminal.
        System.setProperty("audio.hw.allow_close", "1");
        playSound();
    }

    // TODO: This is a stupid hack to force the audio hal to read the system property audio.hw.allow_close
    private static void playSound() {
        try {
            Uri alert = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.powersave);
            MediaPlayer m = new MediaPlayer();
            m.setDataSource(context, alert);
            m.setAudioStreamType(5);
            m.prepare();
            m.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
