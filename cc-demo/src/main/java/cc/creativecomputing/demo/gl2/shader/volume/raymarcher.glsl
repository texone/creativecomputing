/*

	Visualization is based on Paniq's Light Propagation Volume shadertoy here:

	https://www.shadertoy.com/view/XdtSRn

	Buffer A contains a 3D reaction-diffusion system, and Buffer B contains
    a propagating distance field derived from a 3D level set (3D contours) on the
    voxel data in Buffer A. The packing and unpacking scheme used is also due to Paniq.

    Try changing the texture size to 64^3 below and in the first line in 
    Buffer A and B. You'll probably need to be in fullscreen for it to work.
    

*/

const vec3 size = vec3(48.0);

float packfragcoord2 (vec2 p, vec2 s) {
    return floor(p.y) * s.x + p.x;
}
vec2 unpackfragcoord2 (float p, vec2 s) {
    float x = mod(p, s.x);
    float y = (p - x) / s.x + 0.5;
    return vec2(x,y); 
}
float packfragcoord3 (vec3 p, vec3 s) {
    return floor(p.z) * s.x * s.y + floor(p.y) * s.x + p.x;
}
vec3 unpackfragcoord3 (float p, vec3 s) {
    float x = mod(p, s.x);
    float y = mod((p - x) / s.x, s.y);
    float z = (p - x - floor(y) * s.x) / (s.x * s.y);
    return vec3(x,y+0.5,z+0.5);
}

uniform sampler3D volume;

vec4 fetch_lpv(vec3 p) {
    return texture3D(volume, p);    
}

// branchless range check
float inrange(float x, float min, float max) {
    return abs(0.5 * (sign(max - x)  + sign(x - min)));   
}

float inrange(vec3 x, vec3 min, vec3 max) {
    return inrange(x.x, min.x, max.x) * inrange(x.y, min.y, max.y) * inrange(x.z, min.z, max.z);  
}

/* 
   The tricky part here is how to approach raymarching the area outside of the 3D texture,
   which has no defined distance field. We could clamp, but the resulting distance field
   interferes with visibility from the camera's POV. The solution I use here is to raymarch
   the bounding cube until we hit the boundary, then switch to raymarching the 3D texture.
   There are artifacting issues on surfaces up against the boundary, there's probably
   a better way to handle that...
*/
vec2 sample_lpv_trilin(vec3 p) {
    p = p * size;
    float inr = inrange(p, vec3(0.0), size);
    vec3 pc = clamp(p, vec3(0.0), size);
    float cubedist = distance(p, pc);
    vec2 e = vec2(0.0,1.0);
    vec4 p000 = fetch_lpv(pc + e.xxx);
    vec4 p001 = fetch_lpv(pc + e.xxy);
    vec4 p010 = fetch_lpv(pc + e.xyx);
    vec4 p011 = fetch_lpv(pc + e.xyy);
    vec4 p100 = fetch_lpv(pc + e.yxx);
    vec4 p101 = fetch_lpv(pc + e.yxy);
    vec4 p110 = fetch_lpv(pc + e.yyx);
    vec4 p111 = fetch_lpv(pc + e.yyy);

    vec3 w = fract(pc);

    vec3 q = 1.0 - w;

    vec2 h = vec2(q.x,w.x);
    vec4 k = vec4(h*q.y, h*w.y);
    vec4 s = k * q.z;
    vec4 t = k * w.z;
        
    vec4 tril = 
          p000*s.x + p100*s.y + p010*s.z + p110*s.w
        + p001*t.x + p101*t.y + p011*t.z + p111*t.w;
    
    //return vec2(inr * tril.x, (1.0 - inr) * cubedist);
    return vec2(tril.x, (1.0 - inr) * cubedist);

}

vec4 sh_project(vec3 n) {
    return vec4(
        n,
        0.57735026918963);
}

float sh_dot(vec4 a, vec4 b) {
    return max(dot(a,b),0.0);
}

// 3 / (4 * pi)
const float m3div4pi = 0.23873241463784;
float sh_flux(float d) {
	return d * m3div4pi;
}

#ifndef M_DIVPI
#define M_DIVPI 0.3183098861837907
#endif

float sh_shade(vec4 vL, vec4 vN) {
    return sh_flux(sh_dot(vL, vN)) * M_DIVPI;
}

#define SHSharpness 1.0 // 2.0
vec4 sh_irradiance_probe(vec4 v) {
    const float sh_c0 = (2.0 - SHSharpness) * 1.0;
    const float sh_c1 = SHSharpness * 2.0 / 3.0;
    return vec4(v.xyz * sh_c1, v.w * sh_c0);
}

float shade_probe(vec4 sh, vec4 shn) {
    return sh_shade(sh_irradiance_probe(sh), shn);
}

void doCamera( out vec3 camPos, out vec3 camTar, in float time, in vec4 m ) {
    if (max(m.z, m.w) <= 0.0) {
    	float an = 1.5 + sin(time * 0.1) * 3.0;
		camPos = vec3(6.5*sin(an), 1.0 ,6.5*cos(an));
    	camTar = vec3(0.5,0.5,0.5);        
    } else {
    	float an = 10.0 * m.x - 5.0;
		camPos = vec3(6.5*sin(an),10.0 * m.y - 5.0,6.5*cos(an));
    	camTar = vec3(0.5,0.5,0.5);
    }
}

vec3 doBackground( void ) {
    return vec3( 0.0, 0.0, 0.0);
}

vec3 rayToTexture( vec3 p ) {
    return (p - vec3(0.0,0.5,0.0)) * 0.2 + 0.5;
}

vec2 doModel( vec3 p ) {
    p = rayToTexture(p);
    return sample_lpv_trilin(p);
    
}

vec4 doMaterial( in vec3 pos, in vec3 nor ) {
    return vec4(1.0,1.0,1.0,0.0);
}

//------------------------------------------------------------------------
// Lighting
//------------------------------------------------------------------------
vec3 doLighting( in vec3 pos, in vec3 nor, in vec3 rd, in float dis, in vec4 mal )
{
    vec3 col = mal.rgb;
    vec4 shr = vec4(1.0);
    
    // cross with ray dir for a velvety sort of effect
    vec4 shn = sh_project(cross(nor,rd));
    
    col *= 10.0 * vec3(shade_probe(shr, shn)); 

    return col;
}

float calcIntersection( in vec3 ro, in vec3 rd )
{
	const float maxd = 10.0;           // max trace distance
	const float precis = 1.0;          // precision of the intersection
    float h = precis*2.0;
    float t = 0.0;
	float res = -1.0;
    for( int i=0; i<90; i++ )          // max number of raymarching iterations is 90
    {
        if( h<precis||t>maxd ) break;
        vec2 d = doModel(ro+rd*t);
        if (d.y > 0.0) {
            t += 0.1 * d.y + 0.06;
            // we don't update h when raymarching the bounding cube, because
            // we want to continue marching once we intersect it.
        } else {
            t += 0.01 * d.x;
            h = d.x;
        }
    }

    if( t<maxd ) res = t;
    return res;
}

vec3 calcNormal( in vec3 pos )
{
    // a large epsilon is used here because the underlying data is coarse
    const float eps = 0.01;             // precision of the normal computation

    //const vec3 v1 = vec3( 1.0,-1.0,-1.0);
    const vec3 v1 = vec3( 1.0,-1.0,-1.0);
    const vec3 v2 = vec3(-1.0,-1.0, 1.0);
    const vec3 v3 = vec3(-1.0, 1.0,-1.0);
    const vec3 v4 = vec3( 1.0, 1.0, 1.0);

	return normalize( v1*doModel( pos + v1*eps ).x + 
					  v2*doModel( pos + v2*eps ).x + 
					  v3*doModel( pos + v3*eps ).x + 
					  v4*doModel( pos + v4*eps ).x );
}

mat3 calcLookAtMatrix( in vec3 ro, in vec3 ta, in float roll )
{
    vec3 ww = normalize( ta - ro );
    vec3 uu = normalize( cross(ww,vec3(sin(roll),cos(roll),0.0) ) );
    vec3 vv = normalize( cross(uu,ww));
    return mat3( uu, vv, ww );
}

vec3 ff_filmic_gamma3(vec3 linear) {
    vec3 x = max(vec3(0.0), linear-0.004);
    return (x*(x*6.2+0.5))/(x*(x*6.2+1.7)+0.06);
}

uniform vec2 iResolution;
uniform vec4 iMouse;
uniform float iTime;

void main(  )
{ 
    vec2 p = (-iResolution.xy + 2.0*gl_FragCoord.xy)/iResolution.y;
    vec4 m = vec4(iMouse.xy/iResolution.xy, iMouse.zw);

    //-----------------------------------------------------
    // camera
    //-----------------------------------------------------
    
    // camera movement
    vec3 ro, ta;
    doCamera( ro, ta, iTime, m );
    
    // camera matrix
    mat3 camMat = calcLookAtMatrix( ro, ta, 0.0 );  // 0.0 is the camera roll
    
    float dh = (0.666 / iResolution.y);
    


    //-----------------------------------------------------
	// render
    //-----------------------------------------------------

	vec3 colmin = doBackground();
    vec3 colmax = colmin;
    vec3 colavg = colmin;
    vec3 colavg2 = colmin;
    vec3 col = colmin;
    
    const int samps = 8;
    
    vec3 cols[samps];
    
    const float rads = 6.283185 / float(samps);
    
    for (int i = 0; i < samps; i++) {
        // create view ray
        vec2 dxy = dh * vec2(cos(float(i) * rads), sin(float(i) * rads));
        vec3 rd = normalize( camMat * vec3(p.xy + dxy,2.0) ); // 2.0 is the lens length

        // raymarch
        float t = calcIntersection( ro, rd );
        if( t>-0.5 )
        {
            // geometry
            vec3 pos = ro + t*rd;
            vec3 nor = calcNormal(pos);

            // materials
            vec4 mal = doMaterial( pos, nor );
            vec3 tc = doLighting( pos, nor, rd, t, mal );

            if (i == 0) {
            	colmin = tc;   
                colmax = tc; 
            }
			
            colmin = min(colmin, tc);
            colmax = max(colmax, tc);
            colavg += tc;
            cols[i] = tc;

        }
    }
    
    colavg /= float(samps);
    
    // outlier rejection, cleans up some artifacts
    float sum = 0.0;
    for (int i = 0; i < samps; i++) {
    	vec3 x = cols[i];
        float w = exp(-length(x - colavg) / 0.2);
        colavg2 += w * x;
        sum += w;
    }
    
    colavg2 /= sum;
    
    float sum2 = 0.0;
    for (int i = 0; i < samps; i++) {
    	vec3 x = cols[i];
        float w = exp(-length(x - colavg2) / 0.2);
        col += w * x;
        sum2 += w;
    }
    
    col /= sum2;
    

	//-----------------------------------------------------
	// postprocessing
    //-----------------------------------------------------
    // gamma
	col = ff_filmic_gamma3(col); //pow( clamp(col,0.0,1.0), vec3(0.4545) );
	   
    gl_FragColor = vec4( col, 1.0 );
}