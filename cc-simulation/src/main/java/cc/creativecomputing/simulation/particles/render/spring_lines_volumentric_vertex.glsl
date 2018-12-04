#version 120 
#extension GL_ARB_texture_rectangle : enable

uniform float tanHalfFOV;

uniform sampler2DRect springs;
uniform sampler2DRect positions;
uniform sampler2DRect infos;
uniform sampler2DRect colors;

uniform vec2 offset;

@CCProperty(name = "radius", min = 0, max = 10)
uniform float radius;
uniform float aspectRatio;

uniform sampler2DRect lifeTimeBlends;
uniform float lifeTimeID;

void main (){
	vec4 offsets = gl_MultiTexCoord1;
	gl_TexCoord[0].xy = offsets.zw;
	vec4 myIndex = texture2DRect(springs, gl_Vertex.xy + offset);
	vec4 myValues = texture2DRect(infos, gl_Vertex.xy);
	
	float myAlpha = texture2DRect (lifeTimeBlends, vec2(myValues.x / myValues.y * (1 - myValues.z) * 100.0, lifeTimeID)).x;
	if(myIndex.x < 0 ){
		myAlpha = 0;
		gl_Position = gl_ModelViewProjectionMatrix * vec4(10000,0,0,1);
		return;
	}
	//compute vertices position in clip space
	vec4 p1 = gl_ModelViewProjectionMatrix * texture2DRect(positions, myIndex.xy);
	vec4 p0 = gl_ModelViewProjectionMatrix * texture2DRect(positions, gl_Vertex.xy);

	//  line direction in screen space (perspective division required)
	vec2 lineDirProj = radius * normalize(p0.xy/p0.w - p1.xy/p1.w);

	// small trick to avoid inversed line condition when points are not on the same side of Z plane
	if( sign(p1.w) != sign(p0.w) )
		lineDirProj = -lineDirProj;

		
	gl_FrontColor = vec4(1.0);
	if(gl_MultiTexCoord0.x <= 0){
	//	myAlpha = 0;
	} else {
		
		vec4 tmp = p0;
		p0 = p1;
		p1 = p0;
	} 
	
	// offset position in screen space along line direction and orthogonal direction
	p0.xy += lineDirProj.xy * offsets.xx * vec2(1.0,aspectRatio);
	p0.xy += lineDirProj.yx * offsets.yy * vec2(1.0,aspectRatio) * vec2(1.0,-1.0);

	gl_TexCoord[0].z = p0.z;
	p0.xyz *= myIndex.w;
	gl_Position = p0;
	
	
	gl_FrontColor.a = myAlpha * myIndex.w;
}
	           