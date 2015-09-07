package cc.creativecomputing.kle.formats;

import java.nio.file.Path;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCSequenceMapping;

public enum CCSequenceFormats {
	NONE(new CCSequenceFormat(){

		@Override
		public void save(Path theFile, CCSequenceMapping theMapping, CCSequence theSequence) {}

		@Override
		public CCSequence load(Path theFile, CCSequenceMapping theMapping) {
			return null;
		}

		@Override
		public String extension() {
			return null;
		}
		
	}, false),
	ANIM(new CCSequenceAnimFormat(), true),
	BIN(new CCSequenceBinFormat(), false),
	CSV(new CCSequenceCSVFormat(), false),
	PNG(new CCSequencePNGFormat(), false),
	TXT(new CCSequenceTXTFormat(), true);
	
	private final CCSequenceFormat _myFormat;
	private final boolean _mySavePosition;
	
	private CCSequenceFormats(CCSequenceFormat theFormat, boolean theSavePosition){
		_myFormat = theFormat;
		_mySavePosition = theSavePosition;
	}
	
	public boolean isFolder(){
		return _myFormat.extension() == null;
	}
	
	public boolean savePosition(){
		return _mySavePosition;
	}
	
	public CCSequenceFormat format(){
		return _myFormat;
	}
	
	public void save(Path thePath, CCSequenceMapping<?> theMapping, CCSequence theSequence){
		if(_myFormat == null)return;
		_myFormat.save(thePath, theMapping, theSequence);
	}
	
	public CCSequence load(Path thePath, CCSequenceMapping<?> theMapping){
		if(_myFormat == null)return null;
		return _myFormat.load(thePath, theMapping);
	}
	
	public String extension(){
		return _myFormat.extension();
	}
}
