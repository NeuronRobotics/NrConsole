package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

import com.neuronrobotics.sdk.dyio.DyIOChannelEvent;
import com.neuronrobotics.sdk.dyio.IChannelEventListener;
import com.neuronrobotics.sdk.dyio.peripherals.AnalogInputChannel;
import com.neuronrobotics.sdk.dyio.peripherals.DyIOAbstractPeripheral;
import com.neuronrobotics.sdk.dyio.peripherals.IAnalogInputListener;
import com.neuronrobotics.sdk.dyio.peripherals.ServoChannel;

public class ServoOutputScheduleChannel implements ISchedulerListener, IAnalogInputListener {
	
	private ServoChannel output;
	AnalogInputChannel input;
	private double inputScale;
	private int inputCenter = 128;
	private int outputCenter=128;
	private int inputValue;
	
	private boolean recording=false;
	private double interval;
	private double lastTime=0;
	
	public ServoOutputScheduleChannel(ServoChannel srv) {
		output=srv;
	}
	public int getChannelNumber(){
		return output.getChannel().getChannelNumber();
	}
	public void pauseRecording(){
		input.removeAnalogInputListener(this);
		recording=false;
	}
	public void resumeRecording(){
		input.addAnalogInputListener(this);
		recording=true;
	}
	public void startRecording(AnalogInputChannel in, int inCenter, double inScale){
		inputCenter=inCenter;
		inputScale=inScale;
		input=in;
		input.setAsync(true);
		input.configAdvancedAsyncNotEqual(10);
		resumeRecording();
	}

	@Override
	public void onTimeUpdate(double ms) {

	}

	@Override
	public void setIntervalTime(double ms) {
		interval=ms;
	}

	@Override
	public void isStopped() {
		// unused
	}
	@Override
	public void onAnalogValueChange(AnalogInputChannel chan, double value) {
		inputValue = (int) value;
	}
	
	public void setOutput(ServoChannel output) {
		this.output = output;
	}

	public ServoChannel getOutput() {
		return output;
	}

	private class MapData{
		public int input;
		public double time;
	}

	

}
