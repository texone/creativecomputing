package cc.creativecomputing.image.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.Rasters;
import mil.nga.tiff.TIFFImage;
import mil.nga.tiff.TiffReader;
import mil.nga.tiff.TiffWriter;
import mil.nga.tiff.util.TiffConstants;

public class CCTiffFormat extends CCStreamBasedTextureFormat{

	@Override
	public CCImage createImage(InputStream theStream, CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat, String theFileSuffix) throws CCImageException {
		/*try {
			TIFFImage tiffImage = TiffReader.readTiff(theStream);
			List<FileDirectory> directories = tiffImage.getFileDirectories();
			FileDirectory directory = directories.get(0);
			Rasters rasters = directory.readRasters();
		} catch (IOException e) {
			throw new CCImageException(e);
		}
		
		// TODO Auto-generated method stub*/
		return null;
	}

	@Override
	public boolean write(Path theFile, CCImage theData, double theQuality) throws CCImageException {
		/*
		int width = 256;
		int height = 256;
		int samplesPerPixel = 1;
		int bitsPerSample = 32;

		Rasters rasters = new Rasters(theData.width(), theData.height(), samplesPerPixel, bitsPerSample);

		int rowsPerStrip = rasters.calculateRowsPerStrip(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);

		FileDirectory directory = new FileDirectory();
		directory.setImageWidth(width);
		directory.setImageHeight(height);
		directory.setBitsPerSample(bitsPerSample);
		directory.setCompression(TiffConstants.COMPRESSION_NO);
		directory.setPhotometricInterpretation(TiffConstants.PHOTOMETRIC_INTERPRETATION_BLACK_IS_ZERO);
		directory.setSamplesPerPixel(samplesPerPixel);
		directory.setRowsPerStrip(rowsPerStrip);
		directory.setPlanarConfiguration(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);
		directory.setSampleFormat(TiffConstants.SAMPLE_FORMAT_FLOAT);
		directory.setWriteRasters(rasters);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				float pixelValue = 0;//...
				rasters.setFirstPixelSample(x, y, pixelValue);
			}
		}

		TIFFImage tiffImage = new TIFFImage();
		tiffImage.add(directory);
		byte[] bytes = TiffWriter.writeTiffToBytes(tiffImage);
		// or
		File file = ...
		TiffWriter.writeTiff(file, tiffImage);
		// TODO Auto-generated method stub*/
		return false;
	}

}
