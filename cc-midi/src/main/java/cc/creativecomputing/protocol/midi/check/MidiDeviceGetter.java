package cc.creativecomputing.protocol.midi.check;

import java.io.IOException;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public class MidiDeviceGetter {

   public MidiDeviceGetter() {}

   public static void listTransmitterDevices() throws MidiUnavailableException {
      MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
      for (int i = 0; i < infos.length; i++) {
         MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
         if (device.getMaxTransmitters() != 0)
            System.out.println(device.getDeviceInfo().getName().toString()
                  + " has transmitters");
      }
   }

   // should get me my USB MIDI Interface. There are two of them but only one
   // has Transmitters so the if statement should get me the one i want
   public static MidiDevice getInputDevice() throws MidiUnavailableException {
      MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
      for (int i = 0; i < infos.length; i++) {
         MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
         System.out.println(i + ":" + device.getDeviceInfo().getName());
         if (device.getMaxTransmitters() != 0
               && device.getDeviceInfo().getName().contains("Bus 1")) {
            System.out.println(device.getDeviceInfo().getName().toString()
                  + " was chosen");
            return device;
         }
      }
      return null;
   }

   public static void main(String[] args) throws MidiUnavailableException,
         IOException {
      MidiDevice inputDevice;

      // MidiDeviceGetter.listTransmitterDevices();
      inputDevice = MidiDeviceGetter.getInputDevice();

      // just to make sure that i got the right one
      System.out.println(inputDevice.getDeviceInfo().getName().toString());
      System.out.println(inputDevice.getMaxTransmitters());

      // opening the device
      System.out.println("open inputDevice: "
            + inputDevice.getDeviceInfo().toString());
      inputDevice.open();
      System.out.println("connect Transmitter to Receiver");

      // Creating a Dumpreceiver and setting up the Midi wiring
      Receiver r = new DumpReceiver(System.out);
      Transmitter t = inputDevice.getTransmitter();
      t.setReceiver(r);
      
//      t.setReceiver(new Receiver() {
//		
//		@Override
//		public void send(MidiMessage message, long timeStamp) {
//			System.out.println(timeStamp + ":" + message);
//		}
//		
//		@Override
//		public void close() {
//			System.out.println("CLSOE");
//		}
//	});

      System.out.println("connected.");
      System.out.println("running...");
      System.in.read();
      // at this point the console should print out at least something, as the
      // send method of the receiver should be called when i hit a key on my
      // keyboard
      System.out.println("close inputDevice: "
            + inputDevice.getDeviceInfo().toString());
      inputDevice.close();
      System.out.println(("Received " + ((DumpReceiver) r).seCount
            + " sysex messages with a total of "
            + ((DumpReceiver) r).seByteCount + " bytes"));
      System.out.println(("Received " + ((DumpReceiver) r).smCount
            + " short messages with a total of "
            + ((DumpReceiver) r).smByteCount + " bytes"));
      System.out.println(("Received a total of "
                  + (((DumpReceiver) r).smByteCount + 
                        ((DumpReceiver) r).seByteCount) + " bytes"));
   }
}