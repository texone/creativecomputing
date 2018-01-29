uniform sampler2D heightMap;
uniform sampler2D diffuseMap;

@CCProperty(name = "parallaxScale", min = 0, max = 1)
uniform float parallaxScale;
@CCProperty(name = "parallaxMinLayers", min = 5, max = 50)
uniform float parallaxMinLayers;
@CCProperty(name = "parallaxMaxLayers", min = 5, max = 50)
uniform float parallaxMaxLayers;

varying vec2 vUv;
varying vec3 vViewPosition;
varying vec3 vNormal;


/*
vec2 parallaxMap( in vec3 V ) {

	float initialHeight = texture2D( heightMap, vUv ).r;

	// No Offset Limitting: messy, floating output at grazing angles.
	//"vec2 texCoordOffset = parallaxScale * V.xy / V.z * initialHeight;

	// Offset Limiting
	vec2 texCoordOffset = 1.0 * V.xy * initialHeight;
	return vUv - texCoordOffset;
}
*/

vec2 parallaxMap( in vec3 V ) {
float parallaxScale = 1.;
float parallaxMinLayers = 5.;
float parallaxMaxLayers = 50.;
	// Determine number of layers from angle between V and N
	float numLayers = mix( parallaxMaxLayers, parallaxMinLayers, abs( dot( vec3( 0.0, 0.0, 1.0 ), V ) ) );

	float layerHeight = 1.0 / numLayers;
	float currentLayerHeight = 0.0;
	// Shift of texture coordinates for each iteration
	vec2 dtex = parallaxScale * V.xy / V.z / numLayers;

	vec2 currentTextureCoords = vUv;

	float heightFromTexture = texture2D( heightMap, currentTextureCoords ).r;

	// while ( heightFromTexture > currentLayerHeight )
	// Infinite loops are not well supported. Do a "large" finite
	// loop, but not too large, as it slows down some compilers.
	for ( int i = 0; i < 30; i += 1 ) {
		if ( heightFromTexture <= currentLayerHeight ) {
			break;
		}
		currentLayerHeight += layerHeight;
		// Shift texture coordinates along vector V
		currentTextureCoords -= dtex;
		heightFromTexture = texture2D( heightMap, currentTextureCoords ).r;
	}


	return currentTextureCoords;
/*
#elif defined( USE_RELIEF_PARALLAX )

	vec2 deltaTexCoord = dtex / 2.0;
	float deltaHeight = layerHeight / 2.0;

	// Return to the mid point of previous layer
	currentTextureCoords += deltaTexCoord;
	currentLayerHeight -= deltaHeight;

	// Binary search to increase precision of Steep Parallax Mapping
	const int numSearches = 5;
	for ( int i = 0; i < numSearches; i += 1 ) {

		deltaTexCoord /= 2.0;
		deltaHeight /= 2.0;
		heightFromTexture = texture2D( bumpMap, currentTextureCoords ).r;
		// Shift along or against vector V
		if( heightFromTexture > currentLayerHeight ) { // Below the surface

			currentTextureCoords -= deltaTexCoord;
			currentLayerHeight += deltaHeight;

		} else { // above the surface

			currentTextureCoords += deltaTexCoord;
			currentLayerHeight -= deltaHeight;

		}

	}
	return currentTextureCoords;

#elif defined( USE_OCLUSION_PARALLAX )

	vec2 prevTCoords = currentTextureCoords + dtex;

	// Heights for linear interpolation
	float nextH = heightFromTexture - currentLayerHeight;
	float prevH = texture2D( bumpMap, prevTCoords ).r - currentLayerHeight + layerHeight;

	// Proportions for linear interpolation
	float weight = nextH / ( nextH - prevH );

	// Interpolation of texture coordinates
	return prevTCoords * weight + currentTextureCoords * ( 1.0 - weight );

#else // NO_PARALLAX

	return vUv;

#endif
*/ 
}



vec2 perturbUv( vec3 surfPosition, vec3 surfNormal, vec3 viewPosition, out vec4 debug ) {

 	vec2 texDx = dFdx( vUv );
	vec2 texDy = dFdy( vUv );

	vec3 vSigmaX = dFdx( surfPosition );
	vec3 vSigmaY = dFdy( surfPosition );

	
	vec3 vR1 = cross( vSigmaY, surfNormal );
	vec3 vR2 = cross( surfNormal, vSigmaX );
	float fDet = dot( vSigmaX, vR1 );

	vec2 vProjVscr = ( 1.0 / fDet ) * vec2( dot( vR1, viewPosition ), dot( vR2, viewPosition ) );
	vec3 vProjVtex;
	vProjVtex.xy = texDx * vProjVscr.x + texDy * vProjVscr.y;
	vProjVtex.z = dot( surfNormal, viewPosition );

	return parallaxMap( vProjVtex * 0.);
}

void main() {
	vec4 debug;
	vec2 mapUv = perturbUv( -vViewPosition, normalize( vNormal ), normalize( vViewPosition ),debug );
	
	gl_FragColor = texture2D( diffuseMap, mapUv );

}