package cc.creativecomputing.video;

import static org.bytedeco.javacpp.avformat.av_register_all;

public class CCFFMPEG {
	
	private static boolean _myIsOpen = false;

	public static void init(){
		if(_myIsOpen)return;
		_myIsOpen = true;
		av_register_all();
	}
}
