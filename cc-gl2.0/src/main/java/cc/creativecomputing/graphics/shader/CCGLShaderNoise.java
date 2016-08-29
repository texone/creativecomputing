package cc.creativecomputing.graphics.shader;

import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCGLShaderNoise {

	public static CCImage randomData;
	
	static{
		CCColor[][] myBaseColorMap = new CCColor[256][256];
		
		for (int y=0;y<256;y++){
			for (int x=0;x<256;x++){
				myBaseColorMap[x][y] = new CCColor(CCMath.random(),0,0,0);
			}
		}

		for (int y=0;y<256;y++){
			for (int x=0;x<256;x++){
				int x2 = (x + 37) % 256;
				int y2 = (y + 17) % 256;
				myBaseColorMap[x2][y2].g = myBaseColorMap[x][y].r;
			}
		}
		
		randomData = new CCImage(256,256);
		for(int x = 0; x < randomData.width(); x++){
			for(int y = 0; y < randomData.height(); y++){
				randomData.setPixel(x, y, myBaseColorMap[x][y]);
			}
		}
	}
	
	public static String textureUniform = "randomTexture";
	
	public static String source = 
			"uniform sampler2D randomTexture;\n" + 
			"\n" + 
			"float noise( in vec3 x ){\n" + 
			"    vec3 p = floor(x);\n" + 
			"    vec3 f = fract(x);\n" + 
			"	f = f * f * (3.0 - 2.0 * f);\n" + 
			"	\n" + 
			"	vec2 uv = (p.xy + vec2(37.0, 17.0) * p.z) + f.xy;\n" + 
			"	vec2 rg = texture2D( randomTexture, (uv+ 0.5) / 256.0 ).yx;//, -100.0\n" + 
			"	return mix( rg.x, rg.y, f.z );\n" + 
			"}\n" + 
			"\n" + 
			"float octavedNoise(in vec3 p, int octaves, float gain, float lacunarity){\n" + 
			"	float result = 0.0;\n" + 
			"	float myFallOff = gain;\n" + 
			"	float myAmp = 0.0;\n" + 
			"	\n" + 
			"	vec3 q = p;\n" + 
			"	for(int i = 0; i < octaves; i++){\n" + 
			"		myAmp += myFallOff;\n" + 
			"		result += myFallOff * noise( q ); \n" + 
			"		q = q * lacunarity;\n" + 
			"		myFallOff *= gain;\n" + 
			"	}\n" + 
			"	\n" + 
			"	return result / myAmp;\n" + 
			"}" +
			"\n"; 
	
	
	
}
