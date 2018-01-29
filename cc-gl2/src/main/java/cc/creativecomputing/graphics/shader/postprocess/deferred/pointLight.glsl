#version 120

uniform sampler2D colors;
uniform sampler2D normals;
uniform sampler2D positions;

varying vec4 lightPosition;
varying vec4 cameraPosition;

uniform float specularPower;
uniform float specularIntensity;

uniform float radius;
uniform float intensity;
uniform vec3 lightColor;

uniform vec2 screenSize;

void main(){

	//get normal data from the normalMap
    vec4 normalData = texture2D(normals, gl_FragCoord.xy / screenSize);
    
    //tranform normal back into [-1,1] range
    vec3 normal = 2.0 * normalData.xyz - 1.0;
    
    //read depth
 	float depthVal = normalData.a;
 	
 	//compute screen-space position
 	vec4 position = texture2D(positions, gl_FragCoord.xy / screenSize);
 	
 	vec3 color = lightColor * intensity;//texture2D(colors, gl_TexCoord[0].xy);
 	
 	//compute diffuse light
 	vec3 lightVector = lightPosition.xyz -  position.xyz;
    
    //compute attenuation based on distance - linear attenuation
    float attenuation = clamp(1.0 - length(lightVector) / radius, 0.0, 1.0); 
    
    //normalize light vector
    lightVector = normalize(lightVector); 
    float nDl = clamp(dot(normal,lightVector), 0.0, 1.0);
    color *= nDl;
    //vec3 diffuseLight = NdL * color.rgb * lightColor * attenuation * lightIntensity ;
    
    //reflexion vector
    vec3 reflectionVector = normalize(reflect(lightVector, normal));
    
    //camera-to-surface vector
    vec3 directionToCamera = normalize(position.xyz - cameraPosition.xyz);
    
    //compute specular light
    float specularLight = specularIntensity * pow( clamp(dot(reflectionVector, directionToCamera),0.0,1.0), specularPower);
    
    color = (color + specularLight) * attenuation;
    
    gl_FragColor = vec4(color, 1.0);
    
    /*
    //get specular power, and get it into [0,255] range]
    float specularPower = normalData.a * 255;
    
    //get specular intensity from the colorMap
    float specularIntensity = texture2D(colorSampler, gl_TexCoord[0].xy).a;
 	
    //reflexion vector
    vec3 reflectionVector = normalize(reflect(lightVector, normal));
    
    //camera-to-surface vector
    vec3 directionToCamera = normalize(cameraPosition - position);
    
    //compute specular light
    float specularLight = specularIntensity * pow( saturate(dot(reflectionVector, directionToCamera)), specularPower);
    
    //output the two lights
    return float4(diffuseLight.rgb, specularLight) ;
    */
}