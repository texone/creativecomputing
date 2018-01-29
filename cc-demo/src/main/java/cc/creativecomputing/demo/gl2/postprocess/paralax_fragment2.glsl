
// data from vertex shader
varying vec3 toLightInTangentSpace;
varying vec3 toCameraInTangentSpace;

// textures
uniform sampler2D diffuseMap;
uniform sampler2D heightMap;
uniform sampler2D normalMap;

////////////////////////////////////////

// scale for size of Parallax Mapping effect
@CCProperty(name = "parallaxScale", min = 0, max = 1)
uniform float	parallaxScale; // ~0.1

//////////////////////////////////////////////////////
// Implements Parallax Mapping technique
// Returns modified texture coordinates, and last used depth
vec2 parallaxMapping(
	in vec3 V, 
	in vec2 T, 
	out float parallaxHeight
){
	float parallaxScale = 0.1;
	
   // get depth for this fragment
   float initialHeight = texture2D(heightMap, gl_TexCoord[0].xy).r;

   // calculate amount of offset for Parallax Mapping
   vec2 texCoordOffset = parallaxScale * V.xy / V.z * initialHeight;

   // calculate amount of offset for Parallax Mapping With Offset Limiting
   //texCoordOffset = parallaxScale * V.xy * initialHeight;

   // retunr modified texture coordinates
   return gl_TexCoord[0].xy - texCoordOffset;
}

//////////////////////////////////////////////////////
// Implements self-shadowing technique - hard or soft shadows
// Returns shadow factor
float parallaxSoftShadowMultiplier(in vec3 L, in vec2 initialTexCoord,
                                       in float initialHeight)
{
   return 0.;
}

//////////////////////////////////////////////////////
// Calculates lighting by Blinn-Phong model and Normal Mapping
// Returns color of the fragment
vec4 normalMappingLighting(
	in vec2 T, 
	in vec3 L, 
	in vec3 V, 
	float shadowMultiplier
){
	// restore normal from normal map
	vec3 N = normalize(texture2D(normalMap, T).xyz * 2. - 1.);
	vec3 D = texture2D(diffuseMap, T).rgb;

	// ambient lighting
	float iamb = 0.2;
	// diffuse lighting
	float idiff = clamp(dot(N, L), 0., 1.);
	// specular lighting
	float ispec = 0.;
   
	if(dot(N, L) > 0.2){
		vec3 R = reflect(-L, N);
		ispec = pow(dot(R, V), 32.) / 1.5;
	}

	vec3 ambientLighting = vec3(1.);
 	vec4 resColor;
	resColor.rgb = D * (ambientLighting + (idiff + ispec) * pow(shadowMultiplier, 4.));
	resColor.a = 1.;

	return resColor;
}

/////////////////////////////////////////////
// Entry point for Parallax Mapping shader
void main(void)
{
   // normalize vectors after vertex shader
   vec3 V = normalize(toCameraInTangentSpace);
   vec3 L = normalize(toLightInTangentSpace);

   // get new texture coordinates from Parallax Mapping
   float parallaxHeight;
   vec2 T = parallaxMapping(V, gl_TexCoord[0].xy, parallaxHeight);

   // get self-shadowing factor for elements of parallax
   float shadowMultiplier = parallaxSoftShadowMultiplier(L, T, parallaxHeight - 0.05);

   // calculate lighting
   gl_FragColor = normalMappingLighting(T, L, V, shadowMultiplier);
	gl_FragColor.rgb = V;
}