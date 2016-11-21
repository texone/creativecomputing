uniform sampler2DRect previous_cells;
uniform sampler2DRect current_cells;
uniform sampler2DRect wave_break_inner_edges;

uniform float waveInnerEdgesStrength;
uniform float damping;

uniform float startRange;
uniform float range;

uniform float normalHeightScale;

void main(){
	float s = gl_TexCoord[0].x;
    float t = gl_TexCoord[0].y;

    const float d = 1.0;

    // sum neighbours from current heightfield
    // the values are stored in the 2nd (green or y) channel
    float current_neighbour_sum =
        texture2DRect( current_cells, vec2(s - d, 	t - d)).y +
        texture2DRect( current_cells, vec2(s, 		t - d)).y +
        texture2DRect( current_cells, vec2(s + d, 	t - d)).y +

        texture2DRect( current_cells, vec2(s - d, 	t)).y +
        texture2DRect( current_cells, vec2(s + d, 	t)).y +

        texture2DRect( current_cells, vec2(s - d, 	t + d)).y +
        texture2DRect( current_cells, vec2(s, 		t + d)).y +
        texture2DRect( current_cells, vec2(s + d, 	t + d)).y;
        
         // wave breaks
	float height = texture2DRect( wave_break_inner_edges, gl_TexCoord[0].xy).r;
    
    float blend = 1.0 - clamp((height - (startRange + range)) / (1.0 - (startRange + range)), 0.0, 1.0);
        
 	vec3 myPos = vec3(s - 10.0, t - 10.0,texture2DRect( current_cells, vec2(s, t)).y * normalHeightScale * blend);
 	vec3 myA = vec3(s + 10.0,t,texture2DRect( current_cells, vec2(s + 1.0, t)).y * normalHeightScale) - myPos;
 	vec3 myB = vec3(s,t + 10.0,texture2DRect( current_cells, vec2(s, t + 1.0)).y * normalHeightScale) - myPos;
 	vec3 normal3 = (normalize(cross(myA,myB)) + 1.0) / 2.0;
 	gl_FragData[1] = vec4(normal3.x,normal3.y,normal3.z,1.0);
 	

    // fetch previous height at this position
    float previous_height = texture2DRect( previous_cells, gl_TexCoord[0].xy ).y;

    // fetch splash intensity from the red channel of the current buffer
    float splash = texture2DRect( current_cells, gl_TexCoord[0].xy).r;

	// wave breaks
	float waveBreakInnerEdges = texture2DRect( wave_break_inner_edges, gl_TexCoord[0].xy).r;
	waveBreakInnerEdges = clamp((waveBreakInnerEdges - startRange) / range, 0.0, 1.0);
    waveBreakInnerEdges =  (1.0 - waveBreakInnerEdges) * waveInnerEdgesStrength;
    float new_height = damping * ( current_neighbour_sum / 4.0 - previous_height )  + splash + waveBreakInnerEdges;

	// clamp	
	new_height = max (new_height, -0.5);
	new_height = min (new_height, 0.5);

    // output new height in the green channel
    gl_FragData[0] = vec4(0, new_height, 0, 1);
    //color = vec4(0, waveBreakInnerEdges, 0, 1);
}
