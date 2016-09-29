uniform mat4 joints[50];

void main(){
	mat4 skinningMatrix = mat4(0.0);
    
	vec4 weights = gl_MultiTexCoord1;
	vec4 indices = gl_MultiTexCoord2;
	float myTotalWeight = weights[0] + weights[1] +weights[2] + weights[3];
    skinningMatrix += joints[int(indices[0]) + 1] * weights[0];
    skinningMatrix += joints[int(indices[1]) + 1] * weights[1];
    skinningMatrix += joints[int(indices[2]) + 1] * weights[2];
    skinningMatrix += joints[int(indices[3]) + 1] * weights[3];
    skinningMatrix /= myTotalWeight;
    
    mat4 bindMatrix = joints[0];
    
	gl_Position = ftransform();
	//gl_Position = gl_ModelViewProjectionMatrix * 
	gl_TexCoord[0] = (skinningMatrix * bindMatrix * gl_MultiTexCoord0);
	//gl_FrontColor.xyz /= 300.0;
	//gl_FrontColor.xyz += 0.5;
}