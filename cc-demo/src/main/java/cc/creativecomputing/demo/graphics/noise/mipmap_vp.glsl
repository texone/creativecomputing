#1.4

uniform sampler2D texture;
uniform float lod;

void main(){

	vec4 col = texture2DLod(texture, gl_MultiTexCoord0.xy,lod);
	vec4 pos = gl_Vertex;
	pos.z += col.r * 100.0;
	gl_Position = gl_ModelViewProjectionMatrix * pos;
	gl_FrontColor = col;//vec4(1.0, 1.0, 1.0, 1.0);
}