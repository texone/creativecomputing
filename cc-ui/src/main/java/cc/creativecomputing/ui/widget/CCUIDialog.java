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
package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.yoga.CCYogaNode;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaEdge;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaFlexDirection;

public class CCUIDialog<ResultType> extends CCGLApp{
	
	public static CCUIDialog<String> createTextInput(
		CCGLApplicationManager theApp, 
		String theText, 
		String theInputText, 
		int theX, 
		int theY
	){
		CCUIWidget myDialogPane = new CCUIWidget();
		myDialogPane.flexDirection(CCYogaFlexDirection.COLUMN);
		myDialogPane.flex(1);
		myDialogPane.padding(CCYogaEdge.ALL, 25);
		myDialogPane.style().background(new CCUIFillDrawable(CCColor.GRAY.brighter()));
		
		CCUILabelWidget myTextLabel = new CCUILabelWidget(theText);
		myTextLabel.margin(CCYogaEdge.BOTTOM, 10);
		myDialogPane.addChild(myTextLabel);
		
		CCUITextFieldWidget myTextWidget = new CCUITextFieldWidget( theInputText);
		myTextWidget.margin(CCYogaEdge.BOTTOM, 10);
		myTextWidget.padding(CCYogaEdge.ALL, 4);
		myDialogPane.addChild(myTextWidget);
		
		CCUIWidget myButtonsWidget = new CCUIWidget();
		myButtonsWidget.flexDirection(CCYogaFlexDirection.ROW_REVERSE);
//		myButtonsWidget.flex(1);

		CCUILabelWidget myOKButton = new CCUILabelWidget( "OK");
		myOKButton.style().background(new CCUIFillDrawable(new CCColor(0.5)));
		myOKButton.margin(CCYogaEdge.LEFT, 10);
		myOKButton.padding(CCYogaEdge.ALL, 4);
		myButtonsWidget.addChild(myOKButton);
		
		CCUILabelWidget myCancelButton = new CCUILabelWidget( "CANCEL");
		myCancelButton.style().background(new CCUIFillDrawable(new CCColor(0.5)));
		myCancelButton.padding(CCYogaEdge.ALL, 4);
		myButtonsWidget.addChild(myCancelButton);
		
		myDialogPane.addChild(myButtonsWidget);
		
		CCUIWidget mySpacer = new CCUIWidget();
		mySpacer.flex(1);
		myDialogPane.addChild(mySpacer);
		
		CCUIDialog<String> myDialog = new CCUIDialog<String>(theApp, myDialogPane, "Add Preset", theX, theY);
		myCancelButton.mouseReleased.add(e -> {myDialog.shouldClose(true);});
		myOKButton.mouseReleased.add(e -> {
			myDialog.events.event(myTextWidget.text());
			myDialog.shouldClose(true);
		});
		return myDialog;
	}
	
	private CCUIContext _myContext;
	
	public CCEventManager<ResultType> events = new CCEventManager<>();
	
	public CCUIDialog(CCGLApplicationManager theApp, CCUIWidget theWidget, String theTitle, int theX, int theY) {
		width = 400;
		height = 400;
		
		title = theTitle;

        
        theApp.add(this);
        
        drawEvents.add( g -> {
    		g.ortho();
        	g.clear();
        	_myContext.display(g);
        });
        updateEvents.add( t -> {_myContext.update(t);});
        
        position(theX, theY);
//        
        // Display the window.
        show();
        _myContext = new CCUIContext(this, CCYogaNode.CCYogaFlexDirection.COLUMN); //, theWidget
        frameBufferSizeEvents.add(size ->{
//        	_myContext.widget().translation().set(-size.x / 2, size.y / 2);
        	_myContext.updateMatrices();
        });
        _myContext.addChild(theWidget);
        _myContext.calculateLayout();
        _myContext.updateMatrices();
	}
	
	@Override
	public void setup() {
	}
	
	@Override
	public void update(CCGLTimer theTimer){
	}

}
