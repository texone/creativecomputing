package cc.creativecomputing.protocol.serial.firmata;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.protocol.serial.CCSerialModule;

/**
 * Together with the Firmata 2 firmware (an Arduino sketch uploaded to the
 * Arduino board), this class allows you to control the Arduino board from
 * Processing: reading from and writing to the digital pins and reading the
 * analog inputs.
 */
public class CCArduino {
	/**
	 * Constant to set a pin to input mode (in a call to pinMode()).
	 */
	public static final int INPUT = 0;
	/**
	 * Constant to set a pin to output mode (in a call to pinMode()).
	 */
	public static final int OUTPUT = 1;
	/**
	 * Constant to set a pin to analog mode (in a call to pinMode()).
	 */
	public static final int ANALOG = 2;
	/**
	 * Constant to set a pin to PWM mode (in a call to pinMode()).
	 */
	public static final int PWM = 3;
	/**
	 * Constant to set a pin to servo mode (in a call to pinMode()).
	 */
	public static final int SERVO = 4;
	/**
	 * Constant to set a pin to shiftIn/shiftOut mode (in a call to pinMode()).
	 */
	public static final int SHIFT = 5;
	/**
	 * Constant to set a pin to I2C mode (in a call to pinMode()).
	 */
	public static final int I2C = 6;
	/**
	 * Constant to set a pin to input mode and enable the pull-up resistor (in a
	 * call to pinMode()).
	 */
	public static final int INPUT_PULLUP = 11;

	/**
	 * Constant to write a high value (+5 volts) to a pin (in a call to
	 * digitalWrite()).
	 */
	public static final int LOW = 0;
	/**
	 * Constant to write a low value (0 volts) to a pin (in a call to
	 * digitalWrite()).
	 */
	public static final int HIGH = 1;

	private CCFirmata _myFirmata;

	public void dispose() {
		_mySerial.stop();
	}

	@CCProperty(name = "serial")
	private CCSerialModule _mySerial;

	/**
	 * Create a proxy to an Arduino board running the Firmata 2 firmware.
	 *
	 * @param theBaudRate the baud rate to use to communicate with the Arduino
	 *            board (the firmata library defaults to 57600, and the examples
	 *            use this rate, but other firmwares may override it)
	 */
	public CCArduino(int theBaudRate) {
		_mySerial = new CCSerialModule("arduino", theBaudRate);
		_mySerial.listener().add(i -> {
			try {
		        // Notify the Arduino class that there's serial data for it to process.
		        while (i.available() > 0)
		        	_myFirmata.processInput(i.read());
		      } catch (Exception e) {
		        e.printStackTrace();
		        throw new RuntimeException("Error inside Arduino.serialEvent()");
		      }
		});
		_mySerial.startEvents.add(serial -> _myFirmata.init());

		_myFirmata = new CCFirmata(val -> _mySerial.output().write(val));

		try {
			Thread.sleep(3000); // let bootloader timeout
		} catch (InterruptedException e) {
		}

	}

	/**
	 * Create a proxy to an Arduino board running the Firmata 2 firmware at the
	 * default baud rate of 57600.
	 */
	public CCArduino() {
		this(57600);
	}
	
	public boolean isConnected(){
		return _mySerial.isConnected();
	}

	/**
	 * Returns the last known value read from the digital pin: HIGH or LOW.
	 *
	 * @param thePin the digital pin whose value should be returned (from 2 to
	 *            13, since pins 0 and 1 are used for serial communication)
	 */
	public int digitalRead(int thePin) {
		return _myFirmata.digitalRead(thePin);
	}

	/**
	 * Returns the last known value read from the analog pin: 0 (0 volts) to
	 * 1023 (5 volts).
	 *
	 * @param thePin the analog pin whose value should be returned (from 0 to 5)
	 */
	public int analogRead(int thePin) {
		return _myFirmata.analogRead(thePin);
	}

	/**
	 * Set a digital pin to input or output mode.
	 *
	 * @param thePin the pin whose mode to set (from 2 to 13)
	 * @param theMode either Arduino.INPUT or Arduino.OUTPUT
	 */
	public void pinMode(int thePin, int theMode) {
		try {
			_myFirmata.pinMode(thePin, theMode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error inside Arduino.pinMode()");
		}
	}

	/**
	 * Write to a digital pin (the pin must have been put into output mode with
	 * pinMode()).
	 *
	 * @param thePin the pin to write to (from 2 to 13)
	 * @param theValue the value to write: Arduino.LOW (0 volts) or Arduino.HIGH
	 *            (5 volts)
	 */
	public void digitalWrite(int thePin, int theValue) {
		try {
			_myFirmata.digitalWrite(thePin, theValue);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error inside Arduino.digitalWrite()");
		}
	}

	/**
	 * Write an analog value (PWM-wave) to a digital pin.
	 *
	 * @param thePin the pin to write to (must be 9, 10, or 11, as those are
	 *            they only ones which support hardware pwm)
	 * @param theValue the value: 0 being the lowest (always off), and 255 the
	 *            highest (always on)
	 */
	public void analogWrite(int thePin, int theValue) {
		try {
			_myFirmata.analogWrite(thePin, theValue);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error inside Arduino.analogWrite()");
		}
	}

	/**
	 * Write a value to a servo pin.
	 *
	 * @param thePin the pin the servo is attached to
	 * @param theValue the value: 0 being the lowest angle, and 180 the highest
	 *            angle
	 */
	public void servoWrite(int thePin, int theValue) {
		try {
			_myFirmata.servoWrite(thePin, theValue);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error inside Arduino.servoWrite()");
		}
	}
}