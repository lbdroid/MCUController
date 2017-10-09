package tk.rabidbeaver.libraries;

public class SerialThread extends Thread {
    private boolean mLoop = true;
    private boolean mPause;
    private ReceiverMcu mReceiver;
    private Serial mSerial;

    public synchronized void start() {
        if (!(isAlive() || this.mSerial == null || this.mReceiver == null)) {
            super.start();
        }
    }

    public synchronized void setSerial(Serial serial) {
        this.mSerial = serial;
        start();
        notify();
    }

    public synchronized void setReceiver(ReceiverMcu receiver) {
        this.mReceiver = receiver;
        start();
        notify();
    }

    public synchronized void set(Serial serial, ReceiverMcu receiver) {
        this.mSerial = serial;
        this.mReceiver = receiver;
        start();
        notify();
    }

    public synchronized void reset() {
        this.mSerial = null;
        this.mReceiver = null;
    }

    public void pause(boolean pause) {
        this.mPause = pause;
        if (!pause) {
            synchronized (this) {
                notify();
            }
        }
    }

    public synchronized void quit() {
        this.mLoop = false;
        notify();
    }

    public void run() {
        while (this.mLoop) {
            Serial serial = this.mSerial;
            ReceiverMcu receiver = this.mReceiver;
            if (serial == null || receiver == null || this.mPause) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                receiver.onReceive(serial.read());
            }
        }
    }
}
