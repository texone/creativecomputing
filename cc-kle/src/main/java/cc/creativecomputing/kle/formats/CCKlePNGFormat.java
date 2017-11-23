package cc.creativecomputing.kle.formats;

import java.nio.file.Path;

import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.image.format.CCPNGImage;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCKleChannel;
import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.sequence.CCSequence;
import cc.creativecomputing.kle.sequence.CCSequenceRecorder.CCSequenceElementRecording;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix2;

public class CCKlePNGFormat implements CCKleFormat{
	
	private final boolean _myUseMappingFolder;
	
	public CCKlePNGFormat(boolean theUseMappingFolder){
		_myUseMappingFolder = theUseMappingFolder;
	}
	
	public CCKlePNGFormat(){
		this(true);
	}
			
	
	private int calcDigits(CCSequence sequence){
		return CCMath.floor(CCMath.log10(sequence.length())) + 1;
	}

	@Override
	public void save(Path thePath, CCKleMapping<?> theMapping, CCSequence theSequence) {
		
		int digits = calcDigits(theSequence);
		int frameCount = 0;
		
		Path myExportPath = _myUseMappingFolder ? thePath.resolve(theMapping.type().id()) : thePath;
		
		CCNIOUtil.createDirectories(myExportPath);
				
		for (CCMatrix2 frame : theSequence) {
			CCPNGImage myPNGImage = new CCPNGImage (frame.columns(), frame.rows(), theMapping.bitDepth(), frame.depth() <= 2, frame.depth() == 2 || frame.depth() == 4);
//			CCImage myImage = new CCImage(frame.columns(), frame.rows());
			
			
			for(int c = 0; c < theMapping.columns();c++){
				for(int r = 0; r < theMapping.rows();r++){
//						CCColor myColor = new CCColor();
//						if(theMapping.depth() == 3){
//							myColor = new CCColor(
//								CCMath.saturate(frame.data()[c][r][0]),
//								CCMath.saturate(frame.data()[c][r][1]),
//								CCMath.saturate(frame.data()[c][r][2])
//							);
//						}
//						myImage.setPixel(c, r, myColor);
					for(int d = 0; d < theMapping.depth();d++){
						double myValue = frame.data()[c][r][d];
						myValue = CCMath.norm(myValue, theMapping.min(c,r,d), theMapping.max(c,r,d));
						myPNGImage.pixelChannel(c, r, d, myValue);
					}
				}
			}
			Path myImagePath = CCNIOUtil.addExtension(myExportPath.resolve("frame_" + CCFormatUtil.nf(frameCount, digits)), "png");
			myPNGImage.write(myImagePath);
//			CCImageIO.write(myImage, CCNIOUtil.addExtension(myExportPath, "png"));
			frameCount += 1;
		}
	}
	
	@Override
	public void savePosition(Path theFile, CCSequenceElementRecording theRecording, boolean[] theSave) {
		
	}

	@Override
	public CCSequence load(Path thePath, CCKleMapping<?> theMapping) {
		
		Path myImportPath = _myUseMappingFolder ? thePath.resolve(theMapping.type().id()) : thePath;
		
		CCSequence myResult = new CCSequence(theMapping.columns(), theMapping.rows(), theMapping.depth());
		
		for(Path myPath:CCNIOUtil.list(myImportPath, "png")){
			CCPNGImage myImage = new CCPNGImage(myImportPath.resolve(myPath));
			
			CCMatrix2 myFrame = new CCMatrix2(theMapping.columns(), theMapping.rows(), theMapping.depth());
//			CCLog.info(theMapping.size());
			for(CCKleChannel myChannel:theMapping){
//				CCLog.info(myChannel);
				int x = myChannel.column();
				int y = myChannel.row();
					
				double myValue = myImage.pixel(x, y)[myChannel.depth()];
				
				myValue = CCMath.blend(myChannel.min(), myChannel.max(), myValue);
				myFrame.data()[myChannel.column()][myChannel.row()][myChannel.depth()] = myValue;
			}
			
			myResult.add(myFrame);
		}
		return myResult;
	}
	
	@Override
	public String extension() {
		return null;
	}
	
}
