package cc.creativecomputing.image;

import java.nio.file.Path;

import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageIO;

public class CCImageAsset extends CCAsset<CCImage>{
	
	public CCImageAsset(){
		_myAsset = new CCImage();
	}
	
	@Override
	public CCImage loadAsset(Path thePath) {
		return CCImageIO.newImage(thePath);
	}
	
	@Override
	public void time(double theGlobalTime, double theEventTime, double theContentOffset) {
	}

}
