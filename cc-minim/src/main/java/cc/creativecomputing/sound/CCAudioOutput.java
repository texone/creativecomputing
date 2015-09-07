/*
 *  Copyright (c) 2007 by Damien Di Fede <ddf@compartmental.net>
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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.SourceDataLine;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.sound.CCAudioOutput;

public final class CCAudioOutput extends Thread implements CCAudioResource{
	private CCAudioListener listener;
	private CCAudioStream stream;
	private CCAudioSignal signal;

	private SourceDataLine line;
	private AudioFormat format;
	private CCFloatSampleBuffer buffer;
	private CCMultiChannelBuffer mcBuffer;
	private int bufferSize;
	private boolean finished;
	private byte[] outBytes;

	CCAudioOutput(SourceDataLine sdl, int bufferSize) {
		super();
		this.bufferSize = bufferSize;
		format = sdl.getFormat();

		buffer = new CCFloatSampleBuffer(format.getChannels(), bufferSize, format.getSampleRate());
		mcBuffer = new CCMultiChannelBuffer(bufferSize, format.getChannels());
		outBytes = new byte[buffer.getByteArrayBufferSize(format)];
		finished = false;
		line = sdl;
	}

	public void run() {
		line.start();
		while (!finished) {
			buffer.makeSilence();
			

			if (signal != null) {
				readSignal();
			} else if (stream != null) {
				readStream();
			}
			if (line.getFormat().getChannels() == CCSoundIO.MONO) {
				listener.samples(buffer.getChannel(0));
			} else {
				listener.samples(buffer.getChannel(0), buffer.getChannel(1));
			}

			CCLog.info("output");
			
			for(float myVal:buffer.getChannel(0)){
				CCLog.info(myVal);
			}
			buffer.convertToByteArray(outBytes, 0, format);
			if (line.available() == line.getBufferSize()) {
				CCSoundIO.debug("Likely buffer underrun in AudioOutput.");
			}
			line.write(outBytes, 0, outBytes.length);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
		line.drain();
		line.stop();
		line.close();
		line = null;
	}

	// TODO: ditch readSignal eventually
	private void readSignal() {
		if (line.getFormat().getChannels() == CCSoundIO.MONO) {
			// long start = System.nanoTime();
			signal.generate(buffer.getChannel(0));
			// long end = System.nanoTime();
			// long elap = (end - start) / 1000;
			// System.out.println("Generated a buffer in " + elap +
			// " microseconds.");
		} else {
			signal.generate(buffer.getChannel(0), buffer.getChannel(1));
		}
	}

	private void readStream() {
		stream.read(mcBuffer);
		for (int i = 0; i < mcBuffer.getChannelCount(); i++) {
			System.arraycopy(mcBuffer.getChannel(i), 0, buffer.getChannel(i), 0, buffer.getSampleCount());
		}
	}

	public void open() {
		start();
	}

	public void close() {
		finished = true;
	}

	/**
	 * @return the size of the buffer used by this output.
	 */
	public int bufferSize() {
		return bufferSize;
	}

	public AudioFormat getFormat() {
		return format;
	}

	/**
	 * Sets the AudioSignal that this output will use to generate sound.
	 * 
	 * @param signal the AudioSignal used to generate sound
	 */
	public void setAudioSignal(CCAudioSignal signal) {
		this.signal = signal;
	}

	/**
	 * Sets the AudioListener that will have sound broadcasted to it as the
	 * output generates.
	 * 
	 * @param listen
	 */
	public void setAudioListener(CCAudioListener listener) {
		this.listener = listener;
	}

	public Control[] getControls() {
		return line.getControls();
	}

	/**
	 * Sets the AudioStream that this output will use to generate sound.
	 * 
	 * @param stream
	 */
	public void setAudioStream(CCAudioStream stream) {
		this.stream = stream;
	}
}
