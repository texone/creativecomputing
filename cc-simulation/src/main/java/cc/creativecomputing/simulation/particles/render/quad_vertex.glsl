#version 120 

uniform float tanHalfFOV;

uniform sampler2DRect positions;
uniform sampler2DRect infos;
uniform sampler2DRect colors;

uniform sampler2DRect lifeTimeBlends;
uniform float lifeTimeID;
uniform sampler2DRect gradient;

uniform float pointSize;
uniform float aspectRatio;

uniform float alpha;
@CCProperty(name = "max height", min = 0, max = 500)
uniform float _cMaxHeight;

vec2 hash2(vec2  p) {
	p = vec2(
		dot(p,vec2(127.1,311.7)),
		dot(p,vec2(269.5,183.3))
	); 
	return fract(sin(p)*43758.5453);
}


void main (){
	vec4 myPosition = texture2DRect(positions, gl_Vertex.xy);
	gl_Position = gl_ModelViewProjectionMatrix * myPosition;

	vec2 rand = hash2(gl_Vertex.xy);

	float size = pointSize + step(0., rand.y) * rand.x * 1;
	gl_Position.xy += vec2(size * gl_Vertex.z,  size * gl_Vertex.w * aspectRatio );

	vec4 lifeTime = texture2DRect(infos, gl_Vertex.xy);
	float myAlpha = texture2DRect (lifeTimeBlends, vec2(lifeTime.x / lifeTime.y * (1 - lifeTime.z) * 100.0, lifeTimeID)).x;
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_TexCoord[0].z = gl_Position.z;
	gl_TexCoord[1] = texture2DRect(colors, gl_Vertex.xy);
	float heightBlend = myPosition.y / _cMaxHeight;
	
	vec4 gradientCol = texture2DRect (gradient, vec2(heightBlend * 100.0, 0));
	
	gl_FrontColor = texture2DRect(colors, gl_Vertex.xy);
	gl_FrontColor.a *= myAlpha;// 1 -lifeTime.x / lifeTime.y ;
}
	           