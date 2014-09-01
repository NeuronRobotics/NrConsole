package com.neuronrobotics.nrconsole.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class Slic3rFilter extends FileFilter {
	
	public String getDescription() {
		return "Slic3r";
	}
	public boolean accept(File f) {
		if(f.isDirectory()) {
			return true;
		}
		String path = f.getAbsolutePath().toLowerCase();
		if ((path.toLowerCase().endsWith("slic3r.exe") && (path.charAt(path.length() - 3)) == '.')) {
			return true;
		}

		return f.getName().matches(".+\\slic3r.exe$");
	}

}
