package cc.creativecomputing.gl4;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import com.jogamp.common.nio.Buffers;

public class GLBufferUtil {

	/*
	 * public static long sizeof(Object o) { if (o instanceof Array ) { Object[]
	 * objectArray = (Object[])o; Class<?> elementClass =
	 * objectArray.getClass().getComponentType(); if (elementClass ==
	 * float.class) { return Buffers.SIZEOF_FLOAT*objectArray.length; } else if
	 * (elementClass == int.class) { return
	 * Buffers.SIZEOF_INT*objectArray.length; } else if (elementClass ==
	 * double.class) { return Buffers.SIZEOF_DOUBLE*objectArray.length; } else
	 * if (elementClass == long.class) { return
	 * Buffers.SIZEOF_LONG*objectArray.length; } else if (elementClass ==
	 * long.class) { return Buffers.SIZEOF_SHORT*objectArray.length; } else if
	 * (elementClass == byte.class) { return
	 * Buffers.SIZEOF_BYTE*objectArray.length; } else if (elementClass ==
	 * char.class) { return Buffers.SIZEOF_CHAR*objectArray.length; } else {
	 * System.err.println("no suitable primitive type found for Object " + o); }
	 * }
	 * 
	 * return 0L; }
	 */
	
	private static FloatBuffer wrapFloatBuffer = FloatBuffer.allocate(16);
	
	/**
	 * In jogl you generally pass values using buffers, this little helper
	 * avoid allocating floatbuffers to just pass parameters. Be aware that this is just meant
	 * to be used with small numbers of parameters up to 16
	 * @param theValues
	 * @return
	 */
	public static FloatBuffer wrapParameters(float...theValues){
		wrapFloatBuffer.rewind();
		wrapFloatBuffer.limit(theValues.length);
		wrapFloatBuffer.put(theValues);
		wrapFloatBuffer.rewind();
		return wrapFloatBuffer;
	}
	
	public static FloatBuffer wrapBuffer(int theSize){
		wrapFloatBuffer.limit(theSize);
		wrapFloatBuffer.rewind();
		return wrapFloatBuffer;
	}
	
	public static FloatBuffer wrapBuffer(){
		return wrapFloatBuffer;
	}
	
	private static IntBuffer wrapIntBuffer = IntBuffer.allocate(16);
	
	/**
	 * In jogl you generally pass values using buffers, this little helper
	 * avoid allocating intbuffers to just pass parameters. Be aware that this is just meant
	 * to be used with small numbers of parameters up to 16
	 * @param theValues
	 * @return
	 */
	public static IntBuffer wrapParameters(int...theValues){
		wrapIntBuffer.rewind();
		wrapIntBuffer.limit(theValues.length);
		wrapIntBuffer.put(theValues);
		wrapIntBuffer.rewind();
		return wrapIntBuffer;
	}
	
	public static IntBuffer intBuffer(){
		wrapIntBuffer.rewind();
		return wrapIntBuffer;
	}

	public static long sizeof(float[] theArray) {
		return Buffers.SIZEOF_FLOAT * theArray.length;
	}

	public static long sizeof(double[] theArray) {
		return Buffers.SIZEOF_DOUBLE * theArray.length;
	}

	public static long sizeof(short[] theArray) {
		return Buffers.SIZEOF_SHORT * theArray.length;
	}

	public static long sizeof(int[] theArray) {
		return Buffers.SIZEOF_INT * theArray.length;
	}

	public static long sizeof(long[] theArray) {
		return Buffers.SIZEOF_LONG * theArray.length;
	}

	public static long sizeof(byte[] theArray) {
		return Buffers.SIZEOF_BYTE * theArray.length;
	}

	public static long sizeof(char[] theArray) {
		return Buffers.SIZEOF_CHAR * theArray.length;
	}

	public static long sizeof(FloatBuffer theArray) {
		return Buffers.SIZEOF_FLOAT * theArray.capacity();
	}

	public static long sizeof(DoubleBuffer theArray) {
		return Buffers.SIZEOF_DOUBLE * theArray.capacity();
	}

	public static long sizeof(ShortBuffer theArray) {
		return Buffers.SIZEOF_SHORT * theArray.capacity();
	}

	public static long sizeof(IntBuffer theArray) {
		return Buffers.SIZEOF_INT * theArray.capacity();
	}

	public static long sizeof(LongBuffer theArray) {
		return Buffers.SIZEOF_LONG * theArray.capacity();
	}

	public static long sizeof(ByteBuffer theArray) {
		return Buffers.SIZEOF_BYTE * theArray.capacity();
	}

	public static long sizeof(CharBuffer theArray) {
		return Buffers.SIZEOF_CHAR * theArray.capacity();
	}
	
	/**
	 * Returns the number of bytes of the buffers type size, for example 4 for a FloatBuffer
	 * @param theBuffer the buffer to check
	 * @return size of the buffers type
	 */
	public static int typeSize(Buffer theBuffer){
		if(theBuffer instanceof FloatBuffer){
			return 4;
		}
		if(theBuffer instanceof IntBuffer){
			return 4;
		}
		if(theBuffer instanceof LongBuffer){
			return 8;
		}
		if(theBuffer instanceof DoubleBuffer){
			return 8;
		}
		if(theBuffer instanceof ShortBuffer){
			return 2;
		}
		return 1;
	}

	
	public static FloatBuffer asBuffer(float[] theArray) {
		return Buffers.newDirectFloatBuffer(theArray);
	}

	public static ShortBuffer asBuffer(short[] theArray) {
		return Buffers.newDirectShortBuffer(theArray);
	}

	public static LongBuffer asBuffer(long[] theArray) {
		return Buffers.newDirectLongBuffer(theArray);
	}

	public static DoubleBuffer asBuffer(double[] theArray) {
		return Buffers.newDirectDoubleBuffer(theArray);
	}

	public static IntBuffer asBuffer(int[] theArray) {
		return Buffers.newDirectIntBuffer(theArray);
	}

	public static ByteBuffer asBuffer(byte[] theArray) {
		return Buffers.newDirectByteBuffer(theArray);
	}

	public static CharBuffer asBuffer(char[] theArray) {
		return Buffers.newDirectCharBuffer(theArray);
	}

	/*
	 * Introducing projection matrix helper functions
	 * 
	 * OpenGL ES 2 vertex projection transformations gets applied inside the
	 * vertex shader, all you have to do are to calculate and supply a
	 * projection matrix.
	 * 
	 * Its recomended to use the com/jogamp/opengl/util/PMVMatrix.java import
	 * com.jogamp.opengl.util.PMVMatrix; To simplify all your projection model
	 * view matrix creation needs.
	 * 
	 * These helpers here are based on PMVMatrix code and common linear algebra
	 * for matrix multiplication, translate and rotations.
	 */
	public static float[] multiplyVec(float[] a, float[] b) {
		float[] result = new float[Math.max(a.length, b.length)];
		for (int i = 0; i < result.length; i++){ 
			float element0 = (i < a.length) ? a[i] : 0; 
			float element1 = (i < b.length) ? b[i] : 0;
			result[i] = element0 * element1;
		}
		return result;
	}
	
	public static void glMultMatrixf(FloatBuffer a, FloatBuffer b, FloatBuffer d) {
		final int aP = a.position();
		final int bP = b.position();
		final int dP = d.position();
		for (int i = 0; i < 4; i++) {
			final float ai0 = a.get(aP + i + 0 * 4), ai1 = a
					.get(aP + i + 1 * 4), ai2 = a.get(aP + i + 2 * 4), ai3 = a
					.get(aP + i + 3 * 4);
			d.put(dP + i + 0 * 4,
					ai0 * b.get(bP + 0 + 0 * 4) + ai1 * b.get(bP + 1 + 0 * 4)
							+ ai2 * b.get(bP + 2 + 0 * 4) + ai3
							* b.get(bP + 3 + 0 * 4));
			d.put(dP + i + 1 * 4,
					ai0 * b.get(bP + 0 + 1 * 4) + ai1 * b.get(bP + 1 + 1 * 4)
							+ ai2 * b.get(bP + 2 + 1 * 4) + ai3
							* b.get(bP + 3 + 1 * 4));
			d.put(dP + i + 2 * 4,
					ai0 * b.get(bP + 0 + 2 * 4) + ai1 * b.get(bP + 1 + 2 * 4)
							+ ai2 * b.get(bP + 2 + 2 * 4) + ai3
							* b.get(bP + 3 + 2 * 4));
			d.put(dP + i + 3 * 4,
					ai0 * b.get(bP + 0 + 3 * 4) + ai1 * b.get(bP + 1 + 3 * 4)
							+ ai2 * b.get(bP + 2 + 3 * 4) + ai3
							* b.get(bP + 3 + 3 * 4));
		}
	}

	public static float[] multiply(float[] a, float[] b) {
		float[] tmp = new float[16];
		glMultMatrixf(FloatBuffer.wrap(a), FloatBuffer.wrap(b),
				FloatBuffer.wrap(tmp));
		return tmp;
	}

	public static float[] translate(float[] m, float x, float y, float z) {
//		float[] t = { 1.0f, 0.0f, 0.0f, x, 0.0f, 1.0f, 0.0f, y, 0.0f,
//				0.0f, 1.0f, z, 0.0f, 0.0f, 0.0f, 1.0f };
		float[] t = { 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
				0.0f, 1.0f, 0.0f, x, y, z, 1.0f };

		return multiply(m, t);
	}
	
	public static float[] scale(float[] m, float sx, float sy, float sz) {
		float[] t = { sx, 0.0f, 0.0f, 0.0f, 0.0f, sy, 0.0f, 0.0f, 0.0f,
				0.0f, sz, 0.0f, 0f, 0f, 0f, 1.0f };
		return multiply(m, t);
	}
	
	public static float[] scale(float[] m, float s) {
		return scale(m, s, s, s);
	}

	public static float[] rotate(float[] m, float a, float x, float y, float z) {
		float s, c;
		s = (float) Math.sin(a);
		c = (float) Math.cos(a);
		float[] r = { x * x * (1.0f - c) + c, y * x * (1.0f - c) + z * s,
				x * z * (1.0f - c) - y * s, 0.0f, x * y * (1.0f - c) - z * s,
				y * y * (1.0f - c) + c, y * z * (1.0f - c) + x * s, 0.0f,
				x * z * (1.0f - c) + y * s, y * z * (1.0f - c) - x * s,
				z * z * (1.0f - c) + c, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };
		return multiply(m, r);
	}
	
	public static float[] loadIdentity() {
		return new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
	}
	
	public static float[] frustumDeprecated(float l, float r, float b, float t, float n, float f) {
		return new float[]{
				2*n/(r-l), 0, (r+l)/(r-l), 0,
				0, 2*n/(t-b), (t+b)/(t-b), 0,
				0, 0, -(f+n)/(f-n), -2*f*n/(f-n),
				0, 0, -1, 0};
	}	
	
	public static float[] frustum(float left, float right, float bottom, float top, float n, float f) {
	    float[] result = loadIdentity();

	    if ((right == left) ||
	        (top == bottom) ||
	        (n == f) ||
	        (n < 0.0) ||
	        (f < 0.0))
	       return result;

	    result[0] = (2.0f * n) / (right - left);
	    result[5] = (2.0f * n) / (top - bottom);

	    result[2] = (right + left) / (right - left);
	    result[6] = (top + bottom) / (top - bottom);
	    result[10] = -(f + n) / (f - n);
	    result[14]= -1.0f;

	    result[11] = -(2.0f * f * n) / (f - n);
	    result[15] =  0.0f;

	    return result;
	}

	public static float[] perspective(float fovy, float aspect, float n, float f)
	{
	    float q = (float)(1.0f / Math.tan((0.5f * fovy) / 180f * Math.PI));
	    float A = q / aspect;
	    float B = (n + f) / (n - f);
	    float C = (2.0f * n * f) / (n - f);
	    
	    return new float[]{	A, 0.0f, 0.0f, 0.0f,
				  			0.0f, q, 0.0f, 0.0f,
				  			0.0f, 0.0f, B, -1.0f,
				  			0.0f, 0.0f, C, 0.0f};
	}

	public static FloatBuffer perspectiveAsBuffer(float fovy, float aspect, float n, float f) {
		return wrapParameters(perspective(fovy, aspect, n, f));
	}
	
	public static void printMatrix(float[] m) {
		System.out.println(m[0] + "|" + m[1] + "|" + m[2] + "|" + m[3] );
		System.out.println(m[4] + "|" + m[5] + "|" + m[6] + "|" + m[7] );
		System.out.println(m[8] + "|" + m[9] + "|" + m[10] + "|" + m[11] );
		System.out.println(m[12] + "|" + m[13] + "|" + m[14] + "|" + m[15] );
	}
	
	public static float sin(float theAngle) {
		return (float)Math.sin(theAngle);
	}

	public static float cos(float theAngle) {
		return (float)Math.cos(theAngle);
	}
}
