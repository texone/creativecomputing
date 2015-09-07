package cc.creativecomputing.control;

import java.nio.file.Path;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;

@SuppressWarnings({"rawtypes","unchecked"})
public abstract class CCAsset <AssetType>{
	
	public static interface CCAssetListener<AssetType>{
		public void onChange(AssetType theAsset);
	}

	protected AssetType _myAsset;
	
	protected Path _myAssetPath;
	
	protected CCListenerManager<CCAssetListener> _myEvents = CCListenerManager.create(CCAssetListener.class);
	
	public CCAsset(){
		
	}
	
	public AssetType value(){
		return _myAsset;
	}
	
	public abstract void onChangePath(Path thePath);
	
	@CCProperty(name = "path")
	public final void path(Path thePath){
		if(thePath == _myAssetPath)return;
		_myAssetPath = thePath;
		onChangePath(thePath);
		_myEvents.proxy().onChange(_myAsset);
	}
	
	public CCListenerManager<CCAssetListener> events(){
		return _myEvents;
	}
	
	public Path path(){
		return _myAssetPath;
	}
	
	public void time(double theGlobalTime, double theEventTime){}
	
	public void out(){}
	
	public void play(){}
	
	public void stop(){}
}
