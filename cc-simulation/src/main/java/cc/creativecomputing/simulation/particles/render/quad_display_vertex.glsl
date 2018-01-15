#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform float tanHalfFOV;

@CCProperty(name = "x scale", min = 0, max = 50)
uniform float boxXscale;
@CCProperty(name = "y scale", min = 0, max = 50)
uniform float boxYscale;
@CCProperty(name = "z scale", min = 0, max = 50)
uniform float boxZscale;

uniform sampler2DRect velocities;
uniform sampler2DRect positions;
uniform sampler2DRect infos;
uniform sampler2DRect colors;

uniform sampler2DRect lifeTimeBlends;

float lifeTimeBlend(float blend, float envelopeIndex){
	return texture2DRect (lifeTimeBlends, vec2(blend * 100.0, envelopeIndex)).x;
}

mat3 calcLookAtMatrix(vec3 origin, vec3 target, float roll) {
	vec3 rr = vec3(sin(roll), cos(roll), 0.0);
	vec3 ww = normalize(target - origin);
	vec3 uu = normalize(cross(ww, rr));
	vec3 vv = normalize(cross(uu, ww));
	return mat3(uu, vv, ww);
}

varying vec3 vPosition;
varying vec3 vNormal;

void main (){

	vec3 myVelocity = normalize(texture2DRect(velocities, gl_Vertex.xy).xyz);
	vec4 myPosition = texture2DRect(positions, gl_Vertex.xy);
	vec4 myValues = texture2DRect(infos, gl_Vertex.xy);
	vec4 myColors = texture2DRect(colors, gl_Vertex.xy);
	float myAlpha = clamp(1 - myValues.x / myValues.y * (1 - myValues.z),0,1);
	myAlpha = lifeTimeBlend(myValues.x / myValues.y * (1 - myValues.z),0.);
	mat4 localRotation = mat4( calcLookAtMatrix(myPosition.xyz, myPosition.xyz + myVelocity, 0. ) );
	myPosition += localRotation * vec4(gl_MultiTexCoord0.xyz * vec3(boxXscale, boxYscale, boxZscale) * myAlpha,0.);
	gl_Position = gl_ModelViewProjectionMatrix * myPosition;
	gl_TexCoord[0] = vec4(gl_Vertex.xy,0,0);

	vec4 normal = localRotation * vec4(gl_Normal,1.0);
	
	// Compute point size.
	vPosition = myPosition.xyz;
	vNormal = normal.xyz;
	
	vec4 posViewSpace = gl_ModelViewMatrix * myPosition;
	//gl_PointSize = max(tanHalfFOV / -posViewSpace.z * pointSize,1);
	 
	gl_FrontColor = texture2DRect(colors, gl_Vertex.xy) * gl_Color;
	gl_FrontColor.a = 1.;//*= pow(myAlpha, 0.1);// * myAlpha;
	gl_FrontColor.rgb = myColors.xyz; 
	
	
}
	           