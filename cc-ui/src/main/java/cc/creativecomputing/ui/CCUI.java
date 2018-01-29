/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.ui;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.CCPropertyObject;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.font.CCFont;
import cc.creativecomputing.gl.font.CCTextureMapFont;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.io.xml.CCXMLObjectSerializer;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.io.xml.property.CCXMLPropertyUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector1;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.actions.CCUIAction;
import cc.creativecomputing.ui.actions.CCUIAnimationAction;
import cc.creativecomputing.ui.decorator.CCUIDecorator;
import cc.creativecomputing.ui.decorator.CCUITextDecorator;
import cc.creativecomputing.ui.decorator.background.CCUIFillBackgroundDecorator;
import cc.creativecomputing.ui.decorator.background.CCUIGradientBackgroundDecorator;
import cc.creativecomputing.ui.decorator.background.CCUIRoundedBackgroundDecorator;
import cc.creativecomputing.ui.decorator.background.CCUITexture;
import cc.creativecomputing.ui.decorator.background.CCUITextureBackgroundDecorator;
import cc.creativecomputing.ui.decorator.border.CCUILineBorderDecorator;
import cc.creativecomputing.ui.widget.CCUISliderWidget;
import cc.creativecomputing.ui.widget.CCUITextFieldWidget;
import cc.creativecomputing.ui.widget.CCUIValueBoxWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;
/**
 * @author christianriekoff
 *
 */
public class CCUI {
	
	public Map<String, Class> _myNodeClassMap = new HashMap<String, Class>();
	
	public void addNodeClass(Class<?> theClass) {
		CCXMLPropertyObject myNode = theClass.getAnnotation(CCXMLPropertyObject.class);
		if(myNode == null)return;
		_myNodeClassMap.put(myNode.name(), theClass);
	}
	
	private Map<String, CCDataElement> _myWidgetMap = new HashMap<>();
	private Map<String, CCDataElement> _myTemplateMap = new HashMap<String, CCDataElement>();
	private Map<String, CCFont<?>> _myFontMap = new HashMap<String, CCFont<?>>();
	private Map<String, CCUITexture> _myTextureMap = new HashMap<String, CCUITexture>();
	private Map<String, CCUIAnimator> _myAnimations = new HashMap<String, CCUIAnimator>();
	private Map<String, List<CCDataElement>> _myActionsMap = new HashMap<String, List<CCDataElement>>();
	
	private List<CCUIWidget> _myWidgets = new ArrayList<CCUIWidget>();
	
	private CCGLApp _myApp;
	
	private CCXMLObjectSerializer _mySerializer;

	public CCUI(CCGLApp theApp) {
		_myApp = theApp;
		
		addNodeClass(CCColor.class);
		addNodeClass(CCVector1.class);
		addNodeClass(CCVector2.class);
		
		addNodeClass(CCUITexture.class);
		addNodeClass(CCUITexture.CCUITextureSplice.class);
		
		addNodeClass(CCUIWidget.class);
		addNodeClass(CCUISliderWidget.class);
		addNodeClass(CCUITextFieldWidget.class);
		addNodeClass(CCUIValueBoxWidget.class);
		
		addNodeClass(CCUIFillBackgroundDecorator.class);
		addNodeClass(CCUIRoundedBackgroundDecorator.class);
		addNodeClass(CCUIGradientBackgroundDecorator.class);
		addNodeClass(CCUITextureBackgroundDecorator.class);
		
		addNodeClass(CCUILineBorderDecorator.class);
		addNodeClass(CCUITextDecorator.class);
		
		addNodeClass(CCUIAnimator.class);
		addNodeClass(CCUIAnimator.CCPropertyTarget.class);
		
		addNodeClass(CCUIAnimationAction.class);
		
		_mySerializer = new CCXMLObjectSerializer(_myNodeClassMap);
	}
	
	private void loadFonts(CCDataElement theUIXML) {
		CCDataElement myFontsXML = theUIXML.child("fonts");
		if(myFontsXML == null)return;
		
		for(CCDataElement myFontXML:myFontsXML) {
			CCFont<?>myFont;
			double mySize = myFontXML.doubleAttribute("size");
			String myType = myFontXML.attribute("font_type", "texture");
			String myFontName = myFontXML.attribute("name");
			
			if(myType.equals("texture")) {
				myFont = new CCTextureMapFont(CCNIOUtil.dataPath(myFontName), (int)mySize);
				_myFontMap.put(myFontXML.attribute("id"), myFont);
			}
			
		}
	}
	
	private void loadTextures(CCDataElement theUIXML) {
		CCDataElement myTexturesXML = theUIXML.child("textures");
		if(myTexturesXML == null)return;
		
		for(CCDataElement myTextureXML:myTexturesXML) {
			String myID = myTextureXML.attribute("id");
			CCUITexture myTexture = _mySerializer.toObject(myTextureXML,CCUITexture.class);
			_myTextureMap.put(myID, myTexture);
		}
	}
	
	private void replaceTemplates(CCDataElement theElement) {
		for(CCDataElement myElement:new ArrayList<CCDataElement>(theElement.children())) {
			if(myElement.isTextElement())continue;
			if(myElement.name().equals("template")) {
				if(!myElement.hasAttribute("id")) {
					throw new CCUIException("To use a template you need to define the id of the template you want to use!");
				}
				String myId = myElement.attribute("id");
				CCDataElement myReplacement = _myTemplateMap.get(myId);
				if(myReplacement == null) {
					throw new CCUIException("You haven't defined a template for the id:" + myId);
				}
				
				int myIndex = theElement.children().indexOf(myElement);
				theElement.children().remove(myIndex);
				for(CCDataElement myTemplateChild:myReplacement) {
					theElement.children().add(myIndex++,myTemplateChild);
				}
			}else {
				replaceTemplates(myElement);
			}
		}
	}
	
	private void loadTemplates(CCDataElement theUIXML) {
		CCDataElement myTemplatesXML = theUIXML.child("templates");
		if(myTemplatesXML == null)return;
		
		for(CCDataElement myTemplateXML:myTemplatesXML) {
			_myTemplateMap.put(myTemplateXML.attribute("id"), myTemplateXML);
		}
		
		for(CCDataElement myTemplateXML:myTemplatesXML) {
			replaceTemplates(myTemplateXML);
		}
	}
	
	private void loadWidgets(CCDataElement theUIXML) {
		CCDataElement myWidgetsXML = theUIXML.child("widgets");
		
		for(CCDataElement myWidgetXML:myWidgetsXML) {
			replaceTemplates(myWidgetXML);
			_myWidgetMap.put(myWidgetXML.attribute("id"), myWidgetXML);
		}
	}
	
	private void loadAnimations(CCDataElement theUIXML) {
		CCDataElement myAnimationsXML = theUIXML.child("animations");
		if(myAnimationsXML == null)return;
		for(CCDataElement myAnimationXML:myAnimationsXML) {
			String myID = myAnimationXML.attribute("id");
			CCUIAnimator myAnimation = (CCUIAnimator)_mySerializer.toObject(myAnimationXML);
			_myAnimations.put(myID, myAnimation);
		}
	}
	
	public CCUIAnimator animation(String theId) {
		return _myAnimations.get(theId);
	}
	
	private void loadActions(CCDataElement theUIXML) {
		CCDataElement myActionsXML = theUIXML.child("actions");
		if(myActionsXML == null)return;
		
		for(CCDataElement myActionXML:myActionsXML) {
			if(!myActionXML.hasAttribute("widget")) {
				throw new CCUIException(
					"Error in ui xml element " + myActionXML.name() + " line:" + myActionXML.line()+"\n"+
					"Action needs to define the attribute widget, to define a widget this action is assigned to.\n"+
					myActionXML.toString()
				);
			}
			String myWidget = myActionXML.attribute("widget").split("\\.")[0];
			if(!_myActionsMap.containsKey(myWidget)) {
				_myActionsMap.put(myWidget, new ArrayList<CCDataElement>());
			}
			_myActionsMap.get(myWidget).add(myActionXML);
		}
	}
	
	public void loadUI(Path theFile) {
		CCDataElement myUIXML = CCXMLIO.createXMLElement(theFile, false);
		
		loadFonts(myUIXML);
		loadTextures(myUIXML);
		loadAnimations(myUIXML);
		loadActions(myUIXML);
		loadTemplates(myUIXML);
		loadWidgets(myUIXML);
	}
	
	public CCFont<?> font(String theFont){
		return _myFontMap.get(theFont);
	}
	
	public CCUITexture texture(String theTexture) {
		return _myTextureMap.get(theTexture);
	}
	
	public List<CCUIDecorator> createDecorator(CCDataElement theDecoratorXML){
		List<CCUIDecorator> myDecorators = new ArrayList<CCUIDecorator>();
		
		return myDecorators;
	}
	
	public CCUIWidget createWidget(String theID) {
		return createWidget(theID, null);
	}
	
	public <Type extends CCUIWidget> Type createWidget(String theID, Class<Type> theClass) {
		CCDataElement myWidgetXML = _myWidgetMap.get(theID);
		
		if(myWidgetXML == null) {
			throw new CCUIException(
				"The given Widget:" + theID +
				" could not be created as there is no such widget defined in the ui description xml file."
			);
		}
		
		Type myWidget = _mySerializer.toObject(myWidgetXML,theClass);
		myWidget.setup(this, null);
		
		if(_myActionsMap.containsKey(theID)) {
			for(CCDataElement myActionXML:_myActionsMap.get(theID)) {
				String myWidgetPath = myActionXML.attribute("widget");
				
				CCUIAction myAction = (CCUIAction)_mySerializer.toObject(myActionXML);
				
				if(myWidgetPath.indexOf(".")>=0) {
					myWidgetPath = myWidgetPath.substring(myWidgetPath.indexOf("."));
					CCUIWidget mySubwidget = (CCUIWidget)CCXMLPropertyUtil.property(myWidgetPath, myWidget);
					myAction.widget(mySubwidget);
				}else {
					myAction.widget(myWidget);
				}
				
				myAction.init(this);
			}
		}
		
		_myWidgets.add(myWidget);
		
		return myWidget;
	}
	
	public void checkEvent(CCVector2 theVector, CCUIInputEventType theEventType) {
		for(CCUIWidget myWidget:_myWidgets) {
			myWidget.checkEvent(theVector, theEventType);
		}
	}
	
	public void update(double theDeltaTime) {
		for(CCUIWidget myWidget:_myWidgets) {
			myWidget.update(theDeltaTime);
		}
		for(CCUIAnimator myAnimation:_myAnimations.values()) {
			myAnimation.update(theDeltaTime);
		}
	}

	
	public void draw(CCGraphics g) {
		for(CCUIWidget myWidget:_myWidgets) {
			myWidget.draw(g);
		}
	}
	
	public List<CCUIWidget> widgets(){
		return _myWidgets;
	}
	
}
