package tk.rabidbeaver.libraries;

import com.syu.jni.JniSerial;

public class Serial {
    int mFd;

    public int open(String path) {
        this.mFd = JniSerial.open(path);
        return this.mFd;
    }

    public int getFd() {
        return this.mFd;
    }

    public void setFd(int fd) {
        this.mFd = fd;
    }

    public void setup(int baud) {
        if (this.mFd > 0) {
            JniSerial.setup(this.mFd, baud, 8, 78, 1);
        }
    }

    public synchronized void write(byte[] data) {
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

    public byte[] read() {
        if (this.mFd > 0) {
            return JniSerial.read(this.mFd, 512, 5);
        }
        sleep();
        return null;
    }
}
