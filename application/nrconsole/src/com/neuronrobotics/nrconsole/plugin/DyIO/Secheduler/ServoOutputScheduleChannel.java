package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.util.ArrayList;

import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;
import com.neuronrobotics.sdk.dyio.peripherals.IServoPositionUpdateListener;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class ServoOutputScheduleChannel implements ISchedulerListener, IAnalogInputListener {
	
	private ServoChannel output;
	AnalogInputChannel input;
	private double inputScale;
	private int inputCenter = 128;
	private int inputValue;
	
	private boolean recording=false;
	private double interval;
	
	private int currentValue;
	private ArrayList<MapData> data = new ArrayList<MapData>();
	private int outputMax=200;
	private int outputMin=50;
	private int index=0;
	public ServoOutputScheduleChannel(ServoChannel srv) {
		output=srv;
		currentValue = output.getValue();
		srv.SetPosition(currentValue);
		srv.flush();
	}
	public int getChannelNumber(){
		return output.getChannel().getChannelNumber();
	}
	public void pauseRecording(){
		System.out.println("pausing recording");
		input.removeAnalogInputListener(this);
		recording=false;
	}
	public void resumeRecording(){
		System.out.println("resuming recording");
		addAnalogInputListener(this);
		recording=true;
	}
	
	public void addAnalogInputListener(IAnalogInputListener l){
		input.addAnalogInputListener(l);
	}
	
	public void startRecording(int analogInputChannelNumber, int inCenter, double inScale){
		setInputCenter(inCenter);
		setInputScale(inScale);
		if(input==null){
			input=new AnalogInputChannel(output.getChannel().getDevice().getChannel(analogInputChannelNumber),true);
			input.configAdvancedAsyncNotEqual(10);
		}
		resumeRecording();
	}

	@Override
	public void onTimeUpdate(double ms) {
		index = (int) (ms/interval);
		if(recording)
			data.get(index).input=inputValue;
		currentValue = data.get(index).input;
		//System.out.println("Setting servo value="+data.get(index).input);
		//Log.enableDebugPrint(true);
		while(index>=data.size()){
			data.add(new MapData(currentValue,index*interval));
		}
		output.SetPosition(data.get(index).input);
		//Log.enableDebugPrint(false);
	}


	@Override
	public void setIntervalTime(int msInterval, int totalTime) {
		interval=msInterval;
		int slices = totalTime/msInterval;
		if(data.size()==0){
			System.out.println("Setting up sample data:");
			data = new ArrayList<MapData>();
			for(int i=0;i<slices;i++){
				data.add(new MapData(currentValue, i*msInterval));
			}
			data.add(new MapData(currentValue, slices*msInterval));
		}
		
	}

	@Override
	public void isStopped() {
		// unused
	}
	@Override
	public void onAnalogValueChange(AnalogInputChannel chan, double value) {
		System.out.println("Analog value="+value);
		inputValue = (int) ((value+getInputCenter())*getInputScale());
		if(inputValue>getOutputMax()){
			inputValue=getOutputMax();
		}
		if(inputValue<getOutputMin()){
			inputValue=getOutputMin();
		}
		System.out.println("Analog value="+value+" scaled="+inputValue);
	}
	
	public void setOutput(ServoChannel output) {
		this.output = output;
	}

	public ServoChannel getOutput() {
		return output;
	}

	public void setInputCenter(int inputCenter) {
		this.inputCenter =inputCenter- 512;
	}
	public int getInputCenter() {
		return inputCenter;
	}

	public void setInputScale(double inputScale) {
		this.inputScale = inputScale;
	}
	public double getInputScale() {
		return inputScale;
	}

	public void setOutputMinMax(int outputMin,int outputMax) {
		this.outputMax = outputMax;
		this.outputMin = outputMin;
	}
	public int getOutputMax() {
		return outputMax;
	}
	public int getOutputMin() {
		return outputMin;
	}
	private class MapData{
		public int input;
		public double time;
		public MapData(int i, double t){
			input=i;
			time=t;
		}
	}
	public boolean isRecording() {
		return recording;
	}
	public void addIServoPositionUpdateListener(IServoPositionUpdateListener l) {
		getOutput().addIServoPositionUpdateListener(l);
	}
	public void removeIServoPositionUpdateListener(IServoPositionUpdateListener l) {
		getOutput().removeIServoPositionUpdateListener(l);
	}
	

}
