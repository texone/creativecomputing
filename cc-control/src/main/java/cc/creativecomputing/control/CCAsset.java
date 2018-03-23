/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.control;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.CCProperty;

public abstract class CCAsset <AssetType>{

	protected AssetType _myAsset ;
	
	protected Path _myAssetPath;
	
	protected Map<Path, AssetType> _myAssetMap = new HashMap<>();
	
	public CCEventManager<AssetType> changeEvents = new CCEventManager<>();
	
	public CCAsset(){
		
	}
	
	public AssetType value(){
		return _myAsset;
	}
	
	public abstract AssetType loadAsset(Path thePath);
	
	public AssetType checkLoadAsset(Path theFilePath){
		if(_myAssetMap.containsKey(theFilePath)){
			return _myAssetMap.get(theFilePath);
		}else{
			try{
				
				AssetType myData = loadAsset(theFilePath);
				_myAssetMap.put(theFilePath, myData);
				
				return myData;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public void onChangePath(Path thePath){
		if(thePath == null){
			_myAsset = null;
			return;
		}
		if(_myAssetMap.containsKey(thePath)){
			_myAsset = _myAssetMap.get(thePath);
			return;
		}else{
			_myAsset = loadAsset(thePath);
			_myAssetMap.put(thePath, _myAsset);
		}
	}
	
	@CCProperty(name = "path")
	public final void path(Path thePath){
		if(thePath == _myAssetPath || (thePath != null && thePath.equals(_myAssetPath)))return;
		_myAssetPath = thePath;
		onChangePath(thePath);
		changeEvents.event(_myAsset);
	}
	
	public void mute(boolean theMute){
		
	}
	
	public String[] extensions(){
		return null;
	}
	
	public Path path(){
		return _myAssetPath;
	}
	
	public void time(double theGlobalTime, double theEventTime, double theContentOffset){}
	
//	public void renderTimedEvent(CCTimedEventPoint theTimedEvent, CCVector2 theLower, CCVector2 theUpper, double lowerTime, double UpperTime, Graphics2D theG2d) {
//		if(theTimedEvent.content() == null || theTimedEvent.content().value() == null) {
//			return;
//		}
//		
//		CCVector2 myPos = theView.controller().curveToViewSpace(new ControlPoint(theTimedEvent.time(),1));
//		CCVector2 myEndPos = theView.controller().curveToViewSpace(new ControlPoint(theTimedEvent.endTime(),1));
//		double width = myEndPos.getX() - myPos.getX();
//		theG2d.setColor(new Color(0,0,0,100));
//		
//		FontMetrics myMetrix = theG2d.getFontMetrics();
//		String myString = theTimedEvent.content().value().toString();
//		int myIndex = myString.length() - 1;
//		StringBuffer myText = new StringBuffer();
//		while(myIndex >= 0 && myMetrix.stringWidth(myText.toString() + myString.charAt(myIndex)) < width - 5){
//			myText.insert(0, myString.charAt(myIndex));
//			myIndex--;
//		}
//		theG2d.drawString(myText.toString(), (int) myPos.getX() + 5, (int) myPos.getY() + 15);
//	}
	
	public void reset(CCTimedEventPoint theTimedEvent){
		theTimedEvent.contentOffset(0);
	}
	
	public void out(){}
	
	public void play(){}
	
	public void stop(){}
}
