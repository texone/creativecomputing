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

package cc.creativecomputing.controlui.timeline.controller.track;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.timeline.CCGroupTrack;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.view.track.SwingGroupTrackView;
import cc.creativecomputing.math.CCColor;


/**
 * @author christianriekoff
 *
 */
public class CCGroupTrackController extends CCEventTrackController{

	private CCGroupTrack _myGroupTrack;
	private SwingGroupTrackView _myGroupTrackView;
	
	private List<CCTrackController> _myTracks = new ArrayList<>();
	
	public CCGroupTrackController(
		CCTimelineController theTimelineController,
		CCGroupTrack theGroupTrack
	) {
		super(theTimelineController, theGroupTrack, null);
		_myParent = this;
		_myGroupTrack = theGroupTrack;
	}
	
	public void view(SwingGroupTrackView theView) {
		super.view(theView);
		_myGroupTrackView = theView;
	}
	
	public void addTrack(CCTrackController theTrackController) {

		synchronized(_myTracks){
			if(!_myTracks.contains(theTrackController))_myTracks.add(theTrackController);
		}
		_myGroupTrack.addTrack(theTrackController.track());
	}
	
	public void removeTrack(CCTrackController theTrackController){
		_myGroupTrack.tracks().remove(theTrackController.track());
		synchronized(_myTracks){
			if(_myTracks.contains(theTrackController))_myTracks.remove(theTrackController);
		}
	}
	
	@Override
	public SwingGroupTrackView view() {
		return _myGroupTrackView;
	}
	
	public CCGroupTrack groupTrack() {
//		GroupTrack myGroupTrack = new GroupTrack();
//		myGroupTrack .setAddress(_myGroupTrack.address());
//		myGroupTrack.color(_myGroupTrack.color());
//		
//		for(TrackController myController:_myTracks) {
//			myGroupTrack.addTrack(myController.track());
//		}
		
		return _myGroupTrack;
	}
	
	public void mute(boolean theIsMuted) {
		for(CCTrackController myTrackController:trackController()) {
			myTrackController.mute(theIsMuted);
		}
	}
	
	public boolean isOpen(){
		return _myGroupTrack.isOpen();
	}
	
	public List<CCTrackController> trackController(){
		return _myTracks;
	}
	
	public void closeGroup(boolean theCloseRecursive) {
		if(!_myGroupTrack.isOpen())return;
		
		if(theCloseRecursive){
			synchronized(_myTracks){
				for(CCTrackController myTrack:_myTracks){
					if(myTrack instanceof CCGroupTrackController){
						((CCGroupTrackController)myTrack).closeGroup(theCloseRecursive);
					}
				}
			}
		}
		_myGroupTrack.isOpen(false);
		if(_myGroupTrackView!=null)_myGroupTrackView.closeGroup();
	}
	
	public void openGroup(boolean theOpenRecursive) {
		if(_myGroupTrack.isOpen())return;
		_myGroupTrack.isOpen(true);
		if(_myGroupTrackView!=null)_myGroupTrackView.openGroup();

		if(!theOpenRecursive)return;
		
		synchronized(_myTracks){
			for(CCTrackController myTrack:_myTracks){
				if(myTrack instanceof CCGroupTrackController){
					((CCGroupTrackController)myTrack).openGroup(theOpenRecursive);
				}
			}
		}
	}

	/**
	 * @param theNewColor
	 */
	public void color(CCColor theNewColor) {
		_myGroupTrack.color(theNewColor);
		if(_myGroupTrackView!=null)_myGroupTrackView.color(theNewColor);
		synchronized(_myTracks){
			for(CCTrackController myController:_myTracks) {
				myController.color(theNewColor);
			}
		}
	}
	
	public void writeValues() {
		// TODO fix write values
//		for (TrackController myController : _myTracks) {
//			myController.writeValue();
//		}
	}
	
	@Override
	public void time(double theTime) {
		super.time(theTime);
		synchronized(_myTracks){
			for(CCTrackController myTrack:_myTracks){
				myTrack.time(theTime);
			}
		}
	}
}
