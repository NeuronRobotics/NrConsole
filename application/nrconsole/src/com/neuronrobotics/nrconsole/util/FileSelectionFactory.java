package com.neuronrobotics.nrconsole.util;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


public class FileSelectionFactory {
	public static File GetFile(File start, FileFilter filter) {
		JFileChooser fc =new JFileChooser();
    	File dir1 = new File (".");
    	if(start!=null){
    		fc.setSelectedFile(start);
    	}else{
    		fc.setCurrentDirectory(dir1);
    	}
    	fc.setFileFilter(filter);
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
        	return fc.getSelectedFile();
        }
        return null;
	}
}
