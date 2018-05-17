package cc.creativecomputing.yoga;

import static org.lwjgl.util.yoga.Yoga.YGFlexDirectionColumn;
import static org.lwjgl.util.yoga.Yoga.YGFlexDirectionColumnReverse;
import static org.lwjgl.util.yoga.Yoga.YGFlexDirectionRow;
import static org.lwjgl.util.yoga.Yoga.YGFlexDirectionRowReverse;
import static org.lwjgl.util.yoga.Yoga.YGNodeNew;

import org.lwjgl.util.yoga.YGNode;
import org.lwjgl.util.yoga.YGValue;

import static org.lwjgl.util.yoga.Yoga.*;

public class CCYogaNode {
	
	public final long id;
	
	private YGNode _myNode;
	
	private CCYogaNode(long theID) {
		id = theID;
		_myNode = YGNode.create(id);
	}

	public CCYogaNode() {
		this(YGNodeNew());
	}
	
	/**
	 * Specifies the height of the element's content area.
	 */
	public void heightAuto() {
		YGNodeStyleSetHeightAuto(id);
	}
	
	/**
	 * Specifies the height of the element's content area in absolute pixels. 
	 * Depending on other properties set on the Yoga node this may or may not be the final dimension of the node.
	 * @param theHeight height of the element's content area in absolute pixels. 
	 */
	public void height(double theHeight) {
		YGNodeStyleSetHeight(id, (float)theHeight);
	}
	
	/**
	 * Specifies the height of the element's content area in percentage of its parent's height.
	 * @param theHeight height of the element's content area in percentage of its parent's height
	 */
	public void heightPercent(double theHeight) {
		YGNodeStyleSetHeightPercent(id, (float)theHeight);
	}
	
	public double styleHeight() {
		YGValue myValue = YGValue.create();
		YGNodeStyleGetHeight(id,myValue);
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
	 * Depending on other properties set on the Yoga node this may or may not be the final dimension of the node.
	 * @param theWidth width of the element's content area in absolute pixels. 
	 */
	public void width(double theWidth) {
		YGNodeStyleSetHeight(id, (float)theWidth);
	}
	
	/**
	 * Specifies the width of the element's content area in percentage of its parent's width.
	 * @param theWidth width of the element's content area in percentage of its parent's width
	 */
	public void widthPercent(double theWidth) {
		YGNodeStyleSetHeightPercent(id, (float)theWidth);
	}

	public double styleWidth() {
		YGValue myValue = YGValue.create();
		YGNodeStyleGetWidth(id,myValue);
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
			case YGFlexDirectionColumn: return COLUMN;
			case YGFlexDirectionColumnReverse: return COLUMN_REVERSE;
			case YGFlexDirectionRow: return ROW;
			case YGFlexDirectionRowReverse: return ROW_REVERSE;
			default: throw new IllegalArgumentException("Unknown enum value: " + value);
		    }
		}
	}
	
	/**
	 * Controls the direction in which children of a node are laid
	 * out.
	 * @param theDirection the direction in which children of a node are laid
	 * out
	 * @see CCYogaFlexDirection
	 */
	public void flexDirection(CCYogaFlexDirection theDirection) {
		YGNodeStyleSetFlexDirection(id, theDirection.id);
	}
	
	/**
	 * Returns the direction in which children of a node are laid
	 * out.
	 * @return the direction in which children of a node are laid
	 * out.
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
			 case YGPositionTypeRelative: return RELATIVE;
			 case YGPositionTypeAbsolute: return ABSOLUTE;
			 default: throw new IllegalArgumentException("Unknown enum value: " + value);
			 }
		 }
	}
	
	/**
	 * The position type of an element defines how it is positioned within its parent.
	 * @param thePositionType position type of an element
	 * @see CCYogaPositionType
	 */
	public void positionType(CCYogaPositionType thePositionType) {
		YGNodeStyleSetPositionType(id, thePositionType.id);
	}
	
	/**
	 * Returns the position type of an element defines how it is positioned within its parent.
	 * @return the position type of an element defines how it is positioned within its parent.
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
		ALL(YGEdgeAll );

		public final int id;

		CCYogaEdge(int theID) {
			id = theID;
		}

		public static CCYogaEdge fromInt(int value) {
			switch (value) {
			case YGEdgeLeft:return LEFT;
			case YGEdgeTop:return TOP;
			case YGEdgeRight:return RIGHT;
			case YGEdgeBottom:return BOTTOM;
			case YGEdgeStart:return START;
			case YGEdgeEnd:return END;
			case YGEdgeHorizontal:return HORIZONTAL;
			case YGEdgeVertical:return VERTICAL;
			case YGEdgeAll :return ALL;
			default:throw new IllegalArgumentException("Unknown enum value: " + value);
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
	 * @param theEdge edge for which you want to change the position
	 * @param thePosition position value
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

	  public int childCount() {
	    return YGNodeGetChildCount(id);
	  }

	  public CCYogaNode childAt(int i) {
		  return new CCYogaNode(YGNodeGetChild(id, i));
	  }
	  
	  public void insertChild(CCYogaNode child, int i) {
	    YGNodeInsertChild(id, child.id, i);
	  }

	  private native void YGNodeInsertSharedChild(long nativePointer, long childPointer, int index);

	  public void addSharedChildAt(CCYogaNode child, int i) {
	    YGNodeInsertSharedChild(id, child.id, i);
	  }

	  @Override
	  public CCYogaNode clone() {
		  return new CCYogaNode(YGNodeClone(id));
	  }

//	  public CCYogaNode cloneWithNewChildren() {
//	    try {
//	      YogaNode clonedYogaNode = (YogaNode) super.clone();
//	      long clonedNativePointer = YGNodeClone(id, clonedYogaNode);
//	      clonedYogaNode.mOwner = null;
//	      clonedYogaNode.id = clonedNativePointer;
//	      clonedYogaNode.clearChildren();
//	      return clonedYogaNode;
//	    } catch (CloneNotSupportedException ex) {
//	      // This class implements Cloneable, this should not happen
//	      throw new RuntimeException(ex);
//	    }
//	  }
//
//	  private void clearChildren() {
//	    YGNode(id);
//	  }

	  public void removeChild(CCYogaNode theChild) {
		  YGNodeRemoveChild(id, theChild.id);
	  }
	  public CCYogaNode removeChildAt(int i) {
		  CCYogaNode myChild = new CCYogaNode(YGNodeGetChild(id, i));
		  removeChild(myChild);
		  return myChild;
	  }

	  public CCYogaNode parent() {
	    return new CCYogaNode(YGNodeGetParent(id));
	  }

	  public void calculateLayout(float width, float height, CCYogaFlexDirection theDirection) {
	    YGNodeCalculateLayout(id, width, height, theDirection.id);
	  }


	  public void dirty() {
	    YGNodeMarkDirty(id);
	  }

//	  public void dirtyAllDescendants() {
//	    YGNodeMarkDirtyAndPropogateToDescendants(id);
//	  }

	  public boolean isDirty() {
	    return YGNodeIsDirty(id);
	  }

	  public void copyStyle(CCYogaNode srcNode) {
	    YGNodeCopyStyle(id, srcNode.id);
	  }

	  public static enum CCYogaDirection {
		  INHERIT(	YGDirectionInherit ),
		  LTR(YGDirectionLTR ),
		  RTL(YGDirectionRTL);

		  public final int id;

		  CCYogaDirection(int theID) {
		    id = theID;
		  }

		  public static CCYogaDirection fromInt(int theID) {
		    switch (theID) {
		      case 	YGDirectionInherit : return INHERIT;
		      case YGDirectionLTR : return LTR;
		      case YGDirectionRTL: return RTL;
		      default: throw new IllegalArgumentException("Unknown enum value: " + theID);
		    }
		  }
		}

	  public CCYogaDirection direction() {
	    return CCYogaDirection.fromInt(YGNodeStyleGetDirection(id));
	  }

	  public void direction(CCYogaDirection direction) {
		  YGNodeStyleSetDirection(id, direction.id);
	  }

	  public static enum CCYogaJustify {
		  FLEX_START(YGJustifyFlexStart ),
		  CENTER(YGJustifyCenter ),
		  FLEX_END(YGJustifyFlexEnd ),
		  SPACE_BETWEEN(YGJustifySpaceBetween ),
		  SPACE_AROUND(YGJustifySpaceAround ),
		  SPACE_EVENLY(YGJustifySpaceEvenly);

		  public final int id;

		  CCYogaJustify(int theID) {
		    id = theID;
		  }


		  public static CCYogaJustify fromInt(int theID) {
		    switch (theID) {
		      case YGJustifyFlexStart : return FLEX_START;
		      case YGJustifyCenter : return CENTER;
		      case YGJustifyFlexEnd : return FLEX_END;
		      case YGJustifySpaceBetween : return SPACE_BETWEEN;
		      case YGJustifySpaceAround : return SPACE_AROUND;
		      case YGJustifySpaceEvenly:
		        return SPACE_EVENLY;
		      default: throw new IllegalArgumentException("Unknown enum value: " + theID);
		    }
		  }
		}

	  public CCYogaJustify justifyContent() {
	    return CCYogaJustify.fromInt(YGNodeStyleGetJustifyContent(id));
	  }

	  public void justifyContent(CCYogaJustify justifyContent) {
	    YGNodeStyleSetJustifyContent(id, justifyContent.id);
	  }
	  
	  public static enum CCYogaAlign {
		  AUTO(YGAlignAuto ),
		  FLEX_START(YGAlignFlexStart ),
		  CENTER(YGAlignCenter ),
		  FLEX_END(YGAlignFlexEnd ),
		  STRETCH(YGAlignStretch),
		  BASELINE(YGAlignBaseline ),
		  SPACE_BETWEEN(YGAlignSpaceBetween ),
		  SPACE_AROUND(YGAlignSpaceAround );

		  public final int id;

		  CCYogaAlign(int theID) {
		    id = theID;
		  }


		  public static CCYogaAlign fromInt(int theID) {
		    switch (theID) {
		      case YGAlignAuto : return AUTO;
		      case YGAlignFlexStart : return FLEX_START;
		      case YGAlignCenter : return CENTER;
		      case YGAlignFlexEnd : return FLEX_END;
		      case YGAlignStretch: return STRETCH;
		      case YGAlignBaseline : return BASELINE;
		      case YGAlignSpaceBetween : return SPACE_BETWEEN;
		      case YGAlignSpaceAround : return SPACE_AROUND;
		      default: throw new IllegalArgumentException("Unknown enum value: " + theID);
		    }
		  }
		}

	  public CCYogaAlign alignItems() {
	    return CCYogaAlign.fromInt(YGNodeStyleGetAlignItems(id));
	  }

	  public void alignItems(CCYogaAlign alignItems) {
	    YGNodeStyleSetAlignItems(id, alignItems.id);
	  }

	  public CCYogaAlign alignSelf() {
	    return CCYogaAlign.fromInt(YGNodeStyleGetAlignSelf(id));
	  }

	  public void alignSelf(CCYogaAlign alignSelf) {
	    YGNodeStyleSetAlignSelf(id, alignSelf.id);
	  }

	  public CCYogaAlign alignContent() {
	    return CCYogaAlign.fromInt(YGNodeStyleGetAlignContent(id));
	  }

	  public void alignContent(CCYogaAlign alignContent) {
	    YGNodeStyleSetAlignContent(id, alignContent.id);
	  }
	  
	  public static enum CCYogaWrap {
		  NO_WRAP(YGWrapNoWrap ),
		  WRAP(YGWrapWrap),
		  WRAP_REVERSE(YGWrapReverse );

		  public final int id;

		  CCYogaWrap(int theID) {
		    id = theID;
		  }

		  public static CCYogaWrap fromInt(int theID) {
		    switch (theID) {
		      case YGWrapNoWrap : return NO_WRAP;
		      case YGWrapWrap: return WRAP;
		      case YGWrapReverse : return WRAP_REVERSE;
		      default: throw new IllegalArgumentException("Unknown enum value: " + theID);
		    }
		  }
		}

	  public void wrap(CCYogaWrap flexWrap) {
	    YGNodeStyleSetFlexWrap(id, flexWrap.id);
	  }

	  public static enum CCYogaOverflow {
		  VISIBLE(YGOverflowVisible),
		  HIDDEN(	YGOverflowHidden ),
		  SCROLL(YGOverflowScroll );

		  public final int id;

		  CCYogaOverflow(int theID) {
		    id = theID;
		  }


		  public static CCYogaOverflow fromInt(int theID) {
		    switch (theID) {
		      case YGOverflowVisible: return VISIBLE;
		      case YGOverflowHidden: return HIDDEN;
		      case YGOverflowScroll : return SCROLL;
		      default: throw new IllegalArgumentException("Unknown enum value: " + theID);
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
		  FLEX(YGDisplayFlex ),
		  NONE(YGDisplayNone);

		  public final int id;

		  CCYogaDisplay(int intValue) {
		    id = intValue;
		  }

		  public int intValue() {
		    return id;
		  }

		  public static CCYogaDisplay fromInt(int value) {
		    switch (value) {
		      case YGDisplayFlex : return FLEX;
		      case YGDisplayNone: return NONE;
		      default: throw new IllegalArgumentException("Unknown enum value: " + value);
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
	    YGNodeStyleSetFlex(id, (float)flex);
	  }

	  public double flexGrow() {
	    return YGNodeStyleGetFlexGrow(id);
	  }

	  public void flexGrow(double flexGrow) {
	    YGNodeStyleSetFlexGrow(id, (float)flexGrow);
	  }

	  public double flexShrink() {
	    return YGNodeStyleGetFlexShrink(id);
	  }

	  public void flexShrink(double flexShrink) {
	    YGNodeStyleSetFlexShrink(id, (float)flexShrink);
	  }

	  public double flexBasis() {
		  YGValue myValue = YGValue.create();
	    YGNodeStyleGetFlexBasis(id,myValue);
	    return myValue.value();
	  }

	  public void flexBasis(double flexBasis) {
	    YGNodeStyleSetFlexBasis(id, (float)flexBasis);
	  }

	  public void flexBasisPercent(double percent) {
	    YGNodeStyleSetFlexBasisPercent(id, (float)percent);
	  }

	  public void flexBasisAuto() {
	    YGNodeStyleSetFlexBasisAuto(id);
	  }

	  public double margin(CCYogaEdge edge) {
		  YGValue myValue = YGValue.create();
		  YGNodeStyleGetMargin(id,edge.id,myValue);
		    return myValue.value();
	  }

	  public void margin(CCYogaEdge edge, double margin) {
	    YGNodeStyleSetMargin(id, edge.id, (float)margin);
	  }

	  public void marginPercent(CCYogaEdge edge, double percent) {
	    YGNodeStyleSetMarginPercent(id, edge.id, (float)percent);
	  }

	  public void marginAuto(CCYogaEdge edge) {
	    YGNodeStyleSetMarginAuto(id, edge.id);
	  }

	  public double padding(CCYogaEdge edge) {
		  YGValue myValue = YGValue.create();
		  YGNodeStyleGetPadding(id,edge.id,myValue);
		    return myValue.value();
	  }

	  public void padding(CCYogaEdge edge, double padding) {
	    YGNodeStyleSetPadding(id, edge.id, (float)padding);
	  }

	  public void paddingPercent(CCYogaEdge edge, double percent) {
	    YGNodeStyleSetPaddingPercent(id, edge.id, (float)percent);
	  }

	  public double border(CCYogaEdge edge) {
	    return YGNodeStyleGetBorder(id, edge.id);
	  }

	  public void border(CCYogaEdge edge, double border) {
	    YGNodeStyleSetBorder(id, edge.id, (float)border);
	  }


	  public void positionPercent(CCYogaEdge edge, double percent) {
	    YGNodeStyleSetPositionPercent(id, edge.id, (float)percent);
	  }

	  public double minWidth() {
		  YGValue myValue = YGValue.create();
		  YGNodeStyleGetMinWidth(id,myValue);
		    return myValue.value();
	  }

	  public void minWidth(double minWidth) {
	    YGNodeStyleSetMinWidth(id, (float)minWidth);
	  }

	  public void minWidthPercent(double percent) {
	    YGNodeStyleSetMinWidthPercent(id, (float)percent);
	  }

	  public double minHeight() {
		  YGValue myValue = YGValue.create();
		  YGNodeStyleGetMinHeight(id,myValue);
		    return myValue.value();
	  }

	  public void minHeight(double minHeight) {
	    YGNodeStyleSetMinHeight(id, (float)minHeight);
	  }

	  public void minHeightPercent(double percent) {
	    YGNodeStyleSetMinHeightPercent(id, (float)percent);
	  }

	  public double maxWidth() {
		  YGValue myValue = YGValue.create();
		  YGNodeStyleGetMaxWidth(id,myValue);
		    return myValue.value();
	  }

	  public void maxWidth(double maxWidth) {
	    YGNodeStyleSetMaxWidth(id, (float)maxWidth);
	  }

	  public void maxWidthPercent(double percent) {
	    YGNodeStyleSetMaxWidthPercent(id, (float)percent);
	  }

	  public double maxHeight() {
		  YGValue myValue = YGValue.create();
		  YGNodeStyleGetMaxHeight(id,myValue);
		    return myValue.value();
	  }

	  public void maxHeight(double maxheight) {
	    YGNodeStyleSetMaxHeight(id, (float)maxheight);
	  }

	  public void maxHeightPercent(double percent) {
	    YGNodeStyleSetMaxHeightPercent(id, (float)percent);
	  }

	  public double aspectRatio() {
	    return YGNodeStyleGetAspectRatio(id);
	  }

	  public void aspectRatio(double theAspectRatio) {
		  YGNodeStyleSetAspectRatio(id, (float)theAspectRatio);
	  }

	  public double left() {
		  return YGNodeLayoutGetLeft(id);
	  }

	  public double top() {
	    return YGNodeLayoutGetTop(id);
	  }

	  public double width() {
	    return YGNodeLayoutGetWidth(id);
	  }

	  public double height() {
		    return YGNodeLayoutGetHeight(id);
	  }

//	  public boolean doesLegacyStretchFlagAffectsLayout() {
//	    return mDoesLegacyStretchFlagAffectsLayout;
//	  }

	  public double layoutMargin(CCYogaEdge edge) {
		  return YGNodeLayoutGetMargin(id, edge.id);
	  }

	  public double layoutPadding(CCYogaEdge edge) {
		  return YGNodeLayoutGetPadding(id, edge.id);
	  }

	  public double layoutBorder(CCYogaEdge edge) {
		  return YGNodeLayoutGetBorder(id, edge.id);
	  }

	  public CCYogaDirection layoutDirection() {
	    return CCYogaDirection.fromInt(YGNodeLayoutGetDirection(id));
	  }
	  
//	  public void measureFunction(YogaMeasureFunction measureFunction) {
//	    mMeasureFunction = measureFunction;
//	    YGNodeSetHasMeasureFunc(id, measureFunction != null);
//	  }
//
//	  // Implementation Note: Why this method needs to stay final
//	  //
//	  // We cache the jmethodid for this method in Yoga code. This means that even if a subclass
//	  // were to override measure, we'd still call this implementation from layout code since the
//	  // overriding method will have a different jmethodid. This is final to prevent that mistake.
//	  @DoNotStrip
//	  public final long measure(double width, int widthMode, double height, int heightMode) {
//	    if (!isMeasureDefined()) {
//	      throw new RuntimeException("Measure function isn't defined!");
//	    }
//
//	    return mMeasureFunction.measure(
//	        this,
//	        width,
//	        YogaMeasureMode.fromInt(widthMode),
//	        height,
//	        YogaMeasureMode.fromInt(heightMode));
//	  }
//
//	  private native void YGNodeSetHasBaselineFunc(long nativePointer, boolean hasMeasureFunc);
//	  
//	  public void baselineFunction(YogaBaselineFunction baselineFunction) {
//	    YGNodeSetHasBaselineFunc(id, baselineFunction != null);
//	  }
//
//	  @DoNotStrip
//	  public final double baseline(double width, double height) {
//	    return mBaselineFunction.baseline(this, width, height);
//	  }
//
//	  public boolean isMeasureDefined() {
//	    return mMeasureFunction != null;
//	  }

	


	  /**
	   * Use the set logger (defaults to adb log) to print out the styles, children, and computed
	   * layout of the tree rooted at this node.
	   */
	  public void print() {
//		  YGNodePrint(id);
	  }

//	  /**
//	   * This method replaces the child at childIndex position with the newNode received by parameter.
//	   * This is different than calling removeChildAt and addChildAt because this method ONLY replaces
//	   * the child in the mChildren datastructure. @DoNotStrip: called from JNI
//	   *
//	   * @return the nativePointer of the newNode {@linl YogaNode}
//	   */
//	  @DoNotStrip
//	  private final long replaceChild(YogaNode newNode, int childIndex) {
//	    if (mChildren == null) {
//	      throw new IllegalStateException("Cannot replace child. YogaNode does not have children");
//	    }
//	    mChildren.remove(childIndex);
//	    mChildren.add(childIndex, newNode);
//	    newNode.mOwner = this;
//	    return newNode.id;
//	  }
}
