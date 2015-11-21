package cc.creativecomputing.sound;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;


public class CCAudioAsset extends CCAsset<CCAudioAssetData>{
	
	private Map<Path, CCAudioAssetData> _myPlayerMap = new HashMap<>();
	
	@CCProperty(name = "min time offset", min = 0.01, max = 1)
	private float _cMaxTimeOffset = 0.05f;
	
	@CCProperty(name = "volume", min = 0, max = 1)
	private float _cVolume = 1;

	@CCProperty(name = "pan", min = -1, max = 1)
	private float _cPan = 0;
	
	public CCAudioAsset(){
		_myAsset = null;
	}

	@Override
	public void onChangePath(Path thePath) {
		if(thePath == null){
			if(_myAsset != null)_myAsset.player.pause();
			_myAsset = null;
			return;
		}
		if(_myPlayerMap.containsKey(thePath)){
			_myAsset = _myPlayerMap.get(thePath);
			return;
		}else{
			try{
				_myAsset = new CCAudioAssetData(CCSoundIO.loadFile(thePath, 2048), CCSoundIO.loadSample(thePath).getChannel(0)) ;
				_myPlayerMap.put(thePath, _myAsset);
			}catch(Exception e){
				_myAsset = null;
			}
		}
	}
	
	private boolean _myIsPlaying = false;
	
	@Override
	public void time(double theGlobalTime, double theEventTime) {
		if(_myAsset == null)return;
		if(!_myIsPlaying)return;
		if(!_myAsset.player.isPlaying())_myAsset.player.play((int)(theEventTime * 1000));
//		_myAsset.setGain(_cVolume);
		_myAsset.player.setBalance(_cPan);
		double myOffset = CCMath.abs(_myAsset.player.position() / 1000f - theEventTime);
		if(myOffset > _cMaxTimeOffset){
			_myAsset.player.skip(-(int)((_myAsset.player.position() / 1000f - theEventTime) * 1000));
		}
	}
	
	@Override
	public void out() {
		if(_myAsset == null)return;
		_myAsset.player.pause();
	}

	@Override
	public void play() {
		_myIsPlaying = true;
	}

	@Override
	public void stop() {
		_myIsPlaying = false;
		if(_myAsset == null)return;
		_myAsset.player.pause();
	}
}
