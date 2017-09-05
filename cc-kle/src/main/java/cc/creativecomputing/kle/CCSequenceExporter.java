package cc.creativecomputing.kle;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import cc.creativecomputing.io.CCFileChooser;
import cc.creativecomputing.io.CCFileFilter;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCSequenceRecorder.CCSequenceElementRecording;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.elements.CCSequenceSegment;
import cc.creativecomputing.kle.formats.CCSequenceFormats;
import cc.creativecomputing.kle.formats.CCSequenceKLE1Container;
import cc.creativecomputing.kle.formats.CCSequenceKLE2Container;

public class CCSequenceExporter {

	private CCFileChooser _myFileChooser = new CCFileChooser();
	
	private CCSequenceElements _myElements;
	
	public CCSequenceExporter(CCSequenceElements theElements){
		_myElements = theElements;
		
		_myFileChooser = new CCFileChooser();
		_myFileChooser.setAcceptAllFileFilterUsed(false);
		_myFileChooser.addChoosableFileFilter(new CCFileFilter("KLE_1", "kle"));
		_myFileChooser.addChoosableFileFilter(new CCFileFilter("KLE_2", "kle"));
		for(CCSequenceFormats myFormat:CCSequenceFormats.values()){
			if(myFormat == CCSequenceFormats.NONE)continue;
			_myFileChooser.addChoosableFileFilter(new CCFileFilter(myFormat.name()));
		}
	}
	
	public void save(Path theRecordPath, Map<CCKleChannelType, CCSequence> _myRecordings, List<CCSequenceSegment> theSegments){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				int myRetVal = _myFileChooser.show("");
				if (myRetVal == JFileChooser.APPROVE_OPTION) {
					try {
						Path myChoosenPath = _myFileChooser.path();
						String myExtension = _myFileChooser.extension();
						
						switch(myExtension){
						case "KLE_1":
							myChoosenPath = CCNIOUtil.addExtension(myChoosenPath, "kle");
							Path myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
							
							if(myPath == null)return;
				
							CCSequenceKLE1Container myKLE1Container = new CCSequenceKLE1Container();
							CCSequence myRecording = _myRecordings.get(CCKleChannelType.MOTORS);
		
							myKLE1Container.save(myPath, _myElements, _myRecordings);
							break;
						case "KLE_2":
							myChoosenPath = CCNIOUtil.addExtension(myChoosenPath, "kle");
							myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
							if(myPath == null)return;
				
							CCSequenceKLE2Container myKLEContainer = new CCSequenceKLE2Container();
							myKLEContainer.save(myPath, _myElements, _myRecordings, theSegments);
							break;
						default:
							CCSequenceFormats myFormat = CCSequenceFormats.valueOf(myExtension);
							if(!myFormat.isFolder()){
								myChoosenPath = CCNIOUtil.addExtension(myChoosenPath, myFormat.extension());
							}
							
							myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
							if(myPath == null)return;
							
							if(myFormat == CCSequenceFormats.NONE)return;
							
							for(CCKleChannelType myKey:_myRecordings.keySet()){
								myRecording = _myRecordings.get(myKey);
//								if(!myRecording.export)continue;
								myFormat.save(myPath, _myElements.mappings().get(myKey), myRecording);
							}
								
						}
					} catch (RuntimeException ex) {
						ex.printStackTrace();
					}
				}
			};
		});
	}
	
	public void savePositions(Path theRecordPath, CCSequenceElementRecording theRecordings){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				int myRetVal = _myFileChooser.show("");
				if (myRetVal == JFileChooser.APPROVE_OPTION) {
					try {
						Path myChoosenPath = _myFileChooser.path();
						String myExtension = _myFileChooser.extension();
						
						CCSequenceFormats myFormat = CCSequenceFormats.valueOf(myExtension);
						if(!myFormat.isFolder()){
							myChoosenPath = CCNIOUtil.addExtension(myChoosenPath, myFormat.extension());
						}
							
						Path myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
						if(myPath == null)return;
							
						if(myFormat == CCSequenceFormats.NONE)return;
						theRecordings.savePositions(myPath, myFormat);
					} catch (RuntimeException ex) {
						ex.printStackTrace();
					}
				}
			};
		});
		
	}
}
