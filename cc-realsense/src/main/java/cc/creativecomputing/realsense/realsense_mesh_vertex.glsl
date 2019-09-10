
uniform sampler2D positions;

void main(){
	gl_TexCoord[0] = gl_MultiTexCoord0;
	vec4 position = texture2D(positions, gl_Vertex.xy);
	position.w = 1;
	gl_FrontColor = vec4(1);
	//position.xyz *= 1;
	gl_Position = gl_ModelViewProjectionMatrix * position;
}