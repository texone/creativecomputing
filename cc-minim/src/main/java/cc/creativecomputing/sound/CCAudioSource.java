package cc.creativecomputing.sound;

import javax.sound.sampled.AudioFormat;

/**
 * An <code>AudioSource</code> is a kind of wrapper around an
 * <code>AudioStream</code>. An <code>AudioSource</code> will add its
 * <code>AudioBuffer</code>s as listeners on the stream so that you can access
 * the stream's samples without having to implement <code>AudioListener</code>
 * yourself. It also provides the <code>Effectable</code> and
 * <code>Recordable</code> interface. Because an <code>AudioStream</code> must
 * be closed when you are finished with it, you must remember to call
 * {@link #close()} on any <code>AudioSource</code>s you obtain from Minim, such
 * as <code>AudioInput</code>s, <code>AudioOutput</code>s, and
 * <code>AudioPlayer</code>s.
 * 
 * @author Damien Di Fede
 * @invisible
 * 
 */
public class CCAudioSource extends CCAudioController implements CCAudioResource, CCRecordable {
	// the instance of Minim that created us, if one did.
	CCSoundIO parent;

	private CCAudioOutput stream;
	// the signal splitter used to manage listeners to the source
	// our stereobuffer will be the first in the list
	private CCSignalSplitter splitter;
	// the StereoBuffer that will subscribe to synth
	private CCStereoBuffer buffer;

	/**
	 * The AudioBuffer containing the left channel samples. If this is a mono
	 * sound, it contains the single channel of audio.
	 * 
	 * @example Basics/PlayAFile
	 * 
	 * @related AudioBuffer
	 */
	public final CCAudioBuffer left;

	/**
	 * The AudioBuffer containing the right channel samples. If this is a mono
	 * sound, <code>right</code> contains the same samples as <code>left</code>.
	 * 
	 * @example Basics/PlayAFile
	 * 
	 * @related AudioBuffer
	 */
	public final CCAudioBuffer right;

	/**
	 * The AudioBuffer containing the mix of the left and right channels. If
	 * this is a mono sound, <code>mix</code> contains the same samples as
	 * <code>left</code>.
	 * 
	 * @example Basics/PlayAFile
	 * 
	 * @related AudioBuffer
	 */
	public final CCAudioBuffer mix;

	/**
	 * Constructs an <code>AudioSource</code> that will subscribe to the samples
	 * in <code>stream</code>. It is expected that the stream is using a
	 * <code>DataLine</code> for playback. If it is not, calls to
	 * <code>Controller</code>'s methods will result in a
	 * <code>NullPointerException</code>.
	 * 
	 * @param istream the <code>AudioStream</code> to subscribe to and wrap
	 * 
	 * @invisible
	 */
	public CCAudioSource(CCAudioOutput istream) {
		super(istream.getControls());
		stream = istream;

		// we gots a buffer for users to poll
		buffer = new CCStereoBuffer(stream.getFormat().getChannels(), stream.bufferSize(), this);
		left = buffer.left;
		right = buffer.right;
		mix = buffer.mix;

		// we gots a signal splitter that we'll add any listeners the user wants
		splitter = new CCSignalSplitter(stream.getFormat(), stream.bufferSize());
		// we stick our buffer in the signal splitter because we can only set
		// one
		// listener on the stream
		splitter.addListener(buffer);
		// and there it goes.
		stream.setAudioListener(splitter);

		stream.open();
	}

	/**
	 * Closes this source, making it unavailable.
	 * 
	 * @invisible
	 */
	public void close() {
		CCSoundIO.debug("Closing " + this.toString());

		stream.close();

		// if we have a parent, tell them to stop tracking us
		// so that we can get garbage collected
		if (parent != null) {
			CCSoundIO.removeSource(this);
		}
	}

	/**
	 * Add an AudioListener to this sound generating object, which will have its
	 * samples method called every time this object generates a new buffer of
	 * samples.
	 * 
	 * @shortdesc Add an AudioListener to this sound generating object.
	 * 
	 * @example Advanced/AddAndRemoveAudioListener
	 * 
	 * @param listener the AudioListener that will listen to this
	 * 
	 * @related AudioListener
	 */
	public void addListener(CCAudioListener listener) {
		splitter.addListener(listener);
	}

	/**
	 * The internal buffer size of this sound object. The left, right, and mix
	 * AudioBuffers of this object will be this large, and sample buffers passed
	 * to AudioListeners added to this object will be this large.
	 * 
	 * @shortdesc The internal buffer size of this sound object.
	 * 
	 * @example Basics/PlayAFile
	 * 
	 * @return int: the internal buffer size of this sound object, in sample
	 *         frames.
	 */
	public int bufferSize() {
		return stream.bufferSize();
	}

	/**
	 * Returns an AudioFormat object that describes the audio properties of this
	 * sound generating object. This is often useful information when doing
	 * sound analysis or some synthesis, but typically you will not need to know
	 * about the specific format.
	 * 
	 * @shortdesc Returns AudioFormat object that describes the audio properties
	 *            of this sound generating object.
	 * 
	 * @example Advanced/GetAudioFormat
	 * 
	 * @return an AudioFormat describing this sound object.
	 */
	public AudioFormat getFormat() {
		return stream.getFormat();
	}

	/**
	 * Removes an AudioListener that was previously added to this sound object.
	 * 
	 * @example Advanced/AddAndRemoveAudioListener
	 * 
	 * @param listener the AudioListener that should stop listening to this
	 * 
	 * @related AudioListener
	 */
	public void removeListener(CCAudioListener listener) {
		splitter.removeListener(listener);
	}

	/**
	 * The type is an int describing the number of channels this sound object
	 * has.
	 * 
	 * @return Minim.MONO if this is mono, Minim.STEREO if this is stereo
	 */
	public int type() {
		return stream.getFormat().getChannels();
	}

	/**
	 * Returns the sample rate of this sound object.
	 * 
	 * @return the sample rate of this sound object.
	 */
	public float sampleRate() {
		return stream.getFormat().getSampleRate();
	}

	@Override
	public void open() {
		
	}
}
