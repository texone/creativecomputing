package cc.creativecomputing.graphics.texture;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Context;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageIO;

public class CCTexture2DAsset extends CCAsset<CCTexture2D>{
	
	
	private Map<Path, CCTexture2D> _myTextureMap = new HashMap<>();
	
	private Path _myPath;
	private CCImage _myImage;
	
	public CCTexture2DAsset(CCGL2Context theContext){
		_myAsset = new CCTexture2D();
		theContext.listener().add(new CCGL2Adapter(){
			@Override
			public void display(CCGraphics theG) {
				if(_myImage != null){
					_myAsset = new CCTexture2D(_myImage);
					_myTextureMap.put(_myPath, _myAsset);
					_myImage = null;
				}
			}
		});
	}

	@Override
	public void onChangePath(Path thePath) {
		if(thePath == null){
			_myPath = thePath;
			_myAsset = null;
			return;
		}
		if(_myTextureMap.containsKey(thePath)){
			_myAsset = _myTextureMap.get(thePath);
			return;
		}
		_myImage = CCImageIO.newImage(thePath);
	}
	
	@Override
	public void time(double theGlobalTime, double theEventTime) {
	}

}
