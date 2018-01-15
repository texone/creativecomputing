package cc.creativecomputing.control;

import java.util.Map;

import cc.creativecomputing.control.handles.CCPropertyListener;

public interface CCPropertyFeedbackObject {

	Map<String, CCPropertyListener<?>> propertyListener();
}
