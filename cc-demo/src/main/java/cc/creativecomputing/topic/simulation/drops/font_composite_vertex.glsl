void main(){
	gl_Position = gl_ModelViewProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;	
	gl_TexCoord[0] = gl_MultiTexCoord0;
}