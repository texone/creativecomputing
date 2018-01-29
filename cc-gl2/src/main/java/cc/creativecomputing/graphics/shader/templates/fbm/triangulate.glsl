//from jessifin (https://www.shadertoy.com/view/lslXDf)
vec3 bary(vec2 a, vec2 b, vec2 c, vec2 p) {
    vec2 v0 = b - a, v1 = c - a, v2 = p - a;
    float inv_denom = 1.0 / (v0.x * v1.y - v1.x * v0.y)+1e-9;
    float v = (v2.x * v1.y - v1.x * v2.y) * inv_denom;
    float w = (v0.x * v2.y - v2.x * v0.y) * inv_denom;
    float u = 1.0 - v - w;
    return abs(vec3(u,v,w));
}

/*
	Idea is quite simple, find which (triangular) side of a given tile we're in,
	then get 3 samples and compute height using barycentric coordinates.
*/


float map(vec3 p){
    vec3 q = fract(p) - 0.5;
    vec3 iq = floor(p);
    vec2 p1 = vec2(iq.x-.5, iq.z+.5);
    vec2 p2 = vec2(iq.x+.5, iq.z-.5);
    
    float d1 = heightmap(p1);
    float d2 = heightmap(p2);
    
    float sw = sign(q.x+q.z); 
    vec2 px = vec2(iq.x+.5*sw, iq.z+.5*sw);
    float dx = heightmap(px);
    vec3 bar = bary(vec2(.5*sw,.5*sw),vec2(-.5,.5),vec2(.5,-.5), q.xz);
    return (bar.x*dx + bar.y*d1 + bar.z*d2 + p.y + 3.)*.9;
}