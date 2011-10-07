package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.io.File;
import java.util.ArrayList;

import javax.swing.filechooser.FileFilter;

import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.DyIOChannelMode;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;
import com.neuronrobotics.sdk.util.ThreadUtil;


public class CoreScheduler {
	private final int loopTime = 100;
	private long flushTime = 0; 
	private SchedulerThread st=null;
	private MP3 mp3;
	private boolean loop = false;
	private ArrayList< ISchedulerListener> listeners = new ArrayList< ISchedulerListener>();
	private ArrayList< ServoOutputScheduleChannel> outputs = new ArrayList< ServoOutputScheduleChannel>();
	private DyIO dyio;
	public CoreScheduler(){
		
	}
	public void setAudioFile(File f) {
    	mp3 = new MP3(f.getAbsolutePath());
	}
	public int getTrackLength(){
		return mp3.getTrackLength();
	}
	public void setLooping(boolean b){
		loop=b;
	}
	private boolean isLooping(){
		return loop;
	}
	public boolean isPlaying() {
		return st!=null;
	}
	
	public ServoOutputScheduleChannel addServoChannel(ServoChannel srv){
		dyio=srv.getChannel().getDevice();
		srv.getChannel().setCachedMode(true);
		ServoOutputScheduleChannel soc = new ServoOutputScheduleChannel(srv);
		addISchedulerListener(soc);
		soc.setIntervalTime(loopTime);
		outputs.add(soc);
		return soc;
	}
	
	public void play(int setpoint,long StartOffset) {
		//System.out.println("Starting scheduler setpoint="+setpoint+" offset="+StartOffset);
		st = new SchedulerThread(setpoint,StartOffset);
		st.start();
	}
	public void pause() {
		st.kill();
		st=null;
	}
	
	public void addISchedulerListener(ISchedulerListener l){
		for(ISchedulerListener sl:listeners){
			if(sl==l)
				return;
		}
		listeners.add(l);
	}
	public void removeISchedulerListener(ISchedulerListener l){
		listeners.remove(l);
	}
	public void setCurrentTime(long time) {
		for(ISchedulerListener l:listeners){
			l.onTimeUpdate(time);
		}
		long start = System.currentTimeMillis();
		if(dyio!=null)
			dyio.flushCache(loopTime);
		flushTime = System.currentTimeMillis()-start;
		if(flushTime>loopTime){
			System.out.println("Flush took:"+flushTime+ " and loop time="+loopTime);
			flushTime=loopTime;
		}
			
			
	}
	
	private void callStop(){
		for(ISchedulerListener l:listeners){
			l.isStopped();
		}
	}
	
	private class SchedulerThread extends Thread{
		private double time;
		private boolean run = true;
		private long StartOffset;
		
		public SchedulerThread(double ms,final long so){
			time = ms;
			StartOffset=so;
			//System.out.println("Slider value of init="+StartOffset);
			if(mp3!=null) {
				mp3.setCurrentTime((int) (StartOffset));
			}
		}
		public void run(){
			//System.out.println("Starting timer");
			do{
				long start = System.currentTimeMillis();
				//System.out.println("Initial slider value = "+StartOffset);
				if(mp3==null){
					while( (((double)(System.currentTimeMillis()-start))<(time-StartOffset)) && run){
						long offset = ((System.currentTimeMillis()-start))+StartOffset;
						setCurrentTime(offset);
						long t  = loopTime-flushTime;
						try {
							Thread.sleep(t);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else{
					mp3.play();
					while(mp3.isPlaying() && run) {
						setCurrentTime(mp3.getCurrentTime());
						long t  = loopTime-flushTime;
						try {
							Thread.sleep(t);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if(run && isLooping())
					setCurrentTime(0);
			}while(isLooping() && run);
			
			callStop();
			st=null;
		}
		public void kill(){
			if(mp3!=null) {
				mp3.pause();
			}
			run = false;
		}
	}


}
