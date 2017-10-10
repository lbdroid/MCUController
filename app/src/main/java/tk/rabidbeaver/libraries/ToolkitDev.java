package tk.rabidbeaver.libraries;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class ToolkitDev {

    public static final Serial SERIAL_MCU = new Serial();
    public static final SerialThread SERIAL_THREAD_MCU = new SerialThread();
    public static ReceiverMcu RECEIVER_MCU;
    public static boolean sMcuActived = true;
    public static Context context = null;

    private static final Handler mHandler = new Handler();

    public static byte[] packFrameMcu(int... args) {
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

    public static void writeMcu(int... args) {
        if (sMcuActived) {
            String inCommand = "0x";
            String hexStr = "";
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
            mHandler.postDelayed(this, 1000);
        }
    };

    public static void setupDevMcu(Context c) {
        context = c;

        String path = "/dev/ttyS0";
        int baud = 38400;
        SERIAL_MCU.open(path);
        SERIAL_MCU.setup(baud);
        SERIAL_THREAD_MCU.setName(String.format("MCU DEV PATH = %s FD = %d BAUD = %d", new Object[]{path, Integer.valueOf(SERIAL_MCU.getFd()), Integer.valueOf(baud)}));
        RECEIVER_MCU = new ReceiverMcu();
        SERIAL_THREAD_MCU.set(SERIAL_MCU, RECEIVER_MCU);

        // write "NULL" MCU state
        ToolkitDev.writeMcu(1, 0, 27);

        // begin heartbeat
        // TODO: This will have to be *stopped* when go to sleep.
        mcuHeartbeat.run();
    }
}
