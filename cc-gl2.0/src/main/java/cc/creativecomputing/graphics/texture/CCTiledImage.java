package cc.creativecomputing.graphics.texture;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCMath;

public class CCTiledImage {
	
	private static class CCTile{
		private int _myX;
		private int _myY;
		
		private CCTexture2D _myTexture;
		
		public CCTile(CCTexture2D theTexture, int theX, int theY){
			_myTexture = theTexture;
			_myX = theX;
			_myY = theY;
		}
		
		public void draw(CCGraphics g){
			g.image(_myTexture, _myX, _myY);
		}
	}
	
	private int _myMaxTexWidth;
	private int _myMaxTexHeight;
	
	private List<CCTile>[][] _myTextureGrid;
	
	private int _myXImages;
	private int _myYImages;

	@SuppressWarnings("unchecked")
	public CCTiledImage(int theXImages, int theYImages, int theMaxWidth, int theMaxHeight){
		_myTextureGrid = new List[theXImages][theYImages];
		_myMaxTexWidth = theMaxWidth;
		_myMaxTexHeight = theMaxHeight;
		_myXImages = theXImages;
		_myYImages = theYImages;
	}
	
	public void addImage(CCImage theImage, int theX, int theY){
		_myTextureGrid[theX][theY] = new ArrayList<>();
		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.format(CCTexture.pixelFormat(theImage));
		myAttributes.pixelType(CCTexture.pixelType(theImage));
		myAttributes.internalFormat(CCTexture.internalFormat(theImage));
		System.out.println(theX+":"+theY);
		for(int x = 0; x < theImage.width(); x+= _myMaxTexWidth){
			for(int y = 0; y < theImage.height(); y += _myMaxTexHeight){
				int myTexWidth = CCMath.min(_myMaxTexWidth, theImage.width() - x);
				int myTexHeight = CCMath.min(_myMaxTexHeight, theImage.height() - y);
				CCTexture2D myTex = new CCTexture2D(myAttributes,myTexWidth,myTexHeight);
				myTex.updateData(theImage, 0, 0, x, y, myTexWidth, myTexHeight);
				_myTextureGrid[theX][theY].add(new CCTile(myTex, x + theX * theImage.width(), y + theY * theImage.height()));
				System.out.println(x+":"+y + ":" + myTexWidth + ":" + myTexHeight);
			}
		}
	}
	
	public void draw(CCGraphics g){
		int i = 0;
		for(int x = 0; x < _myXImages;x++){
			for(int y = 0; y < _myYImages;y++){
				for(CCTile myTile:_myTextureGrid[x][y]){
					myTile.draw(g);
					i++;
					if(i >= 4)return;
				}
			}
		}
	}
}
