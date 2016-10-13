#version 120

vec3 hsv2rgb(vec3 c){
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main (){
	gl_Position = gl_ModelViewProjectionMatrix * vec4(gl_Vertex.xyz,1.0); 
	gl_TexCoord[0] = vec4(gl_Vertex.xyz,1.0);
	gl_FrontColor = vec4(1.0,gl_Vertex.z,0.0, 1.0);
}