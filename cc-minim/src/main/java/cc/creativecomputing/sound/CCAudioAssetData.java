package cc.creativecomputing.sound;
public class CCAudioAssetData{
	 public final CCAudioPlayer player;
	 public final float[] data;
	
	public CCAudioAssetData(CCAudioPlayer theAudioPlayer, float[] theData){
		player = theAudioPlayer;
		data = theData;
	}
}