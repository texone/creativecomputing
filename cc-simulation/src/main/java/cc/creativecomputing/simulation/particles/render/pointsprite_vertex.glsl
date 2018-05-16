#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform float tanHalfFOV;

uniform sampler2DRect positions;
uniform sampler2DRect infos;
uniform sampler2DRect colors;

uniform sampler2DRect lifeTimeBlends;
uniform sampler2DRect gradient;

uniform float pointSize;
uniform float aspectRatio;

uniform float alpha;
@CCProperty(name = "max height", min = 0, max = 500)
uniform float _cMaxHeight;

void main (){
	vec4 myPosition = texture2DRect(positions, gl_Vertex.xy);
	gl_Position = gl_ModelViewProjectionMatrix * myPosition;
	gl_Position.xy += vec2(pointSize * gl_Vertex.z,  pointSize * gl_Vertex.w * aspectRatio);

	vec4 lifeTime = texture2DRect(infos, gl_Vertex.xy);
	float myAlpha = texture2DRect (lifeTimeBlends, vec2(lifeTime.x / lifeTime.y * (1 - lifeTime.z) * 100.0, 0)).x;
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_TexCoord[1] = texture2DRect(colors, gl_Vertex.xy);
	float heightBlend = myPosition.y / _cMaxHeight;
	
	vec4 gradientCol = texture2DRect (gradient, vec2(heightBlend * 100.0, 0));
	
	gl_FrontColor = gradientCol;
	gl_FrontColor.a = myAlpha;
}
	           