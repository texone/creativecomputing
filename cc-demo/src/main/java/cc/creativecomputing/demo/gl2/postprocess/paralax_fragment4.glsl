
uniform sampler2D tex_norm;
uniform sampler2D tex_diffuse;
uniform sampler2D tex_depth;

@CCProperty(name = "depth scale", min = -1, max = 1)
uniform float depth_scale;
@CCProperty(name = "num layers", min = 0, max = 100)
uniform float num_layers;

varying vec3 ts_light_pos;
varying vec3 ts_view_pos;
varying vec3 ts_frag_pos;

// The MIT License
// Copyright © 2013 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


// Gradient Noise (http://en.wikipedia.org/wiki/Gradient_noise), not to be confused with
// Value Noise, and neither with Perlin's Noise (which is one form of Gradient Noise)
// is probably the most convenient way to generate noise (a random smooth signal with 
// mostly all its energy in the low frequencies) suitable for procedural texturing/shading,
// modeling and animation.
//
// It produces smoother and higher quality than Value Noise, but it's of course slighty more
// expensive.
//
// The princpiple is to create a virtual grid/latice all over the plane, and assign one
// random vector to every vertex in the grid. When querying/requesting a noise value at
// an arbitrary point in the plane, the grid cell in which the query is performed is
// determined (line 32), the four vertices of the grid are determined and their random
// vectors fetched (lines 37 to 40). Then, the position of the current point under 
// evaluation relative to each vertex is doted (projected) with that vertex' random
// vector, and the result is bilinearly interpolated (lines 37 to 40 again) with a 
// smooth interpolant (line 33 and 35).

vec2 hash( vec2 x ){
    const vec2 k = vec2( 0.3183099, 0.3678794 );
    x = x*k + k.yx;
    return -1.0 + 2.0*fract( 16.0 * k*fract( x.x*x.y*(x.x+x.y)) );
}

float noise( in vec2 p ){
    vec2 i = floor( p );
    vec2 f = fract( p );
	
	vec2 u = f*f*(3.0-2.0*f);

    return mix( mix( dot( hash( i + vec2(0.0,0.0) ), f - vec2(0.0,0.0) ), 
                     dot( hash( i + vec2(1.0,0.0) ), f - vec2(1.0,0.0) ), u.x),
                mix( dot( hash( i + vec2(0.0,1.0) ), f - vec2(0.0,1.0) ), 
                     dot( hash( i + vec2(1.0,1.0) ), f - vec2(1.0,1.0) ), u.x), u.y);
}

@CCProperty(name = "scale", min = 0, max = 1)
uniform float scale;
@CCProperty(name = "gain", min = 0, max = 1)
uniform float gain;
@CCProperty(name = "octaves", min = 1, max = 4)
uniform float octaves;
@CCProperty(name = "lacunarity", min = 0, max = 4)
uniform float lacunarity;

float octavedNoise(in vec2 s){ 
	float myScale = scale;
	float myFallOff = gain;
	
	int myOctaves = int(floor(octaves)); 
	float myResult = 0.;  
	float myAmp = 0.;
	
	for(int i = 0; i < myOctaves;i++){
		float noiseVal = noise(s * myScale); 
		myResult += noiseVal * myFallOff;
		myAmp += myFallOff;
		myFallOff *= gain;
		myScale *= lacunarity;
	}
	float myBlend = octaves - float(myOctaves);
	
	myResult += noise(s * myScale) * myFallOff * myBlend;   
	myAmp += myFallOff * myBlend;
	
	if(myAmp > 0.0){
		myResult /= myAmp;
	}
	
	return myResult;
}

float depth(vec2 uv){
	return (octavedNoise(uv * 200.) + 1.) / 2.;
}

vec2 parallax_uv(vec2 uv, vec3 view_dir, out float parallaxHeight){
	float layer_depth = 1.0 / num_layers;
	float cur_layer_depth = 0.0;
	vec2 delta_uv = view_dir.xy * depth_scale / (view_dir.z * num_layers);
	vec2 cur_uv = uv;

	float depth_from_tex = depth(cur_uv);

	for (float i = 0.; i < num_layers; i++) {
		cur_layer_depth += layer_depth;
		cur_uv -= delta_uv;
		depth_from_tex = depth( cur_uv);

		if (depth_from_tex < cur_layer_depth) {
			break;
		}
	}
        
	// Parallax occlusion mapping
	vec2 prev_uv = cur_uv + delta_uv;
	float next = depth_from_tex - cur_layer_depth;
	float prev = depth( prev_uv) - cur_layer_depth + layer_depth;
	float weight = next / (next - prev);

	parallaxHeight = cur_layer_depth + mix(next, prev, weight);
	
	return mix(cur_uv, prev_uv, weight);
}

float parallaxSoftShadowMultiplier(
	vec2 uv,
	vec3 L, 
     in float initialHeight
){
   float shadowMultiplier = 1.;

   const float minLayers = 15.;
   const float maxLayers = 30.;

   // calculate lighting only for surface oriented to the light source
   if(dot(vec3(0., 0., 1.), L) <= 0.) return 1.;
      // calculate initial parameters
      float numSamplesUnderSurface	= 0.;
      shadowMultiplier	= 0.;
      float numLayers	= mix(maxLayers, minLayers, abs(dot(vec3(0, 0, 1), L)));
      float layerHeight	= initialHeight / numLayers;
      vec2 texStep	= depth_scale * L.xy / L.z / numLayers;

      // current parameters
      float currentLayerHeight	= initialHeight - layerHeight;
      vec2 currentTextureCoords	= uv + texStep;
      float heightFromTexture	= depth( currentTextureCoords);
      int stepIndex	= 1;

      // while point is below depth 0.0 )
      while(currentLayerHeight > 0.){
         // if point is under the surface
         if(heightFromTexture < currentLayerHeight)
         {
            // calculate partial shadowing factor
            numSamplesUnderSurface	+= 1.;
            float newShadowMultiplier = (currentLayerHeight - heightFromTexture) *
                                             (1.0 - float(stepIndex) / numLayers);
            shadowMultiplier	= max(shadowMultiplier, newShadowMultiplier);
         }

         // offset to the next layer
         stepIndex	+= 1;
         currentLayerHeight	-= layerHeight;
         currentTextureCoords	+= texStep;
         heightFromTexture	= depth( currentTextureCoords);
      }

      // Shadowing factor should be 1 if there were no points under the surface
    /*
      if(numSamplesUnderSurface < 1.){
         return 1.;
      }*/
      return shadowMultiplier = 1.0 - shadowMultiplier;
}

void main(){
	
    vec3 light_dir = normalize(ts_light_pos - ts_frag_pos);
    vec3 view_dir = normalize(ts_view_pos - ts_frag_pos);

    // Only perturb the texture coordinates if a parallax technique is selected
    float parallaxHeight;
    vec2 uv = parallax_uv(gl_TexCoord[0].xy, view_dir, parallaxHeight);
	// get self-shadowing factor for elements of parallax
    float shadowMultiplier = parallaxSoftShadowMultiplier(uv, light_dir, parallaxHeight - 0.05);
    vec3 albedo = texture2D(tex_diffuse, uv).rgb;
    vec3 ambient = 0.3 * albedo;

   
	// Normal mapping
	vec3 norm = normalize(texture2D(tex_norm, uv).rgb * 2.0 - 1.0);
	float diffuse = max(dot(light_dir, norm), 0.0);
    gl_FragColor = vec4((diffuse * albedo + ambient) * shadowMultiplier, 1.0);

    //gl_FragColor.rgb = gl_TexCoord[1].xyz * 0.002; 
gl_FragColor.rgb = norm;
    //gl_FragColor.rgb = vec3(depth(gl_TexCoord[0].xy));
    //gl_FragColor.rgb = vec3(shadowMultiplier);
}