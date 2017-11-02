# MCUController ******** DEPRECATED!!!
Control the MCU on Jerking INTEL car radios.

# Switch now to MCUd:
https://github.com/lbdroid/MCUd

Note: MCUd does not yet (as of https://github.com/lbdroid/MCUd/commit/8a9025df51b4ba4803e66aac7efb2d1c9b9364bb) have AMFM radio or SWI implemented. This project is marked as deprecated because it will NOT continue to be updated AT ALL, and no pull requests will be accepted (not that there have been any).


Brief rundown;<br>
This application is to go in place of sofia server that ships with jerking car radios with intel (sofia) CPU.<br>
<br>
It is massively simplified compared to their software.<br>
<br>
Files:<br>
MCUMain: this is just a main activity that starts everything up. Ultimately, I want to introduce a service that launches with boot complete.<br>
ToolkitDev: This sets everything up. Opens the serial port, starts the reading process, adds the input handler, starts the heartbeat, etc.<br>
Serial: Where all the serial I/O happens.<br>
SerialThread: This reads from the serial in a loop, and sends everything it reads to ReceiverMcu<br>
ReceiverMcu: This processes the data received from the MCU and either deals with it directly, or passes it on to the applicable handler.<br>
HandlerMain: Deal with GPIO (for instance, ACC input signal, LCD backlight, etc.)<br>
HandlerRadio: Deal with AMFM radio input data, such as RDS.<br>
HandlerSteer: Deal with steering wheel button inputs.<br>
DataMain: Mostly redundant variables to store "main" state. Should probably move the useful ones into HandlerMain and delete the rest<br>
CmdRadio: Output commands to control the AMFM radio.<br>
CmdSteer: Output commands to program the steering wheel interface<br>
<br>
Note: This is a very VERY early alpha. In current form, at most, it will keep the MCU from rebooting the radio due to lack of hearbeat.
