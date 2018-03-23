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

import cc.creativecomputing.core.CCEventManager;

public class CCTriggerProgress {
	
	public interface CCTriggerProgressListener{
		void start();
		
		void progress(double theProgress);
		
		void end();
		
		void interrupt();
	}
	
	public CCEventManager<?> startEvents = new CCEventManager<>();
	public CCEventManager<Double> progressEvents = new CCEventManager<>();
	public CCEventManager<?> endEvents = new CCEventManager<>();
	public CCEventManager<?> interruptEvents = new CCEventManager<>();
	
	public void start(){
		startEvents.event();
	}

	public void progress(double theProgress){
		progressEvents.event(theProgress);
	}

	public void interrupt(){
		interruptEvents.event();
	}
	
	public void end(){
		endEvents.event();
	}
}
