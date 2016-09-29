/**
 * These methods perform texture lookups at the four nearest neighbors of the 
 * position s and bilinearly interpolate them.
 */ 

float4 f4texRECTbilerp(samplerRECT tex, float2 s){
  float4 st;
  st.xy = floor(s - 0.5) + 0.5;
  st.zw = st.xy + 1;
  
  float2 t = s - st.xy; //interpolating factors 
    
  float4 tex11 = f4texRECT(tex, st.xy);
  float4 tex21 = f4texRECT(tex, st.zy);
  float4 tex12 = f4texRECT(tex, st.xw);
  float4 tex22 = f4texRECT(tex, st.zw);

  // bilinear interpolation
  return lerp(lerp(tex11, tex21, t.x), lerp(tex12, tex22, t.x), t.y);
}


half4 h4texRECTbilerp(samplerRECT tex, half2 s){
  half4 st;
  st.xy = floor(s - 0.5) + 0.5;
  st.zw = st.xy + 1;
  
  half2 t = s - st.xy; //interpolating factors 
    
  half4 tex11 = h4texRECT(tex, st.xy);
  half4 tex21 = h4texRECT(tex, st.zy);
  half4 tex12 = h4texRECT(tex, st.xw);
  half4 tex22 = h4texRECT(tex, st.zw);

  // bilinear interpolation
  return lerp(lerp(tex11, tex21, t.x), lerp(tex12, tex22, t.x), t.y);
}

half h1texRECTbilerp(samplerRECT tex, half2 s){
	half4 st;
	st.xy = floor(s - 0.5) + 0.5;
	st.zw = st.xy + 1;
  
	half2 t = s - st.xy; //interpolating factors 
	 
	half4 texels;
	texels.x = h1texRECT(tex, st.xy);
	texels.y = h1texRECT(tex, st.zy);
	texels.z = h1texRECT(tex, st.xw);
	texels.w = h1texRECT(tex, st.zw);

	texels.xy = lerp(texels.xz, texels.yw, t.x);
	return lerp(texels.x, texels.y, t.y);
}