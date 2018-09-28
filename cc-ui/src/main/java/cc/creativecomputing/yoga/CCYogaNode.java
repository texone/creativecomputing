package cc.creativecomputing.yoga;

import static org.lwjgl.util.yoga.Yoga.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.lwjgl.util.yoga.YGNode;
import org.lwjgl.util.yoga.YGValue;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMatrix32;
import cc.creativecomputing.math.CCVector2;

public class CCYogaNode implements Iterable<CCYogaNode>{

	public final long id;

	private YGNode _myNode;

	public final CCEventManager<CCGLMouseEvent> mouseReleasedOutside = new CCEventManager<>();
	public final CCEventManager<CCGLMouseEvent> mouseReleased = new CCEventManager<>();
	public final CCEventManager<CCGLMouseEvent> mousePressed = new CCEventManager<>();
	public final CCEventManager<CCGLMouseEvent> mouseClicked = new CCEventManager<>();
	public final CCEventManager<CCVector2> mouseMoved = new CCEventManager<>();
	public final CCEventManager<CCVector2> mouseDragged = new CCEventManager<>();
	public final CCEventManager<Object> onOver = new CCEventManager<>();
	public final CCEventManager<Object> onOut = new CCEventManager<>();

	public final CCEventManager<CCVector2> scrollEvents = new CCEventManager<>();
	
	public final CCEventManager<?> focusGained = new CCEventManager<>();
	public final CCEventManager<?> focusLost = new CCEventManager<>();
	
	public final CCEventManager<CCGLKeyEvent> keyReleased = new CCEventManager<>();
	public final CCEventManager<CCGLKeyEvent> keyPressed = new CCEventManager<>();
	public final CCEventManager<CCGLKeyEvent> keyRepeatEvents = new CCEventManager<>();
	public final CCEventManager<Character> keyChar = new CCEventManager<>();

	public CCEventManager<CCGLTimer> updateEvents = new CCEventManager<>();

	protected CCMatrix32 _myLocalMatrix = new CCMatrix32();
	protected CCMatrix32 _myLocalInverseMatrix = new CCMatrix32();

	protected CCMatrix32 _myWorldMatrix = new CCMatrix32();
	protected CCMatrix32 _myWorldInverseMatrix = new CCMatrix32();
	
	private Optional<CCYogaNode> _myParent = Optional.empty();
	private List<CCYogaNode> _myChildren = new ArrayList<>();
	
	public String _myDebugName = "NODE";
	private CCColor _myDebugColor = CCColor.RED;
	
	protected Optional<CCYogaNode> _myOverlay = Optional.empty();
	
	protected boolean _myIsActive = true;

	private CCYogaNode(long theID) {
		id = theID;
		_myNode = YGNode.create(id);
	}

	public CCYogaNode() {
		this(YGNodeNew());
	}
	
	public CCYogaNode debugInfo(String theName, CCColor theColor) {
		_myDebugName = theName;
		_myDebugColor = theColor;
		return this;
	}
	
	public boolean isActive(){
		return _myIsActive;
	}
	
	public void isActive(boolean theIsActive){
		_myIsActive = theIsActive;
	}
	protected boolean _myIsOverlay = false;
	
	public void isOverlay(boolean theIsOverlay) {
		_myIsOverlay = theIsOverlay;
	}
	
	public void overlay(CCYogaNode theOverlay){
		theOverlay._myIsOverlay = true;
		_myOverlay = Optional.of(theOverlay);
	}
	
	public Optional<CCYogaNode> overlay(){
		return _myOverlay;
	}

	public void update(CCGLTimer theTimer) {
		updateEvents.event(theTimer);
		for(CCYogaNode myNode:_myChildren) {
			myNode.update(theTimer);
		}
	}
	
	public void updateMatrices(){
		_myLocalMatrix.reset();
		_myLocalMatrix.translate(left(), top());
		
		_myLocalInverseMatrix = _myLocalMatrix.inverse();
		
		if(_myParent.isPresent()){
			_myWorldMatrix = _myParent.get()._myWorldMatrix.clone();
			_myWorldMatrix.preApply(_myLocalMatrix);
			
			_myWorldInverseMatrix = _myWorldMatrix.inverse();
		}else{
			_myWorldMatrix = _myLocalMatrix;
			_myWorldInverseMatrix = _myLocalInverseMatrix;
		}
		
		for(CCYogaNode myWidget:this) {
			myWidget.updateMatrices();
		}
		
		_myOverlay.ifPresent(o ->{
			o._myLocalMatrix.set(_myLocalMatrix);
			o._myLocalMatrix.translate(o.left(),o.top());
			o._myLocalInverseMatrix = o._myLocalMatrix.inverse();
			CCMatrix32 myWorldInverse = _myWorldMatrix.clone();
			myWorldInverse.translate(o.left(),o.top());
			o._myWorldInverseMatrix.set(myWorldInverse.inverse());
		});
	}
	
	public void displayDebug(CCGraphics g) {
		g.pushMatrix();
	    g.translate(left(), top());

	    g.color(_myDebugColor);
		g.rect(0,0,width(),height());

        g.color(0, 0, 0);
		g.text(_myDebugName, 18, 18);
		
		for(CCYogaNode myChild:this) {
			myChild.displayDebug(g);
		}
	    g.popMatrix();
	}

	public void display(CCGraphics g) {
		// TODO Auto-generated method stub
		
	}
	
	public void parent(CCYogaNode theParent) {
		_myParent = Optional.ofNullable(theParent);
	}
	
	public Optional<CCYogaNode> parent() {
		return _myParent;
	}
	
	public CCYogaNode root(){
		if(!_myParent.isPresent())return this;
		else return _myParent.get().root();
	}

	public int childCount() {
		return _myChildren.size();
	}

	public CCYogaNode childAt(int i) {
		return _myChildren.get(i);
	}

	public void insertChild(CCYogaNode theNode, int i) {
		YGNodeInsertChild(id, theNode.id, i);
		theNode.parent(this);
		_myChildren.add(i, theNode);
	}
	
	public void addChild(CCYogaNode theNode) {
		if(_myChildren.contains(theNode))return;
		YGNodeInsertChild(id, theNode.id, _myChildren.size());
		theNode.parent(this);
		_myChildren.add(theNode);
	}

	public void removeChild(CCYogaNode theNode) {
		_myChildren.remove(theNode);
		theNode.parent(null);
		YGNodeRemoveChild(id, theNode.id);
	}
	
	public void removeAllChildren() {
		for(CCYogaNode myChild:new ArrayList<>(_myChildren)) {
			removeChild(myChild);
		}
	}

	public CCYogaNode removeChildAt(int i) {
		long childID = YGNodeGetChild(id, i);
		YGNodeRemoveChild(id, childID);
		CCYogaNode myResult = _myChildren.remove(i);
		myResult.parent(null);
		return myResult;
	}
	
	@Override
	public Iterator<CCYogaNode> iterator() {
		return _myChildren.iterator();
	}
	
	public CCMatrix32 localTransform() {
		return _myLocalMatrix;
	}
	
	public CCMatrix32 localInverseTransform() {
		return _myLocalInverseMatrix;
	}
	
	public CCMatrix32 worldTransform() {
		return _myWorldMatrix;
	}
	
	public CCMatrix32 worldInverseTransform() {
		return _myWorldInverseMatrix;
	}
	
	public boolean isInsideLocal(double theX, double theY) {
		return 
			theX >= 0 && 
			theX <= width() &&
			theY >= 0 && 
			theY <= height();
	}
	
	public boolean isInsideLocal(CCVector2 theVector) {
		return isInsideLocal(theVector.x, theVector.y);
	}
	
	public boolean isInside(CCVector2 theVector) {
		CCVector2 myLocalPos = _myWorldInverseMatrix.transform(theVector);
		return isInsideLocal(myLocalPos);
	}
	
	public boolean isInside(double theX, double theY) {
		return isInside(new CCVector2(theX, theY));
	}
	
	public boolean isEndNode() {
		return false;
	}
	
	public CCYogaNode childAtPosition(CCVector2 thePosition) {
		
		for(CCYogaNode myNode:_myChildren) {
			if(!myNode.isInside(thePosition)) {
				continue;
			}
			if(myNode.childCount() > 0 && !myNode.isEndNode()) {
				CCYogaNode myResult = myNode.childAtPosition(thePosition);
				if(myResult != null){
					return myResult;
				}else{
					return myNode;
				}
			}else {
				return myNode;
			}
			
		}
		return null;
	}

	/**
	 * Specifies the height of the element's content area.
	 */
	public void heightAuto() {
		YGNodeStyleSetHeightAuto(id);
	}

	/**
	 * Specifies the height of the element's content area in absolute pixels.
	 * Depending on other properties set on the Yoga node this may or may not be the
	 * final dimension of the node.
	 * 
	 * @param theHeight
	 *            height of the element's content area in absolute pixels.
	 */
	public CCYogaNode height(double theHeight) {
		YGNodeStyleSetHeight(id, (float) theHeight);
		return this;
	}

	/**
	 * Specifies the height of the element's content area in percentage of its
	 * parent's height.
	 * 
	 * @param theHeight
	 *            height of the element's content area in percentage of its parent's
	 *            height
	 */
	public void heightPercent(double theHeight) {
		YGNodeStyleSetHeightPercent(id, (float) theHeight);
	}

	public double styleHeight() {
		YGValue myValue = YGValue.create();
		YGNodeStyleGetHeight(id, myValue);
		return myValue.value();
	}

	/**
	 * Specifies the width of the element's content area.
	 */
	public void widthtAuto() {
		YGNodeStyleSetHeightAuto(id);
	}

	/**
	 * Specifies the width of the element's content area in absolute pixels.
	 * Depending on other properties set on the Yoga node this may or may not be the
	 * final dimension of the node.
	 * 
	 * @param theWidth
	 *            width of the element's content area in absolute pixels.
	 */
	public void width(double theWidth) {
		YGNodeStyleSetWidth(id, (float) theWidth);
	}

	/**
	 * Specifies the width of the element's content area in percentage of its
	 * parent's width.
	 * 
	 * @param theWidth
	 *            width of the element's content area in percentage of its parent's
	 *            width
	 */
	public void widthPercent(double theWidth) {
		YGNodeStyleSetWidthPercent(id, (float) theWidth);
	}

	public double styleWidth() {
		YGValue myValue = YGValue.create();
		YGNodeStyleGetWidth(id, myValue);
		return myValue.value();
	}

	/**
	 * Flex direction controls the direction in which children of a node are laid
	 * out. This is also referred to as the main axis. The main axis is the
	 * direction in which children are laid out. The cross axis the the axis
	 * perpendicular to the main axis, or the axis which wrapping lines are laid out
	 * in.
	 * 
	 * @author christian riekoff
	 *
	 */
	public static enum CCYogaFlexDirection {
		/**
		 * (DEFAULT) Align children from left to right. If wrapping is enabled then the
		 * next line will start under the first item on the left of the container.
		 */
		ROW(YGFlexDirectionRow),
		/**
		 * Align children from top to bottom. If wrapping is enabled then the next line
		 * will start to the left first item on the top of the container.
		 */
		COLUMN(YGFlexDirectionColumn),
		/**
		 * Align children from right to left. If wrapping is enabled then the next line
		 * will start under the first item on the right of the container.
		 */
		ROW_REVERSE(YGFlexDirectionRowReverse),
		/**
		 * Align children from bottom to top. If wrapping is enabled then the next line
		 * will start to the left first item on the bottom of the container.
		 */
		COLUMN_REVERSE(YGFlexDirectionColumnReverse);

		public final int id;

		private CCYogaFlexDirection(int theID) {
			id = theID;
		}

		public static CCYogaFlexDirection fromInt(int value) {
			switch (value) {
			case YGFlexDirectionColumn:
				return COLUMN;
			case YGFlexDirectionColumnReverse:
				return COLUMN_REVERSE;
			case YGFlexDirectionRow:
				return ROW;
			case YGFlexDirectionRowReverse:
				return ROW_REVERSE;
			default:
				throw new IllegalArgumentException("Unknown enum value: " + value);
			}
		}
	}

	/**
	 * Controls the direction in which children of a node are laid out.
	 * 
	 * @param theDirection
	 *            the direction in which children of a node are laid out
	 * @see CCYogaFlexDirection
	 */
	public void flexDirection(CCYogaFlexDirection theDirection) {
		YGNodeStyleSetFlexDirection(id, theDirection.id);
	}

	/**
	 * Returns the direction in which children of a node are laid out.
	 * 
	 * @return the direction in which children of a node are laid out.
	 */
	public CCYogaFlexDirection flexDirection() {
		return CCYogaFlexDirection.fromInt(YGNodeStyleGetFlexDirection(id));
	}

	/**
	 * The position type of an element defines how it is positioned within its
	 * parent.
	 * 
	 * @author christian riekoff
	 *
	 */
	public static enum CCYogaPositionType {
		/**
		 * When positioned absolutely an element doesn't take part in the normal layout
		 * flow. It is instead laid out independent of its siblings. The position is
		 * determined based on the top, right, bottom, and left values.
		 */
		ABSOLUTE(YGPositionTypeAbsolute),
		/**
		 * (DEFAULT) By default an element is positioned relatively. This means an
		 * element is positioned according to the normal flow of the layout, and then
		 * offset relative to that position based on the values of top, right, bottom,
		 * and left. The offset does not affect the position of any sibling or parent
		 * elements.
		 */
		RELATIVE(YGPositionTypeRelative);
		public final int id;

		private CCYogaPositionType(int theID) {
			id = theID;
		}

		public static CCYogaPositionType fromInt(int value) {
			switch (value) {
			case YGPositionTypeRelative:
				return RELATIVE;
			case YGPositionTypeAbsolute:
				return ABSOLUTE;
			default:
				throw new IllegalArgumentException("Unknown enum value: " + value);
			}
		}
	}

	/**
	 * The position type of an element defines how it is positioned within its
	 * parent.
	 * 
	 * @param thePositionType
	 *            position type of an element
	 * @see CCYogaPositionType
	 */
	public void positionType(CCYogaPositionType thePositionType) {
		YGNodeStyleSetPositionType(id, thePositionType.id);
	}

	/**
	 * Returns the position type of an element defines how it is positioned within
	 * its parent.
	 * 
	 * @return the position type of an element defines how it is positioned within
	 *         its parent.
	 */
	public CCYogaPositionType positionType() {
		return CCYogaPositionType.fromInt(YGNodeStyleGetPositionType(id));
	}

	public static enum CCYogaEdge {
		LEFT(YGEdgeLeft), 
		TOP(YGEdgeTop), 
		RIGHT(YGEdgeRight), 
		BOTTOM(YGEdgeBottom), 
		START(YGEdgeStart), 
		END(YGEdgeEnd), 
		HORIZONTAL(YGEdgeHorizontal), 
		VERTICAL(YGEdgeVertical), 
		ALL(YGEdgeAll);

		public final int id;

		CCYogaEdge(int theID) {
			id = theID;
		}

		public static CCYogaEdge fromInt(int value) {
			switch (value) {
			case YGEdgeLeft:
				return LEFT;
			case YGEdgeTop:
				return TOP;
			case YGEdgeRight:
				return RIGHT;
			case YGEdgeBottom:
				return BOTTOM;
			case YGEdgeStart:
				return START;
			case YGEdgeEnd:
				return END;
			case YGEdgeHorizontal:
				return HORIZONTAL;
			case YGEdgeVertical:
				return VERTICAL;
			case YGEdgeAll:
				return ALL;
			default:
				throw new IllegalArgumentException("Unknown enum value: " + value);
			}
		}
	}

	/**
	 * Sets the position of an element. The position values top, right, bottom, and
	 * left behave differently depending on the position type of the element. For a
	 * relative element they offset the position of the element in the direction
	 * specified. For absolute element though these properties specify the offset of
	 * the element's side from the same side on the parent.
	 * 
	 * @param theEdge
	 *            edge for which you want to change the position
	 * @param thePosition
	 *            position value
	 */
	public void position(CCYogaEdge theEdge, double thePosition) {
		YGNodeStyleSetPosition(id, theEdge.id, (float) thePosition);
	}

	public double position(CCYogaEdge theEdge) {
		YGValue myValue = YGValue.create();
		YGNodeStyleGetPosition(id, theEdge.id, myValue);
		return myValue.value();
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			YGNodeFree(id);
		} finally {
			super.finalize();
		}
	}

	private native void YGNodeReset(long nativePointer);

	public void reset() {
		YGNodeReset(id);
	}

	@Override
	public CCYogaNode clone() {
		return new CCYogaNode(YGNodeClone(id));
	}

	// public CCYogaNode cloneWithNewChildren() {
	// try {
	// YogaNode clonedYogaNode = (YogaNode) super.clone();
	// long clonedNativePointer = YGNodeClone(id, clonedYogaNode);
	// clonedYogaNode.mOwner = null;
	// clonedYogaNode.id = clonedNativePointer;
	// clonedYogaNode.clearChildren();
	// return clonedYogaNode;
	// } catch (CloneNotSupportedException ex) {
	// // This class implements Cloneable, this should not happen
	// throw new RuntimeException(ex);
	// }
	// }
	//
	// private void clearChildren() {
	// YGNode(id);
	// }

	private double _myWidth;
	private double _myHeight;
	private CCYogaFlexDirection _myDirection;

	public void calculateLayout(double width, double height, CCYogaFlexDirection theDirection) {
		_myWidth = width;
		_myHeight = height;
		_myDirection = theDirection;
		YGNodeCalculateLayout(id, (float)width, (float)height, theDirection.id);
		updateMatrices();
	}
	
	public void calculateLayout() {
		if(_myDirection == null)return;
		YGNodeCalculateLayout(id, (float)_myWidth, (float)_myHeight, _myDirection.id);
		updateMatrices();
	}

	public void dirty() {
		YGNodeMarkDirty(id);
	}

	// public void dirtyAllDescendants() {
	// YGNodeMarkDirtyAndPropogateToDescendants(id);
	// }

	public boolean isDirty() {
		return YGNodeIsDirty(id);
	}

	public void copyStyle(CCYogaNode srcNode) {
		YGNodeCopyStyle(id, srcNode.id);
	}

	/**
	 * Layout direction specifies the direction in which children and text 
	 * in a hierarchy should be laid out. Layout direction also effects what 
	 * edge start and end refer to. By default Yoga lays out with LTR layout 
	 * direction. In this mode start refers to left and end refers to right. 
	 * When localizing your apps for markets with RTL languages you should 
	 * customize this by either by passing a direction to the CalculateLayout 
	 * call or by setting the direction on the root node.
	 * @author christian riekoff
	 *
	 */
	public static enum CCYogaDirection {
		INHERIT(YGDirectionInherit), 
		/**
		 * Text and children and laid our from left to right. 
		 * Margin and padding applied the start of an element are applied on the left side.
		 */
		LTR(YGDirectionLTR), 
		/**
		 *  Text and children and laid our from right to left. 
		 *  Margin and padding applied the start of an element are applied on the right side.
		 */
		RTL(YGDirectionRTL);

		public final int id;

		CCYogaDirection(int theID) {
			id = theID;
		}

		public static CCYogaDirection fromInt(int theID) {
			switch (theID) {
			case YGDirectionInherit:
				return INHERIT;
			case YGDirectionLTR:
				return LTR;
			case YGDirectionRTL:
				return RTL;
			default:
				throw new IllegalArgumentException("Unknown enum value: " + theID);
			}
		}
	}

	public CCYogaDirection direction() {
		return CCYogaDirection.fromInt(YGNodeStyleGetDirection(id));
	}

	public void direction(CCYogaDirection direction) {
		YGNodeStyleSetDirection(id, direction.id);
	}

	/**
	 * Justify content describes how to align children within the main axis of their container. 
	 * For example, you can use this property to center a child horizontally within a container 
	 * with flex direction set to row or vertically within a container with flex direction set to column.
	 * @author christian riekoff
	 *
	 */
	public static enum CCYogaJustify {
		/**
		 * Align children of a container to the start of the container's main axis.
		 */
		FLEX_START(YGJustifyFlexStart), 
		/**
		 * Align children of a container in the center of the container's main axis.
		 */
		CENTER(YGJustifyCenter), 
		/**
		 * Align children of a container to the end of the container's main axis.
		 */
		FLEX_END(YGJustifyFlexEnd), 
		/**
		 * Evenly space of children across the container's main axis, distributing remaining space between the children.
		 */
		SPACE_BETWEEN(YGJustifySpaceBetween), 
		/**
		 * Evenly space of children across the container's main axis, distributing remaining space around the children. 
		 * Compared to space between using Space around will result in space being distributed to the beginning of the first child and end of the last child.
		 */
		SPACE_AROUND(YGJustifySpaceAround), 
		SPACE_EVENLY(YGJustifySpaceEvenly);

		public final int id;

		CCYogaJustify(int theID) {
			id = theID;
		}

		public static CCYogaJustify fromInt(int theID) {
			switch (theID) {
			case YGJustifyFlexStart:
				return FLEX_START;
			case YGJustifyCenter:
				return CENTER;
			case YGJustifyFlexEnd:
				return FLEX_END;
			case YGJustifySpaceBetween:
				return SPACE_BETWEEN;
			case YGJustifySpaceAround:
				return SPACE_AROUND;
			case YGJustifySpaceEvenly:
				return SPACE_EVENLY;
			default:
				throw new IllegalArgumentException("Unknown enum value: " + theID);
			}
		}
	}

	/**
	 * @see #justifyContent(CCYogaJustify)
	 * @return
	 */
	public CCYogaJustify justifyContent() {
		return CCYogaJustify.fromInt(YGNodeStyleGetJustifyContent(id));
	}

	/**
	 * Justify content describes how to align children within the main axis of their container. 
	 * For example, you can use this property to center a child horizontally within a container 
	 * with flex direction set to row or vertically within a container with flex direction set to column.
	 * @param justifyContent
	 */
	public void justifyContent(CCYogaJustify justifyContent) {
		YGNodeStyleSetJustifyContent(id, justifyContent.id);
	}

	/**
	 * Align content defines the distribution of lines along the cross-axis. 
	 * This only has effect when items are wrapped to multiple lines using flex wrap.
	 * @author christian riekoff
	 *
	 */
	public static enum CCYogaAlign {
		AUTO(YGAlignAuto), 
		/**
		 * Align wrapped lines to the start of the container's cross axis.
		 */
		FLEX_START(YGAlignFlexStart), 
		/**
		 * Align wrapped lines in the center of the container's cross axis.
		 */
		CENTER(YGAlignCenter), 
		/**
		 * Align wrapped lines to the end of the container's cross axis.
		 */
		FLEX_END(YGAlignFlexEnd), 
		/**
		 * Stretch wrapped lines to match the height of the container's cross axis.
		 */
		STRETCH(YGAlignStretch), 
		BASELINE(YGAlignBaseline), 
		/**
		 * Evenly space wrapped lines across the container's main axis, 
		 * distributing remaining space between the lines.
		 */
		SPACE_BETWEEN(YGAlignSpaceBetween), 
		/**
		 * Evenly space wrapped lines across the container's main axis, 
		 * distributing remaining space around the lines. Compared to space 
		 * between using space around will result in space being distributed 
		 * to the begining of the first lines and end of the last line.
		 */
		SPACE_AROUND(YGAlignSpaceAround);

		public final int id;

		CCYogaAlign(int theID) {
			id = theID;
		}

		public static CCYogaAlign fromInt(int theID) {
			switch (theID) {
			case YGAlignAuto:
				return AUTO;
			case YGAlignFlexStart:
				return FLEX_START;
			case YGAlignCenter:
				return CENTER;
			case YGAlignFlexEnd:
				return FLEX_END;
			case YGAlignStretch:
				return STRETCH;
			case YGAlignBaseline:
				return BASELINE;
			case YGAlignSpaceBetween:
				return SPACE_BETWEEN;
			case YGAlignSpaceAround:
				return SPACE_AROUND;
			default:
				throw new IllegalArgumentException("Unknown enum value: " + theID);
			}
		}
	}

	/**
	 * @see #alignItems(CCYogaAlign)
	 * @return
	 */
	public CCYogaAlign alignItems() {
		return CCYogaAlign.fromInt(YGNodeStyleGetAlignItems(id));
	}

	/**
	 * Align items describes how to align children along the cross axis of their container. 
	 * Align items is very similar to justify content but instead of applying to the main axis, 
	 * align items applies to the cross axis.
	 * @param alignItems alignment of the children along the cross axis of their container
	 */
	public void alignItems(CCYogaAlign alignItems) {
		YGNodeStyleSetAlignItems(id, alignItems.id);
	}

	/**
	 * @see #alignSelf(CCYogaAlign)
	 * @return
	 */
	public CCYogaAlign alignSelf() {
		return CCYogaAlign.fromInt(YGNodeStyleGetAlignSelf(id));
	}

	/**
	 * Align self has the same options and effect as align items but instead of 
	 * affecting the children within a container, you can apply this property to a 
	 * single child to change its alignment within its parent. 
	 * align self overrides any option set by the parent with align items.
	 * @param alignSelf
	 */
	public void alignSelf(CCYogaAlign alignSelf) {
		YGNodeStyleSetAlignSelf(id, alignSelf.id);
	}

	/**
	 * Returns the distribution of lines along the cross-axis. 
	 * @return distribution of lines along the cross-axis
	 */
	public CCYogaAlign alignContent() {
		return CCYogaAlign.fromInt(YGNodeStyleGetAlignContent(id));
	}

	/**
	 * Align content defines the distribution of lines along the cross-axis. 
	 * This only has effect when items are wrapped to multiple lines using flex wrap.
	 * @param alignContent distribution of lines along the cross-axis
	 */
	public void alignContent(CCYogaAlign alignContent) {
		YGNodeStyleSetAlignContent(id, alignContent.id);
	}

	public static enum CCYogaOverflow {
		VISIBLE(YGOverflowVisible), HIDDEN(YGOverflowHidden), SCROLL(YGOverflowScroll);

		public final int id;

		CCYogaOverflow(int theID) {
			id = theID;
		}

		public static CCYogaOverflow fromInt(int theID) {
			switch (theID) {
			case YGOverflowVisible:
				return VISIBLE;
			case YGOverflowHidden:
				return HIDDEN;
			case YGOverflowScroll:
				return SCROLL;
			default:
				throw new IllegalArgumentException("Unknown enum value: " + theID);
			}
		}
	}

	public CCYogaOverflow overflow() {
		return CCYogaOverflow.fromInt(YGNodeStyleGetOverflow(id));
	}

	public void overflow(CCYogaOverflow overflow) {
		YGNodeStyleSetOverflow(id, overflow.id);
	}

	public enum CCYogaDisplay {
		FLEX(YGDisplayFlex), NONE(YGDisplayNone);

		public final int id;

		CCYogaDisplay(int intValue) {
			id = intValue;
		}

		public int intValue() {
			return id;
		}

		public static CCYogaDisplay fromInt(int value) {
			switch (value) {
			case YGDisplayFlex:
				return FLEX;
			case YGDisplayNone:
				return NONE;
			default:
				throw new IllegalArgumentException("Unknown enum value: " + value);
			}
		}
	}

	public CCYogaDisplay display() {
		return CCYogaDisplay.fromInt(YGNodeStyleGetDisplay(id));
	}

	public void display(CCYogaDisplay display) {
		YGNodeStyleSetDisplay(id, display.id);
	}

	public void flex(double flex) {
		YGNodeStyleSetFlex(id, (float) flex);
	}

	/**
	 * @see #flexGrow(float)
	 * @return
	 */
	public double flexGrow() {
		return YGNodeStyleGetFlexGrow(id);
	}

	/**
	 * Describes how any space within a container should be 
	 * distributed among its children along the main axis. After laying 
	 * out its children, a container will distribute any remaining space 
	 * according to the flex grow values specified by its children.
	 * <p>
	 * Flex grow accepts any floating point value >= 0, with 0 being the 
	 * default value. A container will distribute any remaining space 
	 * among its children weighted by the child's flex grow value.
	 * @param flexGrow
	 */
	public void flexGrow(double flexGrow) {
		YGNodeStyleSetFlexGrow(id, (float) flexGrow);
	}

	/**
	 * @see #flexShrink(float)
	 * @return
	 */
	public double flexShrink() {
		return YGNodeStyleGetFlexShrink(id);
	}

	/**
	 * Describes how to shrink children along the main axis in the 
	 * case that the total size of the children overflow the size 
	 * of the container on the main axis. flex shrink is very similar 
	 * to flex grow and can be thought of in the same way if any 
	 * overflowing size is considered to be negative remaining space. 
	 * These two properties also work well together by allowing 
	 * children to grow and shrink as needed.
	 * <p>
	 * Flex shrink accepts any floating point value >= 0, with 1 being 
	 * the default value. A container will shrink its children 
	 * weighted by the child�s flex shrink value.
	 * @param flexShrink
	 */
	public void flexShrink(double flexShrink) {
		YGNodeStyleSetFlexShrink(id, (float) flexShrink);
	}

	public double flexBasis() {
		YGValue myValue = YGValue.create();
		YGNodeStyleGetFlexBasis(id, myValue);
		return myValue.value();
	}

	/**
	 * FLEX BASIS is an axis-independent way of providing the default size of an 
	 * item along the main axis. Setting the flex basis of a child is similar to 
	 * setting the width of that child if its parent is a container with 
	 * flex direction: row or setting the height of a child if its parent is a 
	 * container with flex direction: column. The flex basis of an item is the 
	 * default size of that item, the size of the item before any flex grow and 
	 * flex shrink calculations are performed.
	 * @param flexBasis
	 */
	public void flexBasis(double flexBasis) {
		YGNodeStyleSetFlexBasis(id, (float) flexBasis);
	}

	/**
	 * @see #flexBasis(float)
	 * @param percent
	 */
	public void flexBasisPercent(double percent) {
		YGNodeStyleSetFlexBasisPercent(id, (float) percent);
	}
	/**
	 * @see #flexBasis(float)
	 * @param percent
	 */
	public void flexBasisAuto() {
		YGNodeStyleSetFlexBasisAuto(id);
	}
	

	/**
	 * The flex wrap property is set on containers and controls what happens when 
	 * children overflow the size of the container along the main axis. 
	 * @author christian riekoff
	 *
	 */
	public static enum CCYogaFlexWrap {
		/**
		 * children are forced into a single line (which can shrink elements).
		 */
		NO_WRAP(YGWrapNoWrap), 
		/**
		 * items are wrapped into multiple lines along the main axis
		 */
		WRAP(YGWrapWrap), 
		/**
		 * behaves like wrap, but the order of the lines is reversed.
		 */
		WRAP_REVERSE(YGWrapReverse);

		public final int id;

		CCYogaFlexWrap(int theID) {
			id = theID;
		}

		public static CCYogaFlexWrap fromInt(int theID) {
			switch (theID) {
			case YGWrapNoWrap:
				return NO_WRAP;
			case YGWrapWrap:
				return WRAP;
			case YGWrapReverse:
				return WRAP_REVERSE;
			default:
				throw new IllegalArgumentException("Unknown enum value: " + theID);
			}
		}
	}

	/**
	 * The flex wrap property is set on containers and controls what happens when 
	 * children overflow the size of the container along the main axis. 
	 * By default children are forced into a single line (which can shrink elements).
	 * <p>
	 * If wrapping is allowed items are wrapped into multiple lines along the main axis 
	 * if needed. wrap reverse behaves the same, but the order of the lines is reversed.
	 * @param flexWrap
	 */
	public void flexWrap(CCYogaFlexWrap flexWrap) {
		YGNodeStyleSetFlexWrap(id, flexWrap.id);
	}
	
	/**
	 * @see #flexWrap(CCYogaFlexWrap)
	 * @return
	 */
	public CCYogaFlexWrap flexWrap() {
		return CCYogaFlexWrap.fromInt(YGNodeStyleGetFlexWrap(id));
	}

	public double margin(CCYogaEdge theEdge) {
		YGValue myValue = YGValue.create();
		YGNodeStyleGetMargin(id, theEdge.id, myValue);
		return myValue.value();
	}
	
	/**
	 * Return the margin value for layout purposes considering different values etc for all vertical left etc
	 * @param theEdge
	 * @return the margin for layout purposes
	 */
	public double layoutMargin(CCYogaEdge theEdge) {
		return YGNodeLayoutGetMargin(id, theEdge.id);
	}

	/**
	 * MARGIN effects the spacing around the outside of a node. 
	 * A node with margin will offset itself from the bounds of its parent 
	 * but also offset the location of any siblings. The margin of a node 
	 * contributes to the total size of its parent if the parent is auto sized.
	 * @param theEdge
	 * @param theMargin
	 */
	public void margin(CCYogaEdge theEdge, double theMargin) {
		YGNodeStyleSetMargin(id, theEdge.id, (float) theMargin);
	}

	public void marginPercent(CCYogaEdge theEdge, double percent) {
		YGNodeStyleSetMarginPercent(id, theEdge.id, (float) percent);
	}

	public void marginAuto(CCYogaEdge theEdge) {
		YGNodeStyleSetMarginAuto(id, theEdge.id);
	}

	public double padding(CCYogaEdge theEdge) {
		YGValue myValue = YGValue.create();
		YGNodeStyleGetPadding(id, theEdge.id, myValue);
		return Float.isNaN(myValue.value()) ? 0 : myValue.value();
	}


	/**
	 * Return the padding value for layout purposes considering different values etc for all vertical left etc
	 * @param theEdge
	 * @return the padding for layout purposes
	 */
	public double layoutPadding(CCYogaEdge theEdge) {
		return YGNodeLayoutGetPadding(id, theEdge.id);
	}

	/**
	 * PADDING affects the size of the node it is applied to. 
	 * Padding in Yoga acts as if box-sizing: border-box; was set. 
	 * That is padding will not add to the total size of an element 
	 * if it has an explicit size set. For auto sized nodes padding 
	 * will increase the size of the node as well as offset the location 
	 * of any children.
	 * @param theEdge
	 * @param thePadding
	 */
	public void padding(CCYogaEdge theEdge, double thePadding) {
		YGNodeStyleSetPadding(id, theEdge.id, (float) thePadding);
	}

	/**
	 * Same like {@linkplain #padding(CCYogaEdge, double)} but in percent of the parents width.
	 * @param theEdge
	 * @param percent
	 */
	public void paddingPercent(CCYogaEdge theEdge, double percent) {
		YGNodeStyleSetPaddingPercent(id, theEdge.id, (float) percent);
	}

	/**
	 * Returns the border value set for the given edge.
	 * @see #border(CCYogaEdge, double)
	 * @param theEdge 
	 * @return
	 */
	public double border(CCYogaEdge theEdge) {
		return YGNodeStyleGetBorder(id, theEdge.id);
	}

	/**
	 * Return the border value for layout purposes considering different values for all vertical left etc
	 * @param theEdge
	 * @return the border for layout purposes
	 */
	public double layoutBorder(CCYogaEdge theEdge) {
		return YGNodeLayoutGetBorder(id, theEdge.id);
	}

	/**
	 * BORDER in Yoga acts exactly like padding and only exists as a 
	 * separate property so that higher level frameworks get a hint 
	 * as to how thick to draw a border. Yoga however does not do any 
	 * drawing so just uses this information during layout where 
	 * border acts exactly like padding.
	 * @param theEdge
	 * @param border
	 */
	public void border(CCYogaEdge theEdge, double border) {
		YGNodeStyleSetBorder(id, theEdge.id, (float) border);
	}

	public void positionPercent(CCYogaEdge theEdge, double percent) {
		YGNodeStyleSetPositionPercent(id, theEdge.id, (float) percent);
	}

	/**
	 * Return the minimum width of an element in absolute pixels.
	 * @return min width of the node in pixels
	 */
	public double minWidth() {
		YGValue myValue = YGValue.create();
		YGNodeStyleGetMinWidth(id, myValue);
		return myValue.value();
	}

	/**
	 * Sets the minimum width of an element in absolute pixels.
	 * This property has higher priority than all other properties 
	 * and will always be respected.
	 * @param theMinWidth min width of the node in pixels
	 */
	public void minWidth(double theMinWidth) {
		YGNodeStyleSetMinWidth(id, (float) theMinWidth);
	}

	/**
	 * Sets the minimum width of an element in percentages of its parent's size.
	 * This property has higher priority than all other properties 
	 * and will always be respected.
	 * @param theMinWidth min width of the node in percentages of its parent's size
	 */
	public void minWidthPercent(double theMinWidth) {
		YGNodeStyleSetMinWidthPercent(id, (float) theMinWidth);
	}

	/**
	 * Return the minimum height of an element in absolute pixels.
	 * @return min height of the node in pixels
	 */
	public double minHeight() {
		YGValue myValue = YGValue.create();
		YGNodeStyleGetMinHeight(id, myValue);
		return myValue.value();
	}

	/**
	 * Sets the minimum height of an element in absolute pixels.
	 * This property has higher priority than all other properties 
	 * and will always be respected.
	 * @param theMinHeight min height of the node in pixels
	 */
	public void minHeight(double theMinHeight) {
		YGNodeStyleSetMinHeight(id, (float) theMinHeight);
	}

	/**
	 * Sets the minimum height of an element in percentages of its parent's size.
	 * This property has higher priority than all other properties 
	 * and will always be respected.
	 * @param theMinWidth min height of the node in percentages of its parent's size
	 */
	public void minHeightPercent(double theMinheight) {
		YGNodeStyleSetMinHeightPercent(id, (float) theMinheight);
	}
	
	/**
	 * Return the maximum width of an element in absolute pixels.
	 * @return max width of the node in pixels
	 */
	public double maxWidth() {
		YGValue myValue = YGValue.create();
		YGNodeStyleGetMaxWidth(id, myValue);
		return myValue.value();
	}

	/**
	 * Sets the maximum width of an element in absolute pixels.
	 * This property has higher priority than all other properties 
	 * and will always be respected.
	 * @param theMaxWidth max width of the node in pixels
	 */
	public void maxWidth(double theMaxWidth) {
		YGNodeStyleSetMaxWidth(id, (float) theMaxWidth);
	}

	/**
	 * Sets the maximum width of an element in percentages of its parent's size.
	 * This property has higher priority than all other properties 
	 * and will always be respected.
	 * @param theMaxWidth max width of the node in percentages of its parent's size
	 */
	public void maxWidthPercent(double theMaxWidth) {
		YGNodeStyleSetMaxWidthPercent(id, (float) theMaxWidth);
	}

	/**
	 * Return the maximum height of an element in absolute pixels.
	 * @return max height of the node in pixels
	 */
	public double maxHeight() {
		YGValue myValue = YGValue.create();
		YGNodeStyleGetMaxHeight(id, myValue);
		return myValue.value();
	}

	/**
	 * Sets the maximum height of an element in absolute pixels.
	 * This property has higher priority than all other properties 
	 * and will always be respected.
	 * @param theMaxHeight max height of the node in pixels
	 */
	public void maxHeight(double theMaxHeight) {
		YGNodeStyleSetMaxHeight(id, (float) theMaxHeight);
	}

	/**
	 * Sets the maximum height of an element in percentages of its parent's size.
	 * This property has higher priority than all other properties 
	 * and will always be respected.
	 * @param theMaxHeight max height of the node in percentages of its parent's size
	 */
	public void maxHeightPercent(double theMaxHeight) {
		YGNodeStyleSetMaxHeightPercent(id, (float) theMaxHeight);
	}

	/**
	 * @see #aspectRatio()
	 * @return
	 */
	public double aspectRatio() {
		return YGNodeStyleGetAspectRatio(id);
	}

	/**
	 * AspectRatio is a property introduced by Yoga and is not present as a
	 * settable property in the css flexbox specification. Flexbox does has 
	 * the notion of aspect ratio though for things with intrinsic aspect ratio such as images.
	 * <p>
	 * The aspect ratio property in Yoga has the following properties:
	 * <ul>
	 * <li>Accepts any floating point value > 0, the default is undefined.
	 * <li>Defined as the ratio between the width and the height of a node e.g. if a node has an aspect ratio of 2 then its width is twice the size of its height.
	 * <li>Respects the min and max dimensions of an item.
	 * <li>Has higher priority than flex grow
	 * <li>If aspect ratio, width, and height are set then the cross axis dimension is overridden.
	 * </ul>

	 * @param theAspectRatio
	 */
	public void aspectRatio(double theAspectRatio) {
		YGNodeStyleSetAspectRatio(id, (float) theAspectRatio);
	}

	public double left() {
		return YGNodeLayoutGetLeft(id);
	}
	
	public double right() {
		return YGNodeLayoutGetRight(id);
	}

	public double top() {
		return YGNodeLayoutGetTop(id);
	}

	public double bottom() {
		return YGNodeLayoutGetBottom(id);
	}

	public double width() {
		return YGNodeLayoutGetWidth(id);
	}

	public double height() {
		return YGNodeLayoutGetHeight(id);
	}

	public CCYogaDirection layoutDirection() {
		return CCYogaDirection.fromInt(YGNodeLayoutGetDirection(id));
	}
	

	// public void measureFunction(YogaMeasureFunction measureFunction) {
	// mMeasureFunction = measureFunction;
	// YGNodeSetHasMeasureFunc(id, measureFunction != null);
	// }
	//
	// // Implementation Note: Why this method needs to stay final
	// //
	// // We cache the jmethodid for this method in Yoga code. This means that even
	// if a subclass
	// // were to override measure, we'd still call this implementation from layout
	// code since the
	// // overriding method will have a different jmethodid. This is final to
	// prevent that mistake.
	// @DoNotStrip
	// public final long measure(double width, int widthMode, double height, int
	// heightMode) {
	// if (!isMeasureDefined()) {
	// throw new RuntimeException("Measure function isn't defined!");
	// }
	//
	// return mMeasureFunction.measure(
	// this,
	// width,
	// YogaMeasureMode.fromInt(widthMode),
	// height,
	// YogaMeasureMode.fromInt(heightMode));
	// }
	//
	// private native void YGNodeSetHasBaselineFunc(long nativePointer, boolean
	// hasMeasureFunc);
	//
	// public void baselineFunction(YogaBaselineFunction baselineFunction) {
	// YGNodeSetHasBaselineFunc(id, baselineFunction != null);
	// }
	//
	// @DoNotStrip
	// public final double baseline(double width, double height) {
	// return mBaselineFunction.baseline(this, width, height);
	// }
	//
	// public boolean isMeasureDefined() {
	// return mMeasureFunction != null;
	// }

	/**
	 * Use the set logger (defaults to adb log) to print out the styles, children,
	 * and computed layout of the tree rooted at this node.
	 */
	public void print() {
		// YGNodePrint(id);
	}

	// /**
	// * This method replaces the child at childIndex position with the newNode
	// received by parameter.
	// * This is different than calling removeChildAt and addChildAt because this
	// method ONLY replaces
	// * the child in the mChildren datastructure. @DoNotStrip: called from JNI
	// *
	// * @return the nativePointer of the newNode {@linl YogaNode}
	// */
	// @DoNotStrip
	// private final long replaceChild(YogaNode newNode, int childIndex) {
	// if (mChildren == null) {
	// throw new IllegalStateException("Cannot replace child. YogaNode does not have
	// children");
	// }
	// mChildren.remove(childIndex);
	// mChildren.add(childIndex, newNode);
	// newNode.mOwner = this;
	// return newNode.id;
	// }
}
