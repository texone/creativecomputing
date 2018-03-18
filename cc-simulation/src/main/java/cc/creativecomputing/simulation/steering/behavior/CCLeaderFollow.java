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
package cc.creativecomputing.simulation.steering.behavior;

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.steering.CCAgent;

public class CCLeaderFollow extends CCSeparation{
	
	private CCAgent _myLeader;
	private CCVector3 _myTarget = new CCVector3();
	private double leaderAvoidWidth = 4F;
    private double leaderAvoidLength = 30F;
    private double _myFollowDistance = 5;
    
    private CCSeekFlee _mySeek;
    private CCArrive _myArrive;
	
	public CCLeaderFollow(final CCAgent theLeader){
		super(50,360);
		_myLeader = theLeader;
		leaderAvoidWidth = 4F * _myLeader.radius;
	    leaderAvoidLength = 30F * _myLeader.radius;
	    
	    _myTarget = new CCVector3();
	    _mySeek = new CCSeekFlee(_myTarget);
	    _myArrive = new CCArrive(_myTarget);
	}
	
	public CCLeaderFollow(
		final CCAgent theLeader, 
		final double theFollowDistance, 
		final double theLeaderAvoidWidth, 
		final double theLeaderAvoidLength
	){
		super(50,360);
		_myLeader = theLeader;
		_myFollowDistance = theFollowDistance;
		leaderAvoidWidth = theLeaderAvoidWidth * _myLeader.radius;
	    leaderAvoidLength = theLeaderAvoidLength * _myLeader.radius;
	    
	    _myTarget = new CCVector3();
	    _mySeek = new CCSeekFlee(_myTarget);
	    _myArrive = new CCArrive(_myTarget);
	}

	public double followDistance(){
		return _myFollowDistance;
	}
	
	public void followDistance(final double theFollowDistance){
		_myFollowDistance = theFollowDistance;
	}
	
	@Override
	public boolean apply(CCParticle theAgent, CCVector3 theForce, double theDeltaTime) {
		CCVector3 local = _myLeader.localizePosition(theAgent.position);
	    double adjust = _myLeader.relativeSpeed();
	    double margin = 3F * _myLeader.radius;
	    
	    CCVector3 myForce1 = new CCVector3();
	    CCVector3 myForce2 = new CCVector3();
	    
	    // check if the agent is in the avoidance range of the leader
	    if(
	    	local.z > 0.0F && local.z < leaderAvoidLength * adjust + margin * 2.0F && 
	    	local.x < leaderAvoidWidth * adjust + margin && local.x > -leaderAvoidWidth * adjust - margin && 
	    	local.y < leaderAvoidWidth * adjust + margin && local.y > -leaderAvoidWidth * adjust - margin
	    ){
	    	if(local.x <= 0)local.x -= 1;
	    	else local.x += 1;
	    	local.x *= 3;
	    	
	    	if(local.y <= 0)local.y -= 1;
	    	else local.y += 1;
	    	local.y *= 3;
	    	
	    	local.z *= 0.5f;
	    	
	    	_myTarget = _myLeader.globalizePosition(local);
	    	_mySeek.target(_myTarget);
	    	_mySeek.apply(theAgent, myForce1, 0);
	     } else {
			double followDistance = _myLeader.radius * -_myFollowDistance;
			_myTarget.set(_myLeader.forward);
			_myTarget.multiplyLocal(followDistance);
			_myTarget.addLocal(_myLeader.position);
			_myArrive.target(_myTarget);
			_myArrive.apply(theAgent, myForce1, 0);
		}
	    super.apply(theAgent, myForce2, theDeltaTime);
	    //myForce2.scale(0.2f);
	    
	    theForce.addLocal(myForce1);
	    theForce.addLocal(myForce2);
	    return true;
	}

}
