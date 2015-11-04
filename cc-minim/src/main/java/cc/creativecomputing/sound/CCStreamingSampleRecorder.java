/*
 *  Copyright (c) 2007 - 2008 by Damien Di Fede <ddf@compartmental.net>
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package cc.creativecomputing.sound;

import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.AudioSystemShadow;
import org.tritonus.share.sampled.AudioUtils;
import org.tritonus.share.sampled.file.AudioOutputStream;

/**
 * JSStreamingSampleRecorder using the Tritonus AudioOutputStream class to
 * stream audio directly to disk. The limitation of this approach is that the
 * file format and the file name must be known before recording begins because
 * the file must be created. The advantage is that you do not incur the overhead
 * of an in-memory buffer and saving will not cause your sketch to hang because
 * all the audio is already on disk and all that must be done is closing the
 * file. Unlike JSBufferedSampleRecorder, specifying the file format upon saving
 * will do nothing and you cannot easily save your recorded audio to multiple
 * formats. There are also fewer formats available to save in, limiting you to
 * AIFF, AU, and WAV.
 * 
 * @author Damien Di Fede
 * 
 */
final class CCStreamingSampleRecorder implements CCSampleRecorder {
	// output stream representing the file being written to
	private AudioOutputStream aos;
	// float sample buffer used for converting float samples to bytes
	private CCFloatSampleBuffer fsb;
	private Path _myPath;
	private AudioFileFormat.Type type;
	private AudioFormat format;
	private boolean recording;

	/**
	 * 
	 * @param thePath
	 * @param fileType
	 * @param fileFormat
	 */
	CCStreamingSampleRecorder(
		Path thePath, 
		AudioFileFormat.Type fileType, 
		AudioFormat fileFormat, 
		int bufferSize
	) {
		_myPath = thePath;
		type = fileType;
		format = fileFormat;
		try {
			aos = AudioSystemShadow.getAudioOutputStream(type, format, AudioSystem.NOT_SPECIFIED, _myPath.toFile());
		} catch (IOException e) {
			throw new CCSoundException("Error obtaining new output stream", e);
		} catch (IllegalArgumentException badarg) {
			throw new CCSoundException("Error obtaining new output stream for " + thePath + " with type " + type.toString()
					+ " format " + format.toString() + " and bufferSize " + bufferSize, badarg);
		}
		fsb = new CCFloatSampleBuffer(format.getChannels(), bufferSize, format.getSampleRate());
		recording = false;
	}

	@Override
	public Path filePath() {
		return _myPath;
	}

	@Override
	public void beginRecord() {
		recording = true;
	}

	@Override
	public void endRecord() {
		recording = false;
	}

	@Override
	public boolean isRecording() {
		return recording;
	}

	/**
	 * Finishes the recording process by closing the file.
	 */
	public CCAudioRecordingStream save() {
		try {
			aos.close();
		} catch (IOException e) {
			throw new CCSoundException("AudioRecorder.save: An error occurred when trying to save the file:\n", e);
		}
		Path filePath = filePath();
		AudioInputStream ais = CCSoundIO.getAudioInputStream(filePath);
		SourceDataLine sdl = CCSoundIO.getSourceDataLine(ais.getFormat(), 1024);
		// this is fine because the recording will always be
		// in a raw format (WAV, AU, etc).
		long length = AudioUtils.frames2Millis(ais.getFrameLength(), format);
		CCAudioMetaData meta = new CCAudioMetaData(filePath, length, ais.getFrameLength());
		CCAudioRecordingStream recording = new CCAudioRecordingStream(meta, ais, sdl, 1024, meta.length());
		return recording;
	}

	public void samples(float[] samp) {
		if (recording) {
			System.arraycopy(samp, 0, fsb.getChannel(0), 0, samp.length);
			byte[] raw = fsb.convertToByteArray(format);
			try {
				aos.write(raw, 0, raw.length);
			} catch (IOException e) {
				throw new CCSoundException("AudioRecorder: An error occurred while trying to write to the file", e);
			}
		}
	}

	public void samples(float[] sampL, float[] sampR) {
		if (recording) {
			System.arraycopy(sampL, 0, fsb.getChannel(0), 0, sampL.length);
			System.arraycopy(sampR, 0, fsb.getChannel(1), 0, sampR.length);
			byte[] raw = fsb.convertToByteArray(format);
			try {
				aos.write(raw, 0, raw.length);
			} catch (IOException e) {
				throw new CCSoundException("AudioRecorder: An error occurred while trying to write to the file", e);
			}
		}
	}
}
