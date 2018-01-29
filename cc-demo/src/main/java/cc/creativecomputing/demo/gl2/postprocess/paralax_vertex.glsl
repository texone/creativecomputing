varying vec2 vUv;
varying vec3 vViewPosition;
varying vec3 vNormal;

void main() {

	vUv = gl_MultiTexCoord0.xy;
	vec4 mvPosition = gl_ModelViewMatrix * vec4( gl_Vertex.xyz, 1.0 );
	vViewPosition = -mvPosition.xyz;
	vNormal = normalize( gl_NormalMatrix * gl_Normal );
	gl_Position = gl_ProjectionMatrix * mvPosition; 

}