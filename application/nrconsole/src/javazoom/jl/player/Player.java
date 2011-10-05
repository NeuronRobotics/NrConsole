/*
 * 11/19/04		1.0 moved to LGPL.
 * 29/01/00		Initial version. mdm@techie.com
 *-----------------------------------------------------------------------
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
 *----------------------------------------------------------------------
 */

package javazoom.jl.player;

import java.io.InputStream;
import java.util.ArrayList;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
	
/**
 * The <code>Player</code> class implements a simple player for playback
 * of an MPEG audio stream. 
 * 
 * @author	Mat McGowan
 * @since	0.0.8
 */

// REVIEW: the audio device should not be opened until the
// first MPEG audio frame has been decoded. 
public class Player
{	  	
	/**
	 * The current frame number. 
	 */
	private int frame = 0;
	
	/**
	 * The MPEG audio bitstream. 
	 */
	// javac blank final bug. 
	/*final*/ private Bitstream		bitstream;
	
	/**
	 * The MPEG audio decoder. 
	 */
	/*final*/ private Decoder		decoder; 
	
	/**
	 * The AudioDevice the audio samples are written to. 
	 */
	private AudioDevice	audio;
	
	/**
	 * Has the player been closed?
	 */
	private boolean		closed = false;
	
	/**
	 * Has the player played back all frames from the stream?
	 */
	private boolean		complete = false;

	private int			numFrames = 0;
	
	private ArrayList<short[]> outputData = new ArrayList<short[]>();
	/**
	 * Creates a new <code>Player</code> instance. 
	 */
	public Player(InputStream stream) throws JavaLayerException
	{
		this(stream, null);	
	}
	
	public Player(InputStream stream, AudioDevice device) throws JavaLayerException
	{
		bitstream = new Bitstream(stream);		
		decoder = new Decoder();
				
		if (device!=null)
		{		
			audio = device;
		}
		else
		{			
			FactoryRegistry r = FactoryRegistry.systemRegistry();
			audio = r.createAudioDevice();
		}
		audio.open(decoder);
		getNumberOfFrames();
		complete = false;
	}
	public double getNumberOfFrames() {
		if(numFrames==0) {
			boolean ret = true;
			while (ret)
			{
				try
				{
					AudioDevice out = audio;
					if (out==null)
						return numFrames;

					Header h = bitstream.readFrame();	
					
					if (h==null)
						return numFrames;	
					// sample buffer set when decoder constructed
					SampleBuffer s = (SampleBuffer)decoder.decodeFrame(h, bitstream);
					outputData.add(s.getBuffer());														
					bitstream.closeFrame();
				}catch (RuntimeException ex){
					ex.printStackTrace();
				} catch (JavaLayerException e) {
					e.printStackTrace();
				}
				numFrames++;
			}
		}
		return numFrames;
	}
	public void play() throws JavaLayerException
	{
		play(Integer.MAX_VALUE);
	}
	
	/**
	 * Plays a number of MPEG audio frames. 
	 * 
	 * @param frames	The number of frames to play. 
	 * @return	true if the last frame was played, or false if there are
	 *			more frames. 
	 */
	public boolean play(int frames) throws JavaLayerException
	{
		boolean ret = true;
		AudioDevice out = audio;
		frame=0;
		complete = false;
		for(short[] s:outputData) {
			synchronized (this)
			{
				out = audio;
				if (out!=null)
				{					
					out.write(s, 0, s.length);
					frame++;
				}				
			}
		}
		complete = true;
		if (out!=null){				
			out.flush();
			synchronized (this)
			{
				close();
			}				
		}
		return ret;
	}
		
	/**
	 * Cloases this player. Any audio currently playing is stopped
	 * immediately. 
	 */
	public synchronized void close()
	{		
		AudioDevice out = audio;
		if (out!=null)
		{ 
			closed = true;
			audio = null;	
			// this may fail, so ensure object state is set up before
			// calling this method. 
			out.close();
			try
			{
				bitstream.close();
			}
			catch (BitstreamException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the completed status of this player.
	 * 
	 * @return	true if all available MPEG audio frames have been
	 *			decoded, or false otherwise. 
	 */
	public boolean isComplete()
	{
		return complete;	
	}
				
	/**
	 * Retrieves the position in milliseconds of the current audio
	 * sample being played. This method delegates to the <code>
	 * AudioDevice</code> that is used by this player to sound
	 * the decoded audio samples. 
	 */
	public double getPercent()
	{
		return (getCurrentFrame()/getNumberOfFrames());
	}

	public void setCurrentFrame(int frame) {
		this.frame = frame;
	}

	public double getCurrentFrame() {
		return frame;
	}		

	
}
