package com.neuronrobotics.nrconsole.util;

import java.util.prefs.Preferences;

public class PrefsLoader {
	private static final String SLIC3R_LOCATION = "slic3r_path";	
	static String path = "/usr/local/Slic3r/bin/slic3r";
	Preferences prefs = Preferences.userNodeForPackage(this.getClass());
	
	
	public String getSlic3rLocation(){
		return prefs.get(SLIC3R_LOCATION, path);
	}
	public void setSlic3rLocation(String _path){
		prefs.put(SLIC3R_LOCATION, _path);
	}
	
	
	
}
