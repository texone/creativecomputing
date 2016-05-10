#version 120

uniform float radius;
uniform float radiusInc;

const float PI = 3.14;

vec4 sphereCoords(vec3 data) {
	float lo = radians(-data.x + 180);
	float la = radians(data.y + 90);
			
	float myRadius = sin(data.z * PI) * radiusInc + radius;
			
	return vec4(
		myRadius * sin(la) * cos(lo),
		myRadius * sin(la) * sin(lo),
		myRadius * cos(la),
		1.0
	);
}

void main(){
	
	vec4 myPosition = sphereCoords(gl_Vertex.xyz);
	gl_Position = gl_ModelViewProjectionMatrix * myPosition;
	gl_FrontColor = gl_Color;//vec4(weights.x, weights.y, weights.z, 1.0);
	
	gl_TexCoord[0] = vec4(gl_Vertex.w,1.0,1.0,1.0);
}