package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.io.File;
import java.util.ArrayList;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;


public class CoreScheduler {
	private int loopTime = 120;
	private long flushTime = 0; 
	private SchedulerThread st=null;
	private MP3 mp3;
	private boolean loop = false;
	private ArrayList< ISchedulerListener> listeners = new ArrayList< ISchedulerListener>();
	private ArrayList< ServoOutputScheduleChannel> outputs = new ArrayList< ServoOutputScheduleChannel>();
	private DyIO dyio;
	private String filename=null;
	private int msDuration=0;
	public CoreScheduler(DyIO d){
		dyio = d;
	}
	public void setAudioFile(File f) {
		filename=f.getAbsolutePath();
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
	
	public ServoOutputScheduleChannel addServoChannel(int dyIOChannel){
		ServoChannel srv = new ServoChannel(dyio.getChannel(dyIOChannel));
		srv.getChannel().setCachedMode(true);
		ServoOutputScheduleChannel soc = new ServoOutputScheduleChannel(srv);
		addISchedulerListener(soc);
		//soc.setIntervalTime(loopTime);
		outputs.add(soc);
		return soc;
	}
	
	public void removeServoOutputScheduleChannel(ServoOutputScheduleChannel s){
		outputs.remove(s);
	}
	
	public void play(int setpoint,long StartOffset) {
		msDuration=setpoint;
		//System.out.println("Starting scheduler setpoint="+setpoint+" offset="+StartOffset);
		st = new SchedulerThread(msDuration,StartOffset);
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
		if(dyio!=null){
			//Log.enableDebugPrint(true);
			double seconds =((double)(loopTime+loopTime/3))/1000;
			dyio.flushCache(seconds);
			//Log.enableDebugPrint(false);
		}
		flushTime = System.currentTimeMillis()-start;
		if(flushTime>loopTime){
			System.err.println("Flush took:"+flushTime+ " and loop time="+loopTime);
			flushTime=loopTime;
		}

	}
	
	private void callStop(){
		for(ISchedulerListener l:listeners){
			l.isStopped();
		}
	}
	public String getXml(){
		String s="";
		s+="<ServoOutputSequenceGroup>\n";
		if(mp3!=null){
			s+="\t<mp3>"+filename+"</mp3>\n";
		}else{
			s+="\t<duriation>"+msDuration+"</duriation>\n";
		}	
		s+="\t<loopTime>"+loopTime+"</loopTime>\n";
		for(ServoOutputScheduleChannel so:outputs){
			s+=so.getXml();
		}
		s+="</ServoOutputSequenceGroup>\n";
		return s;
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
			for(ServoOutputScheduleChannel s:outputs){
				s.setIntervalTime(loopTime, (int) time);
			}
		}
		public void run(){
			//System.out.println("Starting timer");
			do{
				long start = System.currentTimeMillis();
				System.out.println("Initial slider value = "+StartOffset);
				if(mp3!=null) {
					mp3.play();
				}
				run = true;
				while(run){
					boolean playing;
					long current;
					if(mp3==null){
						playing = (((double)(System.currentTimeMillis()-start))<(time-StartOffset));
						current =((System.currentTimeMillis()-start))+StartOffset;
					}else{
						playing = mp3.isPlaying();
						current = mp3.getCurrentTime();
					}
					if(!playing){
						kill();
						break;
					}
						
					setCurrentTime(current);
					long t  = loopTime-flushTime;
					try {
						Thread.sleep(t);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if(run && isLooping())
					setCurrentTime(0);
			}while(isLooping() && run);
			kill();
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
