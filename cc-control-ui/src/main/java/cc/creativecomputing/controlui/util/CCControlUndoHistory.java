/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.controlui.util;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCEventManager;


/**
 * @author christianriekoff
 *
 */
public class CCControlUndoHistory {
	public interface CCUndoAction {

		void apply();
		
		void undo();
	}
	
	private static CCControlUndoHistory instance = new CCControlUndoHistory();
	
	public static CCControlUndoHistory instance() {
		return instance;
	}

	private List<CCUndoAction> _myActions = new ArrayList<CCUndoAction>();
	private int _myIndex = 0;
	
	public final CCEventManager<CCControlUndoHistory> events = new CCEventManager<>();
	
	private CCControlUndoHistory() {
		
	}
	
	public void clear() {
		_myActions.clear();
		events.event(this);
		_myIndex = -1;
	}
	
	public void apply(CCUndoAction theAction) {
		if(_myIndex+1 < _myActions.size())_myActions.subList(_myIndex + 1, _myActions.size()).clear();
		_myActions.add(theAction);
		_myIndex = _myActions.size() - 1;
		events.event(this);
	}
	
	public void undo() {
		if(_myIndex >= 0) {
			_myActions.get(_myIndex).undo();
			_myIndex--;
			events.event(this);
		}
	}
	
	public void redo() {
		if(_myIndex >= -1 && _myIndex < _myActions.size() - 1) {
			_myIndex++;
			_myActions.get(_myIndex).apply();
			events.event(this);
		}
	}
	
	public int size() {
		return _myActions.size();
	}
}
