void main()
{
	vec2 fragCoord = glTexCoord[0].xy;
	
    vec2 cen = iChannelResolution[0].xy*0.5;
    
    vec2 p = texture2D( iChannel0, fragCoord.xy / iResolution.xy, -100.0 ).xy;

    vec3 col = p.x * (0.6+0.4*cos( 0.0025*p.y + vec3(0.0,0.5,1.0 )));

//#if 1
    vec4 m = texture2D( iChannel0, (vec2(0.0,0.0)+0.5)/ iChannelResolution[0].xy, -100.0 );
    col = mix( col, vec3(1.0,1.0,0.0), 1.0-smoothstep( 2.0, 4.0, length(fragCoord-m.xy) ) );

    float r = texture2D( iChannel0, (vec2(1.0,0.0)+0.5)/ iChannelResolution[0].xy, -100.0 ).x;
    col = mix( col, vec3(0.5,0.3,0.0), 1.0-smoothstep( 0.0, 2.0, abs(length(fragCoord-cen) - r) ) );
//#endif
    
	gl_FragColor = vec4( col,1.0);
}