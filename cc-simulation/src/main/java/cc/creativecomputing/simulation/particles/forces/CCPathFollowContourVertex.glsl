
void main(){
	gl_FrontColor = vec4(gl_Normal.xyz / 2.0 + 0.5, 1.0);
	gl_Position = gl_ModelViewProjectionMatrix * gl_vertex;
}