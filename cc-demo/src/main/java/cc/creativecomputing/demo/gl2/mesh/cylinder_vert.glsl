#define time iTime*0.15
#define tau 6.2831853

mat2 makem2(in float theta){float c = cos(theta);float s = sin(theta);return mat2(c,-s,s,c);}

uniform sampler2D randomTexture;

uniform float iTime;
uniform vec2 iResolution;

float noise( in vec2 x ){return texture2D(randomTexture, x *.001).x;}

float fbm(in vec2 p)
{	
	float z=2.;
	float rz = 0.;
	vec2 bp = p;
	for (float i= 0.;i < 3.;i++)
	{
		rz+= abs((noise(p)-0.5)*2.)/z;
		z = z*2.;
		p = p*2.;
	}
	return rz;
}

@CCProperty(name = "dual fbm", min = 0, max = 20)
uniform float dualFBM;

float dualfbm(in vec2 p)
{ 
    //get two rotated fbm calls and displace the domain
	vec2 p2 = p*7.0;
	vec2 basis = vec2(fbm(p2-time*0.6),fbm(p2+time*0.7));
	basis = (basis-.5)* dualFBM;
	p += basis;
	
	//coloring
	return fbm(p*makem2(time*0.2));
}

@CCProperty(name = "length", min = 10, max = 500)
uniform float length;

@CCProperty(name = "radius", min = 10, max = 100)
uniform float radius;

@CCProperty(name = "displacement", min = 0, max = 200)
uniform float displacement;
@CCProperty(name = "scale", min = 0, max = 2)
uniform float scale;

vec3 normal(vec2 coords, float offset){

	vec2 xOffset = vec2(1.0, 0.0);
	vec2 yOffset = vec2(0.0, 1.0);
     
	float s01 = dualfbm(coords.xy - xOffset * offset);
	float s21 = dualfbm(coords.xy + xOffset * offset);
	float s10 = dualfbm(coords.xy - yOffset * offset);
	float s12 = dualfbm(coords.xy + yOffset * offset); 
    
	vec3 va = normalize(vec3(0.25,0,(s21-s01)));
	vec3 vb = normalize(vec3(0,0.25,s12-s10));
	return cross(va,vb); 
}

@CCProperty(name = "normal offset", min = 0, max = 0.1)
uniform float normalOffset;

@CCProperty(name = "normal blend", min = 0, max = 1)
uniform float normalBlend;

void main(){
	vec4 myPos = gl_Vertex * vec4(length / 2., radius, radius, 1.);
	vec3 myNormal = normalize(gl_Normal.xyz);
	float d = dualfbm(gl_Vertex.xy * vec2(5.0,1.0) * scale) * 1.5; 
	vec3 dNormal = normal (gl_Vertex.xy * vec2(5.0,1.0) * scale, normalOffset) ;
	dNormal = mix(dNormal, myNormal,normalBlend);

	gl_FrontColor = vec4((dNormal+ 1.) / 2., 1.0); 
	myPos.xyz += myNormal * d * displacement; 
	gl_Position = gl_ModelViewProjectionMatrix * myPos;

	
	gl_FrontColor = vec4(d,d,d,1.0);
	gl_TexCoord[0].xyz = dNormal;
	gl_TexCoord[1].xyz = myPos.xyz * vec3(0.01,0.05,0.1) * 10.;
}