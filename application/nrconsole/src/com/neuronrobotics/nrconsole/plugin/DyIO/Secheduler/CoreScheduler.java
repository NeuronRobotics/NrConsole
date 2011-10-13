package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.neuronrobotics.sdk.addons.walker.WalkerServoLink;
import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.dyio.DyIO;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;


public class CoreScheduler {
	private int loopTime;
	private long flushTime = 0; 
	private SchedulerThread st=null;
	private MP3 mp3;
	private boolean loop = false;
	private ArrayList< ISchedulerListener> listeners = new ArrayList< ISchedulerListener>();
	private ArrayList< ServoOutputScheduleChannel> outputs = new ArrayList< ServoOutputScheduleChannel>();
	private DyIO dyio;
	private String filename=null;
	private int msDuration=0;
	//private int trackLength;
	private File audioFile=null;
	public CoreScheduler(DyIO d, int loopTime,int duration ){
		dyio = d;
		this.loopTime=loopTime;
		msDuration=duration;
		//dyio.enableDebug();	
	}
	
	public void loadFromFile(File f){
		/**
		 * sample code from
		 * http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
		 */
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder;
	    Document doc = null;
	    try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new FileInputStream(f));
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		//System.out.println("Parsing File...");
		NodeList nList = doc.getElementsByTagName("ServoOutputSequenceGroup");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			//System.out.println("Leg # "+temp);
		    Node nNode = nList.item(temp);
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    	Element eElement = (Element) nNode;
		    	
		    	String filename = getTagValue("mp3",eElement);
		    	if(filename!=null){
		    		setAudioFile(new File(filename));
		    	}else{
		    		msDuration = Integer.parseInt(getTagValue("duration",eElement));
		    	}
		    	loopTime = Integer.parseInt(getTagValue("loopTime",eElement));
		    	NodeList links = eElement.getElementsByTagName("ServoOutputSequence");
		    	for (int i = 0; i < links.getLength(); i++) {
		    		//System.out.println("\tLink # "+i);
		    		Node lNode = links.item(i);
		    		if (lNode.getNodeType() == Node.ELEMENT_NODE) {
			    		Element lElement = (Element) lNode;
			    		int max=Integer.parseInt(getTagValue("outputMax",lElement));
			    		int min=Integer.parseInt(getTagValue("outputMin",lElement));
			    		int channel=Integer.parseInt(getTagValue("outputChannel",lElement));
			    		boolean enabled = getTagValue("inputEnabled",lElement).contains("true");
			    		
			    		double inScale=Double.parseDouble(getTagValue("inputScale",lElement));
			    		int outCenter=Integer.parseInt(getTagValue("outputCenter",lElement));
			    		int inChannel=Integer.parseInt(getTagValue("inputChannel",lElement));
			    		
			    		String [] sdata =  getTagValue("data",lElement).split(",");
			    		int []data=new int[sdata.length];
			    		for(int j=0;j<data.length;j++){
			    			data[j]=Integer.parseInt(sdata[j]);
			    		}
			    		ServoOutputScheduleChannel so = addServoChannel(channel);
			    		so.setOutputMinMax(min,max);
			    		so.setInputCenter(outCenter);
			    		so.setInputScale(inScale);
			    		so.setAnalogInputChannelNumber(inChannel);
			    		if(!enabled){
			    			so.pauseRecording();
			    		}else {
			    			so.startRecording();
			    		}
			    		so.setData(data);
		    		}
		    	}

		    }else{
		    	//System.out.println("Not Element Node");
		    }
		}
		System.out.println("Populated Scheduler");
	}
	private static String getTagValue(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	    //System.out.println("\t\t"+sTag+" = "+nValue.getNodeValue());
	    return nValue.getNodeValue();    
	}
	public void setAudioFile(File f) {
		if( audioFile==f || f==null)
			return;
		audioFile=f;
		filename=f.getAbsolutePath();
    	mp3 = new MP3(f.getAbsolutePath());
    	msDuration = mp3.getTrackLength();
	}
	public int getTrackLength(){
		return msDuration;
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
		soc.setIntervalTime(loopTime, getTrackLength());
		addISchedulerListener(soc);
		//soc.setIntervalTime(loopTime);
		getOutputs().add(soc);
		return soc;
	}
	
	public void removeServoOutputScheduleChannel(ServoOutputScheduleChannel s){
		getOutputs().remove(s);
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
		long start = System.currentTimeMillis();
		if(dyio!=null){
			//Log.enableDebugPrint(true);
			double seconds =((double)(loopTime/3))/1000;
			dyio.flushCache(seconds);
			//Log.enableDebugPrint(false);
		}
		flushTime = System.currentTimeMillis()-start;
		if(flushTime>loopTime){
			System.err.println("Flush took:"+flushTime+ " and loop time="+loopTime);
			flushTime=loopTime;
		}
		for(ISchedulerListener l:listeners){
			l.onTimeUpdate(time);
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
		for(ServoOutputScheduleChannel so:getOutputs()){
			s+=so.getXml();
		}
		s+="</ServoOutputSequenceGroup>\n";
		return s;
	}
	
	public void setOutputs(ArrayList< ServoOutputScheduleChannel> outputs) {
		this.outputs = outputs;
	}

	public ArrayList< ServoOutputScheduleChannel> getOutputs() {
		return outputs;
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
			for(ServoOutputScheduleChannel s:getOutputs()){
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

	public File getAudioFile() {
		return audioFile;
	}


}
