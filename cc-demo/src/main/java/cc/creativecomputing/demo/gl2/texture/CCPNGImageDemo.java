package cc.creativecomputing.demo.gl2.texture;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngWriter;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCBitUtil;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.image.format.CCPNGImage;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCPNGImageDemo  {

	public static PngWriter createWriter(String thePath, ImageInfo theInfo){
		Path myPath = CCNIOUtil.dataPath(thePath);
		CCNIOUtil.createDirectories(myPath.getParent());
		
		OutputStream myOut;
		
		try {
			myOut = Files.newOutputStream(myPath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return new PngWriter(myOut, theInfo);
	}

	public static void main(String[] args) {
//		ImageInfo my16BitInfo = new ImageInfo(800, 600, 16, false, false, false);
		int myFrame = 0;
		int myMax = CCMath.pow(2, 16);
//		for(int i = 0; i < 90;i++){
//			PngWriter myWriter = createWriter("light16/frame_" + CCFormatUtil.nf(myFrame++, 5) + ".png", my16BitInfo);
//			CCLog.info(myFrame);
//			for (int row = 0; row < my16BitInfo.rows; row++) {
//				ImageLineInt myImageLine = new ImageLineInt(my16BitInfo);
//				int[] scanline = myImageLine.getScanline();
//				for (int j = 0; j < my16BitInfo.cols * 3; j++) {
//					scanline[j] = 0;
//				}
//				myWriter.writeRow(myImageLine, row);
//			}
//			myWriter.end();
//		}
//		for(int i = 0; i < 137 * 30;i++){
//			PngWriter myWriter = createWriter("light16/frame_" + CCFormatUtil.nf(myFrame++, 5) + ".png", my16BitInfo);
//			CCLog.info(myFrame);
//			int myFade = (int)(CCMath.norm(i, 0, 137 * 30) * myMax);
//			int mycols = my16BitInfo.cols * 3;
//			for (int row = 0; row < my16BitInfo.rows; row++) {
//				ImageLineInt myImageLine = new ImageLineInt(my16BitInfo);
//				int[] scanline = myImageLine.getScanline();
//				
//				for (int j = 0; j < mycols / 2 ; j++) {
//					scanline[j] = myFade;
//				}
//				for (int j = mycols / 2; j < mycols ; j++) {
//					scanline[j] = 0;
//				}
//				myWriter.writeRow(myImageLine, row);
//			}
//			myWriter.end();
//		}
		
		ImageInfo my8BitInfo = new ImageInfo(800, 600, 8, false, false, false);
		myFrame = 0;
		for(int i = 0; i < 90;i++){
			PngWriter myWriter = createWriter("light8/frame_" + CCFormatUtil.nf(myFrame++, 5) + ".png", my8BitInfo);
			CCLog.info(myFrame);
			for (int row = 0; row < my8BitInfo.rows; row++) {
				ImageLineInt myImageLine = new ImageLineInt(my8BitInfo);
				int[] scanline = myImageLine.getScanline();
				for (int j = 0; j < my8BitInfo.cols * 3; j++) {
					scanline[j] = 0;
				}
				myWriter.writeRow(myImageLine, row);
			}
			myWriter.end();
		}
		for(int i = 0; i < 137 * 30;i++){
			PngWriter myWriter = createWriter("light8/frame_" + CCFormatUtil.nf(myFrame++, 5) + ".png", my8BitInfo);
			CCLog.info(myFrame);
			int myFade = (int)(CCMath.norm(i, 0, 137 * 30) * myMax);
			int myBit1 = CCBitUtil.bit(myFade, 1);
			int myBit0 = CCBitUtil.bit(myFade, 0);
			int mycols = my8BitInfo.cols * 3;
			for (int row = 0; row < my8BitInfo.rows; row++) {
				ImageLineInt myImageLine = new ImageLineInt(my8BitInfo);
				int[] scanline = myImageLine.getScanline();
				
				for (int j = 0; j < mycols / 2 ; j++) {
					scanline[j] = myBit1;
				}
				for (int j = mycols / 2; j < mycols ; j++) {
					scanline[j] = myBit0;
				}
				myWriter.writeRow(myImageLine, row);
			}
			myWriter.end();
		}
		
	}
}
