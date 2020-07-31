package cc.creativecomputing.image.format;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.FloatBuffer;
import java.nio.file.Path;

import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;

public class CCHDRFormat extends CCStreamBasedTextureFormat {

	private static int scanlineToFloatRGB(byte[] scanline, float[] dest, int initialOffset) {
		int off = initialOffset;
		for (int i = 0; i < scanline.length; i += 4) {
			CCRGBE.rgbe2float(dest, scanline, i, off);
			off += 3;
		}
		return off;
	}

	private static int scanlineToFloatGrayscale(byte[] scanline, float[] dest, int initialOffset) {
		int off = initialOffset;
		for (int i = 0; i < scanline.length; i += 4) {
			CCRGBE.re2float(dest, scanline, i, off);
			off++;
		}
		return off;
	}

	@Override
	public CCImage createImage(InputStream theStream, CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat, String theFileSuffix) throws CCImageException {
		try {
			DataInput di = new DataInputStream(theStream);
			CCRGBE.Header header = CCRGBE.readHeader(di);
	
			int width = header.getWidth();
			int height = header.getHeight();
	
			byte[] bbuff = new byte[width * 4];
			int rgbmult = 3;
			float[] outImage = new float[width * height * rgbmult];
			int floatOffset = 0;
	
			for (int h = 0; h < height; h++) {
				CCRGBE.readPixelsRawRLE(di, bbuff, 0, width, 1);
				// Convert RGBE into float
				
					floatOffset = scanlineToFloatRGB(bbuff, outImage, floatOffset);
				
			}
			
			CCImage myResult = new CCImage(width, height);
			myResult.buffer(FloatBuffer.wrap(outImage));
			return myResult;
		}catch(Exception e) {
			throw new CCImageException(e);
		}
	}

	private static final String IDENTIFIER = "#?RGBE";
	private static final String DEFAULT_FORMAT = "32-bit_rle_rgbe";

	/**
	 * Writes a default header into the file
	 */
	private static void writeHeader(PrintStream out, int width, int height) throws IOException {
		out.print(IDENTIFIER + "\n");
		out.print("FORMAT=" + DEFAULT_FORMAT + "\n");
		out.print("\n");
		out.print("-Y " + height + " +X " + width + "\n");
	}

	/**
	 * Writing RGB data non RLE into the file
	 */
	private static void writeDataRgb(OutputStream out, byte[] bdata) throws IOException {
		byte[] buff = new byte[1024];
		int bindex = 0;

		int pixelCount = bdata.length / 4;

		for (int i = 0; i < pixelCount; i++) {
			buff[bindex] = bdata[i];
			buff[bindex + 1] = bdata[pixelCount + i];
			buff[bindex + 2] = bdata[2 * pixelCount + i];
			buff[bindex + 3] = bdata[3 * pixelCount + i];
			bindex += 4;

			if (bindex + 4 >= buff.length || i == pixelCount - 1) {
				out.write(buff, 0, bindex);
				bindex = 0;
			}
		}
	}

	/**
	 * Writing grayscale data non RLE into the file
	 */
	private static void writeDataGrayscale(OutputStream out, byte[] bdata) throws IOException {
		byte[] buff = new byte[1024];
		int bindex = 0;

		int pixelCount = bdata.length / 2;

		for (int i = 0; i < pixelCount; i++) {
			buff[bindex] = bdata[i];
			buff[bindex + 1] = bdata[i];
			buff[bindex + 2] = bdata[i];
			buff[bindex + 3] = bdata[pixelCount + i];
			bindex += 4;

			if (bindex + 4 >= buff.length || i == pixelCount - 1) {
				out.write(buff, 0, bindex);
				bindex = 0;
			}
		}
	}

	/**
	 * This method is pretty much dark magic. I thereby present you the only
	 * existing java HDR RLE writer in the world (to date)
	 * 
	 * Writes into <out> the RGBE array <bdata> considering a scanline with width
	 * <scanlineWidth>. If <rgb> is set to false, <bdata> is considered to only
	 * store the RE components of each RGBE data
	 */
	private static void writeDataRLE(OutputStream out, byte[] bdata, int scanlineWidth, boolean rgb)
			throws IOException {

		byte[] scanlineHeader = { 2, 2, (byte) ((scanlineWidth >> 8) & 0xFF), (byte) (scanlineWidth & 0xFF) }; // Scanline
																												// header
																												// with
																												// the
																												// scanline
																												// width
																												// in
																												// args

		byte[] scanlineBuf = new byte[scanlineWidth * 2];
		int scanlineBufIndex = 0;
		int curr = 0;
		int rgbeCont = 0; // 0 - R iteration , 1 - G iteration , 2 - B iteration , 3 - E iteration

		// For each scanline (R and E of the same scanline count as different scanlines)
		for (int scanlineEnd = scanlineWidth; scanlineEnd <= bdata.length; scanlineEnd += scanlineWidth) {
			int auxIndex = curr;
			boolean repeating = bdata[curr + 1] == bdata[curr];

			while (curr < scanlineEnd - 1) {
				curr++;
				boolean equal = bdata[curr] == bdata[auxIndex];
				int fromAux = curr - auxIndex;
				if (equal != repeating || fromAux >= 127) {
					if (repeating) {
						scanlineBuf[scanlineBufIndex++] = (byte) (128 + fromAux);
						scanlineBuf[scanlineBufIndex++] = bdata[auxIndex];
					} else {
						scanlineBuf[scanlineBufIndex++] = (byte) (fromAux);
						for (int i = 0; i < fromAux; i++)
							scanlineBuf[scanlineBufIndex++] = bdata[auxIndex + i];
					}
					auxIndex = curr;
					repeating = curr != scanlineEnd - 1 && bdata[curr + 1] == bdata[curr];
				}
			}
			curr++;
			int fromAux = curr - auxIndex;
			if (repeating) {
				scanlineBuf[scanlineBufIndex++] = (byte) (128 + fromAux);
				scanlineBuf[scanlineBufIndex++] = bdata[auxIndex];
			} else {
				scanlineBuf[scanlineBufIndex++] = (byte) (fromAux);
				for (int i = 0; i < fromAux; i++)
					scanlineBuf[scanlineBufIndex++] = bdata[auxIndex + i];
			}

			if (rgbeCont == 0)
				out.write(scanlineHeader);

			out.write(scanlineBuf, 0, scanlineBufIndex);

			if (!rgb && rgbeCont == 0) {
				out.write(scanlineBuf, 0, scanlineBufIndex);
				out.write(scanlineBuf, 0, scanlineBufIndex);
			}

			rgbeCont += (rgb ? 1 : 3);
			if (rgbeCont > 3)
				rgbeCont = 0;
			scanlineBufIndex = 0;
		}
	}

	/**
	 * Writes an RGBE array <bdata> into a file <file> creating an HDR image with
	 * width <width> and height <height>. If <rgb> is not set it is considered
	 * <bdata> is an RE array instead of a RGBE one
	 */
	public static void writeHDR(byte[] bdata, int width, int height, boolean rgb, File file) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream(file));
		writeHeader(out, width, height);
		if (width >= 8 && width <= 0x7fff)
			writeDataRLE(out, bdata, width, rgb);
		else if (rgb)
			writeDataRgb(out, bdata);
		else
			writeDataGrayscale(out, bdata);
		out.flush();
		out.close();
	}

	@Override
	public boolean write(Path theFile, CCImage theData, double theQuality) throws CCImageException {
	/*
		byte[] bdata = new byte[theData.width() * theData.height() * (rgb ? 4 : 2)];

		// Get RGBE from data[] values and parse it into bdata. Only RE components are
		// needed as R=G=B in non-rgb images
	
		else
			for (int s = 0; s < img.getInternalData().length; s += theData.width() * 3)
				for (int i = 0; i < theData.width(); i++)
					CCRGBE.float2rgbe(bdata, img.getInternalData()[i * 3 + s], img.getInternalData()[i * 3 + s + 1],
							img.getInternalData()[i * 3 + s + 2], i + (s / 3) * 4, theData.width());

		writeHDR(bdata, theData.width(), theData.height(), rgb, theFile.toFile());
		*/
		return false;
	}

}
