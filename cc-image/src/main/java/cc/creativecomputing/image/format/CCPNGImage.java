package cc.creativecomputing.image.format;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.IImageLineSet;
import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCPNGImage {

	private ImageInfo _myImageInfo;
	private double[][] _myData;
	private int _myChannels;
	private int _myMax;
	
	private int _myWidth;
	private int _myHeight;

	public CCPNGImage(int theWidth, int theHeight, CCPixelFormat theFormat, CCPixelType theType) {

	}

	public CCPNGImage(int theWidth, int theHeight, int theBitDepth, boolean theGrayScale, boolean theAlpha) {
		_myImageInfo = new ImageInfo(theWidth, theHeight, theBitDepth, theAlpha, theGrayScale, false);
		_myChannels = (theGrayScale ? 1 : 3) + (theAlpha ? 1 : 0);
		_myData = new double[theHeight][theWidth * _myChannels];
		_myMax = CCMath.pow(2, theBitDepth);
		
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	public CCPNGImage(Path theFile){
		PngReader myPngReader;
		try {
			myPngReader = new PngReader(Files.newInputStream(theFile));
		} catch (IOException e) {
			throw new CCImageException(e);
		}
		
		IImageLineSet<? extends IImageLine> myLineSet = myPngReader.readRows();
		
		if(myLineSet.size() == 0)return;
		
		IImageLine myCheckLine = myLineSet.getImageLine(0);
		
		if(myCheckLine instanceof ImageLineInt){
			ImageLineInt myImageLineInt = (ImageLineInt)myCheckLine;
			int myBitDepth = myImageLineInt.imgInfo.bitDepth;
			_myChannels = myImageLineInt.imgInfo.channels;
			
			_myHeight = myLineSet.size();
			_myWidth = myImageLineInt.getSize() / _myChannels;
			_myMax = CCMath.pow(2, myBitDepth);
			_myData = new double[_myHeight][_myWidth * _myChannels];
			for(int y = 0; y < myLineSet.size(); y++){
				ImageLineInt myImageLine = (ImageLineInt)myLineSet.getImageLine(y);
				for(int x = 0; x < myImageLine.getSize(); x++){
					_myData[y][x] = myImageLine.getElem(x) / (float)_myMax;
				}
			}
		}
		
		myLineSet.size();
	}
	
	public int width(){
		return _myWidth;
	}
	
	public int height(){
		return _myHeight;
	}
	
	private void pixelImp(int theX, int theY, double...theValues){
		if(theValues.length > _myChannels)throw new RuntimeException("wrong number of values");
		for(int i = 0; i < theValues.length;i++){
			_myData[theY][i + theX * _myChannels] = theValues[i];
		}
	}
	
	public void pixel(int theX, int theY, double theR, double theG, double theB, double theA) {
		pixelImp(theX, theY, theR, theG, theB, theA);
	}
	
	public void pixel(int theX, int theY, double theR, double theG, double theB){
		pixelImp(theX, theY, theR, theG, theB);
	}
	
	public void pixel(int theX, int theY, double theR){
		pixelImp(theX, theY, theR);
	}
	
	public void pixelChannel(int theX, int theY, int theChannel, double theValue){
		_myData[theY][theChannel + theX * _myChannels] = theValue;
	}
	
	public double[] pixel(int theX, int theY){
		double[] myResult = new double[_myChannels];
		for(int i = 0; i < _myChannels;i++){
			myResult[i] = _myData[theY][i + theX * _myChannels];
		}
		return myResult;
	}

	public void write(Path thePath) {
		CCNIOUtil.createDirectories(thePath.getParent());
		
		OutputStream myOut;
		
		try {
			myOut = Files.newOutputStream(thePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		PngWriter myWriter = new PngWriter(myOut, _myImageInfo);

		for (int row = 0; row < _myImageInfo.rows; row++) {
			ImageLineInt myImageLine = new ImageLineInt(_myImageInfo);
			int[] scanline = myImageLine.getScanline();
			for (int j = 0; j < _myImageInfo.cols * _myChannels; j++) {
				scanline[j] = CCMath.floor(_myData[row][j] * (_myMax - 1)/*+0.5d*/);
			}
			myWriter.writeRow(myImageLine, row);
		}
		myWriter.end();
	}
}