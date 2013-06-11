package com.neuronrobotics.nrconsole.plugin.BowlerRPCDisplay;

import java.awt.Dimension;

import javax.swing.JPanel;

import com.neuronrobotics.nrconsole.plugin.AbstractNRConsoleTabedPanelPlugin;
import com.neuronrobotics.nrconsole.plugin.PluginManager;
import com.neuronrobotics.sdk.common.BowlerAbstractConnection;

public class NRConsoleBowlerRPCDisplayPlugin extends AbstractNRConsoleTabedPanelPlugin {
	
	public static final String[] myNames ={"bcs.rpc.*"};
	
	BowlerRPCDisplay display = new BowlerRPCDisplay();
	
	public NRConsoleBowlerRPCDisplayPlugin(PluginManager pm) {
		super(myNames, pm);
	}

	@Override
	public JPanel getTabPane() {
		return display;
	}

	@Override
	public boolean setConnection(BowlerAbstractConnection connection) {
		// TODO Auto-generated method stub
		return display.setConnection(connection);
	}

	@Override
	public Dimension getMinimumWimdowDimentions() {
		// TODO Auto-generated method stub
		return null;
	}


}
