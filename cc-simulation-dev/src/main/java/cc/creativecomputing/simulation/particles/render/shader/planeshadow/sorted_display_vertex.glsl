#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform float tanHalfFOV;

uniform sampler2DRect positions;
uniform sampler2DRect indices;
uniform sampler2DRect infos;

uniform float pointSize;

uniform vec3 planeNormal;
uniform float planeConstant;
uniform vec3 lightDirection;

vec3 intersection(vec3 thePosition, vec3 theDirection) {
	float denominator = dot(planeNormal, theDirection);

	float numerator = -(dot(planeNormal, thePosition) - planeConstant);
	float ratio = numerator / denominator;

	if (ratio < 0)
		return vec3(10000000,0,0); // intersects behind origin

	return theDirection * ratio + thePosition;
}

void main (){
	vec4 myIndices = texture2DRect(indices, gl_Vertex.xy);
	vec4 myPosition = texture2DRect(positions, myIndices.xy);
	
	vec3 direction = normalize(myPosition.xyz - lightDirection);
	vec3 myShadowPosition = intersection(myPosition.xyz, direction);
	gl_Position = gl_ModelViewProjectionMatrix * vec4(myShadowPosition,1);
	gl_TexCoord[0] = vec4(gl_Vertex.xy,0,0);
	
	// Compute point size.
	vec4 posViewSpace = gl_ModelViewMatrix * vec4(myShadowPosition,1);
	gl_PointSize = 2;//max(tanHalfFOV / -posViewSpace.z * pointSize, 1);
	
	vec4 myValues = texture2DRect(infos, myIndices.xy);
	float myAlpha = clamp(1 - myValues.x / myValues.y,0,1);
	
	gl_FrontColor = min(gl_Color * gl_PointSize * gl_PointSize, gl_Color);
	gl_FrontColor.a *= myAlpha * myAlpha;
}
	           