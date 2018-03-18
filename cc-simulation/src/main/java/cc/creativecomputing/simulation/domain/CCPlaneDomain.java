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
package cc.creativecomputing.simulation.domain;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.CCParticle;
/**
 * The point o is a point on the plane. n is the normal vector of the plane. 
 * It need not be unit length. If you have a plane in a,b,c,d form remember 
 * that n = [a,b,c] and you can compute a suitable point o as o = -n*d. 
 * The normal will get normalized, so it need not already be normalized.
 * <br>
 * Generate returns the point o.
 * <br>
 * Within returns true if the point is in the positive half-space of the plane 
 * (in the plane or on the side that n points to).
 * @author christianr
 *
 */
public class CCPlaneDomain extends CCDomain{
	protected CCVector3 _myPoint;
	protected CCVector3 _myNormal;
	double d;

	/**
	 * Initializes a new Plane
	 * @param thePoint
	 * @param theNormal
	 */
	public CCPlaneDomain(final CCVector3 thePoint, final CCVector3 theNormal){
		_myPoint = thePoint;
		_myNormal = theNormal;
		_myNormal.normalizeLocal(); // Must normalize it.
		d = -_myPoint.dot(_myNormal);
	}
	
	public CCPlaneDomain(CCVector3 v1,  CCVector3 v2,  CCVector3 v3){
		set(v1,v2,v3);
	}
	
	/**
	 * Constructor for extensions of plane were the normal is set later
	 * @param thePoint
	 */
	protected CCPlaneDomain(final CCVector3 thePoint){
		_myPoint = thePoint;
	}
	
	public void set(CCVector3 v1,  CCVector3 v2,  CCVector3 v3) {
		CCVector3 aux1 = v1.subtract(v2);
		CCVector3 aux2 = v3.subtract(v2);

		_myNormal = aux2.cross(aux1);
		_myNormal.normalizeLocal();
		_myPoint = v2;
		d = -_myPoint.dot(_myNormal);
	}
	
	/**
	 * Distance from plane = n * p + d
	 * Inside is the positive half-space.
	 */
	public boolean isWithin(final CCVector3 i_vector){
		return _myNormal.dot(i_vector) >= -d;
	}

	/**
	 * How do I sensibly make a point on an infinite plane?
	 */
	public CCVector3 generate(){
		return _myPoint.clone();
	}
	
	/**
	 * Returns the distance of the given vector to the plane
	 * @param theVector
	 * @return
	 */
	public double distance(final CCVector3 theVector){
		return theVector.dot(_myNormal) + d;
	}
	
	@Override
	public boolean intersectsLine(final CCVector3 theVectorA, final CCVector3 theVectorB){
		final CCVector3 p3_p1 = _myPoint.clone();
		p3_p1.subtractLocal(theVectorA);
		
		final CCVector3 p2_p1 = theVectorB.clone();
		p2_p1.subtractLocal(theVectorA);
		
		double u = _myNormal.dot(p3_p1)/_myNormal.dot(p2_p1);
		return u > 0 && u < 1;
	}
	
	

	@Override
	public boolean intersectsLine(
		final CCVector3 theVectorA, final CCVector3 theVectorB,
		final CCVector3 thePointOnSurface, final CCVector3 theNormal
	){
		final CCVector3 p3_p1 = _myPoint.clone();
		p3_p1.subtractLocal(theVectorA);
		
		final CCVector3 p2_p1 = theVectorB.clone();
		p2_p1.subtractLocal(theVectorA);
		
		double lineDot = _myNormal.dot(p2_p1);
		double u = _myNormal.dot(p3_p1)/_myNormal.dot(p2_p1);

		
		if(u > 0 && u < 1){
			thePointOnSurface.set(p2_p1);
			thePointOnSurface.multiplyLocal(u);
			thePointOnSurface.addLocal(theVectorA);
			
			theNormal.set(_myNormal);
			
			if(lineDot > 0)theNormal.multiplyLocal(-1);
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean intersectsBox(final CCVector3 theMinCorner, final CCVector3 theMaxVector) {
		return intersectsLine(theMinCorner, theMaxVector);
	}
	
	@Override
	public void avoidance(final CCParticle theParticle, final CCVector3 theForce, final double theLookAhead, final double theEpsilon){
		
        // See if particle's current and look_ahead positions cross plane.
        // If not, couldn't hit, so keep going.
        CCVector3 myFutureParticlePosition = theParticle.futurePosition(theLookAhead);

        // nrm stores the plane normal (the a,b,c of the plane eqn).
        // Old and new distances: dist(p,plane) = n * p + d
        final double myOldDistance = theParticle.position.dot(_myNormal) + d;
        final double myNewDistance = myFutureParticlePosition.dot(_myNormal) + d;
        
        if(CCMath.sameSign(myOldDistance, myNewDistance))
            return;
       
        // Time to collision
        final double myCollisionTime = -myOldDistance / _myNormal.dot(theParticle.velocity());

        // Vector from projection point to point of impact
        CCVector3 s = new CCVector3(
        	theParticle.velocity().x * myCollisionTime + _myNormal.x * myOldDistance,
        	theParticle.velocity().y * myCollisionTime + _myNormal.y * myOldDistance,
        	theParticle.velocity().z * myCollisionTime + _myNormal.z * myOldDistance
        );
        
        double slen = s.lengthSquared();
        if(slen == 0.0f)
            s = _myNormal;
        else
            s.normalizeLocal();

        s.multiplyLocal(10000f / (CCMath.sq(myCollisionTime)+theEpsilon));
        theForce.add(s);
	}
	
	
	
	@Override
	public void bounce(
		final CCParticle theParticle, 
		final CCVector3 theVector, 
		final double theDeltaTime,
		final double theOneMinusFriction,
		final double theResilence,
		final double theCutOffSquared
	) {

        // See if particle's current and look_ahead positions cross plane.
        // If not, couldn't hit, so keep going.
		CCVector3 myFutureParticlePosition = theParticle.futurePosition(10);

        // Old and new distances: dist(p,plane) = n * p + d
        final double myOldDistance = theParticle.position.dot(_myNormal) + d;
        final double myNewDistance = myFutureParticlePosition.dot(_myNormal) + d;
        
        if(CCMath.sameSign(myOldDistance, myNewDistance))
            return;

        double nv = _myNormal.dot(theParticle.velocity());
//        double t = -myOldDistance / nv; // Time steps before hit

        // A hit! A most palpable hit!
        // Compute tangential and normal components of velocity
        // Normal Vn = (V.N)N
        CCVector3 myNormalComponent = new CCVector3(
        	_myNormal.x * nv,
        	_myNormal.y * nv,
        	_myNormal.z * nv
        );
        // Tangent Vt = V - Vn
        CCVector3 myTangentialComponent = theParticle.velocity().subtract(myNormalComponent);

        // Compute new velocity heading out:
        // Don't apply friction if tangential velocity < cutoff
        if(myTangentialComponent.lengthSquared() <= theCutOffSquared)
        	theParticle.velocity().set(
        		myTangentialComponent.x - myNormalComponent.x * theResilence,
        		myTangentialComponent.y - myNormalComponent.y * theResilence,
        		myTangentialComponent.z - myNormalComponent.z * theResilence
        	);
        else
        	theParticle.velocity().set(
            	myTangentialComponent.x * theOneMinusFriction - myNormalComponent.x * theResilence,
            	myTangentialComponent.y * theOneMinusFriction - myNormalComponent.y * theResilence,
            	myTangentialComponent.z * theOneMinusFriction - myNormalComponent.z * theResilence
            );
	}
}
