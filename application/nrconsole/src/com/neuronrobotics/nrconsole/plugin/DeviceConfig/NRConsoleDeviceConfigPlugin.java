package com.neuronrobotics.nrconsole.plugin.DeviceConfig;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.replicator.driver.BowlerBoardDevice;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRConsoleDeviceConfigPlugin extends AbstractNRConsoleTabedPanelPlugin{
	public static final String[] myNames ={"bcs.cartesian.*"};
	
	
	
	private DeviceConfigPanel gui = new DeviceConfigPanel();
	private JPanel holder;
	private BowlerBoardDevice delt = new BowlerBoardDevice();
	private static NRPrinter printer=null;
	private Dimension minSize = new Dimension(693, 476);

	public NRConsoleDeviceConfigPlugin(PluginManager pm) {
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
			gui.setName("Settings");
			
		}
		return gui;
	}

	@Override
	public boolean setConnection(BowlerAbstractConnection connection) {
		delt.setConnection(connection);
		delt.connect();		
		if(NRConsoleDeviceConfigPlugin.getPrinter() == null)
			NRConsoleDeviceConfigPlugin.setPrinter(new NRPrinter(delt));
		
		if (delt.isAvailable()){
			gui.setDevices(delt, getPrinter());
		}
		if (delt.isAvailable()){
			gui.updateSettings();
		}
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
		NRConsoleDeviceConfigPlugin.printer = printer;
	}

}
