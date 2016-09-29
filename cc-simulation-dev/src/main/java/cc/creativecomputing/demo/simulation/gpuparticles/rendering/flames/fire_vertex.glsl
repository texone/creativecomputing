#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform float tanHalfFOV;

uniform sampler2DRect positions;
uniform sampler2DRect indices;
uniform sampler2DRect infos;

uniform vec2 pointSize;

uniform float minSpeed;
uniform float maxSpeed;

void main (){
	vec4 myIndices = texture2DRect(indices, gl_Vertex.xy);
	vec4 myPosition = texture2DRect(positions, myIndices.xy);
	gl_Position = gl_ModelViewProjectionMatrix * myPosition;
	gl_TexCoord[0] = gl_MultiTexCoord0;
	
	vec2 dim = gl_Vertex.zw;
	if(gl_MultiTexCoord0.z > 0.5)dim.x *= -1.0;
	//if(gl_MultiTexCoord0.w > 0.5)dim.y *= -1.0;
	
	vec2 dimChange = pointSize.xy;//gl_MultiTexCoord0.w * 
	gl_Position.xy += dimChange * dim;

	vec4 myValues = texture2DRect(infos, myIndices.xy);
	float myAlpha = clamp(1 - myValues.x / myValues.y,0,1);
	float myIncrease = myValues.x / myValues.y;
	gl_TexCoord[0].z = min(myIncrease * mix(minSpeed, maxSpeed,gl_MultiTexCoord0.z), 1.0);
	gl_FrontColor = gl_Color;//min(gl_Color * gl_PointSize * gl_PointSize, gl_Color);
	//gl_FrontColor.a *= myAlpha * myAlpha;
}
	           