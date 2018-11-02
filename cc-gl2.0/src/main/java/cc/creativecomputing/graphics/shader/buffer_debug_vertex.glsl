void main(){
	gl_TexCoord[0] = gl_Vertex;	
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;	
}