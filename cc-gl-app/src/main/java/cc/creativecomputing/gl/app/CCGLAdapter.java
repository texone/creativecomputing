package cc.creativecomputing.gl.app;

import cc.creativecomputing.app.modules.CCAnimatorListener;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCBasicAppListener;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.gl.app.events.CCKeyListener;
import cc.creativecomputing.gl.app.events.CCKeyPressedListener;
import cc.creativecomputing.gl.app.events.CCKeyReleasedListener;
import cc.creativecomputing.gl.app.events.CCKeyTypedListener;
import cc.creativecomputing.gl.app.events.CCMouseClickedListener;
import cc.creativecomputing.gl.app.events.CCMouseDraggedListener;
import cc.creativecomputing.gl.app.events.CCMouseEnteredListener;
import cc.creativecomputing.gl.app.events.CCMouseExitedListener;
import cc.creativecomputing.gl.app.events.CCMouseListener;
import cc.creativecomputing.gl.app.events.CCMouseMotionListener;
import cc.creativecomputing.gl.app.events.CCMouseMovedListener;
import cc.creativecomputing.gl.app.events.CCMousePressedListener;
import cc.creativecomputing.gl.app.events.CCMouseReleasedListener;
import cc.creativecomputing.gl.app.events.CCMouseSimpleInfo;
import cc.creativecomputing.gl.app.events.CCMouseWheelListener;

public class CCGLAdapter<GLGraphicsType extends CCGLGraphics<?>, GLContextType extends CCAbstractGLContext<GLGraphicsType>> implements CCAnimatorListener, CCBasicAppListener, CCGLListener<GLGraphicsType> {

	protected GLContextType _myContext;
	
	protected CCControlApp _myControlApp;

	protected CCMouseSimpleInfo _myMouseInfo = new CCMouseSimpleInfo();
	
	
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
		mouseListener().add(_myMouseInfo);
		mouseMotionListener().add(_myMouseInfo);
	}
	
	public GLContextType glContext(){
		return _myContext;
	}
	
	public CCMouseSimpleInfo mouse(){
		return _myMouseInfo;
	}
	
	@SuppressWarnings("rawtypes")
	public CCListenerManager<CCGLListener> glListener(){
		return _myContext.listener();
	}
	
	public CCListenerManager<CCKeyListener> keyListener(){
		return _myContext.keyListener();
	}
	
	public CCListenerManager<CCKeyPressedListener> keyPressedListener(){
		return _myContext.keyPressedListener();
	}
	
	public CCListenerManager<CCKeyReleasedListener> keyReleasedListener(){
		return _myContext.keyReleasedListener();
	}
	
	public CCListenerManager<CCKeyTypedListener> keyTypedListener(){
		return _myContext.keyTypedListener();
	}
	
	public CCListenerManager<CCMouseListener> mouseListener(){
		return _myContext.mouseListener();
	}
	
	public CCListenerManager<CCMousePressedListener> mousePressed(){
		return _myContext.mousePressed();
	}
	
	public CCListenerManager<CCMouseReleasedListener> mouseReleased(){
		return _myContext.mouseReleased();
	}
	
	public CCListenerManager<CCMouseEnteredListener> mouseEntered(){
		return _myContext.mouseEntered();
	}
	
	public CCListenerManager<CCMouseExitedListener> mouseExited(){
		return _myContext.mouseExited();
	}
	
	public CCListenerManager<CCMouseClickedListener> mouseClicked(){
		return _myContext.mouseClicked();
	}
	
	public CCListenerManager<CCMouseMotionListener> mouseMotionListener(){
		return _myContext.mouseMotionListener();
	}
	
	public CCListenerManager<CCMouseMovedListener> mouseMoved(){
		return _myContext.mouseMoved();
	}
	
	public CCListenerManager<CCMouseDraggedListener> mouseDragged(){
		return _myContext.mouseDragged();
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
	
	public void setupControls(CCControlApp theControlApp){
		
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
