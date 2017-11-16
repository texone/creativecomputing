vec3 hash3(vec2 p){
	vec3 q = vec3(
		dot(p,vec2(127.1,311.7)),
		dot(p,vec2(269.5,183.3)),
		dot(p,vec2(419.2,371.9))
	);
    return fract(sin(q)*43758.5453);
}