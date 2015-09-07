package cc.creativecomputing.control;

import java.util.Map;

import cc.creativecomputing.control.handles.CCPropertyListener;

public interface CCPropertyFeedbackObject {

	public Map<String, CCPropertyListener<?>> propertyListener();
}
