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
	for (float i= 0.;i < 5.;i++)
	{
		rz+= abs((noise(p)-0.5)*2.)/z;
		z = z*2.;
		p = p*2.;
	}
	return rz;
}

float dualfbm(in vec2 p)
{
    //get two rotated fbm calls and displace the domain
	vec2 p2 = p*7.0;
	vec2 basis = vec2(fbm(p2-time*0.6),fbm(p2+time*0.7));
	basis = (basis-.5)*(sin(iTime * 1.) + 1.0) * 10.0;
	p += basis;
	
	//coloring
	return fbm(p*makem2(time*0.2));
}

@CCProperty(name = "length", min = 10, max = 500)
uniform float length;

@CCProperty(name = "radius", min = 10, max = 100)
uniform float radius;

void main(){
	vec4 myPos = gl_Vertex * vec4(length / 2., radius, radius, 1.);
	vec3 myNormal = normalize(gl_Normal.xyz);
	gl_Position = gl_ModelViewProjectionMatrix * myPos;

	float d = dualfbm(gl_Vertex.xy) * 1.5; 
	gl_FrontColor = vec4((myNormal+ 1.) / 2., 1.0); 
	gl_FrontColor = vec4(d,d,d,1.0);
}