//Noise animation - Electric
//by nimitz (stormoid.com) (twitter: @stormoid)

//The domain is displaced by two fbm calls one for each axis.
//Turbulent fbm (aka ridged) is used for better effect.

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

float circ(vec2 p) 
{
	float r = length(p)* fract(iTime * 0.1);
	r = log(sqrt(r ));
	return abs(mod(r*4.,tau)-3.14)*6.+.2;

}

void main( )
{
	//setup system
	vec2 p = gl_FragCoord.xy / iResolution.xy-0.5;
	p.x *= iResolution.x/iResolution.y;
	p*=4.;
	
    float rz = dualfbm(p + vec2(iTime * 0.));
	
	//rings
	//p /= exp(mod(time*10.,3.14159));
	rz *= pow(abs((0.1-circ(p))),.9);
	
	//final color
	vec3 col = vec3(.4,0.2,0.1)/rz;
	col=pow(abs(col),vec3(.99));
	float d = dualfbm(p) * 1.5; 
	vec3 col2 = mix(vec3(0.4,0.0,0.0), vec3(1.0,1.0,0.), d);
	gl_FragColor = vec4(col,1.);
}