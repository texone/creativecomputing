vec2 hash2(vec2  p) {
	p = vec2(
		dot(p,vec2(127.1,311.7)),
		dot(p,vec2(269.5,183.3))
	); 
	return fract(sin(p)*43758.5453);
}