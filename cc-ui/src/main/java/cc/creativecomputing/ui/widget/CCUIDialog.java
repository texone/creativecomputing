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
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.layout.CCUIPane;

public class CCUIDialog<ResultType> extends CCGLApp{
	
	public static CCUIDialog createTextInput(CCGLApplicationManager theApp, String theText, String theInputText, int theX, int theY){
		CCUIGridPane myDialogPane = new CCUIGridPane(400,200);
		myDialogPane.style().horizontalAlignment(CCUIHorizontalAlignment.LEFT);
		myDialogPane.style().verticalAlignment(CCUIVerticalAlignment.CENTER);
		myDialogPane.style().inset(4);
		myDialogPane.style().background(new CCUIFillDrawable(CCColor.GRAY.brighter()));
		myDialogPane.rowHeight(25);
		myDialogPane.style().inset(25);
		myDialogPane.space(10);
		myDialogPane.columnWidths(10,10,10);
		myDialogPane.stretchWidth(true);
		myDialogPane.addChild(new CCUILabelWidget(theText), 0, 0, 3, 1);
		
		CCUITextFieldWidget myTextWidget = new CCUITextFieldWidget( theInputText);
		myTextWidget.width(200);
		myDialogPane.addChild(myTextWidget, 0, 1, 1, 1);
		CCUILabelWidget myCancelButton = new CCUILabelWidget( "CANCEL");
		myCancelButton.style().background(new CCUIFillDrawable(new CCColor(0.5)));
		myDialogPane.addChild(myCancelButton, 0, 2, 3, 1);

		CCUILabelWidget myOKButton = new CCUILabelWidget( "OK");
		myOKButton.style().background(new CCUIFillDrawable(new CCColor(0.5)));
		myDialogPane.addChild(myOKButton, 1, 2, 3, 1);
		myDialogPane.updateMatrices();
		
		CCUIDialog myDialog = new CCUIDialog(theApp, myDialogPane, "Add Preset", theX, theY);
		myCancelButton.mouseReleased.add(e -> {myDialog.shouldClose(true);});
		myOKButton.mouseReleased.add(e -> {
			myDialog.events.event(myTextWidget.text());
			myDialog.shouldClose(true);
		});
		return myDialog;
	}
	
	private CCUIContext _myContext;
	
	private CCEventManager<ResultType> events = new CCEventManager<>();
	
	public CCUIDialog(CCGLApplicationManager theApp, CCUIPane theWidget, String theTitle, int theX, int theY) {
		width = (int)theWidget.width();
		height = (int)theWidget.height();
		
		title = theTitle;
        _myContext = new CCUIContext(this, theWidget);
        frameBufferSizeEvents.add(size ->{
        	_myContext.widget().translation().set(-size.x / 2, size.y / 2);
        	_myContext.widget().updateMatrices();
        });
        
        theApp.add(this);
        
        drawEvents.add( g -> {
        	g.clear();
        	_myContext.widget().draw(g);
        });
        updateEvents.add( t -> {_myContext.widget().update(t);});
        
        position(theX, theY);
//        
        // Display the window.
        show();
	}
	
	@Override
	public void setup() {
	}
	
	@Override
	public void update(CCGLTimer theTimer){
	}

}
