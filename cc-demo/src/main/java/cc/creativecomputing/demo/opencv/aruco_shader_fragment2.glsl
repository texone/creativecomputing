uniform sampler2D texture;

@CCProperty(name = "hue min", min = 0, max = 1)
uniform float hueMin;
@CCProperty(name = "hue max", min = 0, max = 1)
uniform float hueMax;

@CCProperty(name = "sat min", min = 0, max = 1)
uniform float satMin;
@CCProperty(name = "sat max", min = 0, max = 1)
uniform float satMax;

@CCProperty(name = "bri min", min = 0, max = 1)
uniform float briMin;
@CCProperty(name = "bri max", min = 0, max = 1)
uniform float briMax;

@CCProperty(name = "x min", min = 0, max = 1)
uniform float xMin;
@CCProperty(name = "x max", min = 0, max = 1)
uniform float xMax;

@CCProperty(name = "y min", min = 0, max = 1)
uniform float yMin;
@CCProperty(name = "y max", min = 0, max = 1)
uniform float yMax;

vec3 rgb2hsb(vec3 c){
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

uniform vec2 resolution;

void main(){
	vec2 uv = gl_FragCoord / resolution;
	uv = vec2(uv.x, 1- uv.y);
	vec4 col = texture2D(texture, uv);
	vec4 result;
	vec3 h=rgb2hsb(col.rgb);
	float hue = mod((h.x + 0.5), 1);
	float sat = h.y;
	float bri = 0.2126 * col.r + 0.7152 * col.g + 0.0722 * col.b;
	result.rgb = mod((h.xxx + 0.5), 1);
	float dHue = hue > hueMin && hue < hueMax;
	float dSat = sat > satMin && sat < satMax;
	float dBri = bri > briMin && bri < briMax;
	float dArea = uv.x > xMin && uv.x < xMax;
	dArea *= uv.y > yMin && uv.y < yMax;
	result.rgb = dArea * dHue * dBri * dSat;
	result.a = 1;
	gl_FragColor = result;
}