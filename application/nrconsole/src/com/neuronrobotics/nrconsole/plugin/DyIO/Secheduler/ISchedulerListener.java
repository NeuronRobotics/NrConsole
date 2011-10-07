package com.neuronrobotics.nrconsole.plugin.DyIO.Secheduler;

public interface ISchedulerListener {
	/**
	 * This is called by the scheduler on regular intervals 
	 * @param ms the current time of the running scheduler
	 */
	public void onTimeUpdate(double ms);
	/**
	 * This method is to configure the listeners timing. This passes in the time interval that the scheduler will run at
	 * @param ms time interval that the scheduler will run at
	 */
	public void setIntervalTime(double ms);
	
	/**
	 * This function is called when the seceduler is stopped
	 */
	public void isStopped();
}
