package cc.creativecomputing.video;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.io.CCBufferUtil;

public class CCFFMPEGRecorder {

	public static enum CCVideoWriteProfile {
		MP4_NORMAL, 
		MP4_LOSSLESS,

		X265_NORMAL, 
		X265_HLG, 
		X265_LOSSLESS
	}

	private File ffmpegOutput = new File("ffmpegOutput.txt");

	private int frameRate = 25;
	private int width = -1;
	private int height = -1;

	private String filename = "rndr.mp4";

	private ByteBuffer frameBuffer;
	private WritableByteChannel channel;
	private Process ffmpeg = null;
	private OutputStream movieStream = null;

	private CCVideoWriteProfile profile;

	private String inputFormat = "rgba";

	public CCFFMPEGRecorder() {
    	LocalDateTime dt = LocalDateTime.now();
    	profile(profile);
    	output(
    		"${program.javaClass.simpleName}-" +
    		"${dt.year.z(4)}-" +
    		"${dt.month.value.z()}-" +
    		"${dt.dayOfMonth.z()}-" +
    		"${dt.hour.z()}." +
    		"${dt.minute.z()}." +
    		"${dt.second.z()}.mp4");
    	//size(program.width, program.height);
    	//frameRate(frameRate);
    	start();
    	    
    }

	public CCFFMPEGRecorder profile(CCVideoWriteProfile theProfile) {
		profile = theProfile;
		return this;
	}

	@SuppressWarnings("incomplete-switch")
	private String[] arguments() {

		List<String> arguments = new ArrayList<>();

		if (System.getProperty("os.name").contains("Windows")) {
			arguments.add("ffmpeg.exe");
		} else {
			arguments.add("ffmpeg");
		}

		arguments.add("-y");
		arguments.add("-f");
		arguments.add("rawvideo");
		arguments.add("-s");
		arguments.add(String.format("%dx%d", width, height));
		arguments.add("-r");
		arguments.add(frameRate + "");
		arguments.add("-i");
		arguments.add("-");
		arguments.add("-vf");
		arguments.add("vflip");

		arguments.add("-pix_fmt");

		switch (profile) {
		case MP4_NORMAL:
		case MP4_LOSSLESS:
			arguments.add("yuv420p");
			break;
		case X265_HLG:
			arguments.add("yuv420p10le");
			break;
		case X265_NORMAL:
			arguments.add("yuv420");
			break;
		case X265_LOSSLESS:
			arguments.add("yuv420p10");
			break;
		default:
			arguments.add(inputFormat);
			break;
		}

		arguments.add("-an");

		arguments.add("-vcodec");

		switch (profile) {
		case MP4_NORMAL:
		case MP4_LOSSLESS:
			arguments.add("libx264");
			break;
		case X265_HLG:
		case X265_NORMAL:
		case X265_LOSSLESS:
			arguments.add("libx265");
			break;
		default:
			arguments.add("rawvideo");
			break;
		}

		arguments.add("-crf");

		switch (profile) {
		case MP4_NORMAL:
		case MP4_LOSSLESS:
			arguments.add("23");
			break;
		case X265_HLG:
		case X265_NORMAL:
		case X265_LOSSLESS:
			arguments.add("28");
			break;
		}

		switch (profile) {
		case MP4_LOSSLESS:
		case X265_LOSSLESS:
			arguments.add("-preset");
			arguments.add("ultrafast");
			break;
		}

		switch (profile) {
		case X265_HLG:
			arguments.add("-color_primaries");
			arguments.add("bt2020");
			arguments.add("-colorspace");
			arguments.add("bt2020_ncl");
			arguments.add("-color_trc");
			arguments.add("arib-std-b67");
			break;

		}

		arguments.add(filename);

		String[] myResult = new String[arguments.size()];
		arguments.toArray(myResult);
		return myResult;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public CCFFMPEGRecorder size(int theWidth, int theHeight) {
		if (theWidth % 2 != 0 || theHeight % 2 != 0) {
			throw new IllegalArgumentException("width ($width) and height ($height) should be divisible by 2");
		}
		width = theWidth;
		height = theHeight;
		return this;
	}

	/**
	 * Set the output file, should be set before calling start()
	 *
	 * @param theFileName the filename of the output file
	 */
	public CCFFMPEGRecorder output(String theFileName) {
		filename = theFileName;
		return this;
	}

	/**
	 * Sets the framerate of the output video
	 *
	 * @param theFrameRate the frame rate in frames per second
	 * @return this
	 */
	public CCFFMPEGRecorder frameRate(int theFrameRate) {
		frameRate = theFrameRate;
		return this;
	}

	/**
	 * Start writing to the video file
	 */
	public CCFFMPEGRecorder start() {

		if (filename == null) {
			throw new RuntimeException("output not set");
		}

		if (width <= 0) {
			throw new RuntimeException("invalid width or width not set");
		}
		if (height <= 0) {
			throw new RuntimeException("invalid height or height not set");
		}

		switch (inputFormat) {
		case "rgba":
			frameBuffer = CCBufferUtil.newByteBuffer(width * height * 4);
			break;
		case "rgba64le":
			frameBuffer = CCBufferUtil.newByteBuffer(width * height * 8);
			break;
		default:
			throw new RuntimeException("unsupported format $inputFormat");
		}

		ProcessBuilder pb = new ProcessBuilder(arguments());
		pb.redirectErrorStream(true);
		pb.redirectOutput(ffmpegOutput);

		try {
			ffmpeg = pb.start();
		} catch (IOException e) {
			throw new RuntimeException("failed to launch ffmpeg", e);
		}

		movieStream = ffmpeg.getOutputStream();
		channel = Channels.newChannel(movieStream);
		return this;
	}

	/**
	 * Feed a frame to the video encoder
	 *
	 * @param frame a ColorBuffer (RGBA, 8bit) holding the image data to be written
	 *              to the video. The ColorBuffer should have the same resolution as
	 *              the VideoWriter.
	 */
	public CCFFMPEGRecorder frame(ByteBuffer frame, int theWidth, int theHeight) {

		if (theWidth != width || theHeight != height) {
			throw new RuntimeException("frame size mismatch");
		}

		frameBuffer.rewind();
		frameBuffer.order(ByteOrder.nativeOrder());
		frameBuffer.put(frame);
		frameBuffer.rewind();

		try {
			channel.write(frameBuffer);
			movieStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to write frame", e);
		}

		return this;
	}

	/**
	 * Stop writing to the video file. This closes the video, after calling stop()
	 * it is no longer possible to provide new frames.
	 */
	public CCFFMPEGRecorder stop() {
		try {
			movieStream.close();
			try {
//                logger.info("waiting for ffmpeg to finish")
				ffmpeg.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			throw new RuntimeException("failed to close the movie stream");
		}

		return this;
	}

}