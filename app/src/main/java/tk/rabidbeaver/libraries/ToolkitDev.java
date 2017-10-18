package tk.rabidbeaver.libraries;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    }
}
