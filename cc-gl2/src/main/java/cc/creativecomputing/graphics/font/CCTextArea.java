package cc.creativecomputing.graphics.font;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCVector2;

public class CCTextArea extends CCTextField{
	
	private CCVector2 _myDimension;

	public CCTextArea(CCFont<?> theFont, String theText) {
		super(theFont, theText);
	}

	
	private class CCTextBreaker{
		private double _myX = 0;
		private double _myY = 0;

		private int _myLineStartIndex = 0;
		private int _myLineEndIndex = 0;
		private int _myLastSplitIndex = -1;
		
		private double _myLineWidth;
		
		private void addLineBreak(){
			if(_myLastSplitIndex != -1){
				CCPlacedTextChar myPlacedChar = _myCharGrid.get(_myLastSplitIndex);
				while(myPlacedChar.charObject.getChar() == ' ' && _myLastSplitIndex < _myCharGrid.size() - 1){
					_myLastSplitIndex++;
					myPlacedChar = _myCharGrid.get(_myLastSplitIndex);
				}
				if(myPlacedChar.charObject.getChar() == ' '){
					CCLog.info(_myLastSplitIndex,myPlacedChar.x);
					return;
				}
				_myY -= _myFont.verticalAdvance() * _myBreakCorrectionScale;
				double myMove = myPlacedChar.x;
				for(int i = _myLastSplitIndex; i < _myLineEndIndex;i++){
					myPlacedChar = _myCharGrid.get(i);
					myPlacedChar.x -= myMove;
					myPlacedChar.y = _myY;
				}
				_myX -= myMove;
			}else{
				_myY -= _myFont.verticalAdvance() * _myBreakCorrectionScale;
				_myX = 0;
			}
			_myHeight += _myFont.verticalAdvance() * _myBreakCorrectionScale;
			for(int i = _myLineStartIndex; i < _myLineEndIndex;i++){
				_myCharGrid.get(i).x += alignCorrection(_myLineWidth);
			}
			_myLastSplitIndex = -1;
			_myLineStartIndex = _myLineEndIndex;
		}
		
		public void breakText(){
			_myHeight = 0;
			if(_myText == null)return;
			if(_myWidth == 0)return;
			_myCharGrid.clear();
			_myX = 0;
			_myY = 0;
			char myLastChar = ' ';
			_myLineStartIndex = 0;
			_myLineEndIndex = 0;
			_myLineWidth = 0;
			_myText = _myText.replaceAll(" +\n", " \n");
			
			_myLastSplitIndex = -1;
			for (char myChar:_myText.toCharArray()) {
				if(myChar == '\n'){
					_myLastSplitIndex = -1;
					addLineBreak();
					continue;
				}
				
				if(_myX > 0){
	        		_myX += _myFont.kernAdvance(myLastChar, myChar) * _myBreakCorrectionScale;
	        	}
				
	        	CCChar myCharObject = _myFont.fontChar(myChar);
	        	double myCharWidth = myCharObject.advanceWidth() * _myBreakCorrectionScale;
	        	
	        	if(myCharObject.getChar() != ' ' && _myX + myCharWidth > _myWidth){
	        		addLineBreak();
	        	}
	        	_myCharGrid.add(new CCPlacedTextChar(myCharObject, _myX, myCharWidth, _myY));
	        	if(myChar == ' ' || myChar == '-' || myChar == '/'){
	        		_myLastSplitIndex = _myCharGrid.size() - 1;
	        	}
	        	_myX += myCharWidth;
	        	myLastChar = myChar;
	        	_myLineEndIndex++;
	        }
			_myLineWidth = _myX;
			for(int i = _myLineStartIndex; i < _myLineEndIndex;i++){
				_myCharGrid.get(i).x += alignCorrection(_myLineWidth);
			}
			_myHeight += _myFont.verticalAdvance() * _myBreakCorrectionScale;
		}

	}
	
	@Override
	public void breakText(){
		new CCTextBreaker().breakText();
		updateBounds();
	}

	public void dimension(double theWidth, double theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;
	}

	

}
