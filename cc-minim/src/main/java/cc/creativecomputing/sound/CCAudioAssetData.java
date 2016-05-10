package cc.creativecomputing.sound;

import java.awt.Color;
import java.awt.image.BufferedImage;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCArrayUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix2;
import cc.creativecomputing.math.interpolate.CCInterpolators;

public class CCAudioAssetData {
	public final CCAudioPlayer player;
	public final float[] data;
	private CCMatrix2 _mySpectrum;
	private BufferedImage _mySpectrumImage;

	public CCAudioAssetData(CCAudioPlayer theAudioPlayer, float[] theData) {
		player = theAudioPlayer;
		data = theData;
	}
	
	public double sampleRate(){
		return player.sampleRate();
	}
	
	public int samples(){
		return data.length;
	}
	
	public float[] data(int theIndex, int theLength){
		return CCArrayUtil.subset(data, theIndex, theLength);
	}
	
	public void spectrum(CCMatrix2 theSpectrum){
//		CCLog.info(theSpectrum);
		if(theSpectrum == null){
			_mySpectrumImage = null;
			return;
		}
		_mySpectrum = theSpectrum;
		_mySpectrumImage = new BufferedImage(_mySpectrum.columns(), _mySpectrum.rows(), BufferedImage.TYPE_INT_RGB);
		for(int c = 0; c < _mySpectrum.columns();c++){
			for(int r = 0; r < _mySpectrum.rows();r++){
				_mySpectrumImage.setRGB(c, r, Color.getHSBColor((float)CCMath.constrain(_mySpectrum.get(c, r)[0],0,1), 1, 1).getRGB());
//				_mySpectrumImage.setRGB(c, r, new Color((float)_mySpectrum.get(c, r)[0], (float)_mySpectrum.get(c, r)[0], (float)_mySpectrum.get(c, r)[0]).getRGB());
			}
		}
//		CCLog.info(_mySpectrumImage);
	}
	
	public BufferedImage image(){
		return _mySpectrumImage;
	}
	
	public boolean hasSpectrum(){
		return _mySpectrum != null;
	}
	
	public double spectrum(double theOffset, int theMilliSecond){
		if(_mySpectrum == null)return 0;
		if(theMilliSecond < 0 || theMilliSecond >= player.length())return 0;
		
		double myColumn = CCMath.map(theMilliSecond, 0, player.length(), 0, _mySpectrum.columns());
		double myRow = theOffset * _mySpectrum.rows();
		
		return _mySpectrum.get(CCInterpolators.CUBIC, myColumn, myRow)[0];
	}
}