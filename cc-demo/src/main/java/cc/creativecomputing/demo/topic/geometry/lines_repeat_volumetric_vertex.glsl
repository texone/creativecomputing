
@CCProperty(name = "radius", min = 0, max = 10)
uniform float radius;
uniform float aspect;

void main(){

	vec4 offsets = gl_MultiTexCoord1;
	gl_TexCoord[0].xy = offsets.zw;
	gl_TexCoord[1] = gl_MultiTexCoord2;

	//compute vertices position in clip space
	vec4 p0 = gl_ModelViewProjectionMatrix * gl_Vertex;
	vec4 p1 = gl_ModelViewProjectionMatrix * gl_MultiTexCoord0;

	//  line direction in screen space (perspective division required)
	vec2 lineDirProj = radius * normalize(p0.xy/p0.w - p1.xy/p1.w);

	// small trick to avoid inversed line condition when points are not on the same side of Z plane
	if( sign(p1.w) != sign(p0.w) )
		lineDirProj = -lineDirProj; 

	// offset position in screen space along line direction and orthogonal direction
	p0.xy += lineDirProj.xy * offsets.xx * vec2(1.0,aspect);
	p0.xy += lineDirProj.yx * offsets.yy * vec2(1.0,aspect) * vec2(1.0,-1.0) ;
	gl_TexCoord[0].z = p0.z;
	gl_Position = p0;
}