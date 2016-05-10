
varying vec3 n;
varying vec3 i;

uniform float amount;

void main(){

	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_FrontColor = gl_Color;//vec4(weights.x, weights.y, weights.z, 1.0);
	
	vec4 ecPosition  = gl_ModelViewMatrix * gl_Vertex;
    vec3 ecPosition3 = ecPosition.xyz / ecPosition.w;

    i = normalize(ecPosition3);
    n = normalize(gl_NormalMatrix * mix(vec3(0.0,0.0,1.0),gl_Normal, amount));

  	gl_TexCoord[0] = gl_MultiTexCoord0;
	
}