package tk.rabidbeaver.mcucontroller;

public class Constants {
    public interface RADIO {
        String BAND = "tk.rabidbeaver.radiocontroller.BAND";
        String TUNE = "tk.rabidbeaver.radiocontroller.TUNE";
        String TUNE_UP = "tk.rabidbeaver.radiocontroller.TUNE_UP";
        String TUNE_DOWN = "tk.rabidbeaver.radiocontroller.TUNE_DOWN";
        String SEEK_UP = "tk.rabidbeaver.radiocontroller.SEEK_UP";
        String SEEK_DOWN = "tk.rabidbeaver.radiocontroller.SEEK_DOWN";
        String POWER_ON = "tk.rabidbeaver.radiocontroller.POWER_ON";
        String POWER_OFF = "tk.rabidbeaver.radiocontroller.POWER_OFF";
        String RDS_ON = "tk.rabidbeaver.radiocontroller.RDS_ON";
        String RDS_OFF = "tk.rabidbeaver.radiocontroller.RDS_OFF";
        String AREA = "tk.rabidbeaver.radiocontroller.AREA";
        String AUTOSENS_ON = "tk.rabidbeaver.radiocontroller.AUTOSENS_ON";
        String AUTOSENS_OFF = "tk.rabidbeaver.radiocontroller.AUTOSENS_OFF";
        String STEREO = "tk.rabidbeaver.radiocontroller.STEREO";
        String LOC = "tk.rabidbeaver.radiocontroller.LOC";
        String CHANNEL = "tk.rabidbeaver.radiocontroller.CHANNEL";

        String BROADCAST = "tk.rabidbeaver.radiocontroller.BROADCAST";
    }

    public interface SWI {
        String DETECT = "tk.rabidbeaver.swicontroller.DETECT";
        String CLEAR = "tk.rabidbeaver.swicontroller.CLEAR";
        String SAVE = "tk.rabidbeaver.swicontroller.SAVE";
        String ADCKEY = "tk.rabidbeaver.swicontroller.ADCKEY";
        String MCUKEYCONTROL = "tk.rabidbeaver.swicontroller.KEYCONTROL";
        String MCUKEY = "tk.rabidbeaver.swicontroller.MCUKEY";

        String BROADCAST = "tk.rabidbeaver.mcureceiver.MCU_KEY";
    }

    public interface MAIN {

    }
}
