package com.neuronrobotics.nrconsole.plugin.BowlerConfig;

import java.awt.Dimension;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.nrconsole.plugin.JobExec.NRConsoleJobExecPlugin;
import com.neuronrobotics.replicator.driver.BowlerBoardDevice;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.sdk.addons.kinematics.gui.SampleGuiNR;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRConsoleBowlerConfigPlugin extends AbstractNRConsoleTabedPanelPlugin{
	public static final String[] myNames ={"bcs.cartesian.*"};
	
	private BowlerConfigPanel gui = new BowlerConfigPanel();
	private BowlerBoardDevice delt = new BowlerBoardDevice();
	private NRPrinter printer=null;
	
	
	public NRConsoleBowlerConfigPlugin(PluginManager pm) {
		super(myNames, pm);
		
	}

	@Override
	public JPanel getTabPane() {
		// TODO Auto-generated method stub
		if(gui == null)
			gui = new BowlerConfigPanel();
			gui.setName("Bowler Config");
		return  gui;
	}

	@Override
	public boolean setConnection(BowlerAbstractConnection connection) {
		delt.setConnection(connection);
		delt.connect();
		if (delt.isAvailable()){
			if(NRConsoleJobExecPlugin.getPrinter() == null)
				NRConsoleJobExecPlugin.setPrinter(new NRPrinter(delt));
			printer = NRConsoleJobExecPlugin.getPrinter();	
		gui.setDevices(delt, printer);
		}
		//gui.setKinematicsModel(printer);
		
		return delt.isAvailable();
	}

	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return null;
	}

}
