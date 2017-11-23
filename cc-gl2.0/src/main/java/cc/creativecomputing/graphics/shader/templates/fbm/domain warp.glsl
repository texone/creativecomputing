@CCProperty(name = "warp1", min = 0, max = 1000)
uniform float warp1;

vec3 warp1domain(in vec2 p, out vec2 q){
	q.x = fbm(p + vec2(0.0,0.0));
	q.y = fbm(p + vec2(5.2,1.3));

	return fbm(p + warp1 * q); 
}

@CCProperty(name = "warp2", min = 0, max = 1000)
uniform float warp2;

vec3 warp2domain(in vec2 p, out vec2 q, out vec2 r){
	q.x = fbm(p + vec2(0.0,0.0));
	q.y = fbm(p + vec2(5.2,1.3));

	r.x = fbm( p + warp1*q + vec2(1.7,9.2));
	r.y = fbm( p + warp1*q + vec2(8.3,2.8));

	return fbm( p + warp2 * r);
}