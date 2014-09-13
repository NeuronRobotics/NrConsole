package com.neuronrobotics.nrconsole.plugin.DeviceConfig;

import java.util.ArrayList;

import javax.swing.JPanel;

public abstract class SettingsPanel extends JPanel {
private ArrayList<MachineSetting> settings;
	
	public abstract String getPanelName();
	
	public ArrayList<MachineSetting> getValues(){
		return settings;
	}
	
	public void setValues(ArrayList<MachineSetting> values) {
		settings = values;
		
	}
	
	public boolean settingExists(String _name){
		for (MachineSetting machineSetting : settings) {
			if (machineSetting.getName() == _name){
				return true;
			}
		}
		return false;
	}
	
	public MachineSetting getSetting(String _name){
		for (MachineSetting machineSetting : settings) {
			if (machineSetting.getName() == _name){
				return machineSetting;
			}
		}
		return null;
	}
	
	public Object getSetValue(String _name){
		for (MachineSetting machineSetting : settings) {
			if (machineSetting.getName() == _name){
				return machineSetting.getValue();
			}
		}
		return null;
	}
	public int getIntValue(String _name){
		for (MachineSetting machineSetting : settings) {
			if (machineSetting.getName() == _name){
				return (int) machineSetting.getValue();
			}
		}
		return -1;
	}
	public boolean getBooleanValue(String _name){
		for (MachineSetting machineSetting : settings) {
			if (machineSetting.getName() == _name){
				return (boolean) machineSetting.getValue();
			}
		}
		return false;
	}
	public double getDoubleValue(String _name){
		for (MachineSetting machineSetting : settings) {
			if (machineSetting.getName() == _name){
				return (double) machineSetting.getValue();
			}
		}
		return -1;
	}
}
