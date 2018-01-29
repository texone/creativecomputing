uniform vec3 lightPosition;
uniform vec3 cameraPos;

varying vec3 toLightInTangentSpace;
varying vec3 toCameraInTangentSpace;

void main(){


	vec3 i_position = gl_Vertex.xyz;
	vec3 i_normal = vec3(0.,0.,1.);
	vec4 i_tangent = gl_MultiTexCoord1;

	// transform to world space
  	vec4 worldPosition	= vec4(i_position, 1); 
  	vec3 worldNormal	= normalize(gl_NormalMatrix * i_normal);
 	vec3 worldTangent	= normalize(gl_NormalMatrix * i_tangent.xyz);


	// calculate vectors to the camera and to the light
	vec3 worldDirectionToLight	= normalize(lightPosition - worldPosition.xyz);
	vec3 worldDirectionToCamera	= normalize(vec3(100.,2.,1.) - worldPosition.xyz);

	// calculate bitangent from normal and tangent
	vec3 worldBitangnent	= cross(worldNormal, worldTangent) * i_tangent.w;

	// transform direction to the light to tangent space
	toLightInTangentSpace = vec3(
         dot(worldDirectionToLight, worldTangent),
         dot(worldDirectionToLight, worldBitangnent),
         dot(worldDirectionToLight, worldNormal)
      );

	// transform direction to the camera to tangent space
	toCameraInTangentSpace= vec3(
         dot(worldDirectionToCamera, worldTangent),
         dot(worldDirectionToCamera, worldBitangnent),
         dot(worldDirectionToCamera, worldNormal)
      );

	// pass texture coordinates to fragment shader
	gl_TexCoord[0] = gl_MultiTexCoord0;

   // calculate screen space position of the vertex
	gl_Position = gl_ModelViewProjectionMatrix * worldPosition;
}