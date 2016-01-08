package cc.creativecomputing.image;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageIO;

public class CCImageAsset extends CCAsset<CCImage>{
	
	
	private Map<Path, CCImage> _myImageMap = new HashMap<>();
	
	private Path _myPath;
	
	public CCImageAsset(){
		_myAsset = new CCImage();
	}

	@Override
	public void onChangePath(Path thePath) {
		if(thePath == null){
			_myPath = thePath;
			_myAsset = null;
			return;
		}
		if(_myImageMap.containsKey(thePath)){
			_myAsset = _myImageMap.get(thePath);
			return;
		}
		try{
			_myAsset = CCImageIO.newImage(thePath);
			_myImageMap.put(_myPath, _myAsset);
		}catch(Exception e){
			
		}
	}
	
	@Override
	public void time(double theGlobalTime, double theEventTime, double theContentOffset) {
	}

}
