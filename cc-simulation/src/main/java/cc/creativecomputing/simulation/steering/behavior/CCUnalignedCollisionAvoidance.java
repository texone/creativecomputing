package cc.creativecomputing.simulation.steering.behavior;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.demo.simulation.steering.CCTestAgent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.steering.CCAgent;

public class CCUnalignedCollisionAvoidance extends CCNeighborHoodBehavior {

	private double _myMinTimeToCollision = 0;

	@CCProperty(name = "collsion radius", min = 0, max = 100)
	private double _cCollisionRadius = 30;

	private float distanceMultiplier = 1;

	private static final float PARALLELNESSCHECK_ANGLE = 0.707f;

	public double predictNearestApproachTime(CCParticle theOtherAgent, CCParticle theAgent) {
		CCVector3 relVelocity = theOtherAgent.velocity().subtract(theAgent.velocity());
		double relSpeed = relVelocity.length();

		CCVector3 relTangent = relVelocity.clone();
		relTangent.normalize();

		CCVector3 relPosition = theAgent.position.subtract(theOtherAgent.position);
		double projection = relTangent.dot(relPosition);

		return projection / relSpeed;
	}

	@Override
	public boolean apply(CCParticle theAgent, CCVector3 theForce, double theDeltaTime) {
		theForce.set(0, 0, 0);

		// "go on to consider potential future collisions"
		CCParticle threat = null;

		/*
		 * "Time (in seconds) until the most immediate collision threat found so
		 * far. Initial value is a threshold: don't look more than this many
		 * frames into the future."
		 */
		double myTimeToNearestApproach = 20.0;
		double myNearestApproachDistance = 0;

		for (CCAgent myOtherAgent : _myNeighborhood.getNearAgents(theAgent, _myNearAreaRadius * 5)) {
			double myTimeToApproach = predictNearestApproachTime(myOtherAgent, theAgent);

			if (myTimeToApproach >= 0.0 && myTimeToApproach < myTimeToNearestApproach) {
				CCVector3 myFuturePosition = theAgent.futurePosition(myTimeToApproach);
				CCVector3 myOtherFuturePosition = myOtherAgent.futurePosition(myTimeToApproach);
				double d = myFuturePosition.distance(myOtherFuturePosition);
				if (d < _cCollisionRadius * 10) {
					myNearestApproachDistance = d;
					myTimeToNearestApproach = myTimeToApproach;
					threat = myOtherAgent;
				}
			}
		}

		if (threat == null)
			return false;
		CCVector3 myOtherFuturePosition = threat.futurePosition(myTimeToNearestApproach);
		CCVector3 myFuturePosition = theAgent.futurePosition(myTimeToNearestApproach);

		CCVector3 lateralAvoidance = theAgent.position.subtract(myOtherFuturePosition);
		lateralAvoidance = theAgent.up.multiply(lateralAvoidance.dot(theAgent.up));

		CCVector3 forwardAvoidance;
		if (theAgent.velocity().length() < 0.2 * theAgent.maxSpeed) {
			forwardAvoidance = theAgent.forward;
		} else {
			forwardAvoidance = myFuturePosition.subtract(myOtherFuturePosition);
			forwardAvoidance = theAgent.forward.multiply(forwardAvoidance.dot(theAgent.forward));
		}

		theForce.set(lateralAvoidance);
		theForce.addLocal(forwardAvoidance);
		return true;

		//
		// // xxx solely for annotation
		// CCVector3 xxxThreatPositionAtNearestApproach = null;
		// CCVector3 xxxOurPositionAtNearestApproach = null;
		//
		// /* "For each of the other vehicles, determine which (if any)
		// pose the most immediate threat of collision." */
		//
		//
		//
		// for (CCAgent myOtherAgent:_myNeighborhood.getNearAgents(theAgent,
		// _myNearAreaRadius)){
		// // "avoid when future positions are this close (or less)"
		// // "At OpenSeer" => float collisionDangerThreshold =
		// this.agent.getRadius() * 2;
		// double collisionDangerThreshold = (_myCollisionRadius * 2 +
		// _myCollisionRadius * 1.25f) * this.distanceMultiplier;
		//
		// // 'predicted time until nearest approach of "this" and "other"'
		// double time = this.agent.predictNearestApproachTime(obstacle);
		// //System.out.println("Time: " + time);//DEBUG
		//
		// /* "If the time is in the future, sooner than any other threatened
		// collision..." */
		// if ((time >= 0) && (time < myTimeToNearestApproach *
		// (obstacle.getRadius() + this.agent.getRadius()))) {
		// // "At OpenSeer" => if ((time >= 0) && (time < minTime))
		// CCVector3 threatPositionAtNearestApproach = new CCVector3();
		// CCVector3 ourPositionAtNearestApproach = new CCVector3();
		//
		// /* "if the two will be close enough to collide, make a note of it" */
		// if (this.agent.computeNearestApproachPositions(obstacle, time,
		// ourPositionAtNearestApproach, threatPositionAtNearestApproach) <
		// collisionDangerThreshold) {
		// myTimeToNearestApproach = time;
		// threat = obstacle;
		// xxxThreatPositionAtNearestApproach = threatPositionAtNearestApproach;
		// xxxOurPositionAtNearestApproach = ourPositionAtNearestApproach;
		// }
		// }
		// }
		//
		// // "if a potential collision was found, compute steering to avoid"
		// if (threat != null) {
		// CCVector3 agentVelocity = theAgent.velocity().clone();
		// CCVector3 otherVelocity = threat.velocity().clone();
		// boolean forceTreatAsParallel = false;
		//
		// //If agent velocity is zero compute the behaviour as the velocities
		// are parallel
		// if (agentVelocity == null || agentVelocity.equals(CCVector3.ZERO)) {
		// agentVelocity = new CCVector3();
		// forceTreatAsParallel = true;
		// }
		//
		// //If Threat velocity is zero compute the behaviour as the velocities
		// are parallel
		// if (otherVelocity == null || otherVelocity.equals(CCVector3.ZERO)) {
		// otherVelocity = new CCVector3();
		// forceTreatAsParallel = true;
		// }
		//
		// CCVector3 agentVNormNeg = agentVelocity.normalize().negate();
		// this.removeNegativeZeros(agentVNormNeg);
		//
		// if ((otherVelocity.normalize()).equals(agentVNormNeg)) {
		// steer = randomVectInPlane(this.agent.getVelocity(),
		// this.agent.getLocalTranslation()).normalize();
		// } else //Check for paralleness
		// {
		// float parallelness =
		// agentVelocity.normalize().dot(otherVelocity.normalize());
		//
		// // "anti-parallel "head on" paths" or "parallel paths": Evade moving
		// to the correct side
		// if ((parallelness < -PARALLELNESSCHECK_ANGLE || parallelness >
		// PARALLELNESSCHECK_ANGLE) || forceTreatAsParallel) {
		// CCVector3 agentFordwardVector;
		//
		// if (!forceTreatAsParallel) {
		// agentFordwardVector = agentVelocity.normalize();
		// } else {
		// agentFordwardVector = this.agent.fordwardVector();
		// }
		//
		// //Calculate side vector
		// CCPlane sidePlane = new CCPlane();
		// sidePlane.setOriginNormal(theAgent.getLocalTranslation(),
		// agentFordwardVector);
		//
		// CCVector3 sidePoint =
		// sidePlane.getClosestPoint(threat.getLocalTranslation());
		// CCVector3 sideVector =
		// theAgent.offset(sidePoint).normalize().negate();
		//
		// if (sideVector.negate().equals(CCVector3.ZERO)) {
		// //Move in a random direction
		// sideVector = randomVectInPlane(theAgent.getVelocity(),
		// theAgent.getLocalTranslation()).normalize();
		// }
		//
		// steer = sideVector.mult(theAgent.getMoveSpeed());
		// } else {
		// // "perpendicular paths:" Steer away and slow/increase the speed
		// knowing future positions
		// steer =
		// xxxOurPositionAtNearestApproach.subtract(xxxThreatPositionAtNearestApproach);
		// }
		// }
		// }
		// return true;
		//
		// int myCount = 0;
		// theForce.set(0,0,0);
		//
		// neighbors.clear();
		//
		// for (CCAgent myOtherAgent:_myNeighborhood.getNearAgents(theAgent,
		// _myNearAreaRadius)){
		// //if (isInAngle(theAgent,myOtherAgent)){
		// myCount++;
		// theForce.addLocal(myOtherAgent.forward);
		// neighbors.add(myOtherAgent);
		// //}
		// }
		//
		// if (myCount > 0){
		// theForce.multiplyLocal(1.0F / myCount);
		// if (deltaHeading)
		// theForce.subtractLocal(theAgent.forward);
		// return true;
		// }
		// return false;
	}

	public void draw(CCGraphics g, CCTestAgent theAgent) {
		
		//"go on to consider potential future collisions"
		CCParticle threat = null;
		        
		/* "Time (in seconds) until the most immediate collision threat found
		    so far.  Initial value is a threshold: don't look more than this
		       many frames into the future." */
		double myTimeToNearestApproach = 120.0;
		double myNearestApproachDistance = 0;
		
		for (CCAgent myOtherAgent:_myNeighborhood.getNearAgents(theAgent, _myNearAreaRadius * 5)){
			double myTimeToApproach = predictNearestApproachTime(myOtherAgent, theAgent);
			
			if (myTimeToApproach >= 0.0 && myTimeToApproach < myTimeToNearestApproach) {
				CCVector3 myFuturePosition = theAgent.futurePosition(myTimeToApproach);
				CCVector3 myOtherFuturePosition = myOtherAgent.futurePosition(myTimeToApproach);
				double d = myFuturePosition.distance(myOtherFuturePosition);
				if (d < _cCollisionRadius) {
					myNearestApproachDistance = d;
					myTimeToNearestApproach = myTimeToApproach;
					threat = myOtherAgent;
				}
			}
		}
		        
		if (threat == null) return;
		CCLog.info(myTimeToNearestApproach);
		g.color(CCColor.YELLOW);
//		g.line(theAgent.position, threat.position);
		CCVector3 myOtherFuturePosition = threat.futurePosition(myTimeToNearestApproach);
		CCVector3 myFuturePosition = theAgent.futurePosition(myTimeToNearestApproach);

		g.line(theAgent.position, myFuturePosition);
		g.line(threat.position, myOtherFuturePosition);
		CCVector3 lateralAvoidance = theAgent.position.subtract(myOtherFuturePosition);
		lateralAvoidance = theAgent.up.multiply(lateralAvoidance.dot(theAgent.up));

		CCVector3 forwardAvoidance;
		if (theAgent.velocity().length() < 0.2 * theAgent.maxSpeed) {
			forwardAvoidance= theAgent.forward;
		} else {
			forwardAvoidance = myFuturePosition.subtract(myOtherFuturePosition);
			forwardAvoidance = theAgent.forward.multiply(forwardAvoidance.dot(theAgent.forward));
		}
		
//		theForce.set(lateralAvoidance);
//		theForce.addLocal(forwardAvoidance);
	}
}
