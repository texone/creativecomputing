/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.gl.app.container;

import java.awt.Rectangle;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorAdapter;
import cc.creativecomputing.core.logging.CCLog;
//import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.gl.app.CCCursor;
import cc.creativecomputing.gl.app.events.CCKeyEvent;
import cc.creativecomputing.gl.app.events.CCKeyEvent.CCKeyEventType;
import cc.creativecomputing.gl.app.events.CCMouseEvent;
import cc.creativecomputing.gl.app.events.CCMouseEvent.CCMouseEventType;
import cc.creativecomputing.gl.app.events.CCMouseWheelEvent;

import com.jogamp.nativewindow.util.InsetsImmutable;
import com.jogamp.newt.Display.PointerIcon;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.Gamma;

/**
 * @author texone
 */
public class GLWindowContainer extends GLContainer{
	
	private CCAbstractGLContext<?> _myContext;
	
	private GLWindow _myWindow;
	
	private boolean _mySkipResize = false;
	private boolean _mySkipMove = false;

	public GLWindowContainer(final CCAbstractGLContext<?> theExtension) {
		_myContext = theExtension;
		_myWindow = GLWindow.create(_myContext.glCapabilities());
		_myWindow.setTitle(_myContext.title);
//		_myWindow.setResizable(_myExtension.isResizable());
		_myWindow.setUndecorated(_myContext.undecorated);
		_myWindow.setAlwaysOnTop(_myContext.alwaysOnTop);
		float[] myScale = new float[2];
		_myWindow.getCurrentSurfaceScale(myScale);
		_myWindow.setSize(_myContext.width, _myContext.height);
		_myWindow.setVisible(true);
		
		_myWindow.setSurfaceScale(new float[]{_myContext.pixelScale.id(), _myContext.pixelScale.id()});
		
		_myWindow.addKeyListener(new KeyAdapter() {
            float gamma = 1f;
            float brightness = 0f;
            float contrast = 1f;

            @Override
            public void keyPressed(final KeyEvent e) {
                if( e.isAutoRepeat() ) {
                    return;
                }
                if( e.getKeySymbol()== KeyEvent.VK_G ) {
                    new Thread() {
                        public void run() {
                            final float newGamma = gamma + ( e.isShiftDown() ? -0.1f : 0.1f );
                            System.err.println("[set gamma]: "+gamma+" -> "+newGamma);
                            if( Gamma.setDisplayGamma(_myWindow, newGamma, brightness, contrast) ) {
                                gamma = newGamma;
                            }
                    } }.start();
                } else if(e.getKeyChar()=='c') {
                    new Thread() {
                        public void run() {
                            final Thread t = _myWindow.setExclusiveContextThread(null);
                            System.err.println("[set pointer-icon pre]");
                            final PointerIcon currentPI = _myWindow.getPointerIcon();
//                            final PointerIcon newPI;
//                            if( pointerIconIdx >= pointerIcons.length ) {
//                                newPI=null;
//                                pointerIconIdx=0;
//                            } else {
//                                newPI=pointerIcons[pointerIconIdx++];
//                            }
//                            _myWindow.setPointerIcon( newPI );
                            System.err.println("[set pointer-icon post] "+currentPI+" -> "+_myWindow.getPointerIcon());
                            _myWindow.setExclusiveContextThread(t);
                    } }.start();
                } else if(e.getKeyChar()=='j') {
                    new Thread() {
                        public void run() {
                            final Thread t = _myWindow.setExclusiveContextThread(null);
                            System.err.println("[set mouse confined pre]: "+_myWindow.isPointerConfined());
                            _myWindow.confinePointer(!_myWindow.isPointerConfined());
                            System.err.println("[set mouse confined post]: "+_myWindow.isPointerConfined());
                            if(!_myWindow.isPointerConfined()) {
//                                demo.setConfinedFixedCenter(false);
                            }
                            _myWindow.setExclusiveContextThread(t);
                    } }.start();
                } else if(e.getKeyChar()=='J') {
                    new Thread() {
                        public void run() {
                            final Thread t = _myWindow.setExclusiveContextThread(null);
                            System.err.println("[set mouse confined pre]: "+_myWindow.isPointerConfined());
                            _myWindow.confinePointer(!_myWindow.isPointerConfined());
                            System.err.println("[set mouse confined post]: "+_myWindow.isPointerConfined());
//                            demo.setConfinedFixedCenter(_myWindow.isPointerConfined());
                            _myWindow.setExclusiveContextThread(t);
                    } }.start();
                } else if(e.getKeyChar()=='w') {
                    new Thread() {
                        public void run() {
                            final Thread t = _myWindow.setExclusiveContextThread(null);
                            System.err.println("[set mouse pos pre]");
                            _myWindow.warpPointer(_myWindow.getSurfaceWidth()/2, _myWindow.getSurfaceHeight()/2);
                            System.err.println("[set mouse pos post]");
                            _myWindow.setExclusiveContextThread(t);
                    } }.start();
                } 
            }
        });
		
		_myWindow.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent theArg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent theEvent) {
				if(theEvent.getKeyCode() == KeyEvent.VK_ESCAPE){
					if(_myContext.fullscreen){
						_myContext.fullscreen = false;
						return;
					}
					switch(_myContext.closeOperation) {
					case DO_NOTHING_ON_CLOSE:
						break;
					case HIDE_ON_CLOSE:
						_myContext.visible = false;
						break;
					case DISPOSE_ON_CLOSE:
						close();
						break;
					case EXIT_ON_CLOSE:
						_myContext.stop();
						break;
					}
				}
			}
		});
		
		_myWindow.addKeyListener(new KeyListener() {
			
//			@Override
//			public void keyTyped(KeyEvent theEvent) {
//				_myApplication.enqueueKeyEvent(new CCKeyEvent(theEvent, CCKeyEventType.TYPED));
//			}
			
			@Override
			public void keyReleased(KeyEvent theEvent) {
				_myContext.enqueueKeyEvent(new CCKeyEvent(theEvent, CCKeyEventType.RELEASED));
			}
			
			@Override
			public void keyPressed(KeyEvent theEvent) {
				_myContext.enqueueKeyEvent(new CCKeyEvent(theEvent, CCKeyEventType.PRESSED));
			}
		});
		
		_myWindow.addWindowListener(new WindowListener() {
			
			@Override
			public void windowResized(com.jogamp.newt.event.WindowEvent theEvent) {
//				_mySkipResize = true;
//				_myContext.width = _myWindow.getWidth() - _myWindow.getInsets().getTotalWidth();
//				_myContext.height = _myWindow.getHeight() - _myWindow.getInsets().getTotalHeight();
//				_mySkipResize = false;
			}
			
			@Override
			public void windowRepaint(WindowUpdateEvent arg0) {}
			
			@Override
			public void windowMoved(com.jogamp.newt.event.WindowEvent arg0) {
				_mySkipMove = true;
				_myContext.windowX = _myWindow.getX();
				_myContext.windowY = _myWindow.getY();
				_mySkipMove = false;
			}
			
			@Override
			public void windowLostFocus(com.jogamp.newt.event.WindowEvent arg0) {}
			
			@Override
			public void windowGainedFocus(com.jogamp.newt.event.WindowEvent arg0) {}
			
			@Override
			public void windowDestroyed(com.jogamp.newt.event.WindowEvent arg0) {
				_myContext.stop();
			}
			
			@Override
			public void windowDestroyNotify(com.jogamp.newt.event.WindowEvent arg0) {
			}
		});
		
		_myWindow.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseWheelMoved(MouseEvent theEvent) {
				
				_myContext.enqueueMouseWheelEvent(new CCMouseWheelEvent(theEvent.getRotationScale(), theEvent.getRotation()));
			}
			
			@Override
			public void mouseReleased(MouseEvent theEvent) {
				_myContext.enqueueMouseEvent(new CCMouseEvent(theEvent, CCMouseEventType.MOUSE_RELEASED));
			}
			
			@Override
			public void mousePressed(MouseEvent theEvent) {
				_myContext.enqueueMouseEvent(new CCMouseEvent(theEvent, CCMouseEventType.MOUSE_PRESSED));
			}
			
			@Override
			public void mouseMoved(MouseEvent theEvent) {
				_myContext.enqueueMouseEvent(new CCMouseEvent(theEvent, CCMouseEventType.MOUSE_MOVED));
			}
			
			@Override
			public void mouseExited(MouseEvent theEvent) {
				_myContext.enqueueMouseEvent(new CCMouseEvent(theEvent, CCMouseEventType.MOUSE_EXITED));
			}
			
			@Override
			public void mouseEntered(MouseEvent theEvent) {
				_myContext.enqueueMouseEvent(new CCMouseEvent(theEvent, CCMouseEventType.MOUSE_ENTERED));
			}
			
			@Override
			public void mouseDragged(MouseEvent theEvent) {
				_myContext.enqueueMouseEvent(new CCMouseEvent(theEvent, CCMouseEventType.MOUSE_DRAGGED));
			}
			
			@Override
			public void mouseClicked(MouseEvent theEvent) {
				_myContext.enqueueMouseEvent(new CCMouseEvent(theEvent, CCMouseEventType.MOUSE_CLICKED));
			}
		});
	}
	
	private class CCWindowThread extends Thread{
		@Override
		public void run() {
			final Thread t = _myWindow.setExclusiveContextThread(null);
            windowTask();
            _myWindow.setExclusiveContextThread(t);
		}
		
		public void windowTask(){}
	}
	
	public void fullScreen(boolean theSetFullScreen){
		new CCWindowThread() {
            public void windowTask() {
            	_myWindow.setFullscreen(theSetFullScreen);
            };
        }.start();
	}
	
	@Override
	public void pixelScale(CCPixelScale thePixelScale) {
		new CCWindowThread() {
            public void windowTask() {
            	_myWindow.setSurfaceScale(new float[]{_myContext.pixelScale.id(), _myContext.pixelScale.id()});
            };
        }.start();
	}
	
	public void alwaysOnTop(boolean theSetAlwaysOnTop){
		new CCWindowThread() {
            public void windowTask() {
            	_myWindow.setAlwaysOnTop(theSetAlwaysOnTop);
            } 
		}.start();
	}
	
	@Override
	public void undecorated(boolean theSetUndecorated){
		new CCWindowThread() {
            public void windowTask() {
            	_myWindow.setUndecorated(theSetUndecorated);
            } 
		}.start();
	}
	
	public void showPointer(boolean theShowPointer){
		new CCWindowThread() {
            public void windowTask() {
                _myWindow.setPointerVisible(theShowPointer);
            } 
		}.start();
	}
	
	public void title(String theTitle){
		new CCWindowThread() {
            public void windowTask() {
                _myWindow.setTitle(theTitle);
            }
		}.start();
	}
	

	
	public void inVsync(boolean theIsInVsync){
		new CCWindowThread() {
            public void windowTask() {
                _myWindow.getGL().setSwapInterval(theIsInVsync ? 1 : 0);
            } 
		}.start();
	}
	
	@Override
	public void setVisible(boolean theIsVisible) {
		_myWindow.setVisible(theIsVisible);
		if(theIsVisible)_myWindow.requestFocus();
	}
	
	private int _myWindowX = -1;
	private int _myWindowY = -1;
	
	public void windowX(int theWindowX){
		_myWindowX = theWindowX;
		position(_myWindowX, _myWindowY);
	}
	
	public void windowY(int theWindowY){
		_myWindowY = theWindowY;
		position(_myWindowX, _myWindowY);
	}
	
	public void position(int theX, int theY){
		if(_mySkipMove)return;
		new CCWindowThread() {
            public void windowTask() {
            	Rectangle myBounds = _myContext.deviceSetup().displayConfiguration().getBounds();

    			int myX = theX > -1 ? myBounds.x + _myContext.windowX : myBounds.x + (myBounds.width - _myContext.width)/2;
    			int myY = theY > -1 ? myBounds.y + _myContext.windowY : myBounds.y + (myBounds.height - _myContext.height)/2;
    					
            	_myWindow.setPosition(myX, myY);
            }
		}.start();
	}
	
	@Override
	public void size(int theWidth, int theHeight){
		if(_mySkipResize)return;
		new CCWindowThread() {
            public void windowTask() {

    			final InsetsImmutable myInsets = _myWindow.getInsets();
    			//get insets to adjust frame size
    			_myWindow.setSize(
    				theWidth, 
    				theHeight
    			);
            }
		}.start();
	}
    
    public void windowX(){
		
	}
	
	private boolean _myIsClosed = false;
	
	@Override
	public void close() {
		if(_myIsClosed)return;
		
		_myIsClosed = true;
		_myWindow.destroy();
	}

	@Override
	public int x() {
		return _myWindow.getX();
	}

	@Override
	public int y() {
		return _myWindow.getY();
	}

	@Override
	public int width() {
		return _myWindow.getWidth();
	}

	@Override
	public int height() {
		return _myWindow.getHeight();
	}
	
	@Override
	public String title() {
		return _myWindow.getTitle();
	}


	@Override
	public boolean isVisible() {
		return _myWindow.isVisible();
	}
	
	@Override
	public void noCursor(){
		_myWindow.setPointerVisible(false);
	}
	
	@Override
	public void cursor(final CCCursor theCursor){
		_myWindow.setPointerVisible(true);
	}
	
	@Override
	public GLAutoDrawable glAutoDrawable() {
		return _myWindow;
	}
	
	private CCAnimatorAdapter _myDisplayUpdate = null;

	@Override
	public void handleAddUpdates(CCAnimator theAnimatorModule) {
		_myDisplayUpdate = new CCAnimatorAdapter() {
			
			@Override
			public void update(CCAnimator theAnimator) {
				if(updateDisplay())_myWindow.display();
			}
		};
		
		theAnimatorModule.listener().add(_myDisplayUpdate);
	}
	
	@Override
	public void handleRemoveUpdates(CCAnimator theAnimatorModule) {
		theAnimatorModule.listener().remove(_myDisplayUpdate);
	}
}
