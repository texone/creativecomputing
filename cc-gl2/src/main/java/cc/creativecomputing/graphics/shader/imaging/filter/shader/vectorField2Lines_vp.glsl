#version 120
#extension GL_ARB_texture_rectangle : enable
#extension GL_EXT_gpu_shader4 : enable

uniform sampler2DRect field;
uniform float scale = 1.0;
uniform int downSample = 1;

void main(){
	
	vec4 direction = vec4(0.0,0.0,0.0,0.0);

	if ((int(gl_Vertex.x) % downSample == 0) && (int(gl_Vertex.y) % downSample == 0)) {
		direction = texture2DRect (field, gl_Vertex.xy);
	}
	
	vec4 position = vec4 (gl_Vertex.xy + direction.xy * scale * gl_Vertex.z, 0, 1.0);
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = gl_ModelViewProjectionMatrix * position;
}