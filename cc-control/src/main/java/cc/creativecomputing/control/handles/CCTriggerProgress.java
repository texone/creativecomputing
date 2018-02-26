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
package cc.creativecomputing.control.handles;

import cc.creativecomputing.core.events.CCListenerManager;

public class CCTriggerProgress {
	
	public interface CCTriggerProgressListener{
		void start();
		
		void progress(double theProgress);
		
		void end();
		
		void interrupt();
	}
	
	private CCListenerManager<CCTriggerProgressListener> _myEvents = new CCListenerManager<>(CCTriggerProgressListener.class);
	
	public CCListenerManager<CCTriggerProgressListener> events(){
		return _myEvents;
	}
	
	public void start(){
		_myEvents.proxy().start();
	}

	public void progress(double theProgress){
		_myEvents.proxy().progress(theProgress);
	}

	public void interrupt(){
		_myEvents.proxy().interrupt();
	}
	
	public void end(){
		_myEvents.proxy().end();
	}
}
