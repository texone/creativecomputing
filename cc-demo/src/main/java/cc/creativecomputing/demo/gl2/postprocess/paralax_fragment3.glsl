
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

vec2 parallax_uv(vec2 uv, vec3 view_dir, out float parallaxHeight){
	float layer_depth = 1.0 / num_layers;
	float cur_layer_depth = 0.0;
	vec2 delta_uv = view_dir.xy * depth_scale / (view_dir.z * num_layers);
	vec2 cur_uv = uv;

	float depth_from_tex = texture2D(tex_depth, cur_uv).r;

	for (float i = 0.; i < num_layers; i++) {
		cur_layer_depth += layer_depth;
		cur_uv -= delta_uv;
		depth_from_tex = texture2D(tex_depth, cur_uv).r;

		if (depth_from_tex < cur_layer_depth) {
			break;
		}
	}
        
	// Parallax occlusion mapping
	vec2 prev_uv = cur_uv + delta_uv;
	float next = depth_from_tex - cur_layer_depth;
	float prev = texture2D(tex_depth, prev_uv).r - cur_layer_depth + layer_depth;
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
      float heightFromTexture	= texture2D(tex_depth, currentTextureCoords).r;
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
         heightFromTexture	= texture2D(tex_depth, currentTextureCoords).r;
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

    //gl_FragColor.rgb = ts_frag_pos * 0.01; 
}