package cc.creativecomputing.gl.app;

import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCBasicAppListener;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.gl.app.events.CCKeyListener;
import cc.creativecomputing.gl.app.events.CCMouseListener;
import cc.creativecomputing.gl.app.events.CCMouseMotionListener;
import cc.creativecomputing.gl.app.events.CCMouseWheelListener;

public class CCGLAdapter<GLGraphicsType extends CCGLGraphics, GLContextType extends CCAbstractGLContext<GLGraphicsType>> implements CCAnimatorListener, CCBasicAppListener, CCGLListener<GLGraphicsType> {

	protected GLContextType _myContext;
	
	protected CCControlApp _myControlApp;
	
	
	
	public CCControlApp controlApp(){
		return _myControlApp;
	}
	
	public TimelineContainer timeline(){
		return _myControlApp.timeline();
	}
	
	public void controlApp(CCControlApp theControlApp){
		_myControlApp = theControlApp;
	}
	
	public void glContext(GLContextType theContext){
		_myContext = theContext;
	}
	
	public GLContextType glContext(){
		return _myContext;
	}
	
	public CCListenerManager<CCKeyListener> keyListener(){
		return _myContext.keyListener();
	}
	
	public CCListenerManager<CCMouseListener> mouseListener(){
		return _myContext.mouseListener();
	}
	
	public CCListenerManager<CCMouseMotionListener> mouseMotionListener(){
		return _myContext.mouseMotionListener();
	}
	
	public CCListenerManager<CCMouseWheelListener> mouseWheelListener(){
		return _myContext.mouseWheelListener();
	}
	
	public CCAnimator animator(){
		return _myContext._myAnimatorModule;
	}
	
	@Override
	public void start() {
	}

	@Override
	public void start(CCAnimator theAnimator) {
	}

	@Override
	public void init(GLGraphicsType g) {
	}
	
	public void init(GLGraphicsType g, CCAnimator theAnimator){
		
	}

	@Override
	public void reshape(GLGraphicsType g) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(GLGraphicsType g) {
	}

	@Override
	public void stop(CCAnimator theAnimator) {
	}

	@Override
	public void stop() {
	}

	@Override
	public void dispose(GLGraphicsType g) {
	}

	
	
	
}
