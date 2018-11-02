

uniform float radius;
uniform float aspect;

void main(){
	vec4 p0 = gl_ModelViewProjectionMatrix * gl_Vertex;
	vec4 p1 = gl_ModelViewProjectionMatrix * gl_MultiTexCoord0;

	vec3 middlepoint = normalize((p0.xyz + p1.xyz)/2.0);
	vec3 lineoffset = p1.xyz - p0.xyz; 
	vec3 linedir = normalize(lineoffset); 
	float texcoef = abs(dot(linedir, middlepoint));

	// Warp transformed points by aspect ratio.
	vec4 w0 = p0;
	vec4 w1 = p1;
	w0.y /= aspect;
	w1.y /= aspect;

	// Calc vectors between points in screen space.
	vec2  delta2 = w1.xy / w1.z - w0.xy / w0.z;
	vec3  delta_p;

	delta_p.xy = delta2;
	delta_p.z = w1.z - w0.z;

	 // Calc UV basis vectors.
	 float l = distance(p0.xyz,p1.xyz);
    
	// Calc U
	float   len = length( delta2 );
	vec3  U = delta_p / len;

	// Create V orthogonal to U.
	vec3  V;
	V.x = U.y;
	V.y = -U.x;
	V.z = 0;

	// Calc offset part of postion.
    	vec3 offset = U * 0 + V * gl_MultiTexCoord1.x;
	offset.y *= aspect;
	
	//p0.xy += ((texcoef * 10) * U.xy);
	p0.xy += offset.xy * 3;
    	gl_TexCoord[0] = vec4(gl_MultiTexCoord1.yz,10 / l,0);
	gl_Position = p0;
}