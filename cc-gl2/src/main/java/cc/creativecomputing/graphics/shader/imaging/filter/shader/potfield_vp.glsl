void main(){

	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = ftransform();
	gl_PointSize = 10.0;
	//gl_Position.z = 10.0;
}