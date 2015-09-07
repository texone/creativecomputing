package cc.creativecomputing.graphics.scene.controllers;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;

public abstract class CCControlledObject {
	private List<CCController<?>> _myController = new ArrayList<>();

	protected CCControlledObject() {

	}

	// Access to the controllers that control this object.
	public int numControllers() {
		return _myController.size();
	}

	public CCController<?> getController(int i) {
		return _myController.get(i);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void attachController( CCController controller) {
		if (_myController.contains(controller))
			return;

		_myController.add(controller);
		controller.object(this);
	}

	public void detachController(CCController<?> controller) {
		if (!_myController.remove(controller))
			return;

		controller.object(null);
	}

	public void detachAllControllers() {
		for (CCController<?> myController : _myController) {
			myController.object(null);
		}
		_myController.clear();
	}

	public boolean updateControllers(CCAnimator theAnimator) {
		boolean someoneUpdated = false;
		for (CCController<?> myController : _myController) {
			someoneUpdated = someoneUpdated || myController.update(theAnimator);
		}
		return someoneUpdated;
	}

}
