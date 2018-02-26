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

import cc.creativecomputing.core.events.CCListenerManager;


/**
 * @author christianriekoff
 *
 */
public class UndoHistory {
	
	public interface HistoryListener{
		void onChange(UndoHistory theHistory);
	}
	
	private static UndoHistory instance = new UndoHistory();
	
	public static UndoHistory instance() {
		return instance;
	}

	private List<Action> _myActions = new ArrayList<Action>();
	private int _myIndex = 0;
	
	private CCListenerManager<HistoryListener> _myListenerManager = CCListenerManager.create(HistoryListener.class);
	
	private UndoHistory() {
		
	}
	
	public CCListenerManager<HistoryListener> events() {
		return _myListenerManager;
	}
	
	public void clear() {
		_myActions.clear();
		_myListenerManager.proxy().onChange(this);
		_myIndex = -1;
	}
	
	public void apply(Action theAction) {
		if(_myIndex+1 < _myActions.size())_myActions.subList(_myIndex + 1, _myActions.size()).clear();
		_myActions.add(theAction);
		_myIndex = _myActions.size() - 1;
		_myListenerManager.proxy().onChange(this);
	}
	
	public void undo() {
		if(_myIndex >= 0) {
			_myActions.get(_myIndex).undo();
			_myIndex--;
			_myListenerManager.proxy().onChange(this);
		}
	}
	
	public void redo() {
		if(_myIndex >= -1 && _myIndex < _myActions.size() - 1) {
			_myIndex++;
			_myActions.get(_myIndex).apply();
			_myListenerManager.proxy().onChange(this);
		}
	}
	
	public int size() {
		return _myActions.size();
	}
}
