#version 120
#extension GL_ARB_shader_texture_lod : enable

uniform sampler2D tex; // 0
uniform float vx_offset;
uniform vec2 invTextureSize;
uniform float FXAA_SPAN_MAX;
uniform float FXAA_REDUCE_MUL;
varying vec4 posPos;

vec3 FxaaPixelShader(
  vec4 posPos // Output of FxaaVertexShader interpolated across screen.
){

    #define FXAA_REDUCE_MIN   (1.0/128.0)
    vec3 rgbNW = texture2DLod(tex, posPos.zw, 0.0).xyz;
    vec3 rgbNE = texture2DLod(tex, posPos.zw + vec2(1,0) * invTextureSize, 0.0).xyz;
    vec3 rgbSW = texture2DLod(tex, posPos.zw + vec2(0,1) * invTextureSize, 0.0).xyz;
    vec3 rgbSE = texture2DLod(tex, posPos.zw + vec2(1,1) * invTextureSize, 0.0).xyz;
    vec3 rgbM  = texture2DLod(tex, posPos.xy, 0.0).xyz;
    
    vec3 luma = vec3(0.299, 0.587, 0.114);
    float lumaNW = dot(rgbNW, luma);
    float lumaNE = dot(rgbNE, luma);
    float lumaSW = dot(rgbSW, luma);
    float lumaSE = dot(rgbSE, luma);
    float lumaM  = dot(rgbM,  luma);
    
    float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
    float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));
    
    vec2 dir;
    dir.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
    dir.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));
    
    float dirReduce = max(
        (lumaNW + lumaNE + lumaSW + lumaSE) * (0.25 * FXAA_REDUCE_MUL),
        FXAA_REDUCE_MIN);
    
    float rcpDirMin = 1.0/(min(abs(dir.x), abs(dir.y)) + dirReduce);
    
    dir = min(vec2( FXAA_SPAN_MAX,  FXAA_SPAN_MAX),
          max(vec2(-FXAA_SPAN_MAX, -FXAA_SPAN_MAX),
          dir * rcpDirMin)) * invTextureSize;
          
    vec3 rgbA = 0.5 * (
        texture2DLod(tex, posPos.xy + dir * (1.0 / 3.0 - 0.5), 0.0).xyz +
        texture2DLod(tex, posPos.xy + dir * (2.0 / 3.0 - 0.5), 0.0).xyz);
    
    vec3 rgbB = rgbA * 0.5 + 0.25 * (
        texture2DLod(tex, posPos.xy + dir * -0.5, 0.0).xyz +
        texture2DLod(tex, posPos.xy + dir *  0.5, 0.0).xyz
	);
    
    float lumaB = dot(rgbB, luma);
    
    if((lumaB < lumaMin) || (lumaB > lumaMax)) return rgbA;
    
    return rgbB;
}

void main(){
	vec4 c = vec4(0.0);
	c.rgb = FxaaPixelShader(posPos);
	c.a = 1.0;
	gl_FragColor = c;
}