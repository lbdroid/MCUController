package tk.rabidbeaver.libraries;

import com.syu.jni.JniSerial;

class Serial {
    private int mFd;

    int open(String path) {
        this.mFd = JniSerial.open(path);
        return this.mFd;
    }

    int getFd() {
        return this.mFd;
    }

    void setup(int baud) {
        if (this.mFd > 0) {
            JniSerial.setup(this.mFd, baud, 8, 78, 1);
        }
    }

    synchronized void write(byte[] data) {
        if (this.mFd > 0) {
            JniSerial.write(this.mFd, data, 0, data.length);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    byte[] read() {
        if (this.mFd > 0) {
            return JniSerial.read(this.mFd, 512, 5);
        }
        sleep();
        return null;
    }
}
