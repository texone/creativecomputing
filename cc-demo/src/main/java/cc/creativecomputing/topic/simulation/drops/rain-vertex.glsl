void main() {
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_TexCoord[1] = gl_MultiTexCoord1;
	gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;	
}
