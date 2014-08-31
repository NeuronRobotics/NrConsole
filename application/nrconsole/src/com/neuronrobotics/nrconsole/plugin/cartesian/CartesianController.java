package com.neuronrobotics.nrconsole.plugin.cartesian;

import java.awt.Dimension;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.replicator.driver.BowlerBoardDevice;
import com.neuronrobotics.replicator.driver.NRPrinter;
import com.neuronrobotics.sdk.addons.kinematics.gui.SampleGuiNR;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class CartesianController extends AbstractNRConsoleTabedPanelPlugin{
	public static final String[] myNames ={"bcs.cartesian.*"};
	
	private PrinterConfiguration gui = new PrinterConfiguration();
	private BowlerBoardDevice delt = new BowlerBoardDevice();
	private NRPrinter printer=null;
	
	
	public CartesianController(PluginManager pm) {
		super(myNames, pm);
		
	}

	@Override
	public JPanel getTabPane() {
		// TODO Auto-generated method stub
		if(gui != null)
			gui.setName("Cartesian Robot");
		return  gui;
	}

	@Override
	public boolean setConnection(BowlerAbstractConnection connection) {
		delt.setConnection(connection);
		delt.connect();
		printer = new NRPrinter(delt);
		
		gui.setKinematicsModel(printer);
		
		return delt.isAvailable();
	}

	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return null;
	}

}
