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
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Control;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.AudioUtils;

public class CCAudioRecordingStream implements Runnable, CCAudioStream {
	private Thread iothread;
	private CCAudioListener listener;

	private CCAudioMetaData meta;

	// reading stuff
	private boolean play;
	private boolean loop;
	private int numLoops;
	// loop begin is in milliseconds
	private int loopBegin;
	// loop end is in bytes
	private int loopEnd;
	protected AudioInputStream ais;

	// byte array we use in readBytes
	private byte[] rawBytes;
	// byte array we use in skip
	private byte[] skipBytes;

	// whether or not we should read from the file
	// this is different from whether we should play or not.
	// this will always be true, unless we've got
	// bytes left in rawBytes that need to be written
	// to the output line. when that happens this will be
	// set to false. i use a boolean instead of inferring the
	// the state from the value of bytesWritten so that
	// if the implementation changes, it can.
	private boolean shouldRead;
	// accumulates the total number of bytes that have been
	// written out to the output line so that we can
	// report how far into the stream we are.
	private int totalBytesRead;
	// how many bytes have we written to the output line
	// we keep track of this so that if a line is stopped
	// in the middle of a write, which can happen if
	// the stream is paused, we can pick up where we left
	// off. this means we don't have to sit and spin
	// in writeBytes waiting to be able to write the rest
	// of the bytes, we can just exit and allow silence
	// to be broadcasted out to the listener.
	private int bytesWritten;

	// writing stuff
	protected AudioFormat format;
	private SourceDataLine line;
	private CCFloatSampleBuffer buffer;
	private int bufferSize;
	private boolean finished;
	private float[] silence;

	public CCAudioRecordingStream(CCAudioMetaData metaData, AudioInputStream stream, SourceDataLine sdl, int inBufferSize, int msLen) {
		meta = metaData;
		format = sdl.getFormat();
		bufferSize = inBufferSize;

		// allocate reading data
		buffer = new CCFloatSampleBuffer(format.getChannels(), bufferSize, format.getSampleRate());
		CCSoundIO.debug("JSBaseAudioRecordingStream :: FloatSampleBuffer has " + buffer.getSampleCount() + " samples.");

		rawBytes = new byte[buffer.getByteArrayBufferSize(format)];
		CCSoundIO.debug("JSBaseAudioRecordingStream :: rawBytes has length " + rawBytes.length);

		skipBytes = new byte[(int) AudioUtils.millis2BytesFrameAligned(10000, format)];
		CCSoundIO.debug("JSBaseAudioRecordingStream :: skipBytes has length " + skipBytes.length);

		finished = false;
		line = sdl;

		ais = stream;
		loop = false;
		play = false;
		numLoops = 0;
		loopBegin = 0;
		loopEnd = (int) AudioUtils.millis2BytesFrameAligned(msLen, format);

		silence = new float[bufferSize];
		iothread = null;
		totalBytesRead = 0;
		bytesWritten = 0;
		shouldRead = true;
	}

	/**
	 * Returns meta data about the recording, such as duration, name, ID3 tags
	 * perhaps.
	 * 
	 * @return the MetaData of the recording
	 */
	public CCAudioMetaData getMetaData() {
		return meta;
	}

	/**
	 * Returns the length of the source in milliseconds. Infinite sources, such
	 * as internet radio streams, should return -1.
	 * 
	 * @return the length of the source, in milliseconds
	 */
	public int getMillisecondLength() {
		return meta.length();
	}
	
	public void readSampless(){
		// read in a full buffer of bytes from the file
		if (loop) {
			readBytesLoop();
		} else {
			readBytes();
		}
		// convert them to floating point
		// hand those arrays to our effect
		// and convert back to bytes
		process();
		
		// write to the line.
		writeBytes();
		// send samples to the listener
		// these will be what we just put into the line
		// which means they should be pretty well sync'd
		// with the audible result
		broadcast();
	}

	public void run() {
		while (!finished) {
			if (play) {
				if (shouldRead) {
					// read in a full buffer of bytes from the file
					if (loop) {
						readBytesLoop();
					} else {
						readBytes();
					}
					// convert them to floating point
					// hand those arrays to our effect
					// and convert back to bytes
					process();
				}
				// write to the line.
				writeBytes();
				// send samples to the listener
				// these will be what we just put into the line
				// which means they should be pretty well sync'd
				// with the audible result
				broadcast();
				// take a nap
				Thread.yield();
			} else {
				// if we're not playing, we can just chill out until we're told
				// to play again.
				// no reason to sit and spin doing nothing.
				CCSoundIO.debug("Gonna wait...");
				// but first set out an empty buffer, to represent our silenced
				// state.
				broadcast();
				// go to sleep for a really long time. we'll be interrupted if
				// we need to start up again.
				sleep(30000);
				CCSoundIO.debug("Done waiting!");
			}
		} // while ( !finished )

		// flush the line before we close it. because it's polite.
		line.flush();
		line.close();
		line = null;
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	private int readBytes() {
		int bytesRead = 0;
		int toRead = rawBytes.length;
		try {
			while (bytesRead < toRead) {

				int actualRead = 0;
				synchronized (ais) {
					actualRead = ais.read(rawBytes, bytesRead, toRead - bytesRead);
					// JSMinim.debug("Wanted to read " + (toRead-bytesRead) + ",
					// actually read " + actualRead);
				}
				if (actualRead == -1) {
					CCSoundIO.debug("Actual read was -1, pausing...");
					pause();
					break;
				} else {
					bytesRead += actualRead;
				}
			}

		} catch (IOException e) {
			throw new CCSoundException("Error reading from the file - " + e.getMessage(), e);
		}
		totalBytesRead += bytesRead;

		return bytesRead;
	}

	private void readBytesLoop() {
		int toLoopEnd = loopEnd - totalBytesRead;
		if (toLoopEnd <= 0) {
			System.out.println("Returning to loopBegin because toLoopEnd <= 0");
			if (loop && numLoops != CCSoundIO.LOOP_CONTINUOUSLY) {
				numLoops--;
			}

			if (numLoops != 0) {
				// whoops, our loop end point got switched up
				setMillisecondPosition(loopBegin);
				readBytesLoop();
			} else {
				Arrays.fill(rawBytes, (byte) 0);
			}
			return;
		}
		if (toLoopEnd < rawBytes.length) {
			readBytesWrap(toLoopEnd, 0);
			if (loop && numLoops == 0) {
				loop = false;
				pause();
			} else if (loop) {
				System.out.println("Returning to loopBegin because else if loop");
				if (numLoops != CCSoundIO.LOOP_CONTINUOUSLY) {
					numLoops--;
				}
				setMillisecondPosition(loopBegin);
				readBytesWrap(rawBytes.length - toLoopEnd, toLoopEnd);
			}
		} else {
			readBytesWrap(rawBytes.length, 0);
		}
	}

	// read toRead bytes from ais into rawBytes.
	// we assume here that if we get to the end of the file
	// that we should wrap around to the beginning
	private void readBytesWrap(int toRead, int offset) {
		int bytesRead = 0;
		try {
			while (bytesRead < toRead) {

				int actualRead = 0;
				synchronized (ais) {
					actualRead = ais.read(rawBytes, bytesRead + offset, toRead - bytesRead);
				}
				if (-1 == actualRead) {
					System.out.println("!!!!!!! Looping with numLoops " + numLoops);
					setMillisecondPosition(0);
					if (numLoops != CCSoundIO.LOOP_CONTINUOUSLY) {
						numLoops--;
					}
				} else if (actualRead == 0) {
					// we want to prevent an infinite loop
					// but this will hopefully never happen because
					// we set the loop end point with a frame aligned byte
					// number
					break;
				} else {
					bytesRead += actualRead;
					totalBytesRead += actualRead;
				}
			}

		} catch (IOException ioe) {
			throw new CCSoundException("Error reading from the file - " + ioe.getMessage(), ioe);
		}
	}

	private void writeBytes() {
		// the write call will block until the requested amount of bytes
		// is written, however the user might stop the line in the
		// middle of writing and then we get told how much was actually written.
		// because of that, we might not need to write the entire array when we
		// get here.
		int needToWrite = rawBytes.length - bytesWritten;
		int actualWrit = line.write(rawBytes, bytesWritten, needToWrite);
		// if the total written is not equal to how much we needed to write
		// then we need to remember where we were so that we don't read more
		// until we finished writing our entire rawBytes array.
		if (actualWrit != needToWrite) {
			CCSoundIO.debug("writeBytes: wrote " + actualWrit + " of " + needToWrite);
			shouldRead = false;
			bytesWritten += actualWrit;
		} else {
			// if it all got written, we should continue reading
			// and we reset our bytesWritten value.
			shouldRead = true;
			bytesWritten = 0;
		}
	}

	private void broadcast() {
		if(listener == null)return;
		synchronized (buffer) {
			if (buffer.getChannelCount() == CCSoundIO.MONO) {
				if (play) {
					listener.samples(buffer.getChannel(0));
				} else {
					listener.samples(silence);
				}
			} else if (buffer.getChannelCount() == CCSoundIO.STEREO) {
				if (play) {
					listener.samples(buffer.getChannel(0), buffer.getChannel(1));
				} else {
					listener.samples(silence, silence);
				}
			}
		}
	}

	private synchronized void process() {
		synchronized (buffer) {
			int frameCount = rawBytes.length / format.getFrameSize();
			buffer.setSamplesFromBytes(rawBytes, 0, format, 0, frameCount);

			// finally convert them back to bytes
			buffer.convertToByteArray(rawBytes, 0, format);
		}
	}

	/**
	 * Allows playback/reads of the source. 
	 * 
	 */
	public void play() {
		line.start();
		loop = false;
		numLoops = 0;
		play = true;
		// will wake up our data processing thread.
		// iothread.interrupt();
	}

	public boolean isPlaying() {
		return play;
	}

	/**
	 * Disallows playback/reads of the source. If this is pause, all calls to read 
	 * will generate arrays full of zeros (silence).
	 * 
	 */
	public void pause() {
		line.stop();
		play = false;
	}

	/**
	 * Starts looping playback from the current position. Playback will continue
	 * to the loop's end point, then loop back to the loop start point count
	 * times, and finally continue playback to the end of the clip.
	 * 
	 * If the current position when this method is invoked is greater than the
	 * loop end point, playback simply continues to the end of the source without
	 * looping.
	 * 
	 * A count value of 0 indicates that any current looping should cease and
	 * playback should continue to the end of the clip. The behavior is undefined
	 * when this method is invoked with any other value during a loop operation.
	 * 
	 * If playback is stopped during looping, the current loop status is cleared;
	 * the behavior of subsequent loop and start requests is not affected by an
	 * interrupted loop operation.
	 * 
	 * @param count
	 *           the number of times playback should loop back from the loop's
	 *           end position to the loop's start position, or
	 *           Minim.LOOP_CONTINUOUSLY to indicate that looping should continue
	 *           until interrupted
	 */
	public void loop(int n) {
		// let's get it cued before we muck with any of our state vars.
		setMillisecondPosition(loopBegin);
		loop = true;
		numLoops = n;
		play = true;
		line.start();
		// will wake up our data processing thread.
		// iothread.interrupt();
	}

	public void open() {
		finished = false;
		iothread = new Thread(this);
		iothread.start();
	}

	public void close() {
		finished = true;
		// try
		// {
		// iothread.join(10);
		// }
		// catch (InterruptedException e)
		// {
		// e.printStackTrace();
		// }
		iothread = null;
		try {
			ais.close();
		} catch (IOException e) {
		}

		line.flush();
		line.close();
	}

	public int bufferSize() {
		return bufferSize;
	}

	public AudioFormat getFormat() {
		return format;
	}

	/**
	 * How many loops are left to go. 0 means this isn't looping and -1 means
	 * that it is looping continuously.
	 * 
	 * @return how many loops left
	 */
	public int getLoopCount() {
		return numLoops;
	}

	// TODO: consider using mark for marking the starting loop point
	// in cases where the section being looped is not really huge.
	// doing so will make it possible loop sections of large files
	// without having to make a new AudioInputStream
	/**
	 * Sets the loops points in the source, in milliseconds
	 * 
	 * @param start
	 *           the position of the beginning of the loop
	 * @param stop
	 *           the position of the end of the loop
	 */
	public void setLoopPoints(int start, int stop) {
		if (start <= 0 || start > stop) {
			loopBegin = 0;
		} else {
			loopBegin = start;
		}
		if (stop <= getMillisecondLength() && stop > start) {
			loopEnd = (int) AudioUtils.millis2BytesFrameAligned(stop, format);
		} else {
			loopEnd = (int) AudioUtils.millis2BytesFrameAligned(getMillisecondLength(), format);
		}
	}

	/**
	 * Gets the current millisecond position of the source.
	 * 
	 * @return the current position, in milliseconds in the source
	 */
	public int getMillisecondPosition() {
		return (int) AudioUtils.bytes2Millis(totalBytesRead, format);
	}

	/**
	 * Sets the current millisecond position of the source.
	 * 
	 * @param pos
	 *           the position to cue the playback head to
	 */
	public void setMillisecondPosition(int millis) {
		// millis is guaranteed by methods that call this one to be
		// in the interval [0, getMillisecondLength()], so we don't do bounds
		// checking
		boolean wasPlaying = play;
		play = false;
		if (millis < getMillisecondPosition()) {
			rewind();
			totalBytesRead = skip(millis);
		} else {
			totalBytesRead += skip(millis - getMillisecondPosition());
		}
		play = wasPlaying;
		// if we're supposed to be playing we need to
		// poke the iothread, because it's possible it
		// will have dropped into it's long sleep while we
		// were doing our thing. this is especially
		// likely if we are setting to a previous position.
		// if ( play )
		// {
		// iothread.interrupt();
		// }
	}


	/**
	 * Returns how many sample frames are in this recording. Infinite sources,
	 * such as internet radio streams, should return -1.
	 * 
	 * @return the length of the source, in sample frames
	 */
	public long getSampleFrameLength() {
		return ais.getFrameLength();
	}

	public Control[] getControls() {
		return line.getControls();
	}

	public void setAudioListener(CCAudioListener listener) {
		this.listener = listener;
	}

	synchronized protected void rewind() {
		// close and reload
		// because marking the thing such that you can play the
		// entire file without the mark being invalidated,
		// essentially means you are loading the file into memory
		// as it is played. which can mean out-of-memory for large files.
		try {
			ais.close();
		} catch (IOException e) {
			throw new CCSoundException("JSPCMAudioRecordingStream::rewind - Error closing the stream before reload", e);
		}
		ais = CCSoundIO.getAudioInputStream(meta.fileName());
	}

	protected int skip(int millis) {
		long toSkip = AudioUtils.millis2BytesFrameAligned(millis, format);


		if (toSkip <= 0) {
			if (toSkip < 0) {
				throw new CCSoundException("JSBaseAudioRecordingStream.skip :: Tried to skip negative milleseconds!");
			}
			return 0;
		}

		CCSoundIO.debug("Skipping forward by " + millis + " milliseconds, which is " + toSkip + " bytes.");

		long totalSkipped = 0;
		try {
			while (toSkip > 0) {
				long read;
				synchronized (ais) {
					// we don't use skip here because it sometimes has problems
					// where
					// it's "unable to skip an integer number of frames",
					// which sometimes means it doesn't skip at all and other
					// times
					// means that you wind up with noise because it lands at
					// half
					// a sample off from where it should be.
					// read seems to be rock solid.
					int myBytesToRead = skipBytes.length;
					if (toSkip < myBytesToRead) {
						myBytesToRead = (int) toSkip;
					}
					read = ais.read(skipBytes, 0, myBytesToRead);
				}
				if (read == -1) {
					// EOF!
					CCSoundIO.debug("JSBaseAudioRecordingStream.skip :: EOF reached!");
					break;
				}
				toSkip -= read;
				totalSkipped += read;
			}
		} catch (IOException e) {
			throw new CCSoundException("Unable to skip due to read error", e);
		}
		CCSoundIO.debug("Total actually skipped was " + totalSkipped + ", which is "
				+ AudioUtils.bytes2Millis(totalSkipped, ais.getFormat()) + " milliseconds.");
		readSampless();
		return (int) totalSkipped;
	}

	// TODO: this implementation of float[] read is way temporary
	@Override
	public float[] read() {
		if (buffer.getSampleCount() != 1) {
			buffer.changeSampleCount(1, true);
			rawBytes = new byte[buffer.getByteArrayBufferSize(format)];
		}
		float[] samples = new float[buffer.getChannelCount()];
		if (play) {
			mRead();
			for (int i = 0; i < buffer.getChannelCount(); i++) {
				samples[i] = buffer.getChannel(i)[0];
			}
		}
		return samples;
	}

	// FIXME: temporary implementation of read
	@Override
	public int read(CCMultiChannelBuffer outBuffer) {
		if (buffer.getSampleCount() != outBuffer.getBufferSize()) {
			buffer.changeSampleCount(outBuffer.getBufferSize(), true);
			rawBytes = new byte[buffer.getByteArrayBufferSize(format)];
		}
		int framesRead = 0;
		if (play) {
			framesRead = mRead();
		} else {
			buffer.makeSilence();
		}
		for (int i = 0; i < buffer.getChannelCount(); i++) {
			outBuffer.setChannel(i, buffer.getChannel(i));
		}

		return framesRead;
	}

	// returns number of samples read, not bytes
	private int mRead() {
		// read in a full buffer of bytes from the file
		int bytesRead = rawBytes.length;
		if (loop) {
			readBytesLoop();
		} else {
			bytesRead = readBytes();
		}
		// convert them to floating point
		int frameCount = bytesRead / format.getFrameSize();
		synchronized (buffer) {
			buffer.setSamplesFromBytes(rawBytes, 0, format, 0, frameCount);
		}

		return frameCount;
	}
}
