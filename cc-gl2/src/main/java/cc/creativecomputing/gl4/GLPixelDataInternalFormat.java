package cc.creativecomputing.gl4;

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_R3_G3_B2;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGB10;
import static org.lwjgl.opengl.GL11.GL_RGB10_A2;
import static org.lwjgl.opengl.GL11.GL_RGB12;
import static org.lwjgl.opengl.GL11.GL_RGB16;
import static org.lwjgl.opengl.GL11.GL_RGB4;
import static org.lwjgl.opengl.GL11.GL_RGB5;
import static org.lwjgl.opengl.GL11.GL_RGB5_A1;
import static org.lwjgl.opengl.GL11.GL_RGB8;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA12;
import static org.lwjgl.opengl.GL11.GL_RGBA16;
import static org.lwjgl.opengl.GL11.GL_RGBA2;
import static org.lwjgl.opengl.GL11.GL_RGBA4;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL13.GL_COMPRESSED_RGB;
import static org.lwjgl.opengl.GL13.GL_COMPRESSED_RGBA;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL21.GL_COMPRESSED_SRGB;
import static org.lwjgl.opengl.GL21.GL_COMPRESSED_SRGB_ALPHA;
import static org.lwjgl.opengl.GL21.GL_SRGB;
import static org.lwjgl.opengl.GL21.GL_SRGB8;
import static org.lwjgl.opengl.GL21.GL_SRGB8_ALPHA8;
import static org.lwjgl.opengl.GL21.GL_SRGB_ALPHA;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RED;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RED_RGTC1;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RG;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RG_RGTC2;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_SIGNED_RED_RGTC1;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_SIGNED_RG_RGTC2;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH32F_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL;
import static org.lwjgl.opengl.GL30.GL_R11F_G11F_B10F;
import static org.lwjgl.opengl.GL30.GL_R16;
import static org.lwjgl.opengl.GL30.GL_R16F;
import static org.lwjgl.opengl.GL30.GL_R16I;
import static org.lwjgl.opengl.GL30.GL_R16UI;
import static org.lwjgl.opengl.GL30.GL_R32F;
import static org.lwjgl.opengl.GL30.GL_R32I;
import static org.lwjgl.opengl.GL30.GL_R32UI;
import static org.lwjgl.opengl.GL30.GL_R8;
import static org.lwjgl.opengl.GL30.GL_R8I;
import static org.lwjgl.opengl.GL30.GL_R8UI;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.GL_RG16;
import static org.lwjgl.opengl.GL30.GL_RG16F;
import static org.lwjgl.opengl.GL30.GL_RG16I;
import static org.lwjgl.opengl.GL30.GL_RG16UI;
import static org.lwjgl.opengl.GL30.GL_RG32F;
import static org.lwjgl.opengl.GL30.GL_RG32I;
import static org.lwjgl.opengl.GL30.GL_RG32UI;
import static org.lwjgl.opengl.GL30.GL_RG8;
import static org.lwjgl.opengl.GL30.GL_RG8I;
import static org.lwjgl.opengl.GL30.GL_RG8UI;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL30.GL_RGB16I;
import static org.lwjgl.opengl.GL30.GL_RGB16UI;
import static org.lwjgl.opengl.GL30.GL_RGB32F;
import static org.lwjgl.opengl.GL30.GL_RGB32I;
import static org.lwjgl.opengl.GL30.GL_RGB32UI;
import static org.lwjgl.opengl.GL30.GL_RGB8I;
import static org.lwjgl.opengl.GL30.GL_RGB8UI;
import static org.lwjgl.opengl.GL30.GL_RGB9_E5;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA16I;
import static org.lwjgl.opengl.GL30.GL_RGBA16UI;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.GL_RGBA32I;
import static org.lwjgl.opengl.GL30.GL_RGBA32UI;
import static org.lwjgl.opengl.GL30.GL_RGBA8I;
import static org.lwjgl.opengl.GL30.GL_RGBA8UI;
import static org.lwjgl.opengl.GL30.GL_STENCIL_INDEX8;
import static org.lwjgl.opengl.GL31.GL_R16_SNORM;
import static org.lwjgl.opengl.GL31.GL_R8_SNORM;
import static org.lwjgl.opengl.GL31.GL_RG16_SNORM;
import static org.lwjgl.opengl.GL31.GL_RG8_SNORM;
import static org.lwjgl.opengl.GL31.GL_RGB16_SNORM;
import static org.lwjgl.opengl.GL31.GL_RGB8_SNORM;
import static org.lwjgl.opengl.GL31.GL_RGBA16_SNORM;
import static org.lwjgl.opengl.GL31.GL_RGBA8_SNORM;
import static org.lwjgl.opengl.GL33.GL_RGB10_A2UI;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_R11_EAC;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_RG11_EAC;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_RGB8_ETC2;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_RGBA8_ETC2_EAC;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_SIGNED_R11_EAC;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_SIGNED_RG11_EAC;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_SRGB8_ETC2;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2;

import cc.creativecomputing.image.CCPixelInternalFormat;

public enum GLPixelDataInternalFormat {
	DEPTH_COMPONENT(GL_DEPTH_COMPONENT),
	DEPTH_STENCIL(GL_DEPTH_STENCIL),
	STENCIL_INDEX8(GL_STENCIL_INDEX8),
	RED(GL_RED),
	RG(GL_RG),
	RGB(GL_RGB),
	RGBA(GL_RGBA),
	R8(GL_R8),
	R8_SNORM(GL_R8_SNORM),
	R16(GL_R16),
	R16_SNORM(GL_R16_SNORM),
	RG8(GL_RG8),
	RG8_SNORM(GL_RG8_SNORM),
	RG16(GL_RG16),
	RG16_SNORM(GL_RG16_SNORM),
	R3_G3_B2(GL_R3_G3_B2),
	RGB4(GL_RGB4),
	RGB5(GL_RGB5),
	RGB8(GL_RGB8),
	RGB8_SNORM(GL_RGB8_SNORM),
	RGB10(GL_RGB10),
	RGB12(GL_RGB12),
	RGB16(GL_RGB16),
	RGB16_SNORM(GL_RGB16_SNORM),
	RGBA2(GL_RGBA2),
	RGBA4(GL_RGBA4),
	RGB5_A1(GL_RGB5_A1),
	RGBA8(GL_RGBA8),
	RGBA8_SNORM(GL_RGBA8_SNORM),
	RGB10_A2(GL_RGB10_A2),
	RGB10_A2UI(GL_RGB10_A2UI),
	RGBA12(GL_RGBA12),
	RGBA16(GL_RGBA16),
	RGBA16_SNORM(GL_RGBA16_SNORM),
	SRGB8(GL_SRGB8),
	SRGB(GL_SRGB),
	SRGB8_ALPHA8(GL_SRGB8_ALPHA8),
	SRGB_ALPHA(GL_SRGB_ALPHA),
	R16F(GL_R16F),
	RG16F(GL_RG16F),
	RGB16F(GL_RGB16F),
	RGBA16F(GL_RGBA16F),
	R32F(GL_R32F),
	RG32F(GL_RG32F),
	RGB32F(GL_RGB32F),
	RGBA32F(GL_RGBA32F),
	R11F_G11F_B10F(GL_R11F_G11F_B10F),
	RGB9_E5(GL_RGB9_E5),
	R8I(GL_R8I),
	R8UI(GL_R8UI),
	R16I(GL_R16I),
	R16UI(GL_R16UI),
	R32I(GL_R32I),
	R32UI(GL_R32UI),
	RG8I(GL_RG8I),
	RG8UI(GL_RG8UI),
	RG16I(GL_RG16I),
	RG16UI(GL_RG16UI),
	RG32I(GL_RG32I),
	RG32UI(GL_RG32UI),
	RGB8I(GL_RGB8I),
	RGB8UI(GL_RGB8UI),
	RGB16I(GL_RGB16I),
	RGB16UI(GL_RGB16UI),
	RGB32I(GL_RGB32I),
	RGB32UI(GL_RGB32UI),
	RGBA8I(GL_RGBA8I),
	RGBA8UI(GL_RGBA8UI),
	RGBA16I(GL_RGBA16I),
	RGBA16UI(GL_RGBA16UI),
	RGBA32I(GL_RGBA32I),
	RGBA32UI(GL_RGBA32UI),
	DEPTH_COMPONENT16(GL_DEPTH_COMPONENT16),
	DEPTH_COMPONENT24(GL_DEPTH_COMPONENT24),
	DEPTH_COMPONENT32(GL_DEPTH_COMPONENT32),
	DEPTH_COMPONENT32F(GL_DEPTH_COMPONENT32F),
	DEPTH24_STENCIL8(GL_DEPTH24_STENCIL8),
	DEPTH32F_STENCIL8(GL_DEPTH32F_STENCIL8),
	
	COMPRESSED_RED(GL_COMPRESSED_RED),
	COMPRESSED_RG(GL_COMPRESSED_RG),
	COMPRESSED_RGB(GL_COMPRESSED_RGB),
	COMPRESSED_RGBA(GL_COMPRESSED_RGBA),
	COMPRESSED_SRGB(GL_COMPRESSED_SRGB),
	COMPRESSED_SRGB_ALPHA(GL_COMPRESSED_SRGB_ALPHA),
	
	COMPRESSED_RED_RGTC1(GL_COMPRESSED_RED_RGTC1),
	COMPRESSED_SIGNED_RED_RGTC1(GL_COMPRESSED_SIGNED_RED_RGTC1),
	COMPRESSED_RG_RGTC2(GL_COMPRESSED_RG_RGTC2),
	COMPRESSED_SIGNED_RG_RGTC2(GL_COMPRESSED_SIGNED_RG_RGTC2),
	
	COMPRESSED_RGB8_ETC2(GL_COMPRESSED_RGB8_ETC2),
	COMPRESSED_SRGB8_ETC2(GL_COMPRESSED_SRGB8_ETC2),
	COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2(GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2),
	COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2(GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2),
	COMPRESSED_RGBA8_ETC2_EAC(GL_COMPRESSED_RGBA8_ETC2_EAC),
	COMPRESSED_SRGB8_ALPHA8_ETC2_EAC(GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC),
	
	COMPRESSED_R11_EAC(GL_COMPRESSED_R11_EAC),
	COMPRESSED_SIGNED_R11_EAC(GL_COMPRESSED_SIGNED_R11_EAC),
	COMPRESSED_RG11_EAC(GL_COMPRESSED_RG11_EAC),
	COMPRESSED_SIGNED_RG11_EAC(GL_COMPRESSED_SIGNED_RG11_EAC);
	
	private int _myGLID;
	
	GLPixelDataInternalFormat(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLPixelDataInternalFormat fromCC(CCPixelInternalFormat theFormat){
		switch(theFormat){
		case LUMINANCE: return RED;
		case DEPTH_COMPONENT:return DEPTH_COMPONENT;
//		case DEPTH_STENCIL:return DEPTH_STENCIL;
		case STENCIL_INDEX:return STENCIL_INDEX8;
		case RED:return RED;
//		case RG:return RG;
		case RGB:return RGB;
		case RGBA:return RGBA;
//		case R8:return R8;
//		case R8_SNORM:return R8_SNORM;
//		case R16:return R16;
//		case R16_SNORM:return R16_SNORM;
//		case RG8:return RG8;
//		case RG8_SNORM:return RG8_SNORM;
//		case RG16:return RG16;
//		case RG16_SNORM:return RG16_SNORM;
		case R3_G3_B2:return R3_G3_B2;
		case RGB4:return RGB4;
		case RGB5:return RGB5;
		case RGB8:return RGB8;
//		case RGB8_SNORM:return RGB8_SNORM;
		case RGB10:return RGB10;
		case RGB12:return RGB12;
		case RGB16:return RGB16;
//		case RGB16_SNORM:return RGB16_SNORM;
		case RGBA2:return RGBA2;
		case RGBA4:return RGBA4;
		case RGB5_A1:return RGB5_A1;
		case RGBA8:return RGBA8;
//		case RGBA8_SNORM:return RGBA8_SNORM;
		case RGB10_A2:return RGB10_A2;
//		case RGB10_A2UI:return RGB10_A2UI;
		case RGBA12:return RGBA12;
		case RGBA16:return RGBA16;
//		case RGBA16_SNORM:return RGBA16_SNORM;
		case SRGB8:return SRGB8;
		case SRGB:return SRGB;
		case SRGB8_ALPHA8:return SRGB8_ALPHA8;
		case SRGB_ALPHA:return SRGB_ALPHA;
//		case R16F:return R16F;
//		case RG16F:return RG16F;
		case RGB16F:return RGB16F;
		case RGBA16F:return RGBA16F;
//		case R32F:return R32F;
//		case RG32F:return RG32F;
		case RGB32F:return RGB32F;
		case RGBA32F:return RGBA32F;
//		case R11F_G11F_B10F:return R11F_G11F_B10F;
//		case RGB9_E5:return RGB9_E5;
//		case R8I:return R8I;
//		case R8UI:return R8UI;
//		case R16I:return R16I;
//		case R16UI:return R16UI;
//		case R32I:return R32I;
//		case R32UI:return R32UI;
//		case RG8I:return RG8I;
//		case RG8UI:return RG8UI;
//		case RG16I:return RG16I;
//		case RG16UI:return RG16UI;
//		case RG32I:return RG32I;
//		case RG32UI:return RG32UI;
//		case RGB8I:return RGB8I;
//		case RGB8UI:return RGB8UI;
		case RGB16I:return RGB16I;
		case RGB16UI:return RGB16UI;
		case RGB32I:return RGB32I;
		case RGB32UI:return RGB32UI;
//		case RGBA8I:return RGBA8I;
//		case RGBA8UI:return RGBA8UI;
		case RGBA16I:return RGBA16I;
		case RGBA16UI:return RGBA16UI;
		case RGBA32I:return RGBA32I;
		case RGBA32UI:return RGBA32UI;
		case DEPTH_COMPONENT16:return DEPTH_COMPONENT16;
		case DEPTH_COMPONENT24:return DEPTH_COMPONENT24;
		case DEPTH_COMPONENT32:return DEPTH_COMPONENT32;
//		case DEPTH_COMPONENT32F:return DEPTH_COMPONENT32F;
//		case DEPTH24_STENCIL8:return DEPTH24_STENCIL8;
//		case DEPTH32F_STENCIL8:return DEPTH32F_STENCIL8;
//		case COMPRESSED_RED:return COMPRESSED_RED;
//		case COMPRESSED_RG:return COMPRESSED_RG;
		case COMPRESSED_RGB:return COMPRESSED_RGB;
		case COMPRESSED_RGBA:return COMPRESSED_RGBA;
//		case COMPRESSED_SRGB:return COMPRESSED_SRGB;
//		case COMPRESSED_SRGB_ALPHA:return COMPRESSED_SRGB_ALPHA;
//		case COMPRESSED_RED_RGTC1:return COMPRESSED_RED_RGTC1;
//		case COMPRESSED_SIGNED_RED_RGTC1:return COMPRESSED_SIGNED_RED_RGTC1;
//		case COMPRESSED_RG_RGTC2:return COMPRESSED_RG_RGTC2;
//		case COMPRESSED_SIGNED_RG_RGTC2:return COMPRESSED_SIGNED_RG_RGTC2;
//		case COMPRESSED_RGB8_ETC2:return COMPRESSED_RGB8_ETC2;
//		case COMPRESSED_SRGB8_ETC2:return COMPRESSED_SRGB8_ETC2;
//		case COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2:return COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2;
//		case COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2:return COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2;
//		case COMPRESSED_RGBA8_ETC2_EAC:return COMPRESSED_RGBA8_ETC2_EAC;
//		case COMPRESSED_SRGB8_ALPHA8_ETC2_EAC:return COMPRESSED_SRGB8_ALPHA8_ETC2_EAC;
//		case COMPRESSED_R11_EAC:return COMPRESSED_R11_EAC;
//		case COMPRESSED_SIGNED_R11_EAC:return COMPRESSED_SIGNED_R11_EAC;
//		case COMPRESSED_RG11_EAC:return COMPRESSED_RG11_EAC;
//		case COMPRESSED_SIGNED_RG11_EAC:return COMPRESSED_SIGNED_RG11_EAC;
		}
		return null;
	}
	
	public static GLPixelDataInternalFormat fromGLID(int theGLID){
		switch(theGLID){
		case GL_DEPTH_COMPONENT:return DEPTH_COMPONENT;
		case GL_DEPTH_STENCIL:return DEPTH_STENCIL;
		case GL_STENCIL_INDEX8:return STENCIL_INDEX8;
		case GL_RED:return RED;
		case GL_RG:return RG;
		case GL_RGB:return RGB;
		case GL_RGBA:return RGBA;
		case GL_R8:return R8;
		case GL_R8_SNORM:return R8_SNORM;
		case GL_R16:return R16;
		case GL_R16_SNORM:return R16_SNORM;
		case GL_RG8:return RG8;
		case GL_RG8_SNORM:return RG8_SNORM;
		case GL_RG16:return RG16;
		case GL_RG16_SNORM:return RG16_SNORM;
		case GL_R3_G3_B2:return R3_G3_B2;
		case GL_RGB4:return RGB4;
		case GL_RGB5:return RGB5;
		case GL_RGB8:return RGB8;
		case GL_RGB8_SNORM:return RGB8_SNORM;
		case GL_RGB10:return RGB10;
		case GL_RGB12:return RGB12;
		case GL_RGB16:return RGB16;
		case GL_RGB16_SNORM:return RGB16_SNORM;
		case GL_RGBA2:return RGBA2;
		case GL_RGBA4:return RGBA4;
		case GL_RGB5_A1:return RGB5_A1;
		case GL_RGBA8:return RGBA8;
		case GL_RGBA8_SNORM:return RGBA8_SNORM;
		case GL_RGB10_A2:return RGB10_A2;
		case GL_RGB10_A2UI:return RGB10_A2UI;
		case GL_RGBA12:return RGBA12;
		case GL_RGBA16:return RGBA16;
		case GL_RGBA16_SNORM:return RGBA16_SNORM;
		case GL_SRGB8:return SRGB8;
		case GL_SRGB8_ALPHA8:return SRGB8_ALPHA8;
		case GL_R16F:return R16F;
		case GL_RG16F:return RG16F;
		case GL_RGB16F:return RGB16F;
		case GL_RGBA16F:return RGBA16F;
		case GL_R32F:return R32F;
		case GL_RG32F:return RG32F;
		case GL_RGB32F:return RGB32F;
		case GL_RGBA32F:return RGBA32F;
		case GL_R11F_G11F_B10F:return R11F_G11F_B10F;
		case GL_RGB9_E5:return RGB9_E5;
		case GL_R8I:return R8I;
		case GL_R8UI:return R8UI;
		case GL_R16I:return R16I;
		case GL_R16UI:return R16UI;
		case GL_R32I:return R32I;
		case GL_R32UI:return R32UI;
		case GL_RG8I:return RG8I;
		case GL_RG8UI:return RG8UI;
		case GL_RG16I:return RG16I;
		case GL_RG16UI:return RG16UI;
		case GL_RG32I:return RG32I;
		case GL_RG32UI:return RG32UI;
		case GL_RGB8I:return RGB8I;
		case GL_RGB8UI:return RGB8UI;
		case GL_RGB16I:return RGB16I;
		case GL_RGB16UI:return RGB16UI;
		case GL_RGB32I:return RGB32I;
		case GL_RGB32UI:return RGB32UI;
		case GL_RGBA8I:return RGBA8I;
		case GL_RGBA8UI:return RGBA8UI;
		case GL_RGBA16I:return RGBA16I;
		case GL_RGBA16UI:return RGBA16UI;
		case GL_RGBA32I:return RGBA32I;
		case GL_RGBA32UI:return RGBA32UI;
		case GL_DEPTH_COMPONENT16:return DEPTH_COMPONENT16;
		case GL_DEPTH_COMPONENT24:return DEPTH_COMPONENT24;
		case GL_DEPTH_COMPONENT32:return DEPTH_COMPONENT32;
		case GL_DEPTH_COMPONENT32F:return DEPTH_COMPONENT32F;
		case GL_DEPTH24_STENCIL8:return DEPTH24_STENCIL8;
		case GL_DEPTH32F_STENCIL8:return DEPTH32F_STENCIL8;
		case GL_COMPRESSED_RED:return COMPRESSED_RED;
		case GL_COMPRESSED_RG:return COMPRESSED_RG;
		case GL_COMPRESSED_RGB:return COMPRESSED_RGB;
		case GL_COMPRESSED_RGBA:return COMPRESSED_RGBA;
		case GL_COMPRESSED_SRGB:return COMPRESSED_SRGB;
		case GL_COMPRESSED_SRGB_ALPHA:return COMPRESSED_SRGB_ALPHA;
		case GL_COMPRESSED_RED_RGTC1:return COMPRESSED_RED_RGTC1;
		case GL_COMPRESSED_SIGNED_RED_RGTC1:return COMPRESSED_SIGNED_RED_RGTC1;
		case GL_COMPRESSED_RG_RGTC2:return COMPRESSED_RG_RGTC2;
		case GL_COMPRESSED_SIGNED_RG_RGTC2:return COMPRESSED_SIGNED_RG_RGTC2;
		case GL_COMPRESSED_RGB8_ETC2:return COMPRESSED_RGB8_ETC2;
		case GL_COMPRESSED_SRGB8_ETC2:return COMPRESSED_SRGB8_ETC2;
		case GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2:return COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2;
		case GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2:return COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2;
		case GL_COMPRESSED_RGBA8_ETC2_EAC:return COMPRESSED_RGBA8_ETC2_EAC;
		case GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC:return COMPRESSED_SRGB8_ALPHA8_ETC2_EAC;
		case GL_COMPRESSED_R11_EAC:return COMPRESSED_R11_EAC;
		case GL_COMPRESSED_SIGNED_R11_EAC:return COMPRESSED_SIGNED_R11_EAC;
		case GL_COMPRESSED_RG11_EAC:return COMPRESSED_RG11_EAC;
		case GL_COMPRESSED_SIGNED_RG11_EAC:return COMPRESSED_SIGNED_RG11_EAC;
		}
		return null;
	}
}

