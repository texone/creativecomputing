package cc.creativecomputing.kle.sequence;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.util.CCArrayUtil;
import cc.creativecomputing.io.CCFileChooser;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectables;
import cc.creativecomputing.kle.CCKleSegment;
import cc.creativecomputing.kle.formats.CCKleFormats;
import cc.creativecomputing.kle.formats.CCKleV1Container;
import cc.creativecomputing.kle.formats.CCKleV2Container;
import cc.creativecomputing.kle.sequence.CCSequenceRecorder.CCSequenceElementRecording;

public class CCSequenceExporter {

	private CCFileChooser _myFileChooser = new CCFileChooser();
	
	private CCKleEffectables _myElements;
	
	public CCSequenceExporter(CCKleEffectables theElements){
		_myElements = theElements;
		_myFileChooser = new CCFileChooser(CCArrayUtil.append(CCKleFormats.extensions(), "kle"));
		
	}
	
	public void save(Path theRecordPath, Map<CCKleChannelType, CCSequence> _myRecordings, List<CCKleSegment> theSegments){
		
		_myFileChooser.saveFile("").ifPresent(path -> {
			try {
				String myExtension = CCNIOUtil.fileExtension(path);
				switch(myExtension){
				case "KLE_1":
					path = CCNIOUtil.addExtension(path, "kle");
					Path myPath = theRecordPath == null ? path : theRecordPath;
					
					if(myPath == null)return;
		
					CCKleV1Container myKLE1Container = new CCKleV1Container();
					CCSequence myRecording = _myRecordings.get(CCKleChannelType.MOTORS);

					myKLE1Container.save(myPath, _myElements, _myRecordings);
					break;
				case "KLE_2":
					path = CCNIOUtil.addExtension(path, "kle");
					myPath = theRecordPath == null ? path : theRecordPath;
					if(myPath == null)return;
		
					CCKleV2Container myKLEContainer = new CCKleV2Container();
					myKLEContainer.save(myPath, _myElements, _myRecordings, theSegments);
					break;
				default:
					CCKleFormats myFormat = CCKleFormats.valueOf(myExtension);
					if(!myFormat.isFolder()){
						path = CCNIOUtil.addExtension(path, myFormat.extension());
					}
					
					myPath = theRecordPath == null ? path : theRecordPath;
					if(myPath == null)return;
					
					if(myFormat == CCKleFormats.NONE)return;
					
					for(CCKleChannelType myKey:_myRecordings.keySet()){
						myRecording = _myRecordings.get(myKey);
//						if(!myRecording.export)continue;
						myFormat.save(myPath, _myElements.mappings().get(myKey), myRecording);
					}	
				}
			} catch (RuntimeException ex) {
				ex.printStackTrace();
			}
		});
				
			
					
	}
	
	public void savePositions(Path theRecordPath, CCSequenceElementRecording theRecordings){
		
		_myFileChooser.saveFile("").ifPresent(path -> {
			try {
				String myExtension = CCNIOUtil.fileExtension(path);
				
				CCKleFormats myFormat = CCKleFormats.valueOf(myExtension);
				if(!myFormat.isFolder()){
					path = CCNIOUtil.addExtension(path, myFormat.extension());
				}
					
				Path myPath = theRecordPath == null ? path : theRecordPath;
				if(myPath == null)return;
					
				if(myFormat == CCKleFormats.NONE)return;
				theRecordings.savePositions(myPath, myFormat);
			} catch (RuntimeException ex) {
				ex.printStackTrace();
			}
		});
		
		
	}
}
