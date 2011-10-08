package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import java.util.ArrayList;

import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;
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
	private int outputMax;
	private int outputMin;  
	public ServoOutputScheduleChannel(ServoChannel srv) {
		output=srv;
		currentValue = output.getValue();
	}
	public int getChannelNumber(){
		return output.getChannel().getChannelNumber();
	}
	public void pauseRecording(){
		input.removeAnalogInputListener(this);
		recording=false;
	}
	public void resumeRecording(){
		addAnalogInputListener(this);
		recording=true;
	}
	
	public void addAnalogInputListener(IAnalogInputListener l){
		input.addAnalogInputListener(l);
	}
	
	public void startRecording(int analogInputChannelNumber, int inCenter, double inScale){
		setInputCenter(inCenter);
		setInputScale(inScale);
		input=new AnalogInputChannel(output.getChannel().getDevice().getChannel(analogInputChannelNumber),true);
		input.configAdvancedAsyncNotEqual(10);
		resumeRecording();
	}

	@Override
	public void onTimeUpdate(double ms) {
		int index = (int) (ms/interval);
		if(recording){
			if(inputValue>getOutputMax()){
				inputValue=getOutputMax();
			}
			if(inputValue<getOutputMin()){
				inputValue=getOutputMin();
			}
			data.get(index).input=inputValue;
		}
		output.SetPosition(data.get(index).input);
	}


	@Override
	public void setIntervalTime(int msInterval, int totalTime) {
		interval=msInterval;
		int slices = totalTime/msInterval;
		if(data.size()!=slices){
			data = new ArrayList<MapData>();
			for(int i=0;i<slices;i++){
				data.add(new MapData(currentValue, i*msInterval));
			}
		}
		
	}

	@Override
	public void isStopped() {
		// unused
	}
	@Override
	public void onAnalogValueChange(AnalogInputChannel chan, double value) {
		
		inputValue = (int) ((value+getInputCenter())*getInputScale());

	}
	
	public void setOutput(ServoChannel output) {
		this.output = output;
	}

	public ServoChannel getOutput() {
		return output;
	}

	public void setInputCenter(int inputCenter) {
		this.inputCenter = 512-inputCenter;
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

	

}
