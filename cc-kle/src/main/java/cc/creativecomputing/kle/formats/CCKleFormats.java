package cc.creativecomputing.kle.formats;

import java.nio.file.Path;

import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.sequence.CCSequence;
import cc.creativecomputing.kle.sequence.CCSequenceRecorder.CCSequenceElementRecording;
@SuppressWarnings("rawtypes")
public enum CCKleFormats {
	NONE(new CCKleFormat(){

		@Override
		public void save(Path theFile,  CCKleMapping theMapping, CCSequence theSequence) {}

		@Override
		public CCSequence load(Path theFile, CCKleMapping theMapping) {
			return null;
		}

		@Override
		public String extension() {
			return null;
		}

		@Override
		public void savePosition(Path theFile, CCSequenceElementRecording theRecording, boolean[] theSave) {}
		
	}, false),
	ANIM(new CCKleAnimFormat(), true),
	BIN(new CCKleBinFormat(), false),
	CSV(new CCKleCSVFormat(), false),
	PNG(new CCKlePNGFormat(), false),
	TXT(new CCKleTXTFormat(), true),
	CCA(new CCKleCCAFormat(), true),
	XML(new CCKleXMLFormat(), false);
	
	private final CCKleFormat _myFormat;
	private final boolean _mySavePosition;
	
	CCKleFormats(CCKleFormat theFormat, boolean theSavePosition){
		_myFormat = theFormat;
		_mySavePosition = theSavePosition;
	}
	
	public boolean isFolder(){
		return _myFormat.extension() == null;
	}
	
	public boolean savePosition(){
		return _mySavePosition;
	}
	
	public CCKleFormat format(){
		return _myFormat;
	}
	
	public void save(Path thePath, CCKleMapping<?> theMapping, CCSequence theSequence){
		if(_myFormat == null)return;
		_myFormat.save(thePath, theMapping, theSequence);
	}
	
	public CCSequence load(Path thePath, CCKleMapping<?> theMapping){
		if(_myFormat == null)return null;
		return _myFormat.load(thePath, theMapping);
	}
	
	public String extension(){
		return _myFormat.extension();
	}
}
