package com.neuronrobotics.nrconsole.plugin.JobExec;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.nrconsole.util.FileSelectionFactory;
import com.neuronrobotics.nrconsole.util.PrefsLoader;
import com.neuronrobotics.nrconsole.util.Slic3rFilter;
import com.neuronrobotics.replicator.driver.BowlerBoardDevice;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.replicator.driver.Slic3r;
import com.neuronrobotics.sdk.addons.kinematics.gui.SampleGuiNR;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRConsoleJobExecPlugin extends AbstractNRConsoleTabedPanelPlugin{
	public static final String[] myNames ={"bcs.cartesian.*"};
	
	
	
	private JobExecPanel gui = new JobExecPanel();
	private JPanel holder;
	private BowlerBoardDevice delt = new BowlerBoardDevice();
	private static NRPrinter printer=null;
	private Dimension minSize = new Dimension(693, 476);

	public NRConsoleJobExecPlugin(PluginManager pm) {
		super(myNames, pm);
		//getTabPane();
		//manager = pm;
		//manager.addNRConsoleTabedPanelPlugin(this);
		
	}

	@Override
	public JPanel getTabPane() {
		// TODO Auto-generated method stub
		if(gui!= null){
			//gui = new JPanel(new MigLayout());
			gui.setName("Job Executor");
			
		}
		return gui;
	}

	@Override
	public boolean setConnection(BowlerAbstractConnection connection) {
		delt.setConnection(connection);
		delt.connect();
		//TODO load this from a configuration file, or extract from within jar.
		//Slic3r.setExecutableLocation("/usr/local/Slic3r/bin/slic3r");
		PrefsLoader prefs = new PrefsLoader();
		String path = prefs.getSlic3rLocation();
		if (new File(path).exists() == false){
			 path = FileSelectionFactory.GetFile(null, new Slic3rFilter()).getPath();
			 prefs.setSlic3rLocation(path);
		}
		Slic3r.setExecutableLocation(path);
		if(NRConsoleJobExecPlugin.getPrinter() == null)
			NRConsoleJobExecPlugin.setPrinter(new NRPrinter(delt));
		
		if (delt.isAvailable()){
			gui.setDevices(delt, getPrinter());
		}
		//printer.
		return delt.isAvailable();
		
	}

	
	
	@Override
	public Dimension getMinimumWimdowDimentions() {
		
		return minSize;
	}

	public static NRPrinter getPrinter() {
		return printer;
	}

	public static void setPrinter(NRPrinter printer) {
		NRConsoleJobExecPlugin.printer = printer;
	}

}
