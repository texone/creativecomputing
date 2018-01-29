vec3 normal(sampler2DRect texture, vec2 coords, float offset){

	vec2 xOffset = vec2(1.0, 0.0);
	vec2 yOffset = vec2(0.0, 1.0);
     
	float s01 = texture2DRect(brightTex, coords.xy - xOffset).y;
	float s21 = texture2DRect(brightTex, coords.xy + xOffset).y;
	float s10 = texture2DRect(brightTex, coords.xy - yOffset).y;
	float s12 = texture2DRect(brightTex, coords.xy + yOffset).y; 
    
	vec3 va = normalize(vec3(0.25,0,(s21-s01)));
	vec3 vb = normalize(vec3(0,0.25,s12-s10));
	return cross(va,vb);
}