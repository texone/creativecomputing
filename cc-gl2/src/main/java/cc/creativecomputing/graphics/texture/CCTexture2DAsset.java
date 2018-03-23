package cc.creativecomputing.graphics.texture;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageIO;

public class CCTexture2DAsset extends CCAsset<CCTexture2D>{
	
	private CCImage _myImage;
	private CCImage _myCurrentImage;
	protected Map<Path, CCImage> _myImageMap = new HashMap<>();
	
	@CCProperty(name = "wrap")
	private CCTextureWrap _cTextureWrap = CCTextureWrap.CLAMP;
	@CCProperty(name = "generate mipmaps")
	private boolean _cGenerateMipmaps = false;
	@CCProperty(name = "filter")
	private CCTextureFilter _cFilter = CCTextureFilter.LINEAR;
	@CCProperty(name = "mipmap filter")
	private CCTextureMipmapFilter _cMipMapFilter = CCTextureMipmapFilter.NEAREST;
	
	public CCTexture2DAsset(CCGL2Context theContext){
		_myAsset = new CCTexture2D();
		theContext.listener().add(new CCGL2Adapter(){
			@Override
			public void display(CCGraphics theG) {
				if(_myImage != null){
					_myAsset = new CCTexture2D(_myImage);
					_myAssetMap.put(_myAssetPath, _myAsset);
					_myImageMap.put(_myAssetPath, _myImage);
					_myCurrentImage = _myImage;
					_myImage = null;
				}
				if(_myAsset != null){
					_myAsset.wrap(_cTextureWrap);
					_myAsset.generateMipmaps(_cGenerateMipmaps);
					_myAsset.textureFilter(_cFilter);
					_myAsset.textureMipmapFilter(_cMipMapFilter);
				}
			}
		});
	}
	
	public CCImage image(){
		return _myCurrentImage;
	}
	
	@Override
	public CCTexture2D loadAsset(Path thePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onChangePath(Path thePath) {
		if(thePath == null){
			_myAssetPath = thePath;
			_myAsset = null;
			return;
		}
		if(_myAssetMap.containsKey(thePath)){
			_myAsset = _myAssetMap.get(thePath);
			_myCurrentImage = _myImageMap.get(thePath);
			return;
		}
		try{
			_myImage = CCImageIO.newImage(thePath);
		}catch(Exception e){
			
		}
	}
	
	@Override
	public void time(double theGlobalTime, double theEventTime, double theContentOffset) {
	}

}
