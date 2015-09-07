package cc.creativecomputing.protocol.midi;

import cc.creativecomputing.core.logging.CCLog;

public class CCMidiTimeCode implements CCMidiListener{

	private static int FRAMES = 0;
	private static int SECONDS = 1;
	private static int MTC_MINUTES = 2;
	private static int MTC_HOURS = 3;
	private static int TICKS = 4;
	
	private int _myBPM;
	private double _myTickTime = 0;

	/**
	 * this array will hold our 5 time components (frames, seconds, minutes, hours, ticks)
	 */
	private int _myTimes[] = new int[5];

	// SMPTE type as string (24fps, 25fps, 30fps drop-frame, 30fps)
	private String szType = "";

	
	/**
	 * number of frames per second (start off with arbitrary high number until we receive it)
	 */
	private int _myNumberOfFrames = 100;
	
	public CCMidiTimeCode(){
		_myBPM = 120;
		_myTickTime = 60.0 / _myBPM / 24;
	}
	
	public void bpm(int theBPM){
		_myBPM = theBPM;
		_myTickTime = 60.0 / _myBPM / 24;
	}
	
	private double _myTime = 0;

	@Override
	public void receive(CCMidiMessage theMessage) {
		try{
		// if this is not a MTC message...
		if (theMessage.status() != CCMidiStatus.SYSEX)
			return;
		
		if(theMessage.data().length <= 1){
			_myTime += _myTickTime;
			return;
		}
		CCLog.info(theMessage.status() + ":" + theMessage.data().length);
		
		_myTime = 0;

		// the high nibble: which quarter message is this (0...7).
		int messageIndex = theMessage.data()[1] >> 4;
		// the low nibble: value
		int value = theMessage.data()[1] & 0x0F;
		// which time component (frames, seconds, minutes or hours) is this
		int timeIndex = messageIndex >> 1;
		boolean bNewFrame = messageIndex % 4 == 0;

		// the time encoded in the MTC is 1 frame behind by the time we have
		// received a new frame, so adjust accordingly
		if (bNewFrame) {
			_myTimes[TICKS] = 0;
			_myTimes[FRAMES]++;

			if (_myTimes[FRAMES] >= _myNumberOfFrames) {
				_myTimes[FRAMES] %= _myNumberOfFrames;
				_myTimes[SECONDS]++;

				if (_myTimes[SECONDS] >= 60) {
					_myTimes[SECONDS] %= 60;
					_myTimes[MTC_MINUTES]++;

					if (_myTimes[MTC_MINUTES] >= 60) {
						_myTimes[MTC_MINUTES] %= 60;
						_myTimes[MTC_HOURS]++;
					}
				}
			}
		} else {
			_myTimes[TICKS]++;
		}
		
		if (messageIndex % 2 == 0) {
			// if this is lower nibble of time component
			_myTimes[timeIndex] = value;
		} else {
			// ... or higher nibble
			_myTimes[timeIndex] |= value << 4;
		}

		if (messageIndex == 7) {
			// only use lower 5 bits for hours (higher bits indicate SMPTE type)
			_myTimes[MTC_HOURS] &= 0x1F;
			int smpteType = value >> 1;
			switch (smpteType) {
			case 0:
				_myNumberOfFrames = 24;
				szType = "24 fps";
				break;
			case 1:
				_myNumberOfFrames = 25;
				szType = "25 fps";
				break;
			case 2:
				_myNumberOfFrames = 30;
				szType = "30 fps (drop-frame)";
				break;
			case 3:
				_myNumberOfFrames = 30;
				szType = "30 fps";
				break;
			default:
				_myNumberOfFrames = 100;
				szType = " **** unknown SMPTE type ****";
			}
		}
		CCLog.info(messageIndex + ":" + value +":" +timeIndex +":" + _myTimes[MTC_HOURS] +":" +_myTimes[MTC_MINUTES] +":" +_myTimes[SECONDS] +":" +_myTimes[FRAMES] +":" +_myTimes[TICKS] +":" +szType+":" + bNewFrame);
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public double time(){
		return hours() * 3600 + minutes() * 60 + seconds() + frames() / (double)_myNumberOfFrames + _myTime;
	}
	
	public int frames(){
		return _myTimes[FRAMES];
	}
	
	public int seconds(){
		return _myTimes[SECONDS];
	}
	
	public int minutes(){
		return _myTimes[MTC_MINUTES];
	}
	
	public int hours(){
		return _myTimes[MTC_HOURS];
	}

}
