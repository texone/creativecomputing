#version 120

varying vec4 albedo;
varying vec4 diffuse;
varying vec4 specular;
varying vec4 pos;
varying vec3 normal;
varying float depth;

uniform sampler2D colorTexture;

void main(){
	gl_FragData[0] = pos;
	gl_FragData[1] = vec4(
		normal.x * 0.5f + 0.5f, 
		normal.y * 0.5f + 0.5f, 
		normal.z * 0.5f + 0.5f, 
		depth
	);
	gl_FragData[2] = gl_Color * texture2D(colorTexture, vec2(gl_TexCoord[0].x, 1.0 - gl_TexCoord[0].y));
}