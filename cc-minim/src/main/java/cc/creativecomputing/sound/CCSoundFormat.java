package cc.creativecomputing.sound;

import javax.sound.sampled.AudioFileFormat;

public enum CCSoundFormat {
	
	
	WAVE(AudioFileFormat.Type.WAVE),
	AIFF(AudioFileFormat.Type.AIFF),
	AIFC(AudioFileFormat.Type.AIFC),
	AU(AudioFileFormat.Type.AU),
	SND(AudioFileFormat.Type.SND);
	
	private final AudioFileFormat.Type _myType;
	
	private CCSoundFormat(AudioFileFormat.Type theType){
		_myType = theType;
	}
	
	public AudioFileFormat.Type type(){
		return _myType;
	}
	
	public static CCSoundFormat getFormat(String theFormat){
		switch(theFormat){
		case "wav":
			return WAVE;
		case "aiff":
		case "aif":
			return AIFF;
		case "aifc":
			return AIFC;
		case "au":
			return AU;
		case "snd":
			return SND;
		default:
			throw new CCSoundException("The extension " + theFormat + " is not a recognized audio file type.");
			
		}
	}
	
	
}
