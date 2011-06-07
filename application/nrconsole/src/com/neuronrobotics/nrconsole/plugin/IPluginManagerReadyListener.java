package com.neuronrobotics.nrconsole.plugin;

/**
 * this interface is for listening for the plugin manager to be ready to go.
 * @author hephaestus
 *
 */
public interface IPluginManagerReadyListener {
	public void ready();
	public void setPercentage(int percent);
}
