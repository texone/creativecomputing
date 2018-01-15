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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorAdapter;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.gl.app.CCAbstractGLContext;
import cc.creativecomputing.gl.app.CCCursor;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.gl.app.events.CCKeyEvent;
import cc.creativecomputing.gl.app.events.CCMouseEvent;
import cc.creativecomputing.gl.app.events.CCMouseWheelEvent;
import cc.creativecomputing.math.CCColor;

import com.jogamp.opengl.GLAutoDrawable;

public class GLJavaComponentContainer extends GLContainer{
	
	private static class CCGLJavaRepaintManager extends CCAnimatorAdapter implements Runnable{

		private volatile ArrayList<GLJavaComponentContainer> _myContainers = new ArrayList<>();

		// For efficient rendering of Swing components, in particular when
		// they overlap one another
		private List<JComponent> _myLightweights = new ArrayList<JComponent>();
		
		private Map<RepaintManager, RepaintManager> repaintManagers = new IdentityHashMap<RepaintManager, RepaintManager>();
		private Map<JComponent, Rectangle> dirtyRegions = new IdentityHashMap<JComponent, Rectangle>();
		
		/**
		 * Adds a drawable to the list managed by this Animator. 
		 * @param theContainer the drawable to add
		 */
		public synchronized void add(GLJavaComponentContainer theContainer) {
			@SuppressWarnings("unchecked")
			ArrayList<GLJavaComponentContainer> myNewList = (ArrayList<GLJavaComponentContainer>) _myContainers.clone();
			myNewList.add(theContainer);
			_myContainers = myNewList;
			notifyAll();
		}

		/** Removes a drawable from the list managed by this Animator. */
		public synchronized void remove(GLJavaComponentContainer theContainer) {
			@SuppressWarnings("unchecked")
			ArrayList<GLJavaComponentContainer> myNewList = (ArrayList<GLJavaComponentContainer>) _myContainers.clone();
			myNewList.remove(theContainer);
			_myContainers = myNewList;
		}
		
		@Override
		public void update(CCAnimator theAnimator) {
			for (int i = _myContainers.size() - 1; i >= 0; i--) {
				GLJavaComponentContainer myGLContainer = _myContainers.get(i);
				
				if (myGLContainer._myDrawable instanceof JComponent) {
					// Lightweight components need a more efficient drawing
					// scheme than simply forcing repainting of each one in
					// turn since drawing one can force another one to be
					// drawn in turn
					_myLightweights.add((JComponent) myGLContainer._myDrawable);
				} else {
					if(myGLContainer.updateDisplay())myGLContainer._myDrawable.display();

				}
			}
			if (_myLightweights.size() > 0) {
				try {
					SwingUtilities.invokeAndWait(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				_myLightweights.clear();
			}
		}
		
		public void run() {
			for (JComponent comp : _myLightweights) {
				RepaintManager rm = RepaintManager.currentManager(comp);
				rm.markCompletelyDirty(comp);
				repaintManagers.put(rm, rm);

				// RepaintManagers don't currently optimize the case of
				// overlapping sibling components. If we have two
				// JInternalFrames in a JDesktopPane, the redraw of the
				// bottom one will cause the top one to be redrawn as
				// well. The top one will then be redrawn separately. In
				// order to optimize this case we need to compute the union
				// of all of the dirty regions on a particular JComponent if
				// optimized drawing isn't enabled for it.

				// Walk up the hierarchy trying to find a non-optimizable
				// ancestor
				Rectangle visible = comp.getVisibleRect();
				int x = visible.x;
				int y = visible.y;
				while (comp != null) {
					x += comp.getX();
					y += comp.getY();
					Component c = comp.getParent();
					if ((c == null) || (!(c instanceof JComponent))) {
						comp = null;
					} else {
						comp = (JComponent) c;
						if (!comp.isOptimizedDrawingEnabled()) {
							rm = RepaintManager.currentManager(comp);
							repaintManagers.put(rm, rm);
							// Need to dirty this region
							Rectangle dirty = dirtyRegions.get(comp);
							if (dirty == null) {
								dirty = new Rectangle(x, y, visible.width, visible.height);
								dirtyRegions.put(comp, dirty);
							} else {
								// Compute union with already dirty region
								// Note we could compute multiple
								// non-overlapping
								// regions: might want to do that in the future
								// (prob. need more complex algorithm -- dynamic
								// programming?)
								dirty.add(new Rectangle(x, y, visible.width, visible.height));
							}
						}
					}
				}
			}

			// Dirty any needed regions on non-optimizable components
			for (JComponent comp : dirtyRegions.keySet()) {
				Rectangle rect = dirtyRegions.get(comp);
				RepaintManager rm = RepaintManager.currentManager(comp);
				rm.addDirtyRegion(comp, rect.x, rect.y, rect.width, rect.height);
			}

			// Draw all dirty regions
			for (RepaintManager rm : repaintManagers.keySet()) {
				rm.paintDirtyRegions();
			}
			dirtyRegions.clear();
			repaintManagers.clear();
		}
	}
	
	private interface CCJavaContainer {

		void addWindowListener(WindowAdapter theCreateWindowAdapter);

		Container getContentPane();

		void setVisible(boolean theVisible);

		int getX();

		int getY();

		int getWidth();

		int getHeight();
		
		void size(int theWidth, int theHeight);
		
		void position(int theX, int theY);

		boolean isVisible();

		String getTitle();

		void setTitle(String theTitle);

		void processWindowEvent(WindowEvent theWindowEvent);
		
		Window window();
		
	}
	
	private static class CCBaseFrame extends JFrame implements CCJavaContainer{

		private static final long serialVersionUID = 7937229563871713080L;

		private CCBaseFrame(String title, GraphicsConfiguration gc) {
			super(title, gc);
		}

		public void processWindowEvent(WindowEvent theWindowEvent){
			super.processWindowEvent(theWindowEvent);
		}

		@Override
		public void addWindowListener(WindowAdapter theCreateWindowAdapter) {
			super.addWindowListener(theCreateWindowAdapter);
		}

		@Override
		public Window window() {
			return this;
		}

		@Override
		public void size(int theWidth, int theHeight) {
			setSize(theWidth, theHeight);
		}
		
		@Override
		public void position(int theX, int theY) {
			setLocation(theX, theY);
		}
	}
	
	private static class CCFrame extends CCBaseFrame{

		private static final long serialVersionUID = -3566712236052208336L;
		
		public CCFrame(final CCAbstractGLContext<?> theExtension) {
			super(theExtension.title,theExtension.deviceSetup().displayConfiguration());

			setResizable(theExtension.resizable);
			setUndecorated(theExtension.undecorated);
			setAlwaysOnTop(theExtension.alwaysOnTop);
			getContentPane().setLayout(new BorderLayout());
			
			CCColor myBackground = theExtension.background;
			getContentPane().setBackground(new Color(
				(float)myBackground.red(),
				(float)myBackground.green(),
				(float)myBackground.blue())
			);
			
			setDefaultCloseOperation(theExtension.closeOperation.id());
			pack();
		}
	}
	
	private static class CCFullFrame extends CCBaseFrame {

		private static final long serialVersionUID = -3566712236052208336L;

		public CCFullFrame(CCAbstractGLContext<?> theExtension, Component theGLComponent) {
			super(theExtension.title,theExtension.deviceSetup().displayConfiguration());

			getContentPane().add(theGLComponent);
			setDefaultCloseOperation(theExtension.closeOperation.id());
//			pack();
			//
			Rectangle myBounds = theExtension.deviceSetup().displayConfiguration().getBounds();
			setBounds(myBounds);
			
			CCColor myBackground = theExtension.background;
			getContentPane().setBackground(new Color(
				(float)myBackground.red(),
				(float)myBackground.green(),
				(float)myBackground.blue())
			);
			
			
			
			setUndecorated(true);     // no decoration such as title bar
			setExtendedState(Frame.MAXIMIZED_BOTH);  // full screen mode
//			pack();
			
			theExtension.deviceSetup().display().setFullScreenWindow(this);
			setVisible(true);
		}
	}
	
	private static class CCDialog extends JDialog implements CCJavaContainer{

		private static final long serialVersionUID = -3566712236052208336L;
		
		
		public CCDialog(final Window theOwner, CCAbstractGLContext<?> theExtension) {
			super(theOwner,theExtension.title,Dialog.ModalityType.MODELESS,theExtension.deviceSetup().displayConfiguration());

			setResizable(theExtension.resizable);
			setUndecorated(theExtension.undecorated);
			getContentPane().setLayout(new BorderLayout());
			
			CCColor myBackground = theExtension.background;
			getContentPane().setBackground(new Color(
				(float)myBackground.red(),
				(float)myBackground.green(),
				(float)myBackground.blue())
			);
			
			setDefaultCloseOperation(theExtension.closeOperation.id());
			pack();
			
		}
		
		public void processWindowEvent(WindowEvent theEvent) {
			super.processWindowEvent(theEvent);
		}
		
		@Override
		public void addWindowListener(WindowAdapter theCreateWindowAdapter) {
			addWindowListener(theCreateWindowAdapter);
		}
		
		@Override
		public Window window() {
			return this;
		}
		

		@Override
		public void size(int theWidth, int theHeight) {
			setSize(theWidth, theHeight);
		}
		
		@Override
		public void position(int theX, int theY) {
			setLocation(theX, theY);
		}
	}
	
	private static void setup(Container theContainer, CCAbstractGLContext<?> theExtension){
		
		
		//get insets to adjust frame size
		final Insets myInsets = theContainer.getInsets();
		
		theContainer.setSize(
			theExtension.width + myInsets.left + myInsets.right, 
			theExtension.height + myInsets.top + myInsets.bottom
		);
				
		Rectangle myBounds = theExtension.deviceSetup().displayConfiguration().getBounds();
				
		if(theExtension.windowX > -1){
			theContainer.setLocation(myBounds.x+ theExtension.windowX,myBounds.y + theExtension.windowY);
		}else{
			theContainer.setLocation(
				myBounds.x + (myBounds.width - theExtension.width)/2,
				myBounds.y + (myBounds.height - theExtension.height)/2
			);
		}
	}
	
	private CCAbstractGLContext<?> _myGLModule;
	private GLAutoDrawable _myDrawable;
	protected Component _myGLComponent;
	protected CCJavaContainer _myContainerComponent;
	private int _myLastPressedButton = 0;
	
	public GLJavaComponentContainer(
		final CCAbstractGLContext<?> theExtension, 
		GLAutoDrawable theDrawable, 
		Component theGLComponent
	){
		_myGLModule = theExtension;
		_myDrawable = theDrawable;
		_myGLComponent = theGLComponent;
		
		if(_myGLModule.fullscreen){
			_myContainerComponent = new CCFullFrame(_myGLModule, theGLComponent);
		}else{
			switch(_myGLModule.containerType){
			case DIALOG:
				_myContainerComponent = new CCDialog(_myGLModule.dialogOwner(), _myGLModule);
				setup(_myContainerComponent.window(), theExtension);
				break;
			case FRAME:
				_myContainerComponent = new CCFrame(_myGLModule);
				setup(_myContainerComponent.window(), theExtension);
				break;
			default:
				throw new RuntimeException("Invalid display mode for java container: " + _myGLModule.displayMode+" : " + _myGLModule.containerType);
			}
		}
		

		_myContainerComponent.addWindowListener(createWindowAdapter());
		_myContainerComponent.getContentPane().add(theGLComponent, BorderLayout.CENTER);
		

		
		_myGLComponent.addKeyListener(new KeyListener() {
			
			public void keyPressed(KeyEvent theKeyEvent) {
				if(theKeyEvent.getKeyCode() == KeyEvent.VK_ESCAPE){
					switch(_myGLModule.closeOperation) {
					case DO_NOTHING_ON_CLOSE:
						break;
					case HIDE_ON_CLOSE:
						setVisible(false);
						break;
					case DISPOSE_ON_CLOSE:
						close();
						break;
					case EXIT_ON_CLOSE:
						_myGLModule.stop();
						break;
					}
				}
				
				_myGLModule.enqueueKeyEvent(new CCKeyEvent(theKeyEvent));
			}

			public final void keyReleased(KeyEvent theKeyEvent) {
				_myGLModule.enqueueKeyEvent(new CCKeyEvent(theKeyEvent));
			}

			public final void keyTyped(KeyEvent theKeyEvent) {
				_myGLModule.enqueueKeyEvent(new CCKeyEvent(theKeyEvent));
			}
		});
		
		_myGLComponent.addMouseListener(new MouseListener() {
			
			
			public void mousePressed(final MouseEvent theMouseEvent) {
				_myLastPressedButton = theMouseEvent.getButton();
				_myGLModule.enqueueMouseEvent(new CCMouseEvent(theMouseEvent));
			}

			public void mouseReleased(final MouseEvent theMouseEvent) {
				_myGLModule.enqueueMouseEvent(new CCMouseEvent(theMouseEvent));
			}

			public void mouseClicked(final MouseEvent theMouseEvent) {
				_myGLModule.enqueueMouseEvent(new CCMouseEvent(theMouseEvent));
			}
			
			public void mouseEntered(final MouseEvent theMouseEvent) {
				_myGLModule.enqueueMouseEvent(new CCMouseEvent(theMouseEvent));
			}

			public void mouseExited(final MouseEvent theMouseEvent) {
				_myGLModule.enqueueMouseEvent(new CCMouseEvent(theMouseEvent));
			}
		});
		
		_myGLComponent.addMouseMotionListener(new MouseMotionListener() {
			
			public void mouseDragged(final MouseEvent theMouseEvent) {
				_myGLModule.enqueueMouseEvent(new CCMouseEvent(theMouseEvent,_myLastPressedButton));
			}

			public void mouseMoved(final MouseEvent theMouseEvent) {
				_myGLModule.enqueueMouseEvent(new CCMouseEvent(theMouseEvent));
			}
		});
		
		_myGLComponent.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent theMouseEvent) {
				_myGLModule.enqueueMouseWheelEvent(new CCMouseWheelEvent(theMouseEvent));
			}
		});
	}
	
	public WindowAdapter createWindowAdapter(){
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				switch(_myGLModule.closeOperation) {
				case DO_NOTHING_ON_CLOSE:
					break;
				case HIDE_ON_CLOSE:
					setVisible(false);
					break;
				case DISPOSE_ON_CLOSE:
				case EXIT_ON_CLOSE:
					_myGLModule.stop();
					break;
				}
			}
		};
	}
	
	@Override
	public void setVisible(boolean visible) {
		_myGLComponent.setVisible(visible);
		_myContainerComponent.setVisible(visible);

		//attempt to get the focus of the canvas
		if(visible)_myGLComponent.requestFocus();
	}

	@Override
	public int x() {
		return _myContainerComponent.getX();
	}

	@Override
	public int y() {
		return _myContainerComponent.getY();
	}

	@Override
	public int width() {
		return _myContainerComponent.getWidth();
	}

	@Override
	public int height() {
		return _myContainerComponent.getHeight();
	}
	

	@Override
	public void size(int theWidth, int theHeight) {
		_myContainerComponent.size(theWidth, theHeight);
	}
	
	@Override
	public void position(int theX, int theY) {
		_myContainerComponent.position(theX, theY);
	}
	
	@Override
	public void fullScreen(boolean theIsFullScreen) {
		
	}
	
	@Override
	public void pixelScale(CCPixelScale thePixelScale) {
		
	}
	
	@Override
	public void undecorated(boolean theIsDecorated) {
		
	}
	

	@Override
	public boolean isVisible() {
		return _myContainerComponent.isVisible();
	}
	
	@Override
	public void noCursor(){
		_myGLComponent.setCursor(_myGLComponent.getToolkit().createCustomCursor(new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB), new Point(10,10), "Cursor" ));
	}
	
	
	@Override
	public void cursor(final CCCursor theCursor){
		_myGLComponent.setCursor(theCursor.javaCursor());
	}

	@Override
	public GLAutoDrawable glAutoDrawable() {
		return _myDrawable;
	}
	
	@Override
	public String title() {
		return _myContainerComponent.getTitle();
	}
	
	@Override
	public void title(String theTitle) {
		_myContainerComponent.setTitle(theTitle);
	}
	
	@Override
	public void close() {
		_myDrawable.destroy();
		_myContainerComponent.processWindowEvent(new WindowEvent(_myContainerComponent.window(), _myGLModule.closeOperation.id()));
	}
	
	@Override
	public void handleAddUpdates(CCAnimator theAnimatorModule) {
		CCGLJavaRepaintManager myRepaintManager = null;
		for(CCAnimatorListener myListener:theAnimatorModule.listener()){
			if(myListener instanceof CCGLJavaRepaintManager){
				myRepaintManager = (CCGLJavaRepaintManager)myListener;
			}
		}
		if(myRepaintManager == null){
			myRepaintManager = new CCGLJavaRepaintManager();
			theAnimatorModule.listener().add(myRepaintManager);
		}
		myRepaintManager.add(this);
	}
	
	@Override
	public void handleRemoveUpdates(CCAnimator theAnimatorModule) {
		CCGLJavaRepaintManager myRepaintManager = null;
		for(CCAnimatorListener myListener:theAnimatorModule.listener()){
			if(myListener instanceof CCGLJavaRepaintManager){
				myRepaintManager = (CCGLJavaRepaintManager)myListener;
			}
		}
		if(myRepaintManager == null){
			throw new RuntimeException("Remove should not happen without add. If this happens it is a core api bug that you will hopefully report.");
		}
		theAnimatorModule.listener().remove(myRepaintManager);
		myRepaintManager.remove(this);
	}
}
