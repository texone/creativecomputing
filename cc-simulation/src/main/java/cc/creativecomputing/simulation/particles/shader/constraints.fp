float3 bounceReflection(
	float3 theVelocity, float3 theNormal, float thePlacement,
	float theResilience, float theFriction, float theMinimalVelocity
){
	// Distibute velocity to normal and tangential contributions.
	float normalContribution = dot(theVelocity, theNormal);
	float3 vNormal = normalContribution * theNormal;
	float3 vTangent = theVelocity - vNormal;
	
	if (thePlacement < 0){
		// Get particle outside the collider as quickly as possible,
		// either with original or reflected velocity.
		
		if (normalContribution <= 0.0){
			return vTangent - vNormal;
		} else {
			return theVelocity;
		}
	} 
	
	// Slow down particle with friction only if slower than minimal velocity.
	if (length(theVelocity) < theMinimalVelocity)
		theFriction = 1.0;

	// Slowdown tangential movement with friction (in theory 1 - friction)
	// and reflected normal movement via resilience factor.
	return vTangent * theFriction - vNormal * theResilience;
}

float3 simpleReflection(float3 theVelocity, float3 theNormal, float theResilience){
	return reflect(theVelocity, theNormal) * theResilience;
}

interface Constraint{
	float3 constraint(float3 thePosition, float3 theVelocity, float2 theTexID, float theDeltaTime);
};

struct PlaneConstraint : Constraint{
	float3 normal;
	float constant;
	
	float resilience;
	float friction;
	float minimalVelocity;
	
	float3 constraint(float3 theVelocity, float3 thePosition, float2 theTexID, float theDeltaTime){
		float pseudoDistance = dot(normal, thePosition + theVelocity * theDeltaTime) - constant;
		if (pseudoDistance < 0){
			return bounceReflection(
				theVelocity, 
				normalize(normal), 
				pseudoDistance,
				resilience, 
				friction, 
				minimalVelocity
			);
		}
		return theVelocity;
	}
};

struct FloorConstraint : Constraint{
	float y;
	
	float resilience;
	float friction;
	float minimalVelocity;
	
	float3 constraint(float3 theVelocity, float3 thePosition, float2 theTexID, float theDeltaTime){
		if (thePosition.y + theVelocity.y * theDeltaTime < y){
			return bounceReflection(
				theVelocity, 
				float3(0, 1, 0), 
				thePosition.y - y,
				resilience, 
				friction, 
				minimalVelocity
			);
		}
		return theVelocity;
	}
};

struct ZConstraint : Constraint{
	float z;
	
	float resilience;
	float friction;
	float minimalVelocity;
	
	float3 constraint(float3 theVelocity, float3 thePosition, float2 theTexID, float theDeltaTime){
		if (thePosition.z + theVelocity.z * theDeltaTime < z){
			return bounceReflection(
				theVelocity, 
				float3(0, 0, 1), 
				thePosition.z - z,
				resilience, 
				friction, 
				minimalVelocity
			);
		}
		return theVelocity;
	}
};

struct BoxConstraint : Constraint{
	float3 minCorner;
	float3 maxCorner;
	
	float resilience;
	float friction;
	float minimalVelocity;
	
	float3 constraint(float3 theVelocity, float3 thePosition, float2 theTexID, float theDeltaTime){
		float3 futurePosition = thePosition + theVelocity * theDeltaTime;
		
		if (futurePosition.x < minCorner.x){
			return bounceReflection(
				theVelocity, 
				float3(1, 0, 0), 
				thePosition.x - minCorner.x,
				resilience, 
				friction, 
				minimalVelocity
			);
		}
		if (futurePosition.y < minCorner.y){
			return bounceReflection(
				theVelocity, 
				float3(0, 1, 0), 
				thePosition.y - minCorner.y,
				resilience, 
				friction, 
				minimalVelocity
			);
		}
		if (futurePosition.z < minCorner.z){
			return bounceReflection(
				theVelocity, 
				float3(0, 0, 1), 
				thePosition.z - minCorner.z,
				resilience, 
				friction, 
				minimalVelocity
			);
		}
		
		if (futurePosition.x > maxCorner.x){
			return bounceReflection(
				theVelocity, 
				float3(-1, 0, 0), 
				maxCorner.x - thePosition.x,
				resilience, 
				friction, 
				minimalVelocity
			);
		}
		if (futurePosition.y > maxCorner.y){
			return bounceReflection(
				theVelocity, 
				float3(0, -1, 0), 
				maxCorner.y - thePosition.y,
				resilience, 
				friction, 
				minimalVelocity
			);
		}
		if (futurePosition.z > maxCorner.z){
			return bounceReflection(
				theVelocity, 
				float3(0, 0, -1), 
				maxCorner.z - thePosition.z,
				resilience, 
				friction, 
				minimalVelocity
			);
		}
		return theVelocity;
	}
};

/*
 * constraint to let particles bounce of a sphere
 */
struct SphereConstraint : Constraint{

	float3 center;
	float radius;
	
	float resilience;
	float friction;
	float minimalVelocity;
	
	float3 constraint(float3 theVelocity, float3 thePosition, float2 theTexID,float theDeltaTime){
		float3 delta = (thePosition + theVelocity * theDeltaTime) - center;
		float dist = length(delta);

		if (dist < radius){
			return bounceReflection(
				theVelocity, 
				delta / dist,
				distance(thePosition, center) - radius,
				resilience, 
				friction, 
				minimalVelocity
			);
		}
		return theVelocity;
	}
};

/*
 * constraint to let particles bounce of a sphere
 */
 
struct Texture3DConstraint : Constraint{

	sampler3D texture;
	
	float3 textureScale;
	float3 textureOffset;
	
	float minLength;
	
	float3 minCut;
	float3 maxCut;
	
	float minForce;
	
	float resilience;
	float friction;
	float minimalVelocity;
	
	float3 constraint(float3 theVelocity, float3 thePosition, float2 theTexID,float theDeltaTime){
		
		float3 futurePos = (thePosition + theVelocity * theDeltaTime);
		float3 texturePos = (futurePos - textureOffset) / textureScale;
		
		float3 force = tex3D(texture, texturePos);
	
		if(
			texturePos.x >= maxCut.x || 
			texturePos.y >= maxCut.y || 
			texturePos.z >= maxCut.z || 
			texturePos.x <= minCut.x || 
			texturePos.y <= minCut.y || 
			texturePos.z <= minCut.z ||
			length(force) < minForce
		){
			return theVelocity;
		}

		return bounceReflection(
			theVelocity, 
			normalize(force),
			-1,
			resilience, 
			friction, 
			minimalVelocity
		);
	}
};

/*
 * constraint to keep particles inside a sphere.
 */
struct SphereInConstraint : Constraint{

	float3 center;
	float radius;
	
	float resilience;
	float friction;
	float minimalVelocity;
	
	float3 constraint(float3 theVelocity, float3 thePosition, float2 theTexID,float theDeltaTime){
		float3 delta = (thePosition + theVelocity * theDeltaTime) - center;
		float dist = length(delta);

		if (dist > radius){
			return bounceReflection(
				theVelocity, 
				delta / dist,
				distance(thePosition, center) - radius,
				resilience, 
				friction, 
				minimalVelocity
			);
		}
		return theVelocity;
	}
};

/*
 * terrain constraint that takes a heightmap texture to let particles bounce of it.
 * texture can be scaled and moved in all x,y and z direction to place it in the scene
 * correctly
 */
struct TerrainConstraint : Constraint{
	samplerRECT texture;
	
	float2 textureSize;
	float3 scale;
	float3 offset;
	
	float resilience;
	float friction;
	float minimalVelocity;
	
	float exponent;
	
	float terrainHeight(float2 terrainPos){
		return pow((float)texRECT(texture, terrainPos),exponent);
	}

	float3 constraint(float3 theVelocity, float3 thePosition, float2 theTexID, float theDeltaTime){
		float3 fPosition = thePosition + theVelocity * theDeltaTime;
		float2 terrainPos = fPosition.xz / scale.xz + offset.xz;
		float height = terrainHeight(terrainPos);

		if (fPosition.y < height * scale.y + offset.y){
		
			// Calculate normal vector.
			float3 vertex0 = float3(0, height * scale.y, 0);
			float3 vertex1 = float3(scale.x, terrainHeight(terrainPos + float2(1, 0)) * scale.y, 0);
			float3 vertex2 = float3(0, terrainHeight(terrainPos + float2(0, 1)) * scale.y, scale.z);
			
			float3 normal = normalize(cross(vertex1 - vertex0, vertex2 - vertex0));
			
			if (normal.y < 0)
				normal *= -1;
	
			// Check whether previous time step has collision already.
			float2 terrainPosOld = thePosition.xz * (textureSize / scale.xz) + offset.xz;
			float heightOld = terrainHeight(terrainPosOld) * scale.y + offset.y;

			return bounceReflection(
				theVelocity, 
				normal, 
				thePosition.y - heightOld,
				resilience, 
				friction, 
				minimalVelocity
			);
		}
		return theVelocity;
	}
};

/*
* 2d shape to bounce particles in the same plane like texture forces
*/
struct ShapeConstraint : Constraint {
	samplerRECT texture;
	
	float2 textureSize;
	float3 scale;
	float3 offset;
	
	float resilience;
	float friction;
	float minimalVelocity;
	
	float3 constraint(float3 theVelocity, float3 thePosition, float2 theTexID, float theDeltaTime){
		
		
		float3 fPosition = thePosition + theVelocity * theDeltaTime;
		
		float2 shapePos   = thePosition.xy / scale.xy + offset.xy;
		float2 fshapePos  = fPosition.xy / scale.xy + offset.xy;
		
		float3 pointNow  = texRECT (texture, shapePos);
		float3 pointNext = texRECT (texture, fshapePos);
		float3 newVelocity = theVelocity*texRECT (texture, float2(0,0));
		
		// Calculate normal vector for the next point
		float3 normal = float3(0,0,0);
		float weight = 1;
		
		normal.x = texRECT (texture, fshapePos+float2(-1,-1)) + 2*texRECT (texture, fshapePos+float2(-1, 0)) + texRECT (texture, shapePos+float2(-1, 1))
		         - texRECT (texture, fshapePos+float2( 1,-1)) - 2*texRECT (texture, fshapePos+float2( 1, 0)) - texRECT (texture, shapePos+float2( 1, 1));
		normal.y = texRECT (texture, fshapePos+float2(-1,-1)) + 2*texRECT (texture, fshapePos+float2( 0,-1)) + texRECT (texture, shapePos+float2( 1,-1))
		         - texRECT (texture, fshapePos+float2(-1, 1)) - 2*texRECT (texture, fshapePos+float2( 0, 1)) - texRECT (texture, shapePos+float2( 1, 1));
		
		normal = normalize (normal);
		
		// find nearest shape point in velocity direction (shape parametrization unknown)
		float len = length (fshapePos - shapePos);
		int nSteps = log ((float)ceil(len)) / log(2.0);
		
		float2 pos = shapePos;
		float2 dir = fshapePos - shapePos;
		float3 point;
		int sign = 1;
		for (int i=1; i<=nSteps; i++) {
			int div = pow (2.0, (float)i);
			pos += sign*dir/div;
			point = texRECT (texture, pos).xyz;
			if (length(point) > 0) {
				sign = -1;
			}
			else {
				sign = 1;
			}
		}
		float dist = length(fshapePos - pos);

		if (pointNow.z>0.2) {
			//return bounceReflection (theVelocity, normal, dist, resilience*2, friction, minimalVelocity);
			return theVelocity*friction;
		}
	
		return theVelocity;
	}
};


/*
struct CableConstraint : Constraint{
	samplerRECT id1Texture;
	samplerRECT id2Texture;
	
	float maxLength;
	
	float resilience;
	float friction;
	float minimalVelocity;
	
	// constrain a particle to be a fixed distance from another particle
	float3 constraint(float3 theVelocity, float3 thePosition, float2 theTexID, float theDeltaTime){
	
		float4 ids1 = texRECT(id1Texture, theTexID);
		float4 ids2 = texRECT(id2Texture, theTexID);
		
		float3[4] positions;
		
		// get positions of neighbouring particles
		positions[0] = texRECT(positionTexture, ids1.xy);
		positions[1] = texRECT(positionTexture, ids1.zw);
		positions[2] = texRECT(positionTexture, ids2.xy);
		positions[3] = texRECT(positionTexture, ids2.zw);
		
		float3[4] normals;
		
		normal[0] = normalize(positions[0] - thePosition);
		
		float3[4] velocities;
		
		// get velocities of neighbouring particles
		velocities[0] = texRECT(velocityTexture, ids1.xy);
		velocities[1] = texRECT(velocityTexture, ids1.zw);
		velocities[2] = texRECT(velocityTexture, ids2.xy);
		velocities[3] = texRECT(velocityTexture, ids2.zw);
		
		float maxSeperation = 0;
		float3 normal;
		int index = -1;
		
		for(int i = 0; i < 4;i++){
			normal = positions[i] - thePosition;
			float seperation = length(normal);
			if(seperation > maxSeperation){
				maxSeperation = seperation;
				index = i;
			}
		}
		if(maxSeperation < maxLength)return theVelocity;
		
		return bounceReflection(
			theVelocity - velocities[index], 
			normalize(normal), 
			maxLength - maxSeperation,
			resilience, 
			friction, 
			minimalVelocity
		);
	}
};
*/
uniform Constraint constraints[];