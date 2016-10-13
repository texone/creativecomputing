#version 120 

// Created by inigo quilez - 2015
// License Creative Commons Attribution-NonCommercial-ShareAlike 3.0

float hash1( float n ){
    return fract(sin(n)*138.5453123);
}

float isInside( vec2 p, vec2 c ) {
	vec2 d = abs(p-c-0.5) - 0.5; 
	return -max(d.x,d.y);
}

uniform vec2 iChannelResolution;
uniform sampler2D iChannel0;

uniform int iFrame;
uniform float iGlobalTime;

void main(){
	vec2 fragCoord = gl_TexCoord[0].st;
    vec2 cen = 0.5 * iChannelResolution.xy;
    
    //-----------------------------------------------    
    // load
    //-----------------------------------------------    
    vec4  m = texture2D( iChannel0, (vec2(0.0,0.0)+0.5)/ iChannelResolution.xy, -100.0 );
    float r = texture2D( iChannel0, (vec2(1.0,0.0)+0.5)/ iChannelResolution.xy, -100.0 ).x;
    vec2  f = texture2D( iChannel0, fragCoord / iChannelResolution.xy, -100.0 ).xy;

    //-----------------------------------------------    
    // reset
    //-----------------------------------------------    
    if( iFrame <= 1){
        r = 0.005*iChannelResolution.x;
        if( length(fragCoord-cen) < r ) f = vec2(1.0,0.0);
        m = vec4( cen.x+r*1.2, cen.y, -1.0, 0.0 );
    }
    
    for( int k=0; k<512; k++ ){
        // move particle
        vec2 om = m.xy;
        m.xy += m.zw*1.0;

        bool touch = false;

        // if touch, stick
        float n = texture2D( iChannel0, (m.xy+0.5) / iChannelResolution.xy, -100.0 ).x;
        if( n>0.5 )
        {
            touch = true;
            r = max( r, length(om-cen) );
            if( isInside(fragCoord,om.xy) > 0.0 )
            {
                f.x = 1.0;
                f.y = 0.1 * float(150*iFrame + k)/150.0;
            }

        }

        // if outside or touch, respawn
        float d = length( m.xy - cen );
        if( touch || d > r*1.1 )
        {
            m.x = -1.0 + 2.0*hash1( iGlobalTime*1.0 + float(k));
            m.y = -1.0 + 2.0*hash1( iGlobalTime*1.1 + 43.17 + float(k)*3.13);
            m.xy = cen + normalize(m.xy)*r*1.1;

            m.z = -1.0 + 2.0*hash1( iGlobalTime*1.2 + 13.13 + float(k)*23.71);
            m.w = -1.0 + 2.0*hash1( iGlobalTime*1.3 + 37.73 + float(k)*17.9 );
            m.zw = cen + normalize(m.zw)*r*0.9;
            m.zw = normalize( m.zw - m.xy);
        }
    }

    //-----------------------------------------------    
    // store
    //-----------------------------------------------    
    
    if( fragCoord.x<1.0 && fragCoord.y < 1.0 )
        gl_FragColor = m;
    else if( fragCoord.x<2.0 && fragCoord.y < 1.0 )
        gl_FragColor = vec4(r,0.0,0.0,0.0);
    else
        gl_FragColor = vec4(f,0.0,0.0);
}